package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.CharcoalInk
import com.example.ui.theme.DeepPrimary
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceWhite

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.ui.draw.scale

// Custom sticker box with solid outline and clean background
@Composable
fun VibeStickerBox(
  modifier: Modifier = Modifier,
  backgroundColor: Color = SurfaceWhite,
  borderColor: Color = CharcoalInk,
  borderWidth: Dp = 1.5.dp,
  offsetSize: Dp = 0.dp,
  shape: Shape = RoundedCornerShape(14.dp),
  content: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .clip(shape)
      .background(backgroundColor)
      .border(borderWidth, borderColor, shape)
  ) {
    content()
  }
}

// Chunky Button with tactile spring bounce animation
@Composable
fun VibeButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: (@Composable () -> Unit)? = null,
  backgroundColor: Color = PrimaryBlue,
  contentColor: Color = Color.White,
  borderColor: Color = CharcoalInk,
  borderWidth: Dp = 1.5.dp,
  offsetSize: Dp = 0.dp,
  shape: Shape = RoundedCornerShape(9999.dp),
  testTag: String = "vibe_button"
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.94f else 1f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMedium
    ),
    label = "buttonBounce"
  )

  Box(
    modifier = modifier
      .testTag(testTag)
      .scale(scale)
      .clip(shape)
      .background(backgroundColor)
      .border(borderWidth, borderColor, shape)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .padding(horizontal = 16.dp, vertical = 12.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text,
        color = contentColor,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

// Top App Bar matching VibeHabit Header in Screenshots
@Composable
fun VibeTopBar(
  currentScreenTitle: String = "VibeHabit",
  showBack: Boolean = false,
  onBackClick: () -> Unit = {},
  onProfileClick: () -> Unit = {}
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(64.dp)
      .background(SurfaceCanvas)
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f)
    ) {
      if (showBack) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clickable(onClick = onBackClick)
            .padding(4.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "←", fontSize = 20.sp, color = CharcoalInk, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(6.dp))
      }

      // Mascot Logo
      AsyncImage(
        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDJB_8Lof5kw1b39YE4KM8yLJgOvfELzvu8Yxumkb3mGFY63ZFgGWTbT1GzS0oDq_DAGXPtfDRGDGkPG8zEJlZlb8EzBt63GDlH00t7kpfhEXg5qabotVWimue2e6PyUeR7pjCyaN7ECHSjM1h27gaO3ptSCwGpQjaDMy6bO5yRRl0zGdsTzHWXP4b6uLCjtJfx0KXLVPcp8aNKhG64fDL0ze8iiLUydlBDNOcWuQVEA0PXu5tM5e3EAg",
        contentDescription = "VibeHabit Mascot Logo",
        modifier = Modifier.size(32.dp)
      )

      Spacer(modifier = Modifier.width(8.dp))

      Text(
        text = currentScreenTitle,
        style = MaterialTheme.typography.headlineSmall,
        color = CharcoalInk,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.width(6.dp))

      // PRO Badge
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(9999.dp))
          .background(SecondaryContainer)
          .border(1.dp, CharcoalInk.copy(alpha = 0.2f), RoundedCornerShape(9999.dp))
          .padding(horizontal = 8.dp, vertical = 2.dp)
      ) {
        Text(
          text = "PRO",
          style = MaterialTheme.typography.labelSmall,
          color = DeepPrimary,
          fontWeight = FontWeight.ExtraBold
        )
      }
    }

    // Profile Avatar Button on the Right
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(PrimaryBlue)
        .border(1.dp, CharcoalInk.copy(alpha = 0.3f), CircleShape)
        .clickable(onClick = onProfileClick),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Person,
        contentDescription = "Profile",
        tint = Color.White,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

// Pill Badge
@Composable
fun VibePillBadge(
  text: String,
  backgroundColor: Color,
  textColor: Color,
  modifier: Modifier = Modifier,
  icon: (@Composable () -> Unit)? = null
) {
  Row(
    modifier = modifier
      .clip(RoundedCornerShape(9999.dp))
      .background(backgroundColor)
      .border(1.dp, CharcoalInk.copy(alpha = 0.3f), RoundedCornerShape(9999.dp))
      .padding(horizontal = 8.dp, vertical = 3.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    if (icon != null) {
      icon()
      Spacer(modifier = Modifier.width(4.dp))
    }
    Text(
      text = text,
      color = textColor,
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.Bold
    )
  }
}
