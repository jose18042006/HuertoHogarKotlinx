package com.huertohogar.huertohogarkotlinx.data.remote.repository.recipe

import com.huertohogar.huertohogarkotlinx.data.remote.dto.MealDto

interface RecipeRepository {
    /**
     * Obtiene una lista de recetas para un ingrediente específico.
     */
    suspend fun getRecipesForIngredient(ingredient: String): List<MealDto>

    /**
     * Busca recetas que coincidan con un nombre o término de búsqueda.
     */
    suspend fun searchRecipesByName(query: String): List<MealDto>
}
