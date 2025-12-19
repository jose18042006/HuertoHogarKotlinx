package com.huertohogar.huertohogarkotlinx.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarkotlinx.data.remote.AuthInterceptor
import com.huertohogar.huertohogarkotlinx.data.remote.repository.AuthRepository
import com.huertohogar.huertohogarkotlinx.data.remote.repository.AuthRepositoryImpl
import com.huertohogar.huertohogarkotlinx.utils.JwtUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(
    application: Application,
    // 1. ACEPTAMOS EL SHAREDVIEWMODEL
    private val sharedUserViewModel: SharedUserViewModel
) : AndroidViewModel(application) {

    private val authRepository: AuthRepository = AuthRepositoryImpl(application)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.token.collectLatest { token ->
                AuthInterceptor.token = token
                val role = token?.let { JwtUtils.getRoleFromToken(it) }
                val username = token?.let { JwtUtils.getUsernameFromToken(it) }
                _uiState.update { it.copy(token = token, userRole = role, userName = username) }

                if (token != null) {
                    fetchCurrentUser()
                }
            }
        }
    }

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Usuario y contraseña no pueden estar vacíos.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val result = authRepository.login(username, password)
            result.onSuccess { response ->
                val token = response.token
                if (token != null) {
                    val role = JwtUtils.getRoleFromToken(token) ?: "ROLE_CLIENT"
                    authRepository.saveSession(token, role)
                    // El bloque init se encargará de reaccionar, aquí solo apagamos el loading
                    _uiState.update { it.copy(isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error: No se recibió token del servidor.") }
                }
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${throwable.message}") }
            }
        }
    }

    // 2. LÓGICA RESTAURADA PARA OBTENER EL EMAIL
    private fun fetchCurrentUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser().onSuccess { userDto ->
                sharedUserViewModel.updateUserDataFromDto(userDto)
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Todos los campos son obligatorios.") }
            return
        }
        if (!email.contains("@")) {
            _uiState.update { it.copy(errorMessage = "Por favor, introduce un email válido.") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val result = authRepository.register(name, email, password)
            result.onSuccess { successMessage ->
                _uiState.update { it.copy(isLoading = false, successMessage = successMessage) }
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${throwable.message}") }
            }
        }
    }

    // 3. LÓGICA DE LOGOUT MEJORADA
    fun logout() {
        viewModelScope.launch {
            authRepository.clearSession()
            // Reseteamos el estado para limpiar cualquier mensaje o estado de carga residual
            _uiState.value = AuthUiState()
        }
    }
}