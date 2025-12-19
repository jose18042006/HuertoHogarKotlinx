package com.huertohogar.huertohogarkotlinx.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la petición de Login.
 */
data class LoginRequest(
    // CORREGIDO: La variable ahora se llama 'username' para coincidir con el backend.
    val username: String,
    val password: String
)

/**
 * DTO para la petición de Registro.
 */
data class RegisterRequest(
    @SerializedName("username")
    val name: String,
    val email: String,
    val password: String
)
