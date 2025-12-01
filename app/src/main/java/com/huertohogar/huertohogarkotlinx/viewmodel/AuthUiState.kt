package com.huertohogar.huertohogarkotlinx.viewmodel

data class AuthUiState(
    val token: String? = null,
    val isLoading: Boolean = false,
    val userName : String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
