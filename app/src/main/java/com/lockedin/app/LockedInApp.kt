package com.lockedin.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * LockedIn application class.
 *
 * Marked with @HiltAndroidApp to enable Dagger-Hilt dependency injection.
 * This is the root of the DI graph. It must remain lightweight and must not
 * hold sensitive data.
 */
@HiltAndroidApp
class LockedInApp : Application()

