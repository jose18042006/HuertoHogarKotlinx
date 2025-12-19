package com.huertohogar.huertohogarkotlinx.data.remote.api

import com.huertohogar.huertohogarkotlinx.data.model.UserDto
import retrofit2.http.GET

/**
 * Interfaz de Retrofit para los endpoints relacionados con el usuario.
 */
interface UserApi {

    /**
     * Obtiene los detalles del usuario actualmente autenticado.
     * El token JWT se añade automáticamente a través del AuthInterceptor.
     */
    @GET("api/users/me")
    suspend fun getMe(): UserDto
}
