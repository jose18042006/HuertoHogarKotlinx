package com.huertohogar.huertohogarkotlinx.data.remote

import com.huertohogar.huertohogarkotlinx.data.remote.api.AdminApi
import com.huertohogar.huertohogarkotlinx.data.remote.api.AuthApi
import com.huertohogar.huertohogarkotlinx.data.remote.api.UserApi // <-- ¡NUEVO IMPORT!
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://10.75.115.105:8080/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(AuthInterceptor())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // APIs disponibles
    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
    val adminApi: AdminApi = retrofit.create(AdminApi::class.java)
    val userApi: UserApi = retrofit.create(UserApi::class.java) // <-- ¡NUEVA API CONECTADA!
}
