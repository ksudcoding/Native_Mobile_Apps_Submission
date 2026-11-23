package com.example.native_mobile_apps_submission

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Data transfer object representing a quote payload returned by the DummyJSON API.
 */
data class QuoteResponse(
    val quote: String,
    val author: String
)

/**
 * Retrofit service interface for fetching remote motivational quotes.
 */
interface QuoteApiService {
    @GET("random")
    suspend fun getRandomQuote(): QuoteResponse

    companion object {
        // Lazily initialized Retrofit client configured for the DummyJSON quotes endpoint
        val instance: QuoteApiService by lazy {
            Retrofit.Builder()
                .baseUrl("https://dummyjson.com/quotes/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QuoteApiService::class.java)
        }
    }
}