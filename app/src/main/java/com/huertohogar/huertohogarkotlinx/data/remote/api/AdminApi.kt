package com.huertohogar.huertohogarkotlinx.data.remote.api

import com.huertohogar.huertohogarkotlinx.data.model.UserDto
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz de Retrofit para los endpoints de administración.
 */
interface AdminApi {

    @GET("api/users")
    suspend fun getAllUsers(): List<UserDto>

    // CORREGIDO: La ruta ahora es /api/users, como en el UserController unificado.
    @POST("api/users")
    suspend fun createUser(@Body user: UserDto): UserDto

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") userId: Long, @Body user: UserDto): UserDto

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") userId: Long): Response<Unit>
}
