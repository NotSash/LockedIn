package com.lockedin.app.di

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.lockedin.app.core.security.SecureClipboardManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Application-wide, non-feature-specific dependencies.
 *
 * SECURITY:
 * - Exposes a singleton [SecureClipboardManager] so clipboard-clearing logic is centralized.
 * - Dispatcher bindings avoid leaking Android-specific threads into domain layer.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSecureClipboardManager(
        @ApplicationContext context: Context
    ): SecureClipboardManager = SecureClipboardManager(context)

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    fun provideKotlinRandom(): Random = Random.Default

    /**
     * Preferences DataStore used for non-cryptographic, non-secret user settings
     * (e.g., theme, animation level, auto-lock duration). Do NOT store secrets here.
     */
    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ) = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("lockedin_settings") }
    )
}

