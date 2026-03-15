package com.lockedin.app.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * HaveIBeenPwned k-anonymity password range API.
 *
 * Only the first 5 characters of the SHA-1 hash are sent as {prefix}.
 */
interface HibpApi {

    @GET("range/{prefix}")
    suspend fun getHashSuffixes(
        @Path("prefix") prefix: String
    ): Response<String>
}

