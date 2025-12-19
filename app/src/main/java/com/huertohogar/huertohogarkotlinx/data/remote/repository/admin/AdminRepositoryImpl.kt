package com.huertohogar.huertohogarkotlinx.data.remote.repository.admin

import com.huertohogar.huertohogarkotlinx.data.model.UserDto
import com.huertohogar.huertohogarkotlinx.data.remote.ApiClient
import com.huertohogar.huertohogarkotlinx.data.remote.api.AdminApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdminRepositoryImpl(private val adminApi: AdminApi = ApiClient.adminApi) : AdminRepository {

    override suspend fun getAllUsers(): Result<List<UserDto>> = safeCall {
        adminApi.getAllUsers()
    }

    override suspend fun createUser(user: UserDto): Result<UserDto> = safeCall {
        adminApi.createUser(user)
    }

    override suspend fun updateUser(userId: Long, user: UserDto): Result<UserDto> = safeCall {
        adminApi.updateUser(userId, user)
    }

    override suspend fun deleteUser(userId: Long): Result<Unit> = safeCall {
        adminApi.deleteUser(userId)
    }

    private suspend fun <T> safeCall(block: suspend () -> T): Result<T> = withContext(Dispatchers.IO) {
        try {
            Result.success(block())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}