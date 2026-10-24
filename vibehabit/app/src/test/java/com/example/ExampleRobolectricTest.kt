package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.HabitRepository
import com.example.data.model.DailyTask
import com.example.data.model.FrequencyType
import com.example.data.model.Habit
import com.example.data.model.HabitCategory
import com.example.util.AppSecurityIntegrity
import com.example.util.CryptoSecurityManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("VibeHabit", appName)
  }

  @Test
  fun `onboarding completion sets user name and onboarded flag`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = HabitRepository(context)

    repo.completeOnboarding("Budi")

    assertTrue(repo.isOnboarded.value)
    assertEquals("Budi", repo.userProfile.value.name)
    assertEquals("@budi", repo.userProfile.value.handle)
  }

  @Test
  fun `habit repository add and toggle habit updates completion and streak`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = HabitRepository(context)

    val habit = Habit(
      id = "test-habit-1",
      title = "Minum Air 2L",
      category = HabitCategory.HEALTH,
      frequency = FrequencyType.DAILY,
      isCompletedToday = false
    )

    repo.addHabit(habit)
    assertTrue(repo.habits.value.any { it.id == "test-habit-1" })

    repo.toggleHabitToday("test-habit-1")
    val updated = repo.habits.value.first { it.id == "test-habit-1" }
    assertTrue(updated.isCompletedToday)
    assertTrue(repo.isStreakActive.value)
  }

  @Test
  fun `task repository add and toggle task updates completion status`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = HabitRepository(context)

    val task = DailyTask(
      id = "test-task-1",
      title = "Mengerjakan Tugas Kuliah",
      category = "Priority",
      isCompleted = false
    )

    repo.addTask(task)
    assertTrue(repo.tasks.value.any { it.id == "test-task-1" })

    repo.toggleTask("test-task-1")
    val updated = repo.tasks.value.first { it.id == "test-task-1" }
    assertTrue(updated.isCompleted)
    assertTrue(repo.isStreakActive.value)
  }

  @Test
  fun `simulating 24 hours passed extinguishes flame and checking habit reignites it`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = HabitRepository(context)

    repo.simulate24HoursPassed()
    assertFalse(repo.isStreakActive.value)

    repo.igniteStreak()
    assertTrue(repo.isStreakActive.value)
    assertEquals(1, repo.masterStreak.value)
  }

  @Test
  fun `crypto security manager encrypts plaintext and decrypts accurately`() {
    val sensitiveText = "{\"habit\":\"Secret Atomic Habit\",\"priority\":1}"
    val ciphertext = CryptoSecurityManager.encrypt(sensitiveText)

    assertNotEquals(sensitiveText, ciphertext)
    assertTrue(ciphertext.isNotBlank())

    val decrypted = CryptoSecurityManager.decrypt(ciphertext)
    assertEquals(sensitiveText, decrypted)
  }

  @Test
  fun `package integrity verification validates application namespace`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    assertTrue(AppSecurityIntegrity.isPackageIntegrityValid(context))
  }
}
