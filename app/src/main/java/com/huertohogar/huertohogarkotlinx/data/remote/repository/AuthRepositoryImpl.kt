package com.huertohogar.huertohogarkotlinx.data.remote.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.huertohogar.huertohogarkotlinx.data.model.UserDto
import com.huertohogar.huertohogarkotlinx.data.remote.ApiClient
import com.huertohogar.huertohogarkotlinx.data.remote.api.AuthApi
import com.huertohogar.huertohogarkotlinx.data.remote.api.UserApi
import com.huertohogar.huertohogarkotlinx.data.remote.dto.AuthResponse
import com.huertohogar.huertohogarkotlinx.data.remote.dto.LoginRequest
import com.huertohogar.huertohogarkotlinx.data.remote.dto.RegisterRequest
import com.huertohogar.huertohogarkotlinx.di.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(private val context: Context) : AuthRepository {

    private val authApi: AuthApi = ApiClient.authApi
    private val userApi: UserApi = ApiClient.userApi

    private object PreferencesKeys {
        val TOKEN = stringPreferencesKey("jwt_token")
        val USER_ROLE = stringPreferencesKey("user_role")
    }

    override val token: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.TOKEN] }

    override val userRole: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.USER_ROLE] }

    override suspend fun register(nombre: String, email: String, password: String): Result<String> = safeCall {
        authApi.register(RegisterRequest(nombre, email, password)).string()
    }

    override suspend fun login(username: String, password: String): Result<AuthResponse> = safeCall {
        authApi.login(LoginRequest(username, password))
    }

    override suspend fun getCurrentUser(): Result<UserDto> = safeCall {
        userApi.getMe()
    }

    override suspend fun saveSession(token: String, role: String) {
        context.dataStore.edit {
            it[PreferencesKeys.TOKEN] = token
            it[PreferencesKeys.USER_ROLE] = role
        }
    }

    override suspend fun clearSession() {
        context.dataStore.edit {
            it.remove(PreferencesKeys.TOKEN)
            it.remove(PreferencesKeys.USER_ROLE)
        }
    }

    private suspend fun <T> safeCall(block: suspend () -> T): Result<T> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(block())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}