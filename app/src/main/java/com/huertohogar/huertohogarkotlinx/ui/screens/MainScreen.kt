package com.huertohogar.huertohogarkotlinx.ui.screens
import com.huertohogar.huertohogarkotlinx.ui.screens.auth.LoginScreen
import com.huertohogar.huertohogarkotlinx.ui.screens.auth.RegisterScreen
import android.app.Application
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.huertohogar.huertohogarkotlinx.data.repository.CatalogRepository
import com.huertohogar.huertohogarkotlinx.ui.components.WelcomePopup
import com.huertohogar.huertohogarkotlinx.ui.navigation.AppWindowSizeClass
import com.huertohogar.huertohogarkotlinx.ui.navigation.Screen
import com.huertohogar.huertohogarkotlinx.ui.screens.cart.CartScreen
import com.huertohogar.huertohogarkotlinx.ui.screens.catalog.CatalogScreen
import com.huertohogar.huertohogarkotlinx.ui.screens.catalog.ProductDetailScreen
import com.huertohogar.huertohogarkotlinx.ui.screens.home.HomeScreen
import com.huertohogar.huertohogarkotlinx.viewmodel.CartViewModel
import com.huertohogar.huertohogarkotlinx.viewmodel.CatalogViewModel
import com.huertohogar.huertohogarkotlinx.viewmodel.SharedUserViewModel
import com.huertohogar.huertohogarkotlinx.ui.screens.profile.ProfileScreen
data class NavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    NavItem(Screen.Home.route, Icons.Filled.Home, "Inicio"),
    NavItem(Screen.Catalog.route, Icons.Filled.LocalMall, "Catálogo"),
    NavItem(Screen.Cart.route, Icons.Filled.ShoppingCart, "Carrito"),
    NavItem(Screen.Profile.route, Icons.Filled.Person, "Perfil")
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun MainScreen(
    appNavHostController: NavHostController,
    windowSizeClass: AppWindowSizeClass,
    sharedViewModelFactory: ViewModelProvider.Factory
) {
    val sharedViewModel: SharedUserViewModel = viewModel(factory = sharedViewModelFactory)
    val catalogRepository = CatalogRepository()
    val catalogViewModelFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return CatalogViewModel(catalogRepository) as T
        }
    }
    val catalogViewModel: CatalogViewModel = viewModel(factory = catalogViewModelFactory)
    val cartViewModel: CartViewModel = viewModel()
    val nestedNavController = rememberNavController()
    val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val showNavRail = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    val showWelcomePopup by sharedViewModel.showWelcomePopup.collectAsState()

    Row(modifier = Modifier.fillMaxSize()) {
        if (showNavRail) {
            AppNavigationRail(
                navController = nestedNavController,
                currentRoute = currentRoute,
                items = bottomNavItems
            )
            Divider(modifier = Modifier.fillMaxHeight().width(1.dp))
        }

        Scaffold(
            modifier = Modifier.weight(1f),
            bottomBar = {
                if (showBottomBar) {
                    AppBottomNavigationBar(
                        navController = nestedNavController,
                        currentRoute = currentRoute,
                        items = bottomNavItems,
                        cartViewModel = cartViewModel
                    )
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                if (showWelcomePopup) {
                    WelcomePopup(
                        sharedViewModel = sharedViewModel,
                        onNavigateToProfile = {
                            nestedNavController.navigate(Screen.Profile.route) {
                                popUpTo(nestedNavController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }

                NavHost(
                    navController = nestedNavController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(catalogViewModel = catalogViewModel, navController = nestedNavController)
                    }
                    composable(Screen.Catalog.route) {
                        CatalogScreen(catalogViewModel = catalogViewModel, cartViewModel = cartViewModel, navController = nestedNavController)
                    }
                    composable(Screen.Cart.route) {
                        CartScreen(cartViewModel = cartViewModel, navController = nestedNavController)
                    }
                    composable(Screen.Profile.route) {
                        com.huertohogar.huertohogarkotlinx.ui.screens.profile.ProfileScreen(
                            navController = nestedNavController,
                            sharedViewModel = sharedViewModel
                        )
                    }
                    composable(
                        route = Screen.ProductDetail.route,
                        arguments = listOf(navArgument("productId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                        ProductDetailScreen(
                            productId = productId,
                            navController = nestedNavController,
                            catalogViewModel = catalogViewModel,
                            cartViewModel = cartViewModel
                        )
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomNavigationBar(
    navController: NavHostController,
    currentRoute: String?,
    items: List<NavItem>,
    cartViewModel: CartViewModel
) {
    val cartItemCount by cartViewModel.totalQuantity.collectAsState(initial = 0)
    var triggerAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(cartItemCount) {
        if (cartItemCount > 0) {
            triggerAnimation = true
            kotlinx.coroutines.delay(250)
            triggerAnimation = false
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (triggerAnimation || (currentRoute == Screen.Cart.route && cartItemCount > 0)) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "CartIconScale"
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.height(80.dp)
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            modifier = if (item.route == Screen.Cart.route) Modifier.scale(scale).size(24.dp) else Modifier.size(24.dp)
                        )
                        if (item.route == Screen.Cart.route && cartItemCount > 0) {
                            Badge(modifier = Modifier.align(Alignment.TopEnd).offset(x = 4.dp, y = (-4).dp)) {
                                Text(text = cartItemCount.toString(), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                },
                label = { Text(item.label) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun AppNavigationRail(
    navController: NavHostController,
    currentRoute: String?,
    items: List<NavItem>
) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface,
        header = {
            Icon(
                Icons.Filled.Eco,
                contentDescription = "Huerto Hogar Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 12.dp).size(36.dp)
            )
        },
        modifier = Modifier.fillMaxHeight()
    ) {
        Spacer(Modifier.height(8.dp))
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationRailItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}