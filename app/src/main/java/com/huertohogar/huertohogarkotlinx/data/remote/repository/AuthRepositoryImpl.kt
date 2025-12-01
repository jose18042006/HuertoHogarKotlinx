package com.huertohogar.huertohogarkotlinx.data.remote.repository

import com.huertohogar.huertohogarkotlinx.data.remote.ApiClient
import com.huertohogar.huertohogarkotlinx.data.remote.api.AuthApi
import com.huertohogar.huertohogarkotlinx.data.remote.dto.AuthResponse
import com.huertohogar.huertohogarkotlinx.data.remote.dto.LoginRequest
import com.huertohogar.huertohogarkotlinx.data.remote.dto.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val api: AuthApi = ApiClient.authApi
) : AuthRepository {

    override suspend fun register(
        nombre: String,
        email: String,
        password: String
    ): Result<AuthResponse> = safeCall {
        api.register(RegisterRequest(nombre, email, password))
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthResponse> = safeCall {
        api.login(LoginRequest(email, password))
    }

    private suspend fun <T> safeCall(block: suspend () -> T): Result<T> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(block())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
