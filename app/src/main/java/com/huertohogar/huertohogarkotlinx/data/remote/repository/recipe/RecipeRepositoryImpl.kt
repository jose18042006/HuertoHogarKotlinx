package com.huertohogar.huertohogarkotlinx.data.remote.repository.recipe

import com.huertohogar.huertohogarkotlinx.data.remote.RecipeApiClient
import com.huertohogar.huertohogarkotlinx.data.remote.api.RecipeApi
import com.huertohogar.huertohogarkotlinx.data.remote.dto.MealDetailDto
import com.huertohogar.huertohogarkotlinx.data.remote.dto.MealDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeRepositoryImpl(
    private val api: RecipeApi = RecipeApiClient.recipeApi
) : RecipeRepository {

    override suspend fun getRecipesForIngredient(ingredient: String): List<MealDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getRecipesByIngredient(ingredient)
                response.meals ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    override suspend fun searchRecipesByName(query: String): List<MealDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.searchRecipesByName(query)
                response.meals ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    override suspend fun getRecipeDetails(id: String): MealDetailDto? {
        return withContext(Dispatchers.IO) {
            try {
                // La API devuelve una lista, pero solo nos interesa el primer elemento.
                api.getRecipeDetails(id).meals?.firstOrNull()
            } catch (e: Exception) {
                null
            }
        }
    }
}
