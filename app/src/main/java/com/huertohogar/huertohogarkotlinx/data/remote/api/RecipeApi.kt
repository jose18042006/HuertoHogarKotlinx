package com.huertohogar.huertohogarkotlinx.data.remote.api

import com.huertohogar.huertohogarkotlinx.data.remote.dto.MealsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApi {

    /**
     * Busca recetas por un ingrediente principal (ej: tomato, potato).
     */
    @GET("api/json/v1/1/filter.php")
    suspend fun getRecipesByIngredient(@Query("i") ingredient: String): MealsResponse

    /**
     * Busca recetas por nombre (ej: apple, strawberry).
     */
    @GET("api/json/v1/1/search.php")
    suspend fun searchRecipesByName(@Query("s") query: String): MealsResponse
}