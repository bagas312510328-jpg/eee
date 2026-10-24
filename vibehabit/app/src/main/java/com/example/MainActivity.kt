package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.DailyTask
import com.example.ui.AppDestination
import com.example.ui.VibeHabitViewModel
import com.example.ui.components.VibeTopBar
import com.example.ui.navigation.VibeBottomNavigation
import com.example.ui.screens.AddHabitScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.OnboardingNameScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TaskScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.CharcoalInk
import com.example.ui.theme.InkSubtle
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.VibeHabitTheme
import java.util.UUID

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    com.example.util.AppSecurityIntegrity.isPackageIntegrityValid(this)
    setContent {
      VibeHabitTheme {
        val habitViewModel: VibeHabitViewModel = viewModel()
        VibeHabitApp(viewModel = habitViewModel)
      }
    }
  }
}

@Composable
fun VibeHabitApp(
  viewModel: VibeHabitViewModel
) {
  val context = LocalContext.current
  val currentScreen by viewModel.currentScreen.collectAsState()

  var showAddChoiceDialog by remember { mutableStateOf(false) }
  var showAddTaskDialog by remember { mutableStateOf(false) }

  // Handle system back button for sub-screens
  BackHandler(
    enabled = currentScreen != AppDestination.Today &&
        currentScreen != AppDestination.Splash &&
        currentScreen != AppDestination.Onboarding
  ) {
    viewModel.navigateTo(AppDestination.Today)
  }

  val isFullScreen = currentScreen == AppDestination.Splash || currentScreen == AppDestination.Onboarding

  if (isFullScreen) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(SurfaceCanvas)
    ) {
      AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
          (scaleIn(
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioMediumBouncy,
              stiffness = Spring.StiffnessMediumLow
            ),
            initialScale = 0.88f
          ) + fadeIn(animationSpec = tween(240))) togetherWith
          (scaleOut(
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioNoBouncy,
              stiffness = Spring.StiffnessMediumLow
            ),
            targetScale = 1.08f
          ) + fadeOut(animationSpec = tween(180)))
        },
        label = "fullScreenTransition"
      ) { screen ->
        when (screen) {
          AppDestination.Splash -> SplashScreen(viewModel = viewModel)
          AppDestination.Onboarding -> OnboardingNameScreen(viewModel = viewModel)
          else -> Unit
        }
      }
    }
    return
  }

  val screenTitle = when (currentScreen) {
    AppDestination.Today -> "VibeHabit"
    AppDestination.Analytics -> "Analytics"
    AppDestination.Task -> "Tasks"
    AppDestination.You -> "Profile"
    AppDestination.AddHabit -> "Tambah Habit"
    else -> "VibeHabit"
  }

  val isAddScreen = currentScreen == AppDestination.AddHabit

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(SurfaceCanvas)
      .statusBarsPadding(),
    topBar = {
      VibeTopBar(
        currentScreenTitle = screenTitle,
        showBack = isAddScreen,
        onBackClick = { viewModel.navigateTo(AppDestination.Today) },
        onProfileClick = { viewModel.navigateTo(AppDestination.You) }
      )
    },
    bottomBar = {
      if (!isAddScreen) {
        VibeBottomNavigation(
          currentScreen = currentScreen,
          onNavigate = { destination -> viewModel.navigateTo(destination) },
          onAddClick = { showAddChoiceDialog = true }
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(SurfaceCanvas)
    ) {
      AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
          (scaleIn(
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioMediumBouncy,
              stiffness = Spring.StiffnessMediumLow
            ),
            initialScale = 0.88f
          ) + fadeIn(animationSpec = tween(240))) togetherWith
          (scaleOut(
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioNoBouncy,
              stiffness = Spring.StiffnessMediumLow
            ),
            targetScale = 1.08f
          ) + fadeOut(animationSpec = tween(180)))
        },
        label = "screenTransition"
      ) { screen ->
        when (screen) {
          AppDestination.Today -> {
            TodayScreen(
              viewModel = viewModel,
              onAddHabitClick = { viewModel.navigateTo(AppDestination.AddHabit) }
            )
          }
          AppDestination.Analytics -> {
            AnalyticsScreen(viewModel = viewModel)
          }
          AppDestination.Task -> {
            TaskScreen(viewModel = viewModel)
          }
          AppDestination.You -> {
            ProfileScreen(viewModel = viewModel)
          }
          AppDestination.AddHabit -> {
            AddHabitScreen(
              viewModel = viewModel,
              onNavigateBack = { viewModel.navigateTo(AppDestination.Today) }
            )
          }
          else -> Unit
        }
      }
    }
  }

  // 1. Central "+" Choice Dialog (Habit or Task)
  if (showAddChoiceDialog) {
    AlertDialog(
      onDismissRequest = { showAddChoiceDialog = false },
      title = {
        Text("Apa yang ingin ditambahkan?", fontWeight = FontWeight.Bold, color = CharcoalInk)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          // Option A: Tambah Habit
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFFEFF6FF))
              .border(1.5.dp, PrimaryBlue, RoundedCornerShape(12.dp))
              .clickable {
                showAddChoiceDialog = false
                viewModel.navigateTo(AppDestination.AddHabit)
              }
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(PrimaryBlue),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Eco, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Column {
              Text("Habit Baru", fontWeight = FontWeight.Bold, color = CharcoalInk, fontSize = 15.sp)
              Text("Bangun kebiasaan harian & rutinitas", style = MaterialTheme.typography.bodySmall, color = InkSubtle)
            }
          }

          // Option B: Tambah Task
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFFFFF7ED))
              .border(1.5.dp, Color(0xFFEA580C), RoundedCornerShape(12.dp))
              .clickable {
                showAddChoiceDialog = false
                showAddTaskDialog = true
              }
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFFEA580C)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Checklist, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Column {
              Text("Task Baru", fontWeight = FontWeight.Bold, color = CharcoalInk, fontSize = 15.sp)
              Text("Catat tugas cepat (PR, belajar, dll)", style = MaterialTheme.typography.bodySmall, color = InkSubtle)
            }
          }
        }
      },
      confirmButton = {},
      dismissButton = {
        TextButton(onClick = { showAddChoiceDialog = false }) {
          Text("Tutup", color = InkSubtle)
        }
      }
    )
  }

  // 2. Quick Add Task Dialog from "+" FAB
  if (showAddTaskDialog) {
    var taskTitle by remember { mutableStateOf("") }
    var taskCategory by remember { mutableStateOf("Priority") }
    var taskTime by remember { mutableStateOf("20m") }

    AlertDialog(
      onDismissRequest = { showAddTaskDialog = false },
      title = { Text("Tambah Task Baru", fontWeight = FontWeight.Bold, color = CharcoalInk) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          OutlinedTextField(
            value = taskTitle,
            onValueChange = { taskTitle = it },
            label = { Text("Nama Tugas (misal: Mengerjakan Tugas)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Text("Kategori:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("Priority", "Belajar", "Quick Win", "Routine").forEach { cat ->
              val isSel = taskCategory == cat
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSel) PrimaryBlue else Color(0xFFF1F5F9))
                  .clickable { taskCategory = cat }
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
            value = taskTime,
            onValueChange = { taskTime = it },
            label = { Text("Estimasi Waktu (misal: 15m, 30m, 1j)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (taskTitle.isNotBlank()) {
              val newTask = DailyTask(
                id = UUID.randomUUID().toString(),
                title = taskTitle.trim(),
                category = taskCategory,
                isCompleted = false,
                timeEstimate = taskTime.ifBlank { "20m" },
                emoji = ""
              )
              viewModel.addTask(newTask)
              showAddTaskDialog = false
              Toast.makeText(context, "Tugas berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
          Text("Simpan Task")
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
