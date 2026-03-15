package com.lockedin.app.service

import android.app.assist.AssistStructure
import android.os.Build
import android.service.autofill.AutofillService
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import androidx.annotation.RequiresApi
import com.lockedin.app.domain.usecase.password.GetAllPasswordsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Android AutofillService implementation for LockedIn.
 *
 * SECURITY:
 * - Never logs sensitive data.
 * - Only matches entries based on metadata (URL/package/name).
 * - Authentication gating (PIN/biometric) is handled by the host app's
 *   locked/unlocked state, not by sharing secrets via Intent extras.
 */
@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class LockedInAutofillService : AutofillService() {

    @Inject
    lateinit var getAllPasswordsUseCase: GetAllPasswordsUseCase

    private val helper = AutofillHelper()
    private val responseBuilder = AutofillResponseBuilder()

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: android.os.CancellationSignal,
        callback: FillCallback
    ) {
        val context = request.fillContexts.lastOrNull() ?: run {
            callback.onSuccess(null)
            return
        }
        val structure = context.structure ?: run {
            callback.onSuccess(null)
            return
        }

        serviceScope.launch {
            try {
                val detected = helper.detectFields(structure)
                val passwords = getAllPasswordsUseCase().first()
                val matches = helper.findMatches(
                    entries = passwords,
                    webDomain = detected.webDomain,
                    packageName = detected.packageName
                )
                val response = responseBuilder.buildResponse(
                    structure = structure,
                    detected = detected,
                    matches = matches
                )
                callback.onSuccess(response)
            } catch (_: Throwable) {
                callback.onSuccess(null)
            }
        }
    }

    override fun onSaveRequest(
        request: SaveRequest,
        callback: SaveCallback
    ) {
        // For now, do nothing. Later phases can introspect SaveRequest and launch
        // an in-app flow to add credentials to the vault.
        callback.onSuccess()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}

