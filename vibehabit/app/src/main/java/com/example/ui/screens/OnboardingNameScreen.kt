package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.VibeHabitViewModel
import com.example.ui.components.CartoonTodayIcon
import com.example.ui.components.VibeButton
import com.example.ui.components.VibePillBadge
import com.example.ui.components.VibeStickerBox
import com.example.ui.theme.CharcoalInk
import com.example.ui.theme.DeepPrimary
import com.example.ui.theme.InkSubtle
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SoftPastelCell
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceWhite

@Composable
fun OnboardingNameScreen(
  viewModel: VibeHabitViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var userNameInput by remember { mutableStateOf("") }
  val bounceScale = remember { Animatable(0.7f) }

  LaunchedEffect(Unit) {
    bounceScale.animateTo(
      targetValue = 1f,
      animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
      )
    )
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(SurfaceCanvas)
      .padding(24.dp)
      .testTag("onboarding_screen"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .scale(bounceScale.value),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

      // Cartoon Mascot Avatar
      Box(
        modifier = Modifier
          .size(88.dp)
          .clip(CircleShape)
          .background(PrimaryBlue)
          .border(2.5.dp, CharcoalInk, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        CartoonTodayIcon(
          isSelected = true,
          modifier = Modifier.size(52.dp)
        )
      }

      // Title & Tagline
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        VibePillBadge(
          text = "LET'S GET STARTED",
          backgroundColor = SoftPastelCell,
          textColor = DeepPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Welcome to VibeHabit!",
          style = MaterialTheme.typography.headlineLarge,
          color = CharcoalInk,
          fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "What should we call you, bestie?",
          style = MaterialTheme.typography.bodyLarge,
          color = InkSubtle
        )
      }

      // Name Input Card
      VibeStickerBox(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SurfaceWhite,
        shape = RoundedCornerShape(16.dp),
        borderWidth = 2.dp
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "Your Name",
            style = MaterialTheme.typography.labelLarge,
            color = CharcoalInk,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(8.dp))

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFFF1F5F9))
              .border(1.5.dp, CharcoalInk.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
              .padding(horizontal = 16.dp, vertical = 14.dp)
          ) {
            BasicTextField(
              value = userNameInput,
              onValueChange = { userNameInput = it },
              textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalInk
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("name_input_field"),
              decorationBox = { innerTextField ->
                if (userNameInput.isEmpty()) {
                  Text(
                    text = "Enter your name...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = InkSubtle
                  )
                }
                innerTextField()
              }
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Mascot speech bubble
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(SecondaryContainer)
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "⭐", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "\"Let's lock in and build atomic habits together!\"",
              style = MaterialTheme.typography.bodySmall,
              color = DeepPrimary,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Primary Chunky CTA Button ("Start My Journey")
      VibeButton(
        text = "Start My Journey",
        onClick = {
          if (userNameInput.trim().isEmpty()) {
            Toast.makeText(context, "Silakan masukkan namamu untuk memulai!", Toast.LENGTH_SHORT).show()
          } else {
            viewModel.completeOnboarding(userNameInput.trim())
            Toast.makeText(context, "Selamat datang, ${userNameInput.trim()}!", Toast.LENGTH_SHORT).show()
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp),
        backgroundColor = PrimaryBlue,
        contentColor = Color.White,
        borderWidth = 2.dp,
        testTag = "start_journey_btn"
      )
    }
  }
}
