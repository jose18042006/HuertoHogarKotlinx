package com.huertohogar.huertohogarkotlinx.viewmodel

/**
 * Define el estado completo de la UI para la autenticación.
 * Esta es la única fuente de verdad para este estado.
 */
data class AuthUiState(
    val token: String? = null,
    val userName: String? = null,
    val userRole: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
