package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Habit
import com.example.data.model.HabitCategory
import com.example.data.model.Trophy
import com.example.ui.VibeHabitViewModel
import com.example.ui.components.CartoonAnalyticsIcon
import com.example.ui.components.VibeButton
import com.example.ui.components.VibePillBadge
import com.example.ui.components.VibeStickerBox
import com.example.ui.theme.CharcoalInk
import com.example.ui.theme.DeepPrimary
import com.example.ui.theme.InkSubtle
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SoftPastelCell
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnalyticsScreen(
  viewModel: VibeHabitViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val habits by viewModel.activeHabits.collectAsState()
  val trophies by viewModel.trophies.collectAsState()
  val profile by viewModel.userProfile.collectAsState()
  val masterStreak by viewModel.masterStreak.collectAsState()

  val completedHabitsCount = habits.count { it.isCompletedToday }
  val successRate = if (habits.isNotEmpty()) ((completedHabitsCount.toFloat() / habits.size) * 100).toInt() else 0
  val crushedCount = profile.habitsCrushed

  var showAllTrophiesDialog by remember { mutableStateOf(false) }
  var selectedTrophy by remember { mutableStateOf<Trophy?>(null) }
  var isSharedRecap by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(SurfaceCanvas)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("analytics_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {

    // 1. Top Greeting & Stats Counter ("Laporan Mingguan")
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
          verticalAlignment = Alignment.Top
        ) {
          Column(modifier = Modifier.weight(1f)) {
            VibePillBadge(
              text = "LAPORAN MINGGUAN",
              backgroundColor = SoftPastelCell,
              textColor = CharcoalInk,
              icon = {
                CartoonAnalyticsIcon(isSelected = true, modifier = Modifier.size(15.dp))
              }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = if (habits.isEmpty()) "Mulai perjalananmu hari ini!" else "Konsistensi terus meningkat!",
              style = MaterialTheme.typography.headlineMedium,
              color = CharcoalInk,
              fontWeight = FontWeight.Bold,
              lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = if (habits.isEmpty()) "Kunci habit pertamamu untuk melacak perkembangan." else "Melacak ${habits.size} kebiasaan harian secara teratur.",
              style = MaterialTheme.typography.bodySmall,
              color = InkSubtle
            )
          }

          Box(
            modifier = Modifier
              .size(54.dp)
              .rotate(4f)
              .clip(CircleShape)
              .background(PrimaryBlue)
              .border(2.dp, CharcoalInk, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            CartoonAnalyticsIcon(isSelected = true, modifier = Modifier.size(32.dp))
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3-Column Stats Counter
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          StatCard(
            value = "$successRate%",
            label = "TINGKAT SUKSES",
            modifier = Modifier.weight(1f)
          )

          StatCard(
            value = "${masterStreak}d",
            label = "API STREAK",
            modifier = Modifier.weight(1f)
          )

          StatCard(
            value = "$crushedCount",
            label = "SELESAI",
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 2. Weekly Momentum Activity Heatmap Grid
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
          Column {
            Text(
              text = "Riwayat 7 Hari",
              style = MaterialTheme.typography.headlineSmall,
              color = CharcoalInk,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (habits.isEmpty()) "Matriks momentum harian" else "Ketuk kotak untuk menandai check-in",
              style = MaterialTheme.typography.bodySmall,
              color = InkSubtle
            )
          }

          VibePillBadge(
            text = if (habits.isEmpty()) "0 Habit" else "${habits.size} Habit",
            backgroundColor = SoftPastelCell,
            textColor = DeepPrimary,
            icon = {
              Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = DeepPrimary, modifier = Modifier.size(14.dp))
            }
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (habits.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(SurfaceContainerLow)
              .border(1.dp, CharcoalInk.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
              .padding(20.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Checklist,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(36.dp)
              )
              Text(
                text = "Belum Ada Riwayat Check-In",
                style = MaterialTheme.typography.titleMedium,
                color = CharcoalInk,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Riwayat 7 hari mencatat konsistensi harian setiap kebiasaan. Tambahkan habit pertamamu di menu Today untuk mulai mencatat check-in.",
                style = MaterialTheme.typography.bodySmall,
                color = InkSubtle,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        } else {
          // Day Headers: HABIT | S | S | R | K | J | S | M
          val days = listOf("S", "S", "R", "K", "J", "S", "M")
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "HABIT",
              style = MaterialTheme.typography.labelSmall,
              color = InkSubtle,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.width(82.dp)
            )
            days.forEach { day ->
              Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = day,
                  style = MaterialTheme.typography.labelSmall,
                  color = InkSubtle,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          habits.take(5).forEach { habit ->
            val label = habit.title

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                modifier = Modifier.width(82.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                  text = label,
                  style = MaterialTheme.typography.labelSmall,
                  color = CharcoalInk,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              habit.weekMomentum.forEachIndexed { dIdx, isChecked ->
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Box(
                    modifier = Modifier
                      .size(32.dp)
                      .clip(RoundedCornerShape(6.dp))
                      .background(if (isChecked) PrimaryBlue else Color(0xFFE5EBF5))
                      .border(1.dp, CharcoalInk, RoundedCornerShape(6.dp))
                      .clickable {
                        viewModel.toggleMomentum(habit.id, dIdx)
                        val dayName = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")[dIdx]
                        val status = if (!isChecked) "Selesai" else "Direset"
                        Toast.makeText(context, "$label pada $dayName: $status!", Toast.LENGTH_SHORT).show()
                      },
                    contentAlignment = Alignment.Center
                  ) {
                    if (isChecked) {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // 3. Category Breakdown Card (Kategori Dominan)
    val totalChecked = habits.sumOf { it.weekMomentum.count { c -> c } }

    VibeStickerBox(
      modifier = Modifier.fillMaxWidth(),
      backgroundColor = SurfaceWhite,
      shape = RoundedCornerShape(16.dp),
      offsetSize = 3.dp,
      borderWidth = 2.dp
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        if (habits.isEmpty()) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Kategori Dominan",
                style = MaterialTheme.typography.headlineSmall,
                color = CharcoalInk,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Fokus kebiasaan utama mingguan",
                style = MaterialTheme.typography.bodySmall,
                color = InkSubtle
              )
            }
            VibePillBadge(
              text = "Belum Ada Data",
              backgroundColor = SoftPastelCell,
              textColor = CharcoalInk
            )
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(SurfaceContainerLow)
              .border(1.dp, CharcoalInk.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
              .padding(20.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(36.dp)
              )
              Text(
                text = "Belum Ada Analisis Kategori",
                style = MaterialTheme.typography.titleMedium,
                color = CharcoalInk,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Kategori dominan mengukur kategori apa (Mind, Fitness, Health, dll.) yang paling sering kamu selesaikan. Tambahkan habit untuk melihat distribusi dan kategori dominanmu.",
                style = MaterialTheme.typography.bodySmall,
                color = InkSubtle,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        } else {
          // Dynamic category calculation from real habits
          val categoriesPresent = habits.map { it.category }.distinct()
          val categoryStats = categoriesPresent.map { cat ->
            val catHabits = habits.filter { it.category == cat }
            val totalDays = catHabits.size * 7
            val checkedDays = catHabits.sumOf { it.weekMomentum.count { c -> c } }
            val pct = if (totalDays > 0) ((checkedDays.toFloat() / totalDays) * 100).toInt() else 0
            Triple(cat, catHabits.size, pct)
          }.sortedByDescending { it.third }

          val dominantCategory = categoryStats.firstOrNull { it.third > 0 }?.first

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Kategori Dominan",
                style = MaterialTheme.typography.headlineSmall,
                color = CharcoalInk,
                fontWeight = FontWeight.Bold
              )
              val dominantText = if (dominantCategory != null) {
                "Fokus Utama: ${dominantCategory.displayName}"
              } else {
                "Selesaikan check-in untuk menentukan kategori utama"
              }
              Text(
                text = dominantText,
                style = MaterialTheme.typography.bodySmall,
                color = InkSubtle
              )
            }
            Text(
              text = "$totalChecked Selesai",
              style = MaterialTheme.typography.labelMedium,
              color = PrimaryBlue,
              fontWeight = FontWeight.ExtraBold
            )
          }

          categoryStats.forEach { (cat, habitCount, pct) ->
            val icon = when (cat) {
              HabitCategory.MIND -> Icons.Default.Psychology
              HabitCategory.FITNESS -> Icons.Default.FitnessCenter
              HabitCategory.HEALTH -> Icons.Default.WaterDrop
              HabitCategory.GLOW_UP -> Icons.Default.Star
              HabitCategory.DETOX -> Icons.Default.SelfImprovement
              HabitCategory.WELLNESS -> Icons.Default.Spa
            }
            CategoryMasteryBar(
              title = "${cat.displayName} ($habitCount habit)",
              percentage = pct,
              icon = icon
            )
          }
        }
      }
    }

    // 4. Badge Showcase ("Trophy Vault")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Piala & Pencapaian",
          style = MaterialTheme.typography.headlineSmall,
          color = CharcoalInk,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Lihat Semua",
          style = MaterialTheme.typography.labelMedium,
          color = PrimaryBlue,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.clickable { showAllTrophiesDialog = true }
        )
      }

      val displayTrophies = trophies.take(4)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (displayTrophies.isNotEmpty()) {
          TrophyCard(trophy = displayTrophies[0], modifier = Modifier.weight(1f), onClick = { selectedTrophy = displayTrophies[0] })
        }
        if (displayTrophies.size > 1) {
          TrophyCard(trophy = displayTrophies[1], modifier = Modifier.weight(1f), onClick = { selectedTrophy = displayTrophies[1] })
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (displayTrophies.size > 2) {
          TrophyCard(trophy = displayTrophies[2], modifier = Modifier.weight(1f), onClick = { selectedTrophy = displayTrophies[2] })
        }
        if (displayTrophies.size > 3) {
          TrophyCard(trophy = displayTrophies[3], modifier = Modifier.weight(1f), onClick = { selectedTrophy = displayTrophies[3] })
        }
      }
    }

    // 5. Tips Konsistensi (Replaced AI Coach with genuine atomic habit tip)
    VibeStickerBox(
      modifier = Modifier.fillMaxWidth(),
      backgroundColor = SoftPastelCell,
      shape = RoundedCornerShape(16.dp),
      offsetSize = 3.dp,
      borderWidth = 2.dp
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(2.dp, CharcoalInk, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(24.dp)
          )
        }

        Column(modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(9999.dp))
              .background(PrimaryBlue)
              .border(1.dp, CharcoalInk, RoundedCornerShape(9999.dp))
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text(
              text = "TIPS KONSISTENSI",
              style = MaterialTheme.typography.labelSmall,
              color = Color.White,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "\"Kunci sukses kebiasaan adalah memulai dari langkah kecil yang konsisten setiap hari, bukan lompatan besar yang cepat lelah.\"",
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 14.sp),
            color = CharcoalInk,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // 6. Primary Action ("Bagikan Rekap")
    val shareBgColor by animateColorAsState(
      targetValue = if (isSharedRecap) DeepPrimary else PrimaryBlue,
      label = "shareColor"
    )

    VibeButton(
      text = if (isSharedRecap) "Disalin ke Clipboard!" else "Bagikan Rekap Habit",
      onClick = {
        isSharedRecap = true
        Toast.makeText(context, "Rekap berhasil disalin!", Toast.LENGTH_SHORT).show()
        coroutineScope.launch {
          delay(2000)
          isSharedRecap = false
        }
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp),
      backgroundColor = shareBgColor,
      contentColor = Color.White,
      borderWidth = 2.dp,
      offsetSize = 3.dp,
      icon = {
        Icon(
          imageVector = if (isSharedRecap) Icons.Default.DoneAll else Icons.Default.Share,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
      },
      testTag = "share_vibe_btn"
    )

    Spacer(modifier = Modifier.height(10.dp))
  }

  // Trophy Detail Dialog
  selectedTrophy?.let { trophy ->
    AlertDialog(
      onDismissRequest = { selectedTrophy = null },
      title = {
        Text(trophy.title, fontWeight = FontWeight.Bold, color = CharcoalInk)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(trophy.description, style = MaterialTheme.typography.bodyMedium, color = InkSubtle)
          if (trophy.isUnlocked) {
            VibePillBadge(
              text = "TERBUKA",
              backgroundColor = PrimaryBlue,
              textColor = Color.White
            )
          } else {
            trophy.progressText?.let {
              Text("Progres: $it", style = MaterialTheme.typography.labelMedium, color = CharcoalInk)
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { selectedTrophy = null },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
          Text("Mantap!")
        }
      }
    )
  }

  // View All Trophies Dialog
  if (showAllTrophiesDialog) {
    AlertDialog(
      onDismissRequest = { showAllTrophiesDialog = false },
      title = {
        Text("Daftar Pencapaian", fontWeight = FontWeight.Bold, color = CharcoalInk)
      },
      text = {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          items(trophies.size) { idx ->
            val tr = trophies[idx]
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceContainerLow)
                .border(1.dp, CharcoalInk.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = if (tr.isUnlocked) Icons.Default.WorkspacePremium else Icons.Default.Lock,
                contentDescription = null,
                tint = if (tr.isUnlocked) PrimaryBlue else InkSubtle,
                modifier = Modifier.size(24.dp)
              )
              Column(modifier = Modifier.weight(1f)) {
                Text(tr.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                Text(tr.description, style = MaterialTheme.typography.bodySmall, color = InkSubtle)
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { showAllTrophiesDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
          Text("Selesai")
        }
      }
    )
  }
}

@Composable
private fun StatCard(
  value: String,
  label: String,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(Color(0xFFF0F4FB))
      .border(2.dp, CharcoalInk, RoundedCornerShape(8.dp))
      .padding(vertical = 10.dp, horizontal = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = value,
        style = MaterialTheme.typography.headlineMedium,
        color = PrimaryBlue,
        fontWeight = FontWeight.ExtraBold
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
        color = InkSubtle,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
private fun CategoryMasteryBar(
  title: String,
  percentage: Int,
  icon: androidx.compose.ui.graphics.vector.ImageVector
) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium,
          color = CharcoalInk,
          fontWeight = FontWeight.Bold
        )
      }
      Text(
        text = "$percentage%",
        style = MaterialTheme.typography.labelMedium,
        color = PrimaryBlue,
        fontWeight = FontWeight.ExtraBold
      )
    }

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
          .fillMaxWidth(percentage / 100f)
          .height(10.dp)
          .clip(RoundedCornerShape(9999.dp))
          .background(PrimaryBlue)
      )
    }
  }
}

@Composable
private fun TrophyCard(
  trophy: Trophy,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  VibeStickerBox(
    modifier = modifier.clickable(onClick = onClick),
    backgroundColor = if (trophy.isUnlocked) SurfaceWhite else Color(0xFFF0F4FB),
    shape = RoundedCornerShape(12.dp),
    offsetSize = 2.dp,
    borderWidth = 2.dp
  ) {
    Box(modifier = Modifier.padding(12.dp)) {
      Column {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(if (trophy.isUnlocked) SoftPastelCell else Color(0xFFE5EBF5))
            .border(2.dp, CharcoalInk, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          when (trophy.iconType) {
            "wb_twilight" -> Icon(Icons.Default.WbTwilight, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(26.dp))
            "water_drop" -> Icon(Icons.Default.WaterDrop, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(26.dp))
            "lock" -> Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(26.dp))
            "self_improvement" -> Icon(Icons.Default.SelfImprovement, contentDescription = null, tint = DeepPrimary, modifier = Modifier.size(26.dp))
            else -> Icon(Icons.Default.Star, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(26.dp))
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = trophy.title,
          style = MaterialTheme.typography.headlineSmall.copy(fontSize = 14.sp),
          color = CharcoalInk,
          fontWeight = FontWeight.ExtraBold,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
          text = trophy.description,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
          color = InkSubtle,
          lineHeight = 15.sp
        )

        if (trophy.progressPercent != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(9999.dp))
              .background(Color(0xFFDCE3F2))
              .border(1.dp, CharcoalInk, RoundedCornerShape(9999.dp))
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(trophy.progressPercent / 100f)
                .height(8.dp)
                .clip(RoundedCornerShape(9999.dp))
                .background(PrimaryBlue)
            )
          }
        }
      }

      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .rotate(if (trophy.isUnlocked) 4f else 0f)
          .clip(RoundedCornerShape(4.dp))
          .background(if (trophy.isUnlocked) PrimaryBlue else Color.White)
          .border(1.dp, CharcoalInk, RoundedCornerShape(4.dp))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = if (trophy.isUnlocked) "UNLOCKED" else "${trophy.progressPercent}%",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
          color = if (trophy.isUnlocked) Color.White else InkSubtle,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
