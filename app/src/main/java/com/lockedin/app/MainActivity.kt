package com.lockedin.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.lockedin.app.core.ui.theme.LockedInTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity hosts the entire Compose navigation graph.
 *
 * SECURITY:
 * - Applies FLAG_SECURE to block screenshots and screen recording by default.
 *   This will later be controlled by a user setting.
 * - No sensitive data is passed via Intent extras; secrets remain either
 *   encrypted at rest or in-memory inside ViewModels.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install Android 12+ splash before super.onCreate
        val splashScreen: SplashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // SECURITY: prevent screenshots / screen recording globally by default.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // Draw behind system bars; the design system will handle insets.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            LockedInTheme {
                LockedInRoot()
            }
        }
    }
}

@Composable
private fun LockedInRoot() {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "LockedIn • Core Setup")
        }
    }
}
