package com.lockedin.app.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.lockedin.app.MainActivity
import com.lockedin.app.core.ui.theme.LockedInTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Entry activity that hosts the Android 12+ splash screen and immediately
 * forwards to [MainActivity] once initial auth state checks are complete.
 *
 * The heavy lifting (deciding between onboarding, setup, login, or home)
 * is done in the main navigation graph; this activity is purely a shell
 * to integrate the SplashScreen API.
 */
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { false }

        setContent {
            LockedInTheme {
                // Minimal composable; navigation starts in MainActivity.
            }
        }

        startActivity(
            Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
        finish()
    }
}

