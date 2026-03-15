package com.lockedin.app.receiver

import android.app.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lockedin.app.core.security.SecureClipboardManager

/**
 * Receives screen-off events and clears sensitive clipboard contents.
 *
 * SECURITY:
 * - Ensures clipboard is wiped when the screen turns off, reducing the risk
 *   of shoulder-surfing or other apps reading copied passwords.
 */
class ScreenLockReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_SCREEN_OFF == intent.action) {
            SecureClipboardManager(context).clearClipboard()
        }
    }
}

