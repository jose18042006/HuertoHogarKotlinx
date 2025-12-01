package com.huertohogar.huertohogarkotlinx.data.remote.api

import com.huertohogar.huertohogarkotlinx.data.remote.dto.AuthResponse
import com.huertohogar.huertohogarkotlinx.data.remote.dto.LoginRequest
import com.huertohogar.huertohogarkotlinx.data.remote.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
}
