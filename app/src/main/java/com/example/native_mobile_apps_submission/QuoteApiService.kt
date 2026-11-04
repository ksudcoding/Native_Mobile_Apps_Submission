package com.example.native_mobile_apps_submission

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET



data class QuoteResponse(
    val quote: String,
    val author: String
)



interface QuoteApiService {
    @GET("random")
    suspend fun getRandomQuote(): QuoteResponse

    companion object {
        val instance: QuoteApiService by lazy {
            Retrofit.Builder()
                .baseUrl("https://dummyjson.com/quotes/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QuoteApiService::class.java)
        }
    }
}