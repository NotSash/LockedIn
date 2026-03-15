package com.lockedin.app.service

import android.app.assist.AssistStructure
import android.os.Build
import android.service.autofill.Dataset
import android.service.autofill.FillResponse
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.lockedin.app.R
import com.lockedin.app.domain.model.PasswordEntry

/**
 * Builds FillResponse objects for the Android Autofill framework.
 *
 * SECURITY:
 * - Only provides username/password values to fields detected as such.
 * - Does not expose notes, TOTP secrets, or other sensitive metadata.
 */
@RequiresApi(Build.VERSION_CODES.O)
class AutofillResponseBuilder {

    fun buildResponse(
        structure: AssistStructure,
        detected: AutofillHelper.DetectedFields,
        matches: List<PasswordEntry>
    ): FillResponse? {
        val usernameId = detected.usernameId
        val passwordId = detected.passwordId
        if (passwordId == null) return null

        val datasets = matches.mapNotNull { entry ->
            createDatasetForEntry(entry, usernameId, passwordId)
        }
        if (datasets.isEmpty()) return null

        return FillResponse.Builder()
            .apply {
                datasets.forEach { addDataset(it) }
            }
            .build()
    }

    private fun createDatasetForEntry(
        entry: PasswordEntry,
        usernameId: AutofillId?,
        passwordId: AutofillId
    ): Dataset? {
        val rv = RemoteViews("com.lockedin.app", R.layout.autofill_suggestion_row).apply {
            setTextViewText(R.id.autofill_title, entry.siteName)
            setTextViewText(R.id.autofill_subtitle, entry.username)
        }

        val builder = Dataset.Builder(rv)

        if (usernameId != null) {
            builder.setValue(
                usernameId,
                AutofillValue.forText(entry.username),
                rv
            )
        }

        builder.setValue(
            passwordId,
            AutofillValue.forText(entry.password.concatToString()),
            rv
        )

        return builder.build()
    }
}

