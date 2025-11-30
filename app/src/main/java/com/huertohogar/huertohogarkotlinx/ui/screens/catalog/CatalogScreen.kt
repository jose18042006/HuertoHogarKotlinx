package com.huertohogar.huertohogarkotlinx.ui.screens.catalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.huertohogar.huertohogarkotlinx.data.model.ProductModel
import com.huertohogar.huertohogarkotlinx.ui.navigation.Screen
import com.huertohogar.huertohogarkotlinx.viewmodel.CartViewModel
import com.huertohogar.huertohogarkotlinx.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CatalogScreen(
    catalogViewModel: CatalogViewModel,
    cartViewModel: CartViewModel,
    navController: NavController
) {
    val uiState by catalogViewModel.uiState.collectAsState()

    val filteredProducts = remember(uiState.selectedCategory, uiState.searchQuery, uiState.allProducts) {
        catalogViewModel.getFilteredProducts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de Productos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.Eco,
                        contentDescription = "Logo Huerto Hogar",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(start = 8.dp).size(28.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CatalogIntroDescription(selectedCategory = uiState.selectedCategory)

            CategoryFilterBar(
                categories = uiState.allProducts.map { it.category }.distinct().let { listOf("Todo") + it },
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = catalogViewModel::filterProducts
            )

            when {
                uiState.isLoading -> SimpleLoadingState()
                uiState.errorMessage != null -> ErrorState(uiState.errorMessage!!)
                filteredProducts.isEmpty() -> EmptyCatalogState()
                else -> ProductGrid(
                    products = filteredProducts,
                    onAddToCart = cartViewModel::addItem,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun CatalogIntroDescription(selectedCategory: String) {
    val text = when (selectedCategory) {
        "Frutas"   -> "Frutas frescas y de temporada. Usa los filtros para encontrar tus favoritas."
        "Verduras" -> "Hortalizas crujientes y hojas verdes, directo del campo."
        "Otros"    -> "Productos complementarios para tu despensa saludable."
        else       -> "Explora productos orgánicos y locales. Filtra por categoría para afinar tu búsqueda."
    }
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SimpleLoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterBar(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .horizontalScroll(rememberScrollState(), enabled = true)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        // SOLUCIÓN DEFINITIVA: Color explícito en el Text
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    // Solo definimos el color del contenedor (fondo)
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun ProductGrid(
    products: List<ProductModel>,
    onAddToCart: (ProductModel) -> Unit,
    navController: NavController
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 168.dp),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(products, key = { it.id }) { product ->
            ProductCardOrganic(
                product = product,
                onAddToCart = onAddToCart,
                navController = navController
            )
        }
    }
}

@Composable
fun ProductCardOrganic(
    product: ProductModel,
    onAddToCart: (ProductModel) -> Unit,
    navController: NavController
) {
    Card(
        onClick = { navController.navigate(Screen.ProductDetail.createRoute(product.id)) },
        modifier = Modifier.fillMaxWidth().height(300.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)).background(product.color.copy(alpha = 0.15f))
            ) {
                Image(
                    painter = painterResource(id = product.imageResId),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter
                )
                Box(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(28.dp).background(Brush.verticalGradient(colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surface)))
                )
            }
            Column(
                modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(start = 14.dp, end = 70.dp, bottom = 16.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), shape = CircleShape).padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "$${product.price} / ${product.unit}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            IconButton(
                onClick = { onAddToCart(product) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp).size(44.dp)
            ) {
                Icon(imageVector = Icons.Filled.AddShoppingCart, contentDescription = "Añadir a Carrito")
            }
        }
    }
}

@Composable
fun EmptyCatalogState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("No se encontraron productos en esta categoría.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ErrorState(message: String) {
    Text(text = "Error de carga: $message", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
}
