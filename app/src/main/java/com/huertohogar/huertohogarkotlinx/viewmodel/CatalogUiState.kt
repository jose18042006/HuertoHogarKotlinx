package com.huertohogar.huertohogarkotlinx.viewmodel

import com.huertohogar.huertohogarkotlinx.data.model.ProductModel
import com.huertohogar.huertohogarkotlinx.data.remote.dto.MealDto

/**
 * Define el estado completo de la interfaz para las pantallas de catálogo.
 * Esta es la única fuente de verdad para el estado.
 */
data class CatalogUiState(
    val isLoading: Boolean = false,
    val allProducts: List<ProductModel> = emptyList(),
    val offers: List<ProductModel> = emptyList(),
    val selectedCategory: String = "Todo",
    val searchQuery: String = "",
    val suggestedRecipes: List<MealDto> = emptyList(),
    val errorMessage: String? = null
)
