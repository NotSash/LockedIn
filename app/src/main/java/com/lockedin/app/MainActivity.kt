package com.lockedin.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.lockedin.app.core.security.BiometricHelper
import com.lockedin.app.core.security.SecureClipboardManager
import com.lockedin.app.core.ui.theme.LockedInTheme
import com.lockedin.app.presentation.navigation.LockedInRootNav
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
                val clipboardManager = SecureClipboardManager(this)
                val biometricHelper = BiometricHelper(this)
                val clipboardTimeoutMillis = 30_000L

                LockedInRootNav(
                    clipboardManager = clipboardManager,
                    clipboardTimeoutMillis = clipboardTimeoutMillis,
                    biometricHelper = biometricHelper
                )
            }
        }
    }
}
