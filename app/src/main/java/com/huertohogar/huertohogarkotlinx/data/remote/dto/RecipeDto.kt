package com.huertohogar.huertohogarkotlinx.data.remote.dto

import com.google.gson.annotations.SerializedName

// La respuesta completa de la API, que contiene una lista de comidas
data class MealsResponse(
    val meals: List<MealDto>?
)

// Representa una sola receta en la lista
data class MealDto(
    @SerializedName("idMeal")
    val id: String,

    @SerializedName("strMeal")
    val name: String,

    @SerializedName("strMealThumb")
    val imageUrl: String
)
