# VibeHabit

A tactile habit and task tracker for Android built with Jetpack Compose and Material 3.

## Overview

VibeHabit helps you build consistent daily routines and track actionable tasks with:
- Daily habit tracking with active streak flame visualization
- 24-hour inactivity flame extinction and reignition feedback
- Streak tasks list with instant categorization and completion toggles
- Built-in audio and haptic vibration feedback
- AES-256 encrypted local data storage with anti-tamper security integrity checks

## Prerequisites

- Android Studio Meerkat or newer
- JDK 17 or higher
- Android SDK 36 (Minimum SDK 24)

## Getting Started

1. Open Android Studio.
2. Select **Open** and choose this project directory.
3. Allow Gradle to sync and build project dependencies.
4. Run the project on an Android device or emulator.

## Build APK Otomatis via GitHub Actions

Aplikasi ini sudah dilengkapi dengan alur kerja otomatis (CI/CD) GitHub Actions:
1. Buka tab **Actions** pada repositori GitHub Anda.
2. Pilih workflow **Build Android APK**.
3. Klik tombol **Run workflow** (atau lakukan `git push` ke branch `main`/`master`).
4. Setelah build selesai dengan centang hijau (Success), buka halaman run tersebut.
5. Di bagian bawah (Artifacts), unduh file **VibeHabit-Debug-APK** dalam format `.zip`, lalu ekstrak untuk mendapatkan `app-debug.apk` yang siap dipasang langsung di HP Android.

## Tech Stack

- Kotlin
- Jetpack Compose (Material 3)
- Android Architecture Components (ViewModel, StateFlow)
- Moshi & AndroidX Security Crypto
