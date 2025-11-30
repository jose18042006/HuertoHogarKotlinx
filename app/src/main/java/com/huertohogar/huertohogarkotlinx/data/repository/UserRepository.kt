package com.huertohogar.huertohogarkotlinx.data.repository

import com.huertohogar.huertohogarkotlinx.data.local.AppSettingsDataStore
import com.huertohogar.huertohogarkotlinx.data.local.UserDao
import com.huertohogar.huertohogarkotlinx.data.local.toEntity
import com.huertohogar.huertohogarkotlinx.data.local.toModel
import com.huertohogar.huertohogarkotlinx.data.model.FormModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(
    private val userDao: UserDao,
    private val appSettingsDataStore: AppSettingsDataStore
) {
    fun getFormData(): Flow<FormModel?> {
        return userDao.getUserById().map { entity ->
            entity?.toModel()
        }
    }

    suspend fun saveFormData(data: FormModel) {
        userDao.insertUser(data.toEntity())
    }

    fun hasSeenIntro(): Flow<Boolean> {
        return appSettingsDataStore.getHasSeenIntro()
    }

    suspend fun setSeenIntro(seen: Boolean) {
        appSettingsDataStore.setHasSeenIntro(seen)
    }
}