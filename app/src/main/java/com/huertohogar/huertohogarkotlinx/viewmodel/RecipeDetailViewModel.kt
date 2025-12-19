package com.huertohogar.huertohogarkotlinx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarkotlinx.data.remote.dto.MealDetailDto
import com.huertohogar.huertohogarkotlinx.data.remote.repository.recipe.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeDetailUiState(
    val isLoading: Boolean = true,
    val recipe: MealDetailDto? = null,
    val errorMessage: String? = null
)

class RecipeDetailViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    fun loadRecipeDetails(recipeId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val recipeDetails = recipeRepository.getRecipeDetails(recipeId)
            if (recipeDetails != null) {
                _uiState.update { it.copy(isLoading = false, recipe = recipeDetails) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "No se pudieron cargar los detalles de la receta.") }
            }
        }
    }
}