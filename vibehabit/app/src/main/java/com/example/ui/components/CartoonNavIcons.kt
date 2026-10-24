package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CharcoalInk
import com.example.ui.theme.InkSubtle
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryPeriwinkle
import kotlin.math.cos
import kotlin.math.sin

/**
 * Playful cartoon star badge with cute eyes and smile for "Today"
 */
@Composable
fun CartoonTodayIcon(
  isSelected: Boolean,
  modifier: Modifier = Modifier
) {
  val outlineColor = if (isSelected) CharcoalInk else InkSubtle
  val starFill = if (isSelected) Color(0xFFFBBF24) else Color(0xFFE5EBF5)
  val eyeColor = if (isSelected) CharcoalInk else InkSubtle

  Box(modifier = modifier.size(26.dp)) {
    Canvas(modifier = Modifier.size(26.dp)) {
      val w = size.width
      val h = size.height
      val cx = w / 2f
      val cy = h / 2f
      val outerRadius = w * 0.46f
      val innerRadius = w * 0.23f

      // Generate 5-point star path
      val starPath = Path()
      for (i in 0 until 10) {
        val angle = Math.toRadians((i * 36 - 90).toDouble())
        val r = if (i % 2 == 0) outerRadius else innerRadius
        val x = cx + (r * cos(angle)).toFloat()
        val y = cy + (r * sin(angle)).toFloat()
        if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
      }
      starPath.close()

      // Fill star
      drawPath(path = starPath, color = starFill)

      // Solid cartoon outline
      drawPath(
        path = starPath,
        color = outlineColor,
        style = Stroke(
          width = 1.8.dp.toPx(),
          join = StrokeJoin.Round,
          cap = StrokeCap.Round
        )
      )

      // Cartoon smiling face (eyes + smile)
      if (isSelected) {
        // Left eye
        drawCircle(
          color = eyeColor,
          radius = 1.6.dp.toPx(),
          center = Offset(cx - 3.8.dp.toPx(), cy - 1.2.dp.toPx())
        )
        // Right eye
        drawCircle(
          color = eyeColor,
          radius = 1.6.dp.toPx(),
          center = Offset(cx + 3.8.dp.toPx(), cy - 1.2.dp.toPx())
        )
        // Blush cheeks
        drawCircle(
          color = Color(0xFFF87171),
          radius = 1.3.dp.toPx(),
          center = Offset(cx - 5.5.dp.toPx(), cy + 1.2.dp.toPx())
        )
        drawCircle(
          color = Color(0xFFF87171),
          radius = 1.3.dp.toPx(),
          center = Offset(cx + 5.5.dp.toPx(), cy + 1.2.dp.toPx())
        )
        // Smile curve
        val smilePath = Path().apply {
          moveTo(cx - 2.8.dp.toPx(), cy + 2.dp.toPx())
          quadraticTo(cx, cy + 4.2.dp.toPx(), cx + 2.8.dp.toPx(), cy + 2.dp.toPx())
        }
        drawPath(
          path = smilePath,
          color = eyeColor,
          style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
        )
      } else {
        // Subtle resting eyes for inactive state
        drawCircle(
          color = eyeColor,
          radius = 1.3.dp.toPx(),
          center = Offset(cx - 3.2.dp.toPx(), cy - 0.5.dp.toPx())
        )
        drawCircle(
          color = eyeColor,
          radius = 1.3.dp.toPx(),
          center = Offset(cx + 3.2.dp.toPx(), cy - 0.5.dp.toPx())
        )
      }
    }
  }
}

/**
 * Playful cartoon 3-bar graph with a flying star/sparkle for "Analytics"
 */
