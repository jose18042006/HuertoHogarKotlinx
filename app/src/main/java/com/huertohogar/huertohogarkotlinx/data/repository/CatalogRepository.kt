package com.huertohogar.huertohogarkotlinx.data.repository

import androidx.compose.ui.graphics.Color
import com.huertohogar.huertohogarkotlinx.data.model.ProductModel
import kotlinx.coroutines.delay
import com.huertohogar.huertohogarkotlinx.R

/**
 * Repositorio que simula la obtención de datos del catálogo (API o Base de Datos).
 */
class CatalogRepository {

    /**
     * Simula la obtención de la lista completa de productos con un retraso (delay) de red.
     */
    suspend fun getProducts(): List<ProductModel> {
        delay(1000L) // Simula el tiempo de carga de una red

        // Lista de productos de Huerto Hogar
        return listOf(
            ProductModel(
                id = 1,
                name = "Tomates Orgánicos",
                description = "Cosechados a mano, perfectos para ensaladas.",
                price = 2.50,
                unit = "Kg",
                imageResId = R.drawable.tomate,
                category = "Verduras",
                isOffer = true,
                color = Color(0xFFE57373)
            ),
            ProductModel(
                id = 2,
                name = "Lechuga Romana",
                description = "Hojas crujientes, ideal para sándwiches.",
                price = 1.3,
                unit = "C/u",
                imageResId = R.drawable.lechuga,
                category = "Verduras",
                isOffer = false,
                color = Color(0xFF81C784)
            ),
            ProductModel(
                id = 3,
                name = "Zanahorias Baby",
                description = "Dulces y perfectas para un snack.",
                price = 1.80,
                unit = "Kg",
                imageResId = R.drawable.zanahoria,
                category = "Verduras",
                isOffer = true,
                color = Color(0xFFFFCC80)
            ),
            ProductModel(
                id = 4,
                name = "Manzanas Gala",
                description = "Cosecha fresca, crujientes y ligeramente ácidas.",
                price = 3.50,
                unit = "Kg",
                imageResId = R.drawable.manzana,
                category = "Frutas",
                isOffer = false,
                color = Color(0xFFFFCDD2)
            ),
            ProductModel(
                id = 5,
                name = "Papas Orgánicas",
                description = "Alto contenido de almidón.",
                price = 1.20,
                unit = "Kg",
                imageResId = R.drawable.papas,
                category = "Otros",
                isOffer = false,
                color = Color(0xFFD7CCC8)
            ),
            ProductModel(
                id = 6,
                name = "Naranjas de Jugo",
                description = "Perfectas para un jugo matutino.",
                price = 4.00,
                unit = "Kg",
                imageResId = R.drawable.naranja,
                category = "Frutas",
                isOffer = true,
                color = Color(0xFFFFECB3)
            ),
            ProductModel(
                id = 7,
                name = "Brócoli Fresco",
                description = "Ideal para dietas saludables.",
                price = 3.00,
                unit = "C/u",
                imageResId = R.drawable.brocoli,
                category = "Verduras",
                isOffer = false,
                color = Color(0xFFB9F6CA)
            ),
            ProductModel(
                id = 8,
                name = "Fresas Orgánicas",
                description = "Dulces y grandes, de cosecha propia.",
                price = 5.50,
                unit = "Kg",
                imageResId = R.drawable.fresa,
                category = "Frutas",
                isOffer = true,
                color = Color(0xFFFF8A80)
            ),
        )
    }
}