package com.lockedin.app.data.remote

import kotlinx.serialization.json.Json
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType

/**
 * Retrofit + OkHttp client for HaveIBeenPwned Passwords API.
 *
 * SECURITY:
 * - Certificate pinning is enabled; replace placeholder pin with real pins
 *   from HIBP documentation before production release.
 * - Logging interceptor never logs response bodies to avoid leaking hashes.
 */
class HibpService(
    isDebug: Boolean
) {

    companion object {
        private const val BASE_URL = "https://api.pwnedpasswords.com/"

        // TODO: replace placeholder with real SHA-256 pins from HIBP docs.
        private val PINNER: CertificatePinner = CertificatePinner.Builder()
            .add(
                "api.pwnedpasswords.com",
                "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
            )
            .build()
    }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .certificatePinner(PINNER)
        .apply {
            if (isDebug) {
                val logging = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
                addInterceptor(logging)
            }
        }
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("text/plain".toMediaType()))
        .build()

    val api: HibpApi = retrofit.create(HibpApi::class.java)
}

