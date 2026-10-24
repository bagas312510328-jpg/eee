package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CharcoalInk
import com.example.ui.theme.InkSubtle
import com.example.ui.theme.SurfaceWhite
import kotlin.math.cos
import kotlin.math.sin

// Flame color stages by streak days
object FlameTheme {
  fun getColors(streakDays: Int, isActive: Boolean): Triple<Color, Color, Color> {
    if (!isActive || streakDays <= 0) {
      // Extinguished / Dormant ash gray
      return Triple(Color(0xFF8E8E93), Color(0xFF636366), Color(0xFF48484A))
    }
    return when {
      streakDays in 1..3 -> Triple(Color(0xFFFFB300), Color(0xFFFF6D00), Color(0xFFFF3D00)) // Warm Amber / Orange
      streakDays in 4..7 -> Triple(Color(0xFFFF5252), Color(0xFFFF1744), Color(0xFFD50000)) // Crimson Red
      streakDays in 8..14 -> Triple(Color(0xFFE040FB), Color(0xFFAA00FF), Color(0xFF6A1B9A)) // Mystical Purple
      streakDays in 15..29 -> Triple(Color(0xFF18FFFF), Color(0xFF00B0FF), Color(0xFF2979FF)) // Cosmic Electric Blue
      else -> Triple(Color(0xFFFFEA00), Color(0xFFFF9100), Color(0xFFFF3D00)) // Solar Gold (30+)
    }
  }

  fun getFlameName(streakDays: Int, isActive: Boolean): String {
    if (!isActive || streakDays <= 0) return "Padam (Mati Api)"
    return when {
      streakDays in 1..3 -> "Orange Ember Flame"
      streakDays in 4..7 -> "Crimson Blaze Flame"
      streakDays in 8..14 -> "Mystic Violet Flame"
      streakDays in 15..29 -> "Cosmic Cyan Flame"
      else -> "Prismatic Gold Flame"
    }
  }
}

/**
 * Authentic Cartoon Campfire Flame Composable with realistic multi-tongue fire silhouette,
 * floating dancing ember spark, dynamic streak colors, and bold centered day counter.
 */
@Composable
fun StreakFlameView(
  streakDays: Int,
  isActive: Boolean,
  isIgniting: Boolean = false,
  flameSize: Dp = 100.dp,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val (topColor, midColor, botColor) = FlameTheme.getColors(streakDays, isActive)

  val animatedTopColor by animateColorAsState(targetValue = topColor, animationSpec = tween(500), label = "topColor")
  val animatedMidColor by animateColorAsState(targetValue = midColor, animationSpec = tween(500), label = "midColor")
  val animatedBotColor by animateColorAsState(targetValue = botColor, animationSpec = tween(500), label = "botColor")

  // Ignition pop scale
  val ignitionScale = remember { Animatable(1f) }
  val sparkAlpha = remember { Animatable(0f) }

  LaunchedEffect(isIgniting) {
    if (isIgniting) {
      sparkAlpha.snapTo(1f)
      ignitionScale.animateTo(
        targetValue = 1.34f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
      )
      ignitionScale.animateTo(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
      )
      sparkAlpha.animateTo(0f, animationSpec = tween(600))
    }
  }

  // Organic flame flicker and sway
  val infiniteTransition = rememberInfiniteTransition(label = "flameAlive")
  val breatheScale by infiniteTransition.animateFloat(
    initialValue = 0.97f,
    targetValue = 1.04f,
    animationSpec = infiniteRepeatable(
      animation = tween(1100, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "breatheScale"
  )
  val tipSway by infiniteTransition.animateFloat(
    initialValue = -3.5f,
    targetValue = 3.5f,
    animationSpec = infiniteRepeatable(
      animation = tween(850, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "tipSway"
  )
  val emberFloat by infiniteTransition.animateFloat(
    initialValue = -2.5f,
    targetValue = 3f,
    animationSpec = infiniteRepeatable(
      animation = tween(700, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "emberFloat"
  )

  val totalScale = ignitionScale.value * (if (isActive) breatheScale else 1f)

  Box(
    modifier = modifier
      .size(flameSize)
      .scale(totalScale)
      .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
    contentAlignment = Alignment.Center
  ) {
    // 1. Canvas drawing of the multi-tongue cartoon flame
    Canvas(modifier = Modifier.size(flameSize)) {
      val w = size.width
      val h = size.height
      val cx = w / 2f

      // Ignition outer shockwave / aura glow
      if (sparkAlpha.value > 0.01f) {
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(animatedTopColor.copy(alpha = 0.55f * sparkAlpha.value), Color.Transparent),
            center = Offset(cx, h * 0.60f),
            radius = w * 0.75f
          ),
          radius = w * 0.75f,
          center = Offset(cx, h * 0.60f)
        )

        // Draw shooting sparks
        val sparkCount = 12
        for (i in 0 until sparkCount) {
          val angle = (i * 360f / sparkCount) * (Math.PI / 180f)
          val dist = (w * 0.48f) + (sparkAlpha.value * w * 0.28f)
          val sx = cx + (dist * cos(angle)).toFloat()
          val sy = (h * 0.60f) + (dist * sin(angle)).toFloat()
          drawCircle(
            color = animatedTopColor.copy(alpha = sparkAlpha.value),
            radius = 3.5.dp.toPx(),
            center = Offset(sx, sy)
          )
        }
      }

      // ----------------------------------------------------
      // A. Outer Realistic Flame Silhouette (3 Tongues of Fire)
      // ----------------------------------------------------
      val flamePath = Path()
      val bottomX = cx
      val bottomY = h * 0.95f
      val mainTipX = cx + (if (isActive) tipSway * 1.5f else 0f)

      flamePath.moveTo(bottomX, bottomY)

      // Left rounded bowl base
      flamePath.cubicTo(
        w * 0.26f, h * 0.96f,
        w * 0.08f, h * 0.85f,
        w * 0.08f, h * 0.66f
      )

      // Left flame tongue rising up to sharp tip
      flamePath.cubicTo(
        w * 0.08f, h * 0.52f,
        w * 0.14f, h * 0.38f,
        w * 0.22f, h * 0.30f
      )

      // Left cleft dipping down towards center
      flamePath.cubicTo(
        w * 0.25f, h * 0.42f,
        w * 0.32f, h * 0.48f,
        w * 0.38f, h * 0.36f
      )

      // Center main flame tongue rising to highest tip
      flamePath.cubicTo(
        w * 0.40f, h * 0.22f,
        w * 0.44f, h * 0.10f,
        mainTipX, h * 0.06f
      )

      // Center tip curving down to right cleft
      flamePath.cubicTo(
        mainTipX + w * 0.07f, h * 0.18f,
        w * 0.60f, h * 0.30f,
        w * 0.64f, h * 0.40f
      )

      // Right flame tongue branching up to sharp tip
      flamePath.cubicTo(
        w * 0.70f, h * 0.34f,
        w * 0.82f, h * 0.32f,
        w * 0.88f, h * 0.38f
      )

      // Right tongue curving down to base
      flamePath.cubicTo(
        w * 0.94f, h * 0.54f,
        w * 0.92f, h * 0.74f,
        w * 0.72f, h * 0.92f
      )

      // Right base back to bottom center
      flamePath.cubicTo(
        w * 0.62f, h * 0.96f,
        cx + w * 0.08f, bottomY,
        bottomX, bottomY
      )
      flamePath.close()

      // Gradient Fill for outer flame body
      val outerBrush = Brush.verticalGradient(
        colors = listOf(animatedTopColor, animatedMidColor, animatedBotColor),
        startY = h * 0.05f,
        endY = h * 0.95f
      )
      drawPath(path = flamePath, brush = outerBrush)

      // Solid cartoon outline
      drawPath(
        path = flamePath,
        color = CharcoalInk,
        style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
      )

      // ----------------------------------------------------
      // B. Middle Secondary Fiery Tongue Layer
      // ----------------------------------------------------
      val midPath = Path()
      val midBottomY = h * 0.91f
      val midTipX = cx + (if (isActive) tipSway * 1.0f else 0f)

      midPath.moveTo(cx, midBottomY)
      midPath.cubicTo(w * 0.32f, midBottomY, w * 0.20f, h * 0.80f, w * 0.20f, h * 0.65f)
      midPath.cubicTo(w * 0.20f, h * 0.54f, w * 0.26f, h * 0.44f, w * 0.32f, h * 0.38f)
      midPath.cubicTo(w * 0.34f, h * 0.46f, w * 0.38f, h * 0.50f, w * 0.42f, h * 0.42f)
      midPath.cubicTo(w * 0.44f, h * 0.30f, w * 0.46f, h * 0.20f, midTipX, h * 0.18f)
      midPath.cubicTo(midTipX + w * 0.05f, h * 0.26f, w * 0.56f, h * 0.38f, w * 0.60f, h * 0.44f)
      midPath.cubicTo(w * 0.64f, h * 0.40f, w * 0.72f, h * 0.38f, w * 0.76f, h * 0.44f)
      midPath.cubicTo(w * 0.80f, h * 0.56f, w * 0.80f, h * 0.72f, w * 0.66f, h * 0.88f)
      midPath.cubicTo(w * 0.58f, midBottomY, cx + w * 0.06f, midBottomY, cx, midBottomY)
      midPath.close()

      val midFillColor = if (isActive && streakDays > 0) Color(0xFFFFD54F).copy(alpha = 0.85f) else Color(0xFFB0B5BC).copy(alpha = 0.6f)
      drawPath(path = midPath, color = midFillColor)
      drawPath(
        path = midPath,
        color = CharcoalInk.copy(alpha = 0.3f),
        style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
      )

      // ----------------------------------------------------
      // C. Inner Fiery White-Hot Core (Bottom Heart of Flame)
      // ----------------------------------------------------
      val corePath = Path()
      val coreTipX = cx + (if (isActive) tipSway * 0.5f else 0f)
      corePath.moveTo(cx, h * 0.88f)
      corePath.cubicTo(w * 0.36f, h * 0.88f, w * 0.30f, h * 0.78f, w * 0.30f, h * 0.68f)
      corePath.cubicTo(w * 0.30f, h * 0.56f, w * 0.40f, h * 0.46f, coreTipX, h * 0.38f)
      corePath.cubicTo(coreTipX + w * 0.10f, h * 0.46f, w * 0.70f, h * 0.56f, w * 0.70f, h * 0.68f)
      corePath.cubicTo(w * 0.70f, h * 0.78f, w * 0.64f, h * 0.88f, cx, h * 0.88f)
      corePath.close()

      val coreColor = if (isActive && streakDays > 0) Color.White.copy(alpha = 0.88f) else Color(0xFFE2E4E8).copy(alpha = 0.75f)
      drawPath(path = corePath, color = coreColor)

      // ----------------------------------------------------
      // D. Detached Floating Dancing Ember Spark (Above Flame Tip)
      // ----------------------------------------------------
      if (isActive && streakDays > 0) {
        val emberX = cx - w * 0.08f + tipSway
        val emberY = h * 0.03f + emberFloat
        val emberPath = Path().apply {
          moveTo(emberX, emberY)
          cubicTo(emberX + 4.dp.toPx(), emberY + 3.dp.toPx(), emberX + 3.dp.toPx(), emberY + 7.dp.toPx(), emberX, emberY + 7.dp.toPx())
          cubicTo(emberX - 3.dp.toPx(), emberY + 7.dp.toPx(), emberX - 4.dp.toPx(), emberY + 3.dp.toPx(), emberX, emberY)
          close()
        }
        drawPath(path = emberPath, color = animatedTopColor)
        drawPath(path = emberPath, color = CharcoalInk, style = Stroke(width = 1.dp.toPx()))
      }
    }

    // 2. Bold Streak Number Centered in the Lower Heart of the Flame
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(top = (flameSize * 0.30f))
    ) {
      Text(
        text = if (isActive && streakDays > 0) "$streakDays" else "0",
        fontSize = (flameSize.value * 0.30f).sp,
        fontWeight = FontWeight.Black,
        color = CharcoalInk
      )
      Text(
        text = if (isActive && streakDays > 0) "DAY" else "DEAD",
        fontSize = (flameSize.value * 0.10f).sp,
        fontWeight = FontWeight.Black,
        color = CharcoalInk.copy(alpha = 0.85f),
        letterSpacing = 1.sp
      )
    }
  }
}

/**
 * Full Bento Streak Header Card: displays the flame illustration,
 * the streak level name, 24-hour status countdown/indicator, and ignition prompt.
 */
@Composable
fun StreakHeroCard(
  streakDays: Int,
  isActive: Boolean,
  hoursLeft: Int = 24,
  isIgniting: Boolean = false,
  onIgniteClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val flameName = FlameTheme.getFlameName(streakDays, isActive)
  val (topColor, _, _) = FlameTheme.getColors(streakDays, isActive)

  VibeStickerBox(
    modifier = modifier.fillMaxWidth(),
    backgroundColor = SurfaceWhite,
    shape = RoundedCornerShape(16.dp),
    offsetSize = 3.dp,
    borderWidth = 2.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left side: Streak info details
      Column(modifier = Modifier.weight(1f)) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(if (isActive && streakDays > 0) topColor.copy(alpha = 0.18f) else Color(0xFFE5E7EB))
            .border(1.dp, CharcoalInk.copy(alpha = 0.25f), RoundedCornerShape(9999.dp))
            .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
          Text(
            text = if (isActive && streakDays > 0) "ACTIVE STREAK" else "STREAK EXTINCTION",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = if (isActive && streakDays > 0) CharcoalInk else Color(0xFF4B5563)
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = if (isActive && streakDays > 0) "Day $streakDays Streak" else "Streak Padam",
          style = MaterialTheme.typography.headlineMedium,
          color = CharcoalInk,
          fontWeight = FontWeight.ExtraBold
        )

        Text(
          text = flameName,
          style = MaterialTheme.typography.labelMedium,
          color = if (isActive && streakDays > 0) topColor else InkSubtle,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = if (isActive && streakDays > 0) {
            "Aktivitas dicatat hari ini. Selesaikan tugas atau habit untuk memperbesar apimu!"
          } else {
            "Sudah >24 jam tanpa aktivitas! Selesaikan habit atau task untuk menyalakan api kembali!"
          },
          style = MaterialTheme.typography.bodySmall,
          color = InkSubtle,
          lineHeight = 16.sp
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Right side: The Animated Flame View
      StreakFlameView(
        streakDays = streakDays,
        isActive = isActive,
        isIgniting = isIgniting,
        flameSize = 92.dp,
        onClick = onIgniteClick
      )
    }
  }
}
