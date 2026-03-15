package com.lockedin.app.core.security

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.content.getSystemService

/**
 * Copies sensitive data to clipboard and schedules automatic clearing.
 *
 * SECURITY:
 * - Uses AlarmManager with a broadcast to clear clipboard even if app is killed.
 * - Never logs clipboard contents.
 */
class SecureClipboardManager(
    private val context: Context
) {

    companion object {
        const val CLEAR_CLIPBOARD_ACTION = "com.lockedin.app.action.CLEAR_CLIPBOARD"
        const val EXTRA_CLIPBOARD_ID = "clipboard_id"
    }

    private val clipboard: ClipboardManager? = context.getSystemService()
    private val alarmManager: AlarmManager? = context.getSystemService()

    fun copyWithTimeout(id: Long, text: CharSequence, timeoutMillis: Long) {
        val clip = ClipData.newPlainText("LockedIn", text)
        clipboard?.setPrimaryClip(clip)
        scheduleClear(id, timeoutMillis)
    }

    private fun scheduleClear(id: Long, timeoutMillis: Long) {
        val alarm = alarmManager ?: return

        val intent = Intent(context, com.lockedin.app.receiver.ScreenLockReceiver::class.java).apply {
            action = CLEAR_CLIPBOARD_ACTION
            putExtra(EXTRA_CLIPBOARD_ID, id)
        }

        val pi = PendingIntent.getBroadcast(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val trigger = SystemClock.elapsedRealtime() + timeoutMillis
        alarm.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, trigger, pi)
    }

    fun clearClipboard() {
        clipboard?.setPrimaryClip(ClipData.newPlainText("", ""))
    }
}

