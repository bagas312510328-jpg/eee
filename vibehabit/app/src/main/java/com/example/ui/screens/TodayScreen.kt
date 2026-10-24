package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Habit
import com.example.data.model.HabitCategory
import com.example.ui.VibeHabitViewModel
import com.example.ui.components.StreakHeroCard
import com.example.ui.components.VibeButton
import com.example.ui.components.VibeStickerBox
import com.example.ui.theme.CharcoalInk
import com.example.ui.theme.InkSubtle
import com.example.ui.theme.LightPrimaryContainer
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TertiaryFixed

@Composable
fun TodayScreen(
  viewModel: VibeHabitViewModel,
  onAddHabitClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val habits by viewModel.activeHabits.collectAsState()
  val allActiveHabits by viewModel.allActiveHabitsRaw.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()

  val masterStreak by viewModel.masterStreak.collectAsState()
  val isStreakActive by viewModel.isStreakActive.collectAsState()
  val isIgnitingStreak by viewModel.isIgnitingStreak.collectAsState()

  val totalActive = allActiveHabits.size
  val completedCount = allActiveHabits.count { it.isCompletedToday }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(SurfaceCanvas)
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("today_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {

    // 1. Streak Flame Hero Card (With animated Canvas flame, centered streak number, and extinction logic)
    item {
      StreakHeroCard(
        streakDays = masterStreak,
        isActive = isStreakActive,
        isIgniting = isIgnitingStreak,
        onIgniteClick = {
          if (!isStreakActive) {
            viewModel.igniteStreak()
            Toast.makeText(context, "Api streak menyala kembali!", Toast.LENGTH_SHORT).show()
          }
        }
      )
    }

    // 2. Daily Habits Progress Summary (Clean, no AI icons or generic buzzwords)
    item {
      VibeStickerBox(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SurfaceWhite,
        shape = RoundedCornerShape(14.dp),
        borderWidth = 1.5.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "$completedCount dari $totalActive Habit Selesai",
              style = MaterialTheme.typography.headlineSmall.copy(fontSize = 15.sp),
              color = CharcoalInk,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (completedCount == totalActive && totalActive > 0) "Semua habit hari ini tuntas!"
              else "Kunci habit harian untuk memperkuat konsistensi.",
              style = MaterialTheme.typography.bodySmall,
              color = InkSubtle
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (completedCount == totalActive && totalActive > 0) Color(0xFFD1FAE5) else Color(0xFFE5EBF5))
              .border(1.dp, CharcoalInk.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = if (totalActive > 0) "${((completedCount.toFloat() / totalActive) * 100).toInt()}%" else "0%",
              style = MaterialTheme.typography.labelMedium,
              color = CharcoalInk,
              fontWeight = FontWeight.ExtraBold
            )
          }
        }
      }
    }

    // 3. Category Filter Pills
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // "All" Pill
        val isAllSelected = selectedCategory == null
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(if (isAllSelected) PrimaryBlue else Color.White)
            .border(1.5.dp, CharcoalInk, RoundedCornerShape(9999.dp))
            .clickable { viewModel.setCategoryFilter(null) }
            .padding(horizontal = 14.dp, vertical = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "All",
            style = MaterialTheme.typography.labelMedium,
            color = if (isAllSelected) Color.White else CharcoalInk,
            fontWeight = FontWeight.Bold
          )
        }

        HabitCategory.values().forEach { cat ->
          val isSelected = selectedCategory == cat
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(9999.dp))
              .background(if (isSelected) PrimaryBlue else Color.White)
              .border(1.5.dp, CharcoalInk, RoundedCornerShape(9999.dp))
              .clickable { viewModel.setCategoryFilter(cat) }
              .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = cat.displayName,
              style = MaterialTheme.typography.labelMedium,
              color = if (isSelected) Color.White else CharcoalInk,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // 4. Habits Section Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Habit Harian",
          style = MaterialTheme.typography.headlineSmall,
          color = CharcoalInk,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "${habits.size} Kebiasaan",
          style = MaterialTheme.typography.labelMedium,
          color = InkSubtle
        )
      }
    }

    // 5. Habit Cards List or Empty State
    if (habits.isEmpty()) {
      item {
        VibeStickerBox(
          modifier = Modifier.fillMaxWidth(),
          backgroundColor = SurfaceWhite,
          shape = RoundedCornerShape(16.dp),
          borderWidth = 1.5.dp
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.LocalFireDepartment,
              contentDescription = null,
              tint = PrimaryBlue,
              modifier = Modifier.size(36.dp)
            )
            Text(
              text = "Belum ada habit aktif",
              style = MaterialTheme.typography.headlineSmall,
              color = CharcoalInk,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Tekan tombol di bawah atau '+' di navigasi untuk memulai kebiasaan pertamamu.",
              style = MaterialTheme.typography.bodySmall,
              color = InkSubtle,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            VibeButton(
              text = "+ Tambah Habit Baru",
              onClick = onAddHabitClick,
              backgroundColor = PrimaryBlue,
              modifier = Modifier.height(46.dp),
              testTag = "add_first_habit_btn"
            )
          }
        }
      }
    } else {
      items(habits, key = { it.id }) { habit ->
        TodayHabitCard(
          habit = habit,
          onToggle = {
            viewModel.toggleHabit(habit.id)
            val msg = if (!habit.isCompletedToday) "Checked off! Api streak menyala!" else "Habit dibatalkan"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
          },
          onIncrement = { viewModel.incrementProgress(habit.id) },
          onDecrement = { viewModel.decrementProgress(habit.id) }
        )
      }
    }

    // Bottom padding spacer
    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun TodayHabitCard(
  habit: Habit,
  onToggle: () -> Unit,
  onIncrement: () -> Unit,
  onDecrement: () -> Unit
) {
  val checkScale by animateFloatAsState(
    targetValue = if (habit.isCompletedToday) 1.08f else 1.0f,
    animationSpec = spring(),
    label = "checkScale"
  )

  val cardBg = if (habit.isCompletedToday) SurfaceWhite else Color.White
  val checkboxBg by animateColorAsState(
    targetValue = if (habit.isCompletedToday) PrimaryBlue else Color(0xFFEFF3F8),
    label = "checkColor"
  )

  VibeStickerBox(
    modifier = Modifier.fillMaxWidth(),
    backgroundColor = cardBg,
    shape = RoundedCornerShape(14.dp),
    offsetSize = 2.dp,
    borderWidth = 1.5.dp
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Left side: Checkbox + Title Details
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Checkbox Button
          Box(
            modifier = Modifier
              .size(36.dp)
              .scale(checkScale)
              .clip(RoundedCornerShape(8.dp))
              .background(checkboxBg)
              .border(1.5.dp, CharcoalInk, RoundedCornerShape(8.dp))
              .clickable(onClick = onToggle)
              .testTag("checkbox_${habit.id}"),
            contentAlignment = Alignment.Center
          ) {
            if (habit.isCompletedToday) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Completed",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = habit.title,
              style = MaterialTheme.typography.headlineSmall.copy(fontSize = 15.sp),
              color = CharcoalInk,
              fontWeight = FontWeight.Bold,
              textDecoration = if (habit.isCompletedToday) TextDecoration.LineThrough else TextDecoration.None
            )

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              modifier = Modifier.padding(top = 2.dp)
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(
                    when (habit.category) {
                      HabitCategory.WELLNESS -> TertiaryFixed
                      HabitCategory.MIND -> SecondaryContainer
                      HabitCategory.FITNESS -> LightPrimaryContainer
                      else -> SurfaceContainerHigh
                    }
                  )
                  .padding(horizontal = 6.dp, vertical = 1.dp)
              ) {
                Text(
                  text = habit.category.shortLabel,
                  style = MaterialTheme.typography.labelSmall,
                  color = CharcoalInk,
                  fontWeight = FontWeight.Bold
                )
              }

              Text(
                text = habit.scheduleTime,
                style = MaterialTheme.typography.bodySmall,
                color = InkSubtle
              )

              // Streak Count Pill
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .clip(RoundedCornerShape(9999.dp))
                  .background(Color(0xFFFFF0D6))
                  .padding(horizontal = 6.dp, vertical = 1.dp)
              ) {
                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                  text = "${habit.streakDays}d",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color(0xFF92400E),
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        // Numeric Goal Counter Controls (e.g. 15 / 15 Pages)
        if (habit.isNumericGoal) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(SurfaceContainerLow)
              .border(1.dp, CharcoalInk.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
              .padding(horizontal = 4.dp, vertical = 2.dp)
          ) {
            Box(
              modifier = Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White)
                .clickable(onClick = onDecrement),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "−", fontWeight = FontWeight.Bold, color = CharcoalInk)
            }

            Text(
              text = "${habit.currentCount}/${habit.targetCount}",
              style = MaterialTheme.typography.labelSmall,
              color = PrimaryBlue,
              fontWeight = FontWeight.ExtraBold,
              modifier = Modifier.padding(horizontal = 4.dp)
            )

            Box(
              modifier = Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White)
                .clickable(onClick = onIncrement),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "+", fontWeight = FontWeight.Bold, color = CharcoalInk)
            }
          }
        }
      }
    }
  }
}
