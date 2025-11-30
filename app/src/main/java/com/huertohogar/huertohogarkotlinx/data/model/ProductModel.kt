package com.huertohogar.huertohogarkotlinx.data.model

import androidx.compose.ui.graphics.Color

/**
 * Modelo de datos inmutable para un Producto en el catálogo de HuertoHogar.
 */
data class ProductModel(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val unit: String, // Ej: "Kg", "Unidad", "Atado"

    // CAMBIO CLAVE: Ahora almacena el ID de Recurso Local (R.drawable.xxx)
    val imageResId: Int,

    val category: String, // Ej: "Verduras", "Frutas", "Otros"
    val isOffer: Boolean = false,
    val color: Color = Color.White // Para la estética de la tarjeta en la UI
)

/**
 * Modelo de datos para un ítem específico dentro del Carrito de compras.
 */
data class CartItemModel(
    val product: ProductModel,
    val quantity: Int // La cantidad que el usuario desea comprar
)