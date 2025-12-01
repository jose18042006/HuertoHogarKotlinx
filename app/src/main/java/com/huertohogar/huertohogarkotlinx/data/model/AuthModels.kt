package com.huertohogar.huertohogarkotlinx.data.model

// Lo que se envía al registrar
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

// Lo que se envía al iniciar sesión
data class LoginRequest(
    val email: String,
    val password: String
)

// Lo que responde el backend en ambos casos
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val userId: Long? = null,
    val name: String? = null
)
