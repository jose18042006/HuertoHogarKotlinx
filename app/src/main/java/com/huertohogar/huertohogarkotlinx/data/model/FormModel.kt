package com.huertohogar.huertohogarkotlinx.data.model

data class FormModel(
    val nombre: String = "",
    val email: String = "",
    val edad: Int = 0,
    val aceptaTerminos: Boolean = false,
    val comentario: String? = null,
    val profileImageUri: String? = null // <- AÑADIDO
)

data class FormValidationState(
    val nameError: String? = null,
    val emailError: String? = null,
    val ageError: String? = null,
    val termsError: String? = null,
    val commentError: String? = null,
    val isFormValid: Boolean = false
)