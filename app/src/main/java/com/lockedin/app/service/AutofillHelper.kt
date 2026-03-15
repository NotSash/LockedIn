package com.lockedin.app.service

import android.app.assist.AssistStructure
import android.os.Build
import android.view.View
import android.view.autofill.AutofillId
import android.view.autofill.AutofillManager
import android.view.autofill.AutofillValue
import android.widget.EditText
import androidx.annotation.RequiresApi
import com.lockedin.app.domain.model.PasswordEntry

/**
 * Utility functions to inspect AssistStructure, detect fields, and match vault entries.
 *
 * SECURITY:
 * - Does not log field contents.
 * - Only uses non-sensitive metadata (hints, types, ids, package name, URL).
 */
@RequiresApi(Build.VERSION_CODES.O)
class AutofillHelper {

    data class DetectedFields(
        val usernameId: AutofillId?,
        val passwordId: AutofillId?,
        val webDomain: String?,
        val packageName: String?
    )

    fun detectFields(structure: AssistStructure): DetectedFields {
        var usernameId: AutofillId? = null
        var passwordId: AutofillId? = null
        var webDomain: String? = null
        val packageName = structure.activityComponent?.packageName

        val nodes = structure.windowNodeCount
        for (i in 0 until nodes) {
            val node = structure.getWindowNodeAt(i).rootViewNode
            traverse(node) { viewNode ->
                // Extract possible web domain from web URL-like nodes or hints.
                if (webDomain == null) {
                    val webDomainCandidate = viewNode.webDomain
                    if (!webDomainCandidate.isNullOrBlank()) {
                        webDomain = webDomainCandidate
                    }
                }

                val hints = viewNode.autofillHints?.toList().orEmpty()
                if (usernameId == null && hints.any { it == View.AUTOFILL_HINT_USERNAME || it == View.AUTOFILL_HINT_EMAIL_ADDRESS }) {
                    usernameId = viewNode.autofillId
                }
                if (passwordId == null && hints.any { it == View.AUTOFILL_HINT_PASSWORD }) {
                    passwordId = viewNode.autofillId
                }

                // Fallback by inputType if hints are missing
                if (passwordId == null && viewNode.inputType and android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD != 0) {
                    passwordId = viewNode.autofillId
                }
            }
        }

        return DetectedFields(
            usernameId = usernameId,
            passwordId = passwordId,
            webDomain = webDomain,
            packageName = packageName
        )
    }

    /**
     * Matches vault entries against a web domain or package name using simple heuristics.
     */
    fun findMatches(
        entries: List<PasswordEntry>,
        webDomain: String?,
        packageName: String?
    ): List<PasswordEntry> {
        if (entries.isEmpty()) return emptyList()
        val domain = webDomain?.lowercase()

        val byDomain = if (!domain.isNullOrBlank()) {
            entries.filter { e ->
                val url = e.siteUrl.lowercase()
                url.contains(domain) || domain.contains(extractDomain(url))
            }
        } else emptyList()

        // Fallback: use package simple name (after last dot) to match site name
        val byPackage = if (!packageName.isNullOrBlank()) {
            val simple = packageName.substringAfterLast('.').lowercase()
            entries.filter { e ->
                e.siteName.lowercase().contains(simple)
            }
        } else emptyList()

        val combined = (byDomain + byPackage).distinctBy { it.id }
        return if (combined.isNotEmpty()) combined else entries
    }

    private fun extractDomain(url: String): String {
        val withoutScheme = url.removePrefix("https://").removePrefix("http://")
        return withoutScheme.substringBefore('/').lowercase()
    }

    private fun traverse(node: AssistStructure.ViewNode, block: (AssistStructure.ViewNode) -> Unit) {
        block(node)
        for (i in 0 until node.childCount) {
            traverse(node.getChildAt(i), block)
        }
    }
}

