package com.lockedin.app.di

import com.lockedin.app.data.remote.HibpApi
import com.lockedin.app.data.remote.HibpService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Network stack for the HaveIBeenPwned Passwords API.
 *
 * SECURITY:
 * - [HibpService] enables certificate pinning and restricts logging to BASIC, and
 *   it never logs password hashes or response bodies.
 * - Only the k-anonymity prefix of the SHA-1 hash is ever sent to the service.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHibpService(): HibpService {
        // BuildConfig.DEBUG controls whether basic HTTP logging is enabled.
        return HibpService(isDebug = BuildConfig.DEBUG)
    }

    @Provides
    @Singleton
    fun provideHibpApi(
        service: HibpService
    ): HibpApi = service.api
}

