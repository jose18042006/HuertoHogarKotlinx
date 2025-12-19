package com.huertohogar.huertohogarkotlinx.data.remote.dto

/**
 * DTO (Data Transfer Object) para la respuesta de autenticación del backend.
 * Contiene el token JWT que el servidor envía al iniciar sesión o registrarse.
 */
data class AuthResponse(
    val token: String?
)
