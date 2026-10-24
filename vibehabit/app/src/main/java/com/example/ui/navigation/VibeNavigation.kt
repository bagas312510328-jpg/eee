package com.example.ui.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppDestination
import com.example.ui.components.CartoonAnalyticsIcon
import com.example.ui.components.CartoonTaskIcon
import com.example.ui.components.CartoonTodayIcon
import com.example.ui.components.CartoonYouIcon
import com.example.ui.theme.CharcoalInk
import com.example.ui.theme.DeepPrimary
import com.example.ui.theme.InkSubtle
import com.example.ui.theme.PrimaryBlue

@Composable
fun VibeBottomNavigation(
  currentScreen: AppDestination,
  onNavigate: (AppDestination) -> Unit,
  onAddClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(Color(0xFFF4F6F9).copy(alpha = 0.96f))
      .border(
        width = 1.dp,
        color = CharcoalInk.copy(alpha = 0.15f),
        shape = androidx.compose.ui.graphics.RectangleShape
      )
      .navigationBarsPadding()
      .height(70.dp)
      .testTag("bottom_nav_bar")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 6.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Today Tab (Cartoon Star Badge)
      val isToday = currentScreen == AppDestination.Today
      CartoonBottomNavItem(
        label = "Today",
        isSelected = isToday,
        icon = { CartoonTodayIcon(isSelected = isToday) },
        onClick = { onNavigate(AppDestination.Today) },
        testTag = "nav_today"
      )

      // 2. Analytics Tab (Cartoon Trend Graph)
      val isAnalytics = currentScreen == AppDestination.Analytics
      CartoonBottomNavItem(
        label = "Analytics",
        isSelected = isAnalytics,
        icon = { CartoonAnalyticsIcon(isSelected = isAnalytics) },
        onClick = { onNavigate(AppDestination.Analytics) },
        testTag = "nav_analytics"
      )

      // 3. Center Add Action Button (Sticker FAB)
      Box(
        modifier = Modifier
          .offset(y = (-14).dp)
          .size(52.dp)
          .drawBehind {
            // Hard offset sticker shadow
            drawCircle(
              color = CharcoalInk,
              radius = size.minDimension / 2f,
              center = Offset(size.width / 2f, size.height / 2f + 2.5.dp.toPx())
            )
          }
          .clip(CircleShape)
          .background(PrimaryBlue)
          .border(2.dp, CharcoalInk, CircleShape)
          .clickable(onClick = onAddClick)
          .testTag("nav_add_button"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Add Habit",
          tint = Color.White,
          modifier = Modifier.size(30.dp)
        )
      }

      // 4. Task Tab (Changed from Group to Task with Cartoon Clipboard)
      val isTask = currentScreen == AppDestination.Task
      CartoonBottomNavItem(
        label = "Task",
        isSelected = isTask,
        icon = { CartoonTaskIcon(isSelected = isTask) },
        onClick = { onNavigate(AppDestination.Task) },
        testTag = "nav_task"
      )

      // 5. You (Profile) Tab (Cartoon Avatar / Smiley Face)
      val isYou = currentScreen == AppDestination.You
      CartoonBottomNavItem(
        label = "You",
        isSelected = isYou,
        icon = { CartoonYouIcon(isSelected = isYou) },
        onClick = { onNavigate(AppDestination.You) },
        testTag = "nav_profile"
      )
    }
  }
}

@Composable
private fun CartoonBottomNavItem(
  label: String,
  isSelected: Boolean,
  icon: @Composable () -> Unit,
  onClick: () -> Unit,
  testTag: String
) {
  val scale by animateFloatAsState(
    targetValue = if (isSelected) 1.12f else 1.0f,
    animationSpec = spring(),
    label = "navScale"
  )

  Column(
    modifier = Modifier
      .clip(RoundedCornerShape(10.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp, vertical = 4.dp)
      .scale(scale)
      .testTag(testTag),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(contentAlignment = Alignment.Center) {
      icon()
    }
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
      color = if (isSelected) DeepPrimary else InkSubtle,
      fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
    )
  }
}
