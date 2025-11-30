package com.huertohogar.huertohogarkotlinx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarkotlinx.data.model.FormModel
import com.huertohogar.huertohogarkotlinx.data.model.FormValidationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FormViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FormState())
    val uiState: StateFlow<FormState> = _uiState.asStateFlow()

    data class FormState(
        val data: FormModel = FormModel(),
        val validationState: FormValidationState = FormValidationState(),
        val showSnackbar: Boolean = false,
        val snackbarMessage: String = ""
    )

    fun onNombreChange(nombre: String) {
        _uiState.update { currentState ->
            currentState.copy(data = currentState.data.copy(nombre = nombre))
        }
        validateForm()
    }

    fun onEmailChange(email: String) {
        _uiState.update { currentState ->
            currentState.copy(data = currentState.data.copy(email = email))
        }
        validateForm()
    }

    fun onEdadChange(edadStr: String) {
        val edad = edadStr.toIntOrNull() ?: 0
        _uiState.update { currentState ->
            currentState.copy(data = currentState.data.copy(edad = edad))
        }
        validateForm()
    }

    fun onAceptaTerminosChange(acepta: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(data = currentState.data.copy(aceptaTerminos = acepta))
        }
        validateForm()
    }

    fun onComentarioChange(comentario: String) {
        _uiState.update { currentState ->
            currentState.copy(data = currentState.data.copy(comentario = comentario))
        }
    }

    private fun validateForm() {
        val data = _uiState.value.data
        var isValid = true

        val nameError = if (data.nombre.isBlank()) "El nombre es obligatorio." else null
        if (nameError != null) isValid = false

        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        val emailError = if (data.email.isBlank()) {
            "El correo es obligatorio."
        } else if (!data.email.matches(emailRegex)) {
            "Formato de correo inválido."
        } else null
        if (emailError != null) isValid = false

        val ageError = if (data.edad < 18 || data.edad > 99) {
            "La edad debe estar entre 18 y 99."
        } else null
        if (ageError != null) isValid = false

        val termsError = if (!data.aceptaTerminos) "Debes aceptar los términos y condiciones." else null
        if (termsError != null) isValid = false

        // Validación de Dependencia: Comentario
        var commentError: String? = null
        // Descomentar si el comentario es obligatorio al aceptar términos
        // if (data.aceptaTerminos && data.comentario.isNullOrBlank()) {
        //      commentError = "Si acepta, debe dejar un comentario (Regla de Dependencia)."
        //      isValid = false
        // }


        _uiState.update { currentState ->
            currentState.copy(
                validationState = FormValidationState(
                    nameError = nameError,
                    emailError = emailError,
                    ageError = ageError,
                    termsError = termsError,
                    commentError = commentError,
                    isFormValid = isValid
                )
            )
        }
    }

    fun submitForm(onSuccess: (FormModel) -> Unit) {
        validateForm()

        if (_uiState.value.validationState.isFormValid) {
            showSnackbar("¡Formulario enviado con éxito!", isSuccess = true)
            onSuccess(_uiState.value.data)
        } else {
            showSnackbar("Por favor, corrige los errores del formulario.", isSuccess = false)
        }
    }

    private fun showSnackbar(message: String, isSuccess: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(showSnackbar = true, snackbarMessage = message) }
            // No se usa delay aquí, el composable se encarga de ocultarlo
        }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(showSnackbar = false, snackbarMessage = "") }
    }

    init {
        validateForm()
    }
}