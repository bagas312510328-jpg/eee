package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FeedbackHelper(private val context: Context) {

  private val vibrator: Vibrator? by lazy {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
      } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
      }
    } catch (_: Exception) {
      null
    }
  }

  private val toneGenerator: ToneGenerator? by lazy {
    try {
      ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
    } catch (_: Exception) {
      null
    }
  }

  fun playHabitDone(enabled: Boolean) {
    if (!enabled) return
    vibrate(longArrayOf(0, 45, 60, 45), intArrayOf(0, 180, 0, 220))
    playTone(ToneGenerator.TONE_PROP_BEEP, 100)
  }

  fun playTaskDone(enabled: Boolean) {
    if (!enabled) return
    vibrateOneShot(40, 190)
    playTone(ToneGenerator.TONE_PROP_ACK, 90)
  }

  fun playStreakIgnite(enabled: Boolean) {
    if (!enabled) return
    // Multi-pulse celebration vibration
    vibrate(longArrayOf(0, 60, 50, 70, 50, 90), intArrayOf(0, 200, 0, 230, 0, 255))

    // Celebratory ascending chime
    CoroutineScope(Dispatchers.Default).launch {
      try {
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
        delay(90)
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 120)
        delay(130)
        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 160)
      } catch (_: Exception) {}
    }
  }

  fun playToggle(enabled: Boolean) {
    if (!enabled) return
    vibrateOneShot(25, 140)
    playTone(ToneGenerator.TONE_PROP_BEEP, 40)
  }

  private fun vibrateOneShot(durationMs: Long, amplitude: Int) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255)))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(durationMs)
      }
    } catch (_: Exception) {}
  }

  private fun vibrate(timings: LongArray, amplitudes: IntArray) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(timings, -1)
      }
    } catch (_: Exception) {}
  }

  private fun playTone(toneType: Int, durationMs: Int) {
    try {
      toneGenerator?.startTone(toneType, durationMs)
    } catch (_: Exception) {}
  }
}
