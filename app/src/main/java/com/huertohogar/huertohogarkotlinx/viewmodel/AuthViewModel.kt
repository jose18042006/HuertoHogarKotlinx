package com.huertohogar.huertohogarkotlinx.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarkotlinx.data.remote.repository.AuthRepository
import com.huertohogar.huertohogarkotlinx.data.remote.repository.AuthRepositoryImpl
import kotlinx.coroutines.launch

// ------------------- INICIO DE LA ÚNICA CORRECCIÓN -------------------
// Esta es la clase que faltaba y causaba todos los errores.
// Ahora el compilador sabrá qué es "AuthUiState" y qué es "userName".
data class AuthUiState(
    val token: String? = null,
    val userName: String? = null, // <- Usamos el nombre que tu código ya utilizaba.
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
// ------------------- FIN DE LA ÚNICA CORRECCIÓN -------------------


/**
 * ViewModel que habla con el backend de Spring Boot (huertoauth)
 * usando AuthRepository.
 */
class AuthViewModel(
    private val repository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    /** Crear cuenta en el backend */
    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Todos los campos son obligatorios")
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null, successMessage = null)

        viewModelScope.launch {
            val result = repository.register(name, email, password)
            result
                .onSuccess { response ->
                    if (response.success) {
                        uiState = uiState.copy(
                            isLoading = false,
                            token = response.token,
                            userName = name, // <- Esto ahora es válido porque AuthUiState tiene 'userName'
                            successMessage = response.message ?: "Registro exitoso",
                            errorMessage = null
                        )
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = response.message ?: "Error al registrarse"
                        )
                    }
                }
                .onFailure { e ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error de red al registrarse"
                    )
                }
        }
    }

    /** Iniciar sesión contra el backend */
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Email y contraseña son obligatorios")
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null, successMessage = null)

        viewModelScope.launch {
            val result = repository.login(email, password)
            result
                .onSuccess { response ->
                    if (response.success) {
                        uiState = uiState.copy(
                            isLoading = false,
                            token = response.token ?: "OK",
                            // El nombre de usuario puede venir de la API o lo dejamos como está
                            userName = response.nombre ?: uiState.userName,
                            successMessage = response.message ?: "Login exitoso",
                            errorMessage = null
                        )
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            token = null,
                            errorMessage = response.message ?: "Error al iniciar sesión"
                        )
                    }
                }
                .onFailure { e ->
                    uiState = uiState.copy(
                        isLoading = false,
                        token = null,
                        errorMessage = e.message ?: "Error de red al iniciar sesión"
                    )
                }
        }
    }

    /** Cerrar sesión y limpiar todo el estado */
    fun logout() {
        uiState = AuthUiState()
    }

    // He añadido esta función que te di en una versión anterior,
    // es útil y no rompe nada.
    fun clearMessages() {
        uiState = uiState.copy(errorMessage = null, successMessage = null)
    }
}
