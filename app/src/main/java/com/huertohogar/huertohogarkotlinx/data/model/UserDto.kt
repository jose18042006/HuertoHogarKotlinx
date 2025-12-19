package com.huertohogar.huertohogarkotlinx.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO (Data Transfer Object) para representar un usuario.
 */
data class UserDto(
    val id: Long,
    val username: String,
    val email: String,
    val role: String,
    // Se usa para enviar la contraseña al crear o actualizar. 
    // El backend nunca lo devolverá relleno.
    @SerializedName("password") // Asegura que el JSON siempre use "password"
    val password: String? = null 
)
