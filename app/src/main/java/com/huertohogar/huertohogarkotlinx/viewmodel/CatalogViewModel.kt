package com.huertohogar.huertohogarkotlinx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarkotlinx.data.model.ProductModel
import com.huertohogar.huertohogarkotlinx.data.remote.dto.MealDto
import com.huertohogar.huertohogarkotlinx.data.remote.repository.recipe.RecipeRepository
import com.huertohogar.huertohogarkotlinx.data.repository.CatalogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val catalogRepository: CatalogRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState(isLoading = true))
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    private val ingredientMap = mapOf(
        "tomates" to "tomato",
        "lechuga" to "lettuce",
        "zanahorias" to "carrot",
        "manzanas" to "apple",
        "papas" to "potato",
        "naranjas" to "orange",
        "brócoli" to "broccoli",
        "fresas" to "strawberry"
    )

    // CORRECCIÓN: Añadimos "potato" a la lista de búsqueda por nombre
    private val searchByNameIngredients = setOf("apple", "strawberry", "orange", "potato")

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val products = catalogRepository.getProducts()
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

    fun filterProducts(category: String) {
        _uiState.update { it.copy(selectedCategory = category, searchQuery = "") }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun getFilteredProducts(): List<ProductModel> {
        val all = _uiState.value.allProducts
        val category = _uiState.value.selectedCategory
        val query = _uiState.value.searchQuery.trim().lowercase()

        val filteredByCategory = if (category == "Todo") all else all.filter { it.category == category }
        if (query.isBlank()) return filteredByCategory

        return filteredByCategory.filter {
            it.name.lowercase().contains(query) || it.description.lowercase().contains(query)
        }
    }

    fun loadRecipesForProduct(productName: String) {
        viewModelScope.launch {
            val searchKey = productName.split(" ").first().lowercase()
            val ingredientInEnglish = ingredientMap[searchKey] ?: return@launch

            val recipes = if (ingredientInEnglish in searchByNameIngredients) {
                recipeRepository.searchRecipesByName(ingredientInEnglish)
            } else {
                recipeRepository.getRecipesForIngredient(ingredientInEnglish)
            }

            _uiState.update { it.copy(suggestedRecipes = recipes) }
        }
    }

    fun clearRecipes() {
        _uiState.update { it.copy(suggestedRecipes = emptyList()) }
    }
}