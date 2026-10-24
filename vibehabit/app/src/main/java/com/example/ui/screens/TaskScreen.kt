package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyTask
import com.example.ui.VibeHabitViewModel
import com.example.ui.components.StreakFlameView
import com.example.ui.components.VibeStickerBox
import com.example.ui.theme.CharcoalInk
import com.example.ui.theme.InkSubtle
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TertiaryFixed
import java.util.UUID

@Composable
fun TaskScreen(
  viewModel: VibeHabitViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val tasks by viewModel.tasks.collectAsState()
  val masterStreak by viewModel.masterStreak.collectAsState()
  val isStreakActive by viewModel.isStreakActive.collectAsState()
  val isIgnitingStreak by viewModel.isIgnitingStreak.collectAsState()

  var selectedFilter by remember { mutableStateOf("All") } // "All", "Priority", "Quick Win", "Routine", "Belajar"
  var newTaskText by remember { mutableStateOf("") }
  var showAddTaskDialog by remember { mutableStateOf(false) }

  val filteredTasks = when (selectedFilter) {
    "Priority" -> tasks.filter { it.category == "Priority" }
    "Quick Win" -> tasks.filter { it.category == "Quick Win" }
    "Routine" -> tasks.filter { it.category == "Routine" }
    "Belajar" -> tasks.filter { it.category == "Belajar" }
    else -> tasks
  }

  val completedCount = tasks.count { it.isCompleted }
  val totalCount = tasks.size
  val progressPercent = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(SurfaceCanvas)
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("task_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {

    // 1. Streak Tasks Bento Hero Card with Flame View
    item {
      VibeStickerBox(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SurfaceWhite,
        shape = RoundedCornerShape(16.dp),
        offsetSize = 3.dp,
        borderWidth = 2.dp
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(9999.dp))
                  .background(if (isStreakActive) Color(0xFFFED7AA) else Color(0xFFE5E7EB))
                  .border(1.dp, CharcoalInk.copy(alpha = 0.2f), RoundedCornerShape(9999.dp))
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text(
                  text = if (isStreakActive) "STREAK TASK AKTIF" else "STREAK DORMANT",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = CharcoalInk
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "Streak Tasks",
                style = MaterialTheme.typography.headlineMedium,
                color = CharcoalInk,
                fontWeight = FontWeight.ExtraBold
              )

              Text(
                text = "Selesaikan tugas harianmu untuk menjaga nyala api streak!",
                style = MaterialTheme.typography.bodySmall,
                color = InkSubtle,
                modifier = Modifier.padding(top = 2.dp)
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Task Streak Flame Icon
            StreakFlameView(
              streakDays = masterStreak,
              isActive = isStreakActive,
              isIgniting = isIgnitingStreak,
              flameSize = 78.dp,
              onClick = {
                if (!isStreakActive) {
                  viewModel.igniteStreak()
                  Toast.makeText(context, "Api streak tugas menyala!", Toast.LENGTH_SHORT).show()
                }
              }
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Progress Bar
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(14.dp)
              .clip(RoundedCornerShape(9999.dp))
              .background(Color(0xFFE5EBF5))
              .border(1.dp, CharcoalInk, RoundedCornerShape(9999.dp))
              .padding(2.dp)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(progressPercent.coerceIn(0f, 1f))
                .height(10.dp)
                .clip(RoundedCornerShape(9999.dp))
                .background(PrimaryBlue)
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "$completedCount dari $totalCount Selesai (${(progressPercent * 100).toInt()}%)",
              style = MaterialTheme.typography.labelSmall,
              color = PrimaryBlue,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (completedCount == totalCount && totalCount > 0) "Tuntas Semua!" else "${totalCount - completedCount} tersisa",
              style = MaterialTheme.typography.labelSmall,
              color = CharcoalInk,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // 2. Add New Task Quick Input Bar
    item {
      VibeStickerBox(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SurfaceWhite,
        shape = RoundedCornerShape(14.dp),
        offsetSize = 2.dp,
        borderWidth = 1.5.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          BasicTextField(
            value = newTaskText,
            onValueChange = { newTaskText = it },
            textStyle = TextStyle(
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
              color = CharcoalInk
            ),
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 10.dp)
              .testTag("new_task_input"),
            decorationBox = { innerTextField ->
              if (newTaskText.isEmpty()) {
                Text(
                  text = "Tambah tugas (misal: Mengerjakan PR, Baca buku)...",
                  style = MaterialTheme.typography.bodySmall,
                  color = InkSubtle
                )
              }
              innerTextField()
            }
          )

          Button(
            onClick = {
              if (newTaskText.isNotBlank()) {
                val newTask = DailyTask(
                  id = UUID.randomUUID().toString(),
                  title = newTaskText.trim(),
                  category = "Priority",
                  isCompleted = false,
                  timeEstimate = "20m",
                  emoji = ""
                )
                viewModel.addTask(newTask)
                newTaskText = ""
                Toast.makeText(context, "Tugas ditambahkan!", Toast.LENGTH_SHORT).show()
              }
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = PrimaryBlue,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.height(38.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Tambah", style = MaterialTheme.typography.labelMedium)
          }
        }
      }
    }

    // 3. Category Filter Tabs & Add Custom Task Button
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier
            .weight(1f)
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val filters = listOf("All", "Priority", "Belajar", "Quick Win", "Routine")
          filters.forEach { filter ->
            val isSelected = selectedFilter == filter
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(9999.dp))
                .background(if (isSelected) PrimaryBlue else Color.White)
                .border(1.5.dp, CharcoalInk, RoundedCornerShape(9999.dp))
                .clickable { selectedFilter = filter }
                .padding(horizontal = 12.dp, vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = filter,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) Color.White else CharcoalInk,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Custom Task Button
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFE5EBF5))
            .border(1.dp, CharcoalInk, RoundedCornerShape(10.dp))
            .clickable { showAddTaskDialog = true }
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Text(
            text = "+ Baru",
            style = MaterialTheme.typography.labelMedium,
            color = PrimaryBlue,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // 4. Tasks List
    if (filteredTasks.isEmpty()) {
      item {
        VibeStickerBox(
          modifier = Modifier.fillMaxWidth(),
          backgroundColor = SurfaceWhite,
          shape = RoundedCornerShape(14.dp),
          offsetSize = 2.dp
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              tint = InkSubtle,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Belum ada tugas di kategori ini!",
              style = MaterialTheme.typography.headlineSmall.copy(fontSize = 15.sp),
              color = CharcoalInk,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Ketik tugas baru di atas untuk mulai mencatat tugas harianmu.",
              style = MaterialTheme.typography.bodySmall,
              color = InkSubtle
            )
          }
        }
      }
    } else {
      items(filteredTasks, key = { it.id }) { task ->
        TaskItemCard(
          task = task,
          onToggle = {
            viewModel.toggleTask(task.id)
            val msg = if (!task.isCompleted) "Tugas selesai! Api streak menyala!" else "Tugas dibatalkan"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
          },
          onDelete = {
            viewModel.deleteTask(task.id)
            Toast.makeText(context, "Tugas dihapus", Toast.LENGTH_SHORT).show()
          }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
    }
  }

  // Detailed Add Task Dialog
  if (showAddTaskDialog) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Priority") }
    var timeEstimate by remember { mutableStateOf("30m") }

    AlertDialog(
      onDismissRequest = { showAddTaskDialog = false },
      title = {
        Text("Tambah Tugas Baru", fontWeight = FontWeight.Bold, color = CharcoalInk)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Nama Tugas (misal: Mengerjakan Tugas)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Text("Kategori Tugas:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("Priority", "Belajar", "Quick Win", "Routine").forEach { cat ->
              val isSel = category == cat
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSel) PrimaryBlue else Color(0xFFF1F5F9))
                  .clickable { category = cat }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = cat,
                  style = MaterialTheme.typography.labelSmall,
                  color = if (isSel) Color.White else CharcoalInk,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          OutlinedTextField(
            value = timeEstimate,
            onValueChange = { timeEstimate = it },
            label = { Text("Estimasi Waktu (misal: 15m, 30m, 1j)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (title.isNotBlank()) {
              val newTask = DailyTask(
                id = UUID.randomUUID().toString(),
                title = title.trim(),
                category = category,
                isCompleted = false,
                timeEstimate = timeEstimate.ifBlank { "20m" },
                emoji = ""
              )
              viewModel.addTask(newTask)
              showAddTaskDialog = false
              Toast.makeText(context, "Tugas berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
          Text("Simpan Tugas")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddTaskDialog = false }) {
          Text("Batal", color = InkSubtle)
        }
      }
    )
  }
}

@Composable
private fun TaskItemCard(
  task: DailyTask,
  onToggle: () -> Unit,
  onDelete: () -> Unit
) {
  val checkboxBg by animateColorAsState(
    targetValue = if (task.isCompleted) PrimaryBlue else Color(0xFFEFF3F8),
    label = "taskCheckColor"
  )

  VibeStickerBox(
    modifier = Modifier.fillMaxWidth(),
    backgroundColor = if (task.isCompleted) Color(0xFFF8FAFC) else SurfaceWhite,
    shape = RoundedCornerShape(12.dp),
    offsetSize = 2.dp,
    borderWidth = 1.5.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Checkbox button
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(checkboxBg)
            .border(1.5.dp, CharcoalInk, RoundedCornerShape(8.dp))
            .clickable(onClick = onToggle)
            .testTag("task_checkbox_${task.id}"),
          contentAlignment = Alignment.Center
        ) {
          if (task.isCompleted) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Done",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (task.isCompleted) InkSubtle else CharcoalInk,
            fontWeight = FontWeight.SemiBold,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(top = 2.dp)
          ) {
            // Category tag
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(
                  when (task.category) {
                    "Priority" -> Color(0xFFFED7AA)
                    "Belajar" -> Color(0xFFDDD6FE)
                    "Quick Win" -> TertiaryFixed
                    else -> SecondaryContainer
                  }
                )
                .padding(horizontal = 6.dp, vertical = 1.dp)
            ) {
              Text(
                text = task.category,
                style = MaterialTheme.typography.labelSmall,
                color = CharcoalInk,
                fontWeight = FontWeight.Bold
              )
            }

            // Estimate badge
            Text(
              text = task.timeEstimate,
              style = MaterialTheme.typography.labelSmall,
              color = InkSubtle
            )
          }
        }
      }

      // Delete action button
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .clickable(onClick = onDelete)
          .padding(4.dp),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          Icons.Default.DeleteOutline,
          contentDescription = "Hapus Tugas",
          tint = InkSubtle,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
