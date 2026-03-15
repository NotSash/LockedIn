package com.lockedin.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lockedin.app.domain.usecase.security.CheckBreachedPasswordsUseCase
import com.lockedin.app.domain.usecase.password.UpdatePasswordUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Periodic worker that checks stored passwords against HaveIBeenPwned
 * using the domain-level [CheckBreachedPasswordsUseCase].
 *
 * SECURITY:
 * - Runs in background on a schedule configured elsewhere.
 * - Does not display results directly; flags entries so the UI can surface
 *   breached status inside the app.
 */
@HiltWorker
class BreachCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val checkBreachedPasswordsUseCase: CheckBreachedPasswordsUseCase
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            checkBreachedPasswordsUseCase()
            Result.success()
        } catch (_: Throwable) {
            Result.retry()
        }
    }
}

