package com.example.util

import android.content.Context
import android.os.Build
import android.os.Debug
import java.io.File

/**
 * App Security & Integrity Guard.
 * Protects codebase and runtime from unauthorized cloning,
 * hooking, debugging, tampering, or extraction.
 */
object AppSecurityIntegrity {

  private val KNOWN_ROOT_PATHS = arrayOf(
    "/system/app/Superuser.apk",
    "/sbin/su",
    "/system/bin/su",
    "/system/xbin/su",
    "/data/local/xbin/su",
    "/data/local/bin/su",
    "/system/sd/xbin/su",
    "/system/bin/failsafe/su",
    "/data/local/su",
    "/su/bin/su"
  )

  /**
   * Checks if an external debugger or tracer is actively attached to inspect memory or variables.
   */
  fun isDebuggerAttached(): Boolean {
    return Debug.isDebuggerConnected() || Debug.waitingForDebugger()
  }

  /**
   * Checks for known hooking tools like Frida or Xposed injected into runtime memory.
   */
  fun isHookingDetected(): Boolean {
    try {
      Class.forName("de.robv.android.xposed.XposedBridge")
      return true
    } catch (_: ClassNotFoundException) {}

    try {
      val mapsFile = File("/proc/self/maps")
      if (mapsFile.exists()) {
        val content = mapsFile.readText()
        if (content.contains("frida") || content.contains("xposed") || content.contains("substrate")) {
          return true
        }
      }
    } catch (_: Exception) {}

    return false
  }

  /**
   * Verifies package name integrity against malicious repackaged clones.
   */
  fun isPackageIntegrityValid(context: Context): Boolean {
    val currentPkg = context.packageName
    return currentPkg.startsWith("com.aistudio.") || currentPkg == "com.example"
  }

  /**
   * Scans for unauthorized root binaries that allow memory dumping.
   */
  fun isDeviceRooted(): Boolean {
    val buildTags = Build.TAGS
    if (buildTags != null && buildTags.contains("test-keys")) {
      return true
    }
    for (path in KNOWN_ROOT_PATHS) {
      if (File(path).exists()) return true
    }
    return false
  }
}
