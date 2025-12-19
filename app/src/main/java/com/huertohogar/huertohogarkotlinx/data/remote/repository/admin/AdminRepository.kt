package com.huertohogar.huertohogarkotlinx.data.remote.repository.admin

import com.huertohogar.huertohogarkotlinx.data.model.UserDto

/**
 * Repositorio para las acciones de administración.
 */
interface AdminRepository {

    suspend fun getAllUsers(): Result<List<UserDto>>

    suspend fun createUser(user: UserDto): Result<UserDto>

    suspend fun updateUser(userId: Long, user: UserDto): Result<UserDto>

    suspend fun deleteUser(userId: Long): Result<Unit>
}
