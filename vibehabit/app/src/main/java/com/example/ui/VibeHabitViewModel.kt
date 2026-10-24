package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.HabitRepository
import com.example.data.model.DailyTask
import com.example.data.model.Habit
import com.example.data.model.HabitCategory
import com.example.data.model.NotificationPersona
import com.example.data.model.Trophy
import com.example.data.model.UserProfile
import com.example.util.FeedbackHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AppDestination {
  data object Splash : AppDestination()
  data object Onboarding : AppDestination()
  data object Today : AppDestination()
  data object Analytics : AppDestination()
  data object AddHabit : AppDestination()
  data object Task : AppDestination()
  data object You : AppDestination()
}

class VibeHabitViewModel(
  application: Application
) : AndroidViewModel(application) {

  private val repository = HabitRepository(application)
  private val feedbackHelper = FeedbackHelper(application)

  val isOnboarded: StateFlow<Boolean> = repository.isOnboarded
  val userProfile: StateFlow<UserProfile> = repository.userProfile
  val trophies: StateFlow<List<Trophy>> = repository.trophies
  val tasks: StateFlow<List<DailyTask>> = repository.tasks

  val masterStreak: StateFlow<Int> = repository.masterStreak
  val isStreakActive: StateFlow<Boolean> = repository.isStreakActive

  // Trigger for streak ignition flame burst animation
  private val _isIgnitingStreak = MutableStateFlow(false)
  val isIgnitingStreak: StateFlow<Boolean> = _isIgnitingStreak.asStateFlow()

  private val _selectedCategory = MutableStateFlow<HabitCategory?>(null)
  val selectedCategory: StateFlow<HabitCategory?> = _selectedCategory.asStateFlow()

  // Starts at Splash
  private val _currentScreen = MutableStateFlow<AppDestination>(AppDestination.Splash)
  val currentScreen: StateFlow<AppDestination> = _currentScreen.asStateFlow()

  val activeHabits: StateFlow<List<Habit>> = combine(repository.habits, _selectedCategory) { habits, category ->
    habits.filter { !it.isPaused && !it.isArchived }
      .filter { category == null || it.category == category }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val pausedOrArchivedHabits: StateFlow<List<Habit>> = repository.habits
    .combine(_selectedCategory) { habits, _ ->
      habits.filter { it.isPaused || it.isArchived }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allActiveHabitsRaw: StateFlow<List<Habit>> = repository.habits
    .combine(_selectedCategory) { habits, _ ->
      habits.filter { !it.isPaused && !it.isArchived }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    viewModelScope.launch {
      repository.ignitionEvent.collect { streakDays ->
        triggerIgnitionCelebration()
      }
    }
  }

  private fun triggerIgnitionCelebration() {
    viewModelScope.launch {
      _isIgnitingStreak.value = true
      feedbackHelper.playStreakIgnite(userProfile.value.soundHapticsEnabled)
      delay(1200)
      _isIgnitingStreak.value = false
    }
  }

  fun navigateTo(destination: AppDestination) {
    _currentScreen.value = destination
  }

  fun onSplashCompleted() {
    if (isOnboarded.value) {
      _currentScreen.value = AppDestination.Today
    } else {
      _currentScreen.value = AppDestination.Onboarding
    }
  }

  fun completeOnboarding(name: String) {
    repository.completeOnboarding(name)
    feedbackHelper.playToggle(userProfile.value.soundHapticsEnabled)
    _currentScreen.value = AppDestination.Today
  }

  fun setCategoryFilter(category: HabitCategory?) {
    _selectedCategory.value = category
    feedbackHelper.playToggle(userProfile.value.soundHapticsEnabled)
  }

  fun toggleHabit(habitId: String) {
    val habit = activeHabits.value.find { it.id == habitId }
    val willComplete = habit?.isCompletedToday == false
    repository.toggleHabitToday(habitId)

    if (willComplete) {
      feedbackHelper.playHabitDone(userProfile.value.soundHapticsEnabled)
    } else {
      feedbackHelper.playToggle(userProfile.value.soundHapticsEnabled)
    }
  }

  fun incrementProgress(habitId: String) {
    repository.updateHabitProgress(habitId, +1)
    feedbackHelper.playHabitDone(userProfile.value.soundHapticsEnabled)
  }

  fun decrementProgress(habitId: String) {
    repository.updateHabitProgress(habitId, -1)
    feedbackHelper.playToggle(userProfile.value.soundHapticsEnabled)
  }

  fun toggleMomentum(habitId: String, dayIndex: Int) {
    repository.toggleMomentumSquare(habitId, dayIndex)
    feedbackHelper.playToggle(userProfile.value.soundHapticsEnabled)
  }

  fun addHabit(habit: Habit) {
    repository.addHabit(habit)
    feedbackHelper.playHabitDone(userProfile.value.soundHapticsEnabled)
  }

  fun pauseHabit(habitId: String) {
    repository.pauseHabit(habitId)
    feedbackHelper.playToggle(userProfile.value.soundHapticsEnabled)
  }

  fun resumeHabit(habitId: String) {
    repository.resumeHabit(habitId)
    feedbackHelper.playToggle(userProfile.value.soundHapticsEnabled)
  }

  fun archiveHabit(habitId: String) {
    repository.archiveHabit(habitId)
    feedbackHelper.playToggle(userProfile.value.soundHapticsEnabled)
  }

  fun setPersona(persona: NotificationPersona) {
    repository.setNotificationPersona(persona)
    feedbackHelper.playToggle(userProfile.value.soundHapticsEnabled)
  }

  fun setSoundHaptics(enabled: Boolean) {
    repository.setSoundHaptics(enabled)
    // Play test haptic/tone so user immediately feels and hears it work
    if (enabled) {
      feedbackHelper.playStreakIgnite(true)
    }
  }

  fun testFeedback() {
    feedbackHelper.playStreakIgnite(userProfile.value.soundHapticsEnabled)
  }

  fun updateVibeTimes(times: List<String>) {
    repository.updateVibeSchedule(times)
  }

  fun updateProfile(name: String, bio: String) {
    repository.updateProfileDetails(name, bio)
  }

  fun toggleTask(taskId: String) {
    val task = tasks.value.find { it.id == taskId }
    val willComplete = task?.isCompleted == false
    repository.toggleTask(taskId)

    if (willComplete) {
      feedbackHelper.playTaskDone(userProfile.value.soundHapticsEnabled)
    } else {
      feedbackHelper.playToggle(userProfile.value.soundHapticsEnabled)
    }
  }

  fun addTask(task: DailyTask) {
    repository.addTask(task)
    feedbackHelper.playTaskDone(userProfile.value.soundHapticsEnabled)
  }

  fun deleteTask(taskId: String) {
    repository.deleteTask(taskId)
    feedbackHelper.playToggle(userProfile.value.soundHapticsEnabled)
  }

  fun simulate24HoursPassed() {
    repository.simulate24HoursPassed()
    feedbackHelper.playToggle(userProfile.value.soundHapticsEnabled)
  }

  fun igniteStreak() {
    repository.igniteStreak()
    triggerIgnitionCelebration()
  }

  fun resetAllData() {
    repository.resetAllData()
    _currentScreen.value = AppDestination.Onboarding
  }
}
