package com.huertohogar.huertohogarkotlinx.data.remote.repository.recipe

import com.huertohogar.huertohogarkotlinx.data.remote.dto.MealDetailDto
import com.huertohogar.huertohogarkotlinx.data.remote.dto.MealDto

interface RecipeRepository {

    suspend fun getRecipesForIngredient(ingredient: String): List<MealDto>

    suspend fun searchRecipesByName(query: String): List<MealDto>

    /**
     * Busca los detalles completos de una receta por su ID.
     * Devuelve el objeto de detalle o null si no se encuentra o hay un error.
     */
    suspend fun getRecipeDetails(id: String): MealDetailDto?
}
