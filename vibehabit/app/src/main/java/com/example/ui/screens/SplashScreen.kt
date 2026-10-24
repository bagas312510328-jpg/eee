package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.VibeHabitViewModel
import com.example.ui.components.CartoonTodayIcon
import com.example.ui.theme.CharcoalInk
import com.example.ui.theme.DeepPrimary
import com.example.ui.theme.InkSubtle
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SurfaceCanvas
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
  viewModel: VibeHabitViewModel,
  modifier: Modifier = Modifier
) {
  val scaleAnim = remember { Animatable(0.2f) }
  val rotationAnim = remember { Animatable(-20f) }
  val alphaAnim = remember { Animatable(0f) }

  LaunchedEffect(Unit) {
    // Playful bouncy pop-in animation
    alphaAnim.animateTo(1f, tween(300))
    rotationAnim.animateTo(
      targetValue = 0f,
      animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
      )
    )
    scaleAnim.animateTo(
      targetValue = 1f,
      animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
      )
    )

    // Wait and transition to Onboarding / Main screen
    delay(1400)
    viewModel.onSplashCompleted()
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(SurfaceCanvas)
      .clickable { viewModel.onSplashCompleted() }
      .testTag("splash_screen"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Bouncy Cartoon Star Mascot
      Box(
        modifier = Modifier
          .scale(scaleAnim.value)
          .rotate(rotationAnim.value)
          .size(110.dp)
          .clip(CircleShape)
          .background(PrimaryBlue)
          .border(3.dp, CharcoalInk, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        CartoonTodayIcon(
          isSelected = true,
          modifier = Modifier.size(68.dp)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "VibeHabit",
        style = MaterialTheme.typography.displayLarge,
        color = CharcoalInk,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.03).sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(9999.dp))
          .background(SecondaryContainer)
          .border(1.dp, CharcoalInk.copy(alpha = 0.2f), RoundedCornerShape(9999.dp))
          .padding(horizontal = 14.dp, vertical = 4.dp)
      ) {
        Text(
          text = "Building atomic habits one day at a time",
          style = MaterialTheme.typography.bodySmall,
          color = DeepPrimary,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Version label at bottom
    Text(
      text = "Version 2.4.0",
      style = MaterialTheme.typography.labelSmall,
      color = InkSubtle,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 32.dp)
    )
  }
}
