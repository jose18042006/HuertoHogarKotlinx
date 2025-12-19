package com.huertohogar.huertohogarkotlinx.data.remote.api

import com.huertohogar.huertohogarkotlinx.data.remote.dto.AuthResponse
import com.huertohogar.huertohogarkotlinx.data.remote.dto.LoginRequest
import com.huertohogar.huertohogarkotlinx.data.remote.dto.RegisterRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/register")
    // Se cambia AuthResponse por ResponseBody para aceptar la respuesta de texto plano del backend
    suspend fun register(@Body request: RegisterRequest): ResponseBody

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
}
