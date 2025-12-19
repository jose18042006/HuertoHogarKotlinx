package com.huertohogar.huertohogarkotlinx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarkotlinx.data.model.UserDto
import com.huertohogar.huertohogarkotlinx.data.remote.repository.admin.AdminRepository
import com.huertohogar.huertohogarkotlinx.data.remote.repository.admin.AdminRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la UI para las pantallas de administración.
 */
data class AdminUiState(
    val isLoading: Boolean = false,
    val users: List<UserDto> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

/**
 * ViewModel para gestionar la lógica de las pantallas de administración.
 */
class AdminViewModel(
    private val adminRepository: AdminRepository = AdminRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun loadAllUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            adminRepository.getAllUsers().onSuccess { users ->
                _uiState.update { it.copy(isLoading = false, users = users) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${error.message}") }
            }
        }
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            adminRepository.deleteUser(userId).onSuccess {
                _uiState.update { it.copy(isLoading = false, successMessage = "Usuario eliminado correctamente.") }
                loadAllUsers() // Recargamos la lista
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al eliminar: ${error.message}") }
            }
        }
    }

    fun createUser(user: UserDto) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            adminRepository.createUser(user).onSuccess {
                _uiState.update { it.copy(isLoading = false, successMessage = "Usuario creado correctamente.") }
                loadAllUsers() // Recargamos la lista
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al crear: ${error.message}") }
            }
        }
    }

    fun updateUser(userId: Long, user: UserDto) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            adminRepository.updateUser(userId, user).onSuccess {
                _uiState.update { it.copy(isLoading = false, successMessage = "Usuario actualizado correctamente.") }
                loadAllUsers() // Recargamos la lista
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al actualizar: ${error.message}") }
            }
        }
    }

    // Función para limpiar los mensajes y poder volver a la lista
    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}