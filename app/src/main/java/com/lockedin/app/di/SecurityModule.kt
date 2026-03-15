package com.lockedin.app.di

import android.content.Context
import com.lockedin.app.core.security.BiometricHelper
import com.lockedin.app.core.security.CryptoManager
import com.lockedin.app.core.security.KeystoreManager
import com.lockedin.app.core.security.MasterKeyManager
import com.lockedin.app.core.security.PinManager
import com.lockedin.app.core.security.SecureClipboardManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Security-critical components: master password, PIN, biometrics, Keystore, and
 * field-level encryption.
 *
 * SECURITY:
 * - [KeystoreManager] generates non-exportable AES-256 keys (hardware-backed where possible).
 * - [CryptoManager] performs AES-256-GCM encryption with per-call random IVs.
 * - [MasterKeyManager] and [PinManager] store only salted PBKDF2 hashes in
 *   EncryptedSharedPreferences; plaintext secrets are wiped from memory.
 * - [SecureClipboardManager] centralizes clipboard usage to ensure auto-clear logic
 *   is consistently applied.
 */
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideKeystoreManager(): KeystoreManager = KeystoreManager()

    @Provides
    @Singleton
    fun provideCryptoManager(
        keystoreManager: KeystoreManager
    ): CryptoManager = CryptoManager(keystoreManager)

    @Provides
    @Singleton
    fun provideMasterKeyManager(
        @ApplicationContext context: Context
    ): MasterKeyManager = MasterKeyManager(context)

    @Provides
    @Singleton
    fun providePinManager(
        @ApplicationContext context: Context
    ): PinManager = PinManager(context)

    @Provides
    @Singleton
    fun provideBiometricHelper(
        @ApplicationContext context: Context
    ): BiometricHelper = BiometricHelper(context)

    /**
     * Expose [SecureClipboardManager] from here as well so all security-related work
     * (including clipboard auto-clear) lives in a single module from Hilt's perspective.
     */
    @Provides
    @Singleton
    fun provideSecureClipboardManagerSecurity(
        @ApplicationContext context: Context
    ): SecureClipboardManager = SecureClipboardManager(context)
}

