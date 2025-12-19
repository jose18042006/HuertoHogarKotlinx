package com.huertohogar.huertohogarkotlinx.data.remote.api

import com.huertohogar.huertohogarkotlinx.data.remote.dto.MealDetailResponse
import com.huertohogar.huertohogarkotlinx.data.remote.dto.MealsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApi {

    @GET("api/json/v1/1/filter.php")
    suspend fun getRecipesByIngredient(@Query("i") ingredient: String): MealsResponse

    @GET("api/json/v1/1/search.php")
    suspend fun searchRecipesByName(@Query("s") query: String): MealsResponse

    /**
     * Busca los detalles completos de una receta por su ID.
     * Ejemplo: www.themealdb.com/api/json/v1/1/lookup.php?i=52772
     */
    @GET("api/json/v1/1/lookup.php")
    suspend fun getRecipeDetails(@Query("i") id: String): MealDetailResponse
}