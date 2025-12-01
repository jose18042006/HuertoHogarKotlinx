package com.huertohogar.huertohogarkotlinx.data.remote.repository

import com.huertohogar.huertohogarkotlinx.data.remote.dto.AuthResponse

interface AuthRepository {

    suspend fun register(
        nombre: String,
        email: String,
        password: String
    ): Result<AuthResponse>

    suspend fun login(
        email: String,
        password: String
    ): Result<AuthResponse>
}
