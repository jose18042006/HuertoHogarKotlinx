package com.huertohogar.huertohogarkotlinx.data.remote.repository

import com.huertohogar.huertohogarkotlinx.data.model.UserDto
import com.huertohogar.huertohogarkotlinx.data.remote.dto.AuthResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val token: Flow<String?>
    val userRole: Flow<String?>

    suspend fun register(nombre: String, email: String, password: String): Result<String>
    suspend fun login(username: String, password: String): Result<AuthResponse>

    // --- ¡NUEVO MÉTODO! ---
    suspend fun getCurrentUser(): Result<UserDto>

    suspend fun saveSession(token: String, role: String)
    suspend fun clearSession()
}
