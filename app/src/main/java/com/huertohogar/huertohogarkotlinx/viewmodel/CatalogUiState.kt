package com.huertohogar.huertohogarkotlinx.viewmodel

import com.huertohogar.huertohogarkotlinx.data.model.ProductModel

/**
 * ESTADO PRINCIPAL DEL CATÁLOGO
 * Lo separamos para evitar conflictos de importación y aseguramos el campo 'searchQuery'.
 */
data class CatalogUiState(
    val allProducts: List<ProductModel> = emptyList(),
    val offers: List<ProductModel> = emptyList(),
    val categories: List<String> = listOf("Todo", "Verduras", "Frutas", "Otros"),
    val selectedCategory: String = "Todo",
    val searchQuery: String = "", // <--- ESTE ES EL CAMPO QUE FALTABA
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)