package com.lockedin.app.di

import android.content.Context
import com.lockedin.app.data.local.AppDatabase
import com.lockedin.app.data.local.DatabasePassphrase
import com.lockedin.app.data.local.dao.CategoryDao
import com.lockedin.app.data.local.dao.HistoryDao
import com.lockedin.app.data.local.dao.PasswordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides SQLCipher-encrypted Room database and DAO instances.
 *
 * SECURITY:
 * - [DatabasePassphrase] wraps EncryptedSharedPreferences and never exposes the
 *   underlying key material beyond the in-memory byte array needed by SQLCipher.
 * - AppDatabase uses field-level encryption via [com.lockedin.app.core.security.CryptoManager]
 *   for all sensitive columns in addition to full-database encryption.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabasePassphrase(
        @ApplicationContext context: Context
    ): DatabasePassphrase = DatabasePassphrase(context)

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        passphrase: DatabasePassphrase
    ): AppDatabase = AppDatabase.getInstance(context, passphrase)

    @Provides
    fun providePasswordDao(database: AppDatabase): PasswordDao = database.passwordDao()

    @Provides
    fun provideHistoryDao(database: AppDatabase): HistoryDao = database.historyDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()
}

