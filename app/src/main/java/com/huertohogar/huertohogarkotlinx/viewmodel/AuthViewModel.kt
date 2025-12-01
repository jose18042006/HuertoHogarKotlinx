package com.huertohogar.huertohogarkotlinx.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarkotlinx.data.remote.repository.AuthRepository
import com.huertohogar.huertohogarkotlinx.data.remote.repository.AuthRepositoryImpl
import kotlinx.coroutines.launch


class AuthViewModel(
    private val repository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set


    fun register(name: String, email: String, password: String) {
        // Validación rápida en el cliente
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(
                errorMessage = "Todos los campos son obligatorios",
                successMessage = null
            )
            return
        }

        uiState = uiState.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null
        )

        viewModelScope.launch {
            val result = repository.register(name, email, password)
            result
                .onSuccess { response ->
                    if (response.success) {
                        // El backend no devuelve nombre, así que usamos el que se escribió
                        uiState = uiState.copy(
                            isLoading = false,
                            token = response.token,           // normalmente viene null en el registro
                            userName = response.nombre ,
                            successMessage = response.message ?: "Registro exitoso",
                            errorMessage = null
                        )
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = response.message ?: "Error al registrarse",
                            successMessage = null
                        )
                    }
                }
                .onFailure { e ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error de red al registrarse",
                        successMessage = null
                    )
                }
        }
    }

    /** Iniciar sesión contra el backend */
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(
                errorMessage = "Email y contraseña son obligatorios",
                successMessage = null
            )
            return
        }

        uiState = uiState.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null
        )

        viewModelScope.launch {
            val result = repository.login(email, password)
            result
                .onSuccess { response ->
                    if (response.success) {
                        // El backend solo envía token, message, success.
                        // Marcamos como logueado si hay token (o simplemente si success = true)
                        uiState = uiState.copy(
                            isLoading = false,
                            token = response.token ?: "OK",   // si tu backend envía null, igual nos sirve
                            // userName puedes rellenarlo luego desde otro endpoint si quieres
                            successMessage = response.message ?: "Login exitoso",
                            errorMessage = null
                        )
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            token = null,
                            errorMessage = response.message ?: "Error al iniciar sesión",
                            successMessage = null
                        )
                    }
                }
                .onFailure { e ->
                    uiState = uiState.copy(
                        isLoading = false,
                        token = null,
                        errorMessage = e.message ?: "Error de red al iniciar sesión",
                        successMessage = null
                    )
                }
        }
    }

    /** Cerrar sesión y limpiar todo el estado */
    fun logout() {
        uiState = AuthUiState()
    }
}