@Composable
fun CartoonAnalyticsIcon(
  isSelected: Boolean,
  modifier: Modifier = Modifier
) {
  val outlineColor = if (isSelected) CharcoalInk else InkSubtle
  val bar1Color = if (isSelected) SecondaryPeriwinkle else Color(0xFFE2E8F0)
  val bar2Color = if (isSelected) PrimaryBlue else Color(0xFFCBD5E1)
  val bar3Color = if (isSelected) Color(0xFF1C4F9B) else Color(0xFF94A3B8)
  val sparkleColor = if (isSelected) Color(0xFFFBBF24) else Color.Transparent

  Box(modifier = modifier.size(26.dp)) {
    Canvas(modifier = Modifier.size(26.dp)) {
      val w = size.width
      val h = size.height

      val strokeWidth = 1.6.dp.toPx()
      val corner = CornerRadius(2.dp.toPx(), 2.dp.toPx())

      // Bar 1 (Short)
      val b1W = 5.dp.toPx()
      val b1H = 8.dp.toPx()
      val b1X = 2.5.dp.toPx()
      val b1Y = h - b1H - 2.5.dp.toPx()
      drawRoundRect(
        color = bar1Color,
        topLeft = Offset(b1X, b1Y),
        size = Size(b1W, b1H),
        cornerRadius = corner
      )
      drawRoundRect(
        color = outlineColor,
        topLeft = Offset(b1X, b1Y),
        size = Size(b1W, b1H),
        cornerRadius = corner,
        style = Stroke(strokeWidth)
      )

      // Bar 2 (Medium)
      val b2W = 5.dp.toPx()
      val b2H = 14.dp.toPx()
      val b2X = b1X + b1W + 2.5.dp.toPx()
      val b2Y = h - b2H - 2.5.dp.toPx()
      drawRoundRect(
        color = bar2Color,
        topLeft = Offset(b2X, b2Y),
        size = Size(b2W, b2H),
        cornerRadius = corner
      )
      drawRoundRect(
        color = outlineColor,
        topLeft = Offset(b2X, b2Y),
        size = Size(b2W, b2H),
        cornerRadius = corner,
        style = Stroke(strokeWidth)
      )

      // Bar 3 (Tall)
      val b3W = 5.dp.toPx()
      val b3H = 19.dp.toPx()
      val b3X = b2X + b2W + 2.5.dp.toPx()
      val b3Y = h - b3H - 2.5.dp.toPx()
      drawRoundRect(
        color = bar3Color,
        topLeft = Offset(b3X, b3Y),
        size = Size(b3W, b3H),
        cornerRadius = corner
      )
      drawRoundRect(
        color = outlineColor,
        topLeft = Offset(b3X, b3Y),
        size = Size(b3W, b3H),
        cornerRadius = corner,
        style = Stroke(strokeWidth)
      )

    }
  }
}

/**
 * Playful cartoon checklist / clipboard badge for "Task"
 */
@Composable
fun CartoonTaskIcon(
  isSelected: Boolean,
  modifier: Modifier = Modifier
) {
  val outlineColor = if (isSelected) CharcoalInk else InkSubtle
  val boardColor = if (isSelected) PrimaryBlue else Color(0xFFCBD5E1)
  val paperColor = if (isSelected) Color.White else Color(0xFFF1F5F9)
  val checkColor = if (isSelected) Color(0xFF10B981) else InkSubtle
  val clipColor = if (isSelected) Color(0xFFF59E0B) else Color(0xFF94A3B8)

  Box(modifier = modifier.size(26.dp)) {
    Canvas(modifier = Modifier.size(26.dp)) {
      val w = size.width
      val h = size.height
      val strokeWidth = 1.6.dp.toPx()

      // Clipboard back
      val boardCorner = CornerRadius(4.dp.toPx(), 4.dp.toPx())
      drawRoundRect(
        color = boardColor,
        topLeft = Offset(2.dp.toPx(), 4.dp.toPx()),
        size = Size(w - 4.dp.toPx(), h - 5.dp.toPx()),
        cornerRadius = boardCorner
      )
      drawRoundRect(
        color = outlineColor,
        topLeft = Offset(2.dp.toPx(), 4.dp.toPx()),
        size = Size(w - 4.dp.toPx(), h - 5.dp.toPx()),
        cornerRadius = boardCorner,
        style = Stroke(strokeWidth)
      )

      // Paper inner sheet
      val paperCorner = CornerRadius(2.dp.toPx(), 2.dp.toPx())
      drawRoundRect(
        color = paperColor,
        topLeft = Offset(4.5.dp.toPx(), 7.dp.toPx()),
        size = Size(w - 9.dp.toPx(), h - 10.dp.toPx()),
        cornerRadius = paperCorner
      )

      // Top Clip
      val clipCorner = CornerRadius(2.dp.toPx(), 2.dp.toPx())
      drawRoundRect(
        color = clipColor,
        topLeft = Offset(w / 2f - 4.dp.toPx(), 1.5.dp.toPx()),
        size = Size(8.dp.toPx(), 4.5.dp.toPx()),
        cornerRadius = clipCorner
      )
      drawRoundRect(
        color = outlineColor,
        topLeft = Offset(w / 2f - 4.dp.toPx(), 1.5.dp.toPx()),
        size = Size(8.dp.toPx(), 4.5.dp.toPx()),
        cornerRadius = clipCorner,
        style = Stroke(strokeWidth)
      )

      // Checkmark on the paper
      val checkPath = Path().apply {
        moveTo(6.5.dp.toPx(), 14.5.dp.toPx())
        lineTo(9.5.dp.toPx(), 17.5.dp.toPx())
        lineTo(15.dp.toPx(), 11.5.dp.toPx())
      }
      drawPath(
        path = checkPath,
        color = checkColor,
        style = Stroke(
          width = 2.dp.toPx(),
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )

      // Second task mini line
      drawLine(
        color = outlineColor.copy(alpha = 0.5f),
        start = Offset(7.dp.toPx(), 20.dp.toPx()),
        end = Offset(17.dp.toPx(), 20.dp.toPx()),
        strokeWidth = 1.4.dp.toPx(),
        cap = StrokeCap.Round
      )
    }
  }
}

/**
 * Playful cartoon smiling face badge with glasses for "You"
 */
@Composable
fun CartoonYouIcon(
  isSelected: Boolean,
  modifier: Modifier = Modifier
) {
  val outlineColor = if (isSelected) CharcoalInk else InkSubtle
  val faceColor = if (isSelected) Color(0xFFFEF3C7) else Color(0xFFE5EBF5)
  val featureColor = if (isSelected) CharcoalInk else InkSubtle

  Box(modifier = modifier.size(26.dp)) {
    Canvas(modifier = Modifier.size(26.dp)) {
      val w = size.width
      val h = size.height
      val cx = w / 2f
      val cy = h / 2f
      val radius = w * 0.44f

      // Head circle
      drawCircle(color = faceColor, radius = radius, center = Offset(cx, cy))
      drawCircle(
        color = outlineColor,
        radius = radius,
        center = Offset(cx, cy),
        style = Stroke(1.8.dp.toPx())
      )

      // Glasses left circle
      val gRadius = 3.6.dp.toPx()
      val gLeftX = cx - 4.2.dp.toPx()
      val gRightX = cx + 4.2.dp.toPx()
      val gY = cy - 1.5.dp.toPx()

      drawCircle(color = Color.White, radius = gRadius, center = Offset(gLeftX, gY))
      drawCircle(color = featureColor, radius = gRadius, center = Offset(gLeftX, gY), style = Stroke(1.4.dp.toPx()))

      // Glasses right circle
      drawCircle(color = Color.White, radius = gRadius, center = Offset(gRightX, gY))
      drawCircle(color = featureColor, radius = gRadius, center = Offset(gRightX, gY), style = Stroke(1.4.dp.toPx()))

      // Bridge between glasses
      drawLine(
        color = featureColor,
        start = Offset(gLeftX + gRadius - 0.5f, gY),
        end = Offset(gRightX - gRadius + 0.5f, gY),
        strokeWidth = 1.4.dp.toPx()
      )

      // Pupils inside glasses
      drawCircle(color = featureColor, radius = 1.4.dp.toPx(), center = Offset(gLeftX, gY))
      drawCircle(color = featureColor, radius = 1.4.dp.toPx(), center = Offset(gRightX, gY))

      // Smile mouth
      val smilePath = Path().apply {
        moveTo(cx - 3.2.dp.toPx(), cy + 4.5.dp.toPx())
        quadraticTo(cx, cy + 7.dp.toPx(), cx + 3.2.dp.toPx(), cy + 4.5.dp.toPx())
      }
      drawPath(
        path = smilePath,
        color = featureColor,
        style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
      )
    }
  }
}
