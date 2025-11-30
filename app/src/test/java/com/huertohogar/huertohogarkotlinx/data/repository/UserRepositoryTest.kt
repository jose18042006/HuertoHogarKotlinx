package com.huertohogar.huertohogarkotlinx.data.repository

import com.huertohogar.huertohogarkotlinx.data.local.AppSettingsDataStore
import com.huertohogar.huertohogarkotlinx.data.local.UserDao
import com.huertohogar.huertohogarkotlinx.data.local.UserEntity
import com.huertohogar.huertohogarkotlinx.data.local.toEntity
import com.huertohogar.huertohogarkotlinx.data.local.toModel
import com.huertohogar.huertohogarkotlinx.data.model.FormModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    private lateinit var userDao: UserDao

    @RelaxedMockK
    private lateinit var appSettingsDataStore: AppSettingsDataStore

    private lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        userRepository = UserRepository(userDao, appSettingsDataStore)
    }

    @Test
    fun `getFormData returns user from dao`() = runTest {
        // Given
        val userEntity = UserEntity(nombre = "name", email = "email", edad = 25, aceptaTerminos = true, comentario = null, profileImageUri = null)
        coEvery { userDao.getUserById() } returns flowOf(userEntity)

        // When
        val result = userRepository.getFormData().first()

        // Then
        assertEquals(userEntity.toModel(), result)
    }

    @Test
    fun `saveFormData inserts user into dao`() = runTest {
        // Given
        val formModel = FormModel(nombre = "name", email = "email", edad = 30, aceptaTerminos = true, profileImageUri = null)

        // When
        userRepository.saveFormData(formModel)

        // Then
        coVerify { userDao.insertUser(formModel.toEntity()) }
    }

    @Test
    fun `hasSeenIntro returns value from datastore`() = runTest {
        // Given
        coEvery { appSettingsDataStore.getHasSeenIntro() } returns flowOf(true)

        // When
        val result = userRepository.hasSeenIntro().first()

        // Then
        assertEquals(true, result)
    }

    @Test
    fun `setSeenIntro calls datastore`() = runTest {
        // When
        userRepository.setSeenIntro(true)

        // Then
        coVerify { appSettingsDataStore.setHasSeenIntro(true) }
    }
}