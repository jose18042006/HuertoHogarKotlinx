package com.huertohogar.huertohogarkotlinx.ui.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

data class AppWindowSizeClass(
    val widthSizeClass: WindowWidthSizeClass
)

// Rutas selladas de la Aplicación
sealed class Screen(val route: String) {
    data object Home : Screen("home_screen")
    data object Catalog : Screen("catalog_screen")
    data object Cart : Screen("cart_screen")
    data object Profile : Screen("profile_screen")

    // DEFINICIÓN CRÍTICA DE LA RUTA CON ARGUMENTO
    object ProductDetail : Screen("product_detail/{productId}") {
        // Función auxiliar para construir la ruta con el ID real del producto
        fun createRoute(productId: Int) = "product_detail/$productId"
    }
}