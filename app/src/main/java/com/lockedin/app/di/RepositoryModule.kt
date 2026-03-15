package com.lockedin.app.di

import com.lockedin.app.data.local.dao.PasswordDao
import com.lockedin.app.data.mapper.HistoryMapper
import com.lockedin.app.data.mapper.PasswordMapper
import com.lockedin.app.data.repository.BreachRepositoryImpl
import com.lockedin.app.data.repository.CategoryRepositoryAdapter
import com.lockedin.app.data.repository.CategoryRepositoryImpl
import com.lockedin.app.data.repository.HistoryRepositoryImpl
import com.lockedin.app.data.repository.PasswordRepositoryImpl
import com.lockedin.app.data.remote.HibpApi
import com.lockedin.app.domain.repository.BreachRepository
import com.lockedin.app.domain.repository.CategoryRepository
import com.lockedin.app.domain.repository.HistoryRepository
import com.lockedin.app.domain.repository.PasswordRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Bridges data-layer implementations to domain repository interfaces.
 *
 * SECURITY:
 * - Breach checks are funneled through [BreachRepository] which uses k-anonymity
 *   and never persists raw password hashes.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindingsModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryAdapter
    ): CategoryRepository
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePasswordRepositoryImpl(
        dao: PasswordDao,
        mapper: PasswordMapper
    ): PasswordRepositoryImpl = PasswordRepositoryImpl(dao, mapper)

    @Provides
    @Singleton
    fun providePasswordRepository(
        impl: PasswordRepositoryImpl
    ): PasswordRepository = object : PasswordRepository {
        override fun getAll() = impl.getAll().map { list -> list.map { it.toDomain() } }

        override suspend fun getAllOnce() = impl.getAllOnce().map { it.toDomain() }

        override suspend fun getById(id: Long) = impl.getById(id)?.toDomain()

        override fun search(query: String) =
            impl.search(query).map { list -> list.map { it.toDomain() } }

        override suspend fun save(entry: com.lockedin.app.domain.model.PasswordEntry): Long =
            impl.save(entry.toData())

        override suspend fun delete(entry: com.lockedin.app.domain.model.PasswordEntry) {
            impl.delete(entry.toData())
        }

        override suspend fun incrementUsage(id: Long) {
            impl.incrementUsage(id)
        }

        override suspend fun updateCompromiseState(
            id: Long,
            isCompromised: Boolean,
            count: Int
        ) {
            impl.updateCompromiseState(id, isCompromised, count)
        }

        override suspend fun deleteAll() {
            impl.deleteAll()
        }
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(
        impl: HistoryRepositoryImpl
    ): HistoryRepository = object : HistoryRepository {
        override fun getRecent(limit: Int) =
            impl.getRecent(limit).map { list -> list.map { it.toDomain() } }

        override suspend fun add(entry: com.lockedin.app.domain.model.PasswordHistory) {
            impl.add(entry.toData())
        }

        override suspend fun delete(id: Long) {
            impl.delete(id)
        }

        override suspend fun clearAll() {
            impl.clearAll()
        }
    }

    @Provides
    @Singleton
    fun provideCategoryRepositoryImpl(
        dao: com.lockedin.app.data.local.dao.CategoryDao
    ): CategoryRepositoryImpl = CategoryRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideCategoryRepositoryAdapter(
        impl: CategoryRepositoryImpl
    ): CategoryRepositoryAdapter = CategoryRepositoryAdapter(impl)

    @Provides
    @Singleton
    fun provideBreachRepository(
        passwordDao: PasswordDao,
        passwordMapper: PasswordMapper,
        hibpApi: HibpApi
    ): BreachRepository = object : BreachRepository {
        private val delegate = BreachRepositoryImpl(passwordDao, passwordMapper, hibpApi)

        override suspend fun checkAllPasswords(): Map<Long, Boolean> =
            delegate.checkAllPasswords()

        override suspend fun isPasswordCompromised(plainPassword: String): Boolean =
            delegate.isPasswordCompromised(plainPassword)
    }

    @Provides
    @Singleton
    fun provideHistoryRepositoryImpl(
        dao: com.lockedin.app.data.local.dao.HistoryDao,
        mapper: HistoryMapper
    ): HistoryRepositoryImpl = HistoryRepositoryImpl(dao, mapper)
}

// Local mapping helpers keep adapters thin while avoiding leaking data-layer types.
private fun com.lockedin.app.data.mapper.PasswordData.toDomain(): com.lockedin.app.domain.model.PasswordEntry =
    com.lockedin.app.domain.model.PasswordEntry(
        id = id,
        siteName = siteName,
        siteUrl = siteUrl,
        username = username,
        password = password,
        faviconUrl = faviconUrl,
        colorLabel = colorLabel,
        category = category,
        tags = tags,
        notes = notes,
        customFields = customFields,
        totpSecret = totpSecret,
        passwordStrength = passwordStrength,
        isCompromised = isCompromised,
        compromiseCount = compromiseCount,
        timesUsed = timesUsed,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastUsedAt = lastUsedAt
    )

private fun com.lockedin.app.domain.model.PasswordEntry.toData(): com.lockedin.app.data.mapper.PasswordData =
    com.lockedin.app.data.mapper.PasswordData(
        id = id,
        siteName = siteName,
        siteUrl = siteUrl,
        username = username,
        password = password,
        faviconUrl = faviconUrl,
        colorLabel = colorLabel,
        category = category,
        tags = tags,
        notes = notes,
        customFields = customFields,
        totpSecret = totpSecret,
        passwordStrength = passwordStrength,
        isCompromised = isCompromised,
        compromiseCount = compromiseCount,
        timesUsed = timesUsed,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastUsedAt = lastUsedAt
    )

private fun com.lockedin.app.data.mapper.PasswordHistoryData.toDomain():
    com.lockedin.app.domain.model.PasswordHistory =
    com.lockedin.app.domain.model.PasswordHistory(
        id = id,
        password = password,
        strength = strength,
        length = length,
        characterTypes = characterTypes,
        wasSaved = wasSaved,
        createdAt = createdAt
    )

private fun com.lockedin.app.domain.model.PasswordHistory.toData():
    com.lockedin.app.data.mapper.PasswordHistoryData =
    com.lockedin.app.data.mapper.PasswordHistoryData(
        id = id,
        password = password,
        strength = strength,
        length = length,
        characterTypes = characterTypes,
        wasSaved = wasSaved,
        createdAt = createdAt
    )

