package com.huertohogar.huertohogarkotlinx.data.remote

import com.huertohogar.huertohogarkotlinx.data.remote.api.RecipeApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RecipeApiClient {

    private const val BASE_URL = "https://www.themealdb.com/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val recipeApi: RecipeApi = retrofit.create(RecipeApi::class.java)
}
