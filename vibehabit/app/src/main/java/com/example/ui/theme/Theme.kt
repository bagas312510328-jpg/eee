package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = PrimaryBlue,
  onPrimary = Color.White,
  primaryContainer = PrimaryBlue,
  onPrimaryContainer = Color.White,
  secondary = SecondaryPeriwinkle,
  onSecondary = CharcoalInk,
  secondaryContainer = SecondaryContainer,
  onSecondaryContainer = OnSecondaryContainer,
  tertiary = TertiarySage,
  onTertiary = Color.White,
  tertiaryContainer = TertiaryContainer,
  onTertiaryContainer = OnTertiaryContainer,
  background = SurfaceCanvas,
  onBackground = CharcoalInk,
  surface = SurfaceCanvas,
  onSurface = CharcoalInk,
  surfaceVariant = SurfaceContainerHighest,
  onSurfaceVariant = InkSubtle,
  outline = InkMuted,
  outlineVariant = SoftPastelCell,
  error = ErrorRed,
  errorContainer = ErrorContainer,
  onErrorContainer = OnErrorContainer
)

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryBlue,
  onPrimary = Color.White,
  primaryContainer = DeepPrimary,
  onPrimaryContainer = Color.White,
  secondary = SecondaryPeriwinkle,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFF28364F),
  onSecondaryContainer = Color(0xFFD7E2F5),
  tertiary = TertiarySage,
  onTertiary = Color.White,
  background = Color(0xFF141922),
  onBackground = Color(0xFFEBF1FF),
  surface = Color(0xFF181F2A),
  onSurface = Color(0xFFEBF1FF),
  surfaceVariant = Color(0xFF202A39),
  onSurfaceVariant = Color(0xFFA6AFBD),
  outline = Color(0xFF5A6270),
  outlineVariant = Color(0xFF2E3849)
)

@Composable
fun VibeHabitTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = content
  )
}
