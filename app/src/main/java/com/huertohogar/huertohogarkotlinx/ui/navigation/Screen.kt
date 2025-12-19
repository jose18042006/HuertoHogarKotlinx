package com.huertohogar.huertohogarkotlinx.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Catalog : Screen("catalog")
    object Cart : Screen("cart")
    object Profile : Screen("profile")

    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: Int) = "product_detail/$productId"
    }

    // --- NUEVA RUTA ---
    object RecipeDetail : Screen("recipe_detail/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_detail/$recipeId"
    }
}