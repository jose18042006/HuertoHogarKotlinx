package com.huertohogar.huertohogarkotlinx.data.remote.dto

data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val userId: Long?,
    val nombre: String?
)
