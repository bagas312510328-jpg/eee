package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.DailyTask
import com.example.data.model.FrequencyType
import com.example.data.model.Habit
import com.example.data.model.HabitCategory
import com.example.data.model.NotificationPersona
import com.example.data.model.Trophy
import com.example.data.model.UserProfile
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HabitRepository(context: Context) {

  private val prefs: SharedPreferences =
    (context.applicationContext ?: context).getSharedPreferences("vibe_habit_prefs", Context.MODE_PRIVATE)

  private val moshi: Moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

  private val habitListType = Types.newParameterizedType(List::class.java, Habit::class.java)
  private val habitAdapter = moshi.adapter<List<Habit>>(habitListType)

  private val taskListType = Types.newParameterizedType(List::class.java, DailyTask::class.java)
  private val taskAdapter = moshi.adapter<List<DailyTask>>(taskListType)

  private val _isOnboarded = MutableStateFlow(prefs.getBoolean("key_onboarded", false))
  val isOnboarded: StateFlow<Boolean> = _isOnboarded.asStateFlow()

  private val _habits = MutableStateFlow<List<Habit>>(loadHabits())
  val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

  private val _tasks = MutableStateFlow<List<DailyTask>>(loadTasks())
  val tasks: StateFlow<List<DailyTask>> = _tasks.asStateFlow()

  private val _userProfile = MutableStateFlow(loadProfile())
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  private val _trophies = MutableStateFlow<List<Trophy>>(loadTrophies())
  val trophies: StateFlow<List<Trophy>> = _trophies.asStateFlow()

  // Streak state flow (starts at 0 for fresh accounts until first habit/task is completed)
  private val _masterStreak = MutableStateFlow(prefs.getInt("master_streak", 0))
  val masterStreak: StateFlow<Int> = _masterStreak.asStateFlow()

  // 24-hour activity check
  private val _isStreakActive = MutableStateFlow(computeIsStreakActive())
  val isStreakActive: StateFlow<Boolean> = _isStreakActive.asStateFlow()

  // Ignition trigger event flow
  private val _ignitionEvent = MutableSharedFlow<Int>(extraBufferCapacity = 1)
  val ignitionEvent: SharedFlow<Int> = _ignitionEvent.asSharedFlow()

  init {
    if (_habits.value.isEmpty() && _tasks.value.isEmpty() && _masterStreak.value <= 0) {
      _isStreakActive.value = false
    } else {
      checkStreakExpiry()
    }
  }

  fun checkStreakExpiry() {
    val lastTime = prefs.getLong("last_activity_time", 0L)
    val now = System.currentTimeMillis()
    val hoursPassed = if (lastTime > 0) (now - lastTime) / (1000 * 60 * 60) else 0

    val anyDoneToday = _habits.value.any { it.isCompletedToday } || _tasks.value.any { it.isCompleted }
    if (lastTime > 0 && hoursPassed >= 24 && !anyDoneToday) {
      // 24 hours without activity passed! Streak goes dormant
      _isStreakActive.value = false
    } else {
      _isStreakActive.value = anyDoneToday || (lastTime > 0 && hoursPassed < 24)
    }
  }

  private fun computeIsStreakActive(): Boolean {
    val lastTime = prefs.getLong("last_activity_time", 0L)
    val now = System.currentTimeMillis()
    val hoursPassed = if (lastTime > 0) (now - lastTime) / (1000 * 60 * 60) else 0
    val anyDoneToday = _habits.value.any { it.isCompletedToday } || _tasks.value.any { it.isCompleted }
    return anyDoneToday || (lastTime > 0 && hoursPassed < 24)
  }

  private fun registerActivity() {
    val now = System.currentTimeMillis()
    val wasActive = _isStreakActive.value
    val currentStreak = _masterStreak.value

    prefs.edit().putLong("last_activity_time", now).apply()

    if (!wasActive || currentStreak <= 0) {
      // Rekindle flame from zero/dormant!
      val newStreak = 1
      _masterStreak.value = newStreak
      _isStreakActive.value = true
      prefs.edit().putInt("master_streak", newStreak).apply()
      _ignitionEvent.tryEmit(newStreak)
    } else {
      // Already active, verify if needs ignition celebrate on first activity
      _isStreakActive.value = true
      _ignitionEvent.tryEmit(currentStreak)
    }

    _userProfile.update {
      it.copy(
        masterStreakDays = _masterStreak.value,
        lastActivityTimestamp = now,
        longestStreakDays = maxOf(it.longestStreakDays, _masterStreak.value)
      )
    }
    saveProfile(_userProfile.value)
  }

  fun completeOnboarding(name: String) {
    val cleanName = name.trim().ifBlank { "Habit Hero" }
    val handle = "@" + cleanName.lowercase().replace(" ", "_")
    prefs.edit()
      .putBoolean("key_onboarded", true)
      .putString("user_name", cleanName)
      .putString("user_handle", handle)
      .apply()

    _isOnboarded.value = true
    _userProfile.update { it.copy(name = cleanName, handle = handle) }
    saveProfile(_userProfile.value)
  }

  fun toggleHabitToday(habitId: String) {
    var wasNowCompleted = false
    _habits.update { list ->
      list.map { habit ->
        if (habit.id == habitId) {
          val newCompleted = !habit.isCompletedToday
          if (newCompleted) wasNowCompleted = true
          val newCount = if (newCompleted) habit.targetCount else 0
          val streakDelta = if (newCompleted) 1 else -1
          val updatedStreak = maxOf(0, habit.streakDays + streakDelta)
          val newMomentum = habit.weekMomentum.toMutableList()
          if (newMomentum.isNotEmpty()) {
            newMomentum[newMomentum.lastIndex] = newCompleted
          }
          habit.copy(
            isCompletedToday = newCompleted,
            currentCount = newCount,
            streakDays = updatedStreak,
            weekMomentum = newMomentum
          )
        } else habit
      }
    }
    saveHabits(_habits.value)
    if (wasNowCompleted) {
      registerActivity()
    } else {
      checkStreakExpiry()
    }
    recalculateProfileStats()
  }

  fun updateHabitProgress(habitId: String, delta: Int) {
    var isNowDone = false
    _habits.update { list ->
      list.map { habit ->
        if (habit.id == habitId) {
          val next = maxOf(0, habit.currentCount + delta)
          isNowDone = next >= habit.targetCount && habit.targetCount > 0
          val newMomentum = habit.weekMomentum.toMutableList()
          if (newMomentum.isNotEmpty()) {
            newMomentum[newMomentum.lastIndex] = isNowDone
          }
          habit.copy(
            currentCount = next,
            isCompletedToday = isNowDone,
            weekMomentum = newMomentum
          )
        } else habit
      }
    }
    saveHabits(_habits.value)
    if (isNowDone) {
      registerActivity()
    }
    recalculateProfileStats()
  }

  fun toggleMomentumSquare(habitId: String, dayIndex: Int) {
    _habits.update { list ->
      list.map { habit ->
        if (habit.id == habitId && dayIndex in habit.weekMomentum.indices) {
          val nextMomentum = habit.weekMomentum.toMutableList()
          nextMomentum[dayIndex] = !nextMomentum[dayIndex]
          habit.copy(weekMomentum = nextMomentum)
        } else habit
      }
    }
    saveHabits(_habits.value)
  }

  fun addHabit(newHabit: Habit) {
    _habits.update { listOf(newHabit) + it }
    saveHabits(_habits.value)
    recalculateProfileStats()
  }

  fun pauseHabit(habitId: String) {
    _habits.update { list ->
      list.map { habit ->
        if (habit.id == habitId) {
          habit.copy(isPaused = true, statusNote = "Paused today")
        } else habit
      }
    }
    saveHabits(_habits.value)
  }

  fun resumeHabit(habitId: String) {
    _habits.update { list ->
      list.map { habit ->
        if (habit.id == habitId) {
          habit.copy(isPaused = false, isArchived = false, statusNote = null)
        } else habit
      }
    }
    saveHabits(_habits.value)
  }

  fun archiveHabit(habitId: String) {
    _habits.update { list ->
      list.map { habit ->
        if (habit.id == habitId) {
          habit.copy(isArchived = true, statusNote = "Archived")
        } else habit
      }
    }
    saveHabits(_habits.value)
  }

  fun toggleTask(taskId: String) {
    var wasChecked = false
    _tasks.update { list ->
      list.map { task ->
        if (task.id == taskId) {
          val nextState = !task.isCompleted
          if (nextState) wasChecked = true
          task.copy(
            isCompleted = nextState,
            completedAt = if (nextState) System.currentTimeMillis() else null
          )
        } else task
      }
    }
    saveTasks(_tasks.value)
    if (wasChecked) {
      registerActivity()
    } else {
      checkStreakExpiry()
    }
    _trophies.value = loadTrophies()
  }

  fun addTask(task: DailyTask) {
    _tasks.update { listOf(task) + it }
    saveTasks(_tasks.value)
  }

  fun deleteTask(taskId: String) {
    _tasks.update { list -> list.filter { it.id != taskId } }
    saveTasks(_tasks.value)
  }

  // Testing & demo tool: Simulate 24 hours passed without activity
  fun simulate24HoursPassed() {
    val pastTime = System.currentTimeMillis() - (26 * 3600 * 1000L)
    prefs.edit().putLong("last_activity_time", pastTime).apply()

    // Uncheck habits for today
    _habits.update { list ->
      list.map { it.copy(isCompletedToday = false, currentCount = 0) }
    }
    saveHabits(_habits.value)

    // Uncheck tasks
    _tasks.update { list ->
      list.map { it.copy(isCompleted = false) }
    }
    saveTasks(_tasks.value)

    _isStreakActive.value = false
    _userProfile.update { it.copy(habitsCrushed = 0) }
  }

  // Rekindle / ignite streak manually
  fun igniteStreak() {
    registerActivity()
  }

  fun setNotificationPersona(persona: NotificationPersona) {
    _userProfile.update { it.copy(persona = persona) }
    saveProfile(_userProfile.value)
  }

  fun setSoundHaptics(enabled: Boolean) {
    _userProfile.update { it.copy(soundHapticsEnabled = enabled) }
    saveProfile(_userProfile.value)
  }

  fun updateVibeSchedule(times: List<String>) {
    _userProfile.update { it.copy(vibeCheckTimes = times) }
    saveProfile(_userProfile.value)
  }

  fun updateProfileDetails(name: String, bio: String) {
    val cleanName = name.trim().ifBlank { _userProfile.value.name }
    val handle = "@" + cleanName.lowercase().replace(" ", "_")
    _userProfile.update { it.copy(name = cleanName, handle = handle, bio = bio) }
    saveProfile(_userProfile.value)
  }

  fun resetAllData() {
    prefs.edit().clear().apply()
    _isOnboarded.value = false
    _habits.value = emptyList()
    _tasks.value = emptyList()
    _masterStreak.value = 0
    _isStreakActive.value = false
    _userProfile.value = UserProfile(name = "Sahabat", handle = "@sahabat", habitsCrushed = 0, longestStreakDays = 0, focusHoursLogged = 0)
    _trophies.value = loadTrophies()
  }

  private fun recalculateProfileStats() {
    val completedCount = _habits.value.count { it.isCompletedToday && !it.isPaused && !it.isArchived }
    val maxStreak = maxOf(_masterStreak.value, _habits.value.maxOfOrNull { it.streakDays } ?: 0)

    _userProfile.update { current ->
      current.copy(
        habitsCrushed = completedCount,
        longestStreakDays = maxStreak,
        masterStreakDays = _masterStreak.value
      )
    }
    saveProfile(_userProfile.value)
    _trophies.value = loadTrophies()
  }

  // Encrypted persistent storage helpers
  private fun saveHabits(list: List<Habit>) {
    try {
      val json = habitAdapter.toJson(list)
      val encrypted = com.example.util.CryptoSecurityManager.encrypt(json)
      prefs.edit().putString("key_habits_sec", encrypted).apply()
    } catch (_: Exception) {}
  }

  private fun loadHabits(): List<Habit> {
    val stored = prefs.getString("key_habits_sec", null) ?: prefs.getString("key_habits_json", null)
    if (!stored.isNullOrBlank()) {
      try {
        val json = com.example.util.CryptoSecurityManager.decrypt(stored)
        val list = habitAdapter.fromJson(json) ?: emptyList()
        val realList = list.filterNot { it.id == "h1" || it.id == "h2" }
        if (realList != list) {
          saveHabits(realList)
        }
        return realList
      } catch (_: Exception) {}
    }
    // Clean initial state for new accounts: no demo habits
    return emptyList()
  }

  private fun saveTasks(list: List<DailyTask>) {
    try {
      val json = taskAdapter.toJson(list)
      val encrypted = com.example.util.CryptoSecurityManager.encrypt(json)
      prefs.edit().putString("key_tasks_sec", encrypted).apply()
    } catch (_: Exception) {}
  }

  private fun loadTasks(): List<DailyTask> {
    val stored = prefs.getString("key_tasks_sec", null) ?: prefs.getString("key_tasks_json", null)
    if (!stored.isNullOrBlank()) {
      try {
        val json = com.example.util.CryptoSecurityManager.decrypt(stored)
        val list = taskAdapter.fromJson(json) ?: emptyList()
        val realList = list.filterNot { it.id == "t1" || it.id == "t2" }
        if (realList != list) {
          saveTasks(realList)
        }
        return realList
      } catch (_: Exception) {}
    }
    // Clean initial state for new accounts: no demo tasks
    return emptyList()
  }

  private fun saveProfile(profile: UserProfile) {
    prefs.edit()
      .putString("user_name", profile.name)
      .putString("user_handle", profile.handle)
      .putString("user_bio", profile.bio)
      .putInt("user_level", profile.level)
      .putInt("user_crushed", profile.habitsCrushed)
      .putInt("user_streak", profile.longestStreakDays)
      .putInt("master_streak", profile.masterStreakDays)
      .putBoolean("user_sound", profile.soundHapticsEnabled)
      .putString("user_persona", profile.persona.name)
      .apply()
  }

  private fun loadProfile(): UserProfile {
    val name = prefs.getString("user_name", "Sahabat") ?: "Sahabat"
    val handle = prefs.getString("user_handle", "@sahabat") ?: "@sahabat"
    val bio = prefs.getString("user_bio", "Membangun kebiasaan positif hari demi hari") ?: ""
    val level = prefs.getInt("user_level", 1)
    val crushed = prefs.getInt("user_crushed", 0)
    val streak = prefs.getInt("user_streak", 0)
    val masterStreak = prefs.getInt("master_streak", 0)
    val lastTime = prefs.getLong("last_activity_time", 0L)
    val sound = prefs.getBoolean("user_sound", true)
    val personaStr = prefs.getString("user_persona", "HYPE") ?: "HYPE"
    val persona = try { NotificationPersona.valueOf(personaStr) } catch (_: Exception) { NotificationPersona.HYPE }

    return UserProfile(
      name = name,
      handle = handle,
      titleBadge = "Habit Starter",
      bio = bio,
      level = level,
      habitsCrushed = crushed,
      focusHoursLogged = crushed * 2,
      longestStreakDays = maxOf(streak, masterStreak),
      masterStreakDays = masterStreak,
      lastActivityTimestamp = lastTime,
      persona = persona,
      soundHapticsEnabled = sound
    )
  }

  private fun loadTrophies(): List<Trophy> {
    val habitsList = _habits?.value ?: emptyList()
    val tasksList = _tasks?.value ?: emptyList()
    val completedCount = habitsList.count { it.isCompletedToday }
    val maxStreak = _masterStreak?.value ?: 1
    val tasksDone = tasksList.count { it.isCompleted }

    return listOf(
      Trophy(
        id = "t1",
        title = "First Step",
        description = "Mulai dan kunci kebiasaan pertamamu.",
        iconType = "eco",
        isUnlocked = habitsList.isNotEmpty()
      ),
      Trophy(
        id = "t2",
        title = "Streak Starter",
        description = "Pertahankan api streak menyala 3+ hari.",
        iconType = "local_fire_department",
        isUnlocked = maxStreak >= 3,
        progressPercent = minOf(100, (maxStreak * 100) / 3),
        progressText = "$maxStreak/3 hari"
      ),
      Trophy(
        id = "t3",
        title = "Lock In Master",
        description = "Selesaikan semua habit harian hari ini.",
        iconType = "lock",
        isUnlocked = completedCount > 0 && completedCount == habitsList.size
      ),
      Trophy(
        id = "t4",
        title = "Task Slayer",
        description = "Selesaikan tugas harian di Streak Tasks.",
        iconType = "self_improvement",
        isUnlocked = tasksDone >= 3,
        progressPercent = minOf(100, (tasksDone * 100) / 3),
        progressText = "$tasksDone/3 tugas"
      )
    )
  }
}
