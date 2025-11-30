package com.huertohogar.huertohogarkotlinx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarkotlinx.data.model.ProductModel
import com.huertohogar.huertohogarkotlinx.data.repository.CatalogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar el Catálogo de Productos (Home y Catálogo).
 */
class CatalogViewModel(private val repository: CatalogRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState(isLoading = true))
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    /** Carga los productos del repositorio de forma asíncrona. */
    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val products = repository.getProducts()
                _uiState.update { currentState ->
                    currentState.copy(
                        allProducts = products,
                        offers = products.filter { it.isOffer },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar el catálogo: ${e.message}") }
            }
        }
    }

    // Función para actualizar el filtro de categoría
    fun filterProducts(category: String) {
        // Al cambiar de categoría, limpiamos la búsqueda
        _uiState.update { it.copy(selectedCategory = category, searchQuery = "") }
    }

    // NUEVA FUNCIÓN: Actualiza el Query de Búsqueda
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    // FUNCIÓN DE BÚSQUEDA Y FILTRADO COMBINADO (CORRIGE AMBIGÜEDAD)
    fun getFilteredProducts(): List<ProductModel> {
        val all = _uiState.value.allProducts
        val category = _uiState.value.selectedCategory
        // La consulta de búsqueda está limpia y en minúsculas
        val query = _uiState.value.searchQuery.trim().lowercase()

        // 1. Filtrar por Categoría
        val filteredByCategory = if (category == "Todo") {
            all
        } else {
            all.filter { it.category == category }
        }

        // Si la búsqueda está vacía, regresamos solo el filtro por categoría
        if (query.isBlank()) {
            return filteredByCategory
        }

        // 2. Filtrar por Consulta (Resuelve la ambigüedad con argumentos explícitos)
        return filteredByCategory.filter { product ->
            product.name.lowercase().contains(query, ignoreCase = false) ||
                    product.description.lowercase().contains(query, ignoreCase = false)
        }
    }
}