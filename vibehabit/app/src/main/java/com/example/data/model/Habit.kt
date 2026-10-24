package com.example.data.model

enum class HabitCategory(
  val displayName: String,
  val shortLabel: String,
  val emoji: String
) {
  MIND("Mind & Study", "Mind", ""),
  FITNESS("Fitness & Movement", "Fitness", ""),
  HEALTH("Health", "Health", ""),
  GLOW_UP("Glow Up", "Glow Up", ""),
  DETOX("Digital Detox", "Detox", ""),
  WELLNESS("Wellness & Sleep", "Wellness", "")
}

enum class FrequencyType(val label: String) {
  DAILY("Daily"),
  SPECIFIC_DAYS("Specific Days"),
  X_TIMES_PER_WEEK("X times / wk")
}

enum class NotificationPersona(
  val title: String,
  val quote: String
) {
  HYPE("Bestie Hype", "\"Fokus dan jaga konsistensi streak-mu.\""),
  WHISPER("Gentle Whisper", "\"Ambil waktumu sejenak, bernapas dan mulai kembali.\"")
}

data class Habit(
  val id: String,
  val title: String,
  val category: HabitCategory,
  val frequency: FrequencyType = FrequencyType.DAILY,
  val activeDays: List<Int> = listOf(0, 1, 2, 3, 4, 5, 6), // 0=M, 1=T, 2=W, 3=T, 4=F, 5=S, 6=S
  val scheduleTime: String = "08:00 AM",
  val isNumericGoal: Boolean = false,
  val targetCount: Int = 1,
  val currentCount: Int = 0,
  val unit: String = "Times",
  val isCompletedToday: Boolean = false,
  val streakDays: Int = 0,
  val isPaused: Boolean = false,
  val isArchived: Boolean = false,
  val statusNote: String? = null,
  val weekMomentum: List<Boolean> = listOf(false, false, false, false, false, false, false)
)

data class Trophy(
  val id: String,
  val title: String,
  val description: String,
  val iconType: String,
  val isUnlocked: Boolean,
  val progressPercent: Int? = null,
  val progressText: String? = null
)

data class UserProfile(
  val name: String = "Champion",
  val handle: String = "@champion",
  val titleBadge: String = "New Starter",
  val bio: String = "Building atomic habits one day at a time",
  val level: Int = 1,
  val avatarUrl: String = "https://lh3.googleusercontent.com/aida-public/AB6AXuDhcHUiuxI958oEC27g3WAVrrQbyiLrPAjHpTA6PsaNC9M9r0V8CWJLgc7tHm8lA2hOx0vVHVgOdMbJ9j8mLzof_P__l8j5B-Zs14L34vA_U-dAiwoTH7lA5AKpKD2TfAV_iQr8orO8P1A8BF2z0GVofr0tsRfc_NVoEm6GrAOYDgwV8jyakpg_-eILg_mTIzvoHA4f-nmod9p6N30N8R1x5LtcWR1Nasi4Ib6BQqd3khkUPHU6oAuApw",
  val habitsCrushed: Int = 0,
  val focusHoursLogged: Int = 0,
  val longestStreakDays: Int = 1,
  val masterStreakDays: Int = 1,
  val lastActivityTimestamp: Long = 0L,
  val persona: NotificationPersona = NotificationPersona.HYPE,
  val vibeCheckTimes: List<String> = listOf("12:00 PM", "09:00 PM"),
  val soundHapticsEnabled: Boolean = true,
  val currentTheme: String = "Soft Muted Slate Theme"
)

data class DailyTask(
  val id: String,
  val title: String,
  val category: String = "Priority", // "Priority", "Quick Win", "Routine", "Belajar"
  val isCompleted: Boolean = false,
  val timeEstimate: String = "15m",
  val emoji: String = "",
  val completedAt: Long? = null
)
