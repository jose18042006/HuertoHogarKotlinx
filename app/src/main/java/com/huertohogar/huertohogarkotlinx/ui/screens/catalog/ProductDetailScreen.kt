package com.huertohogar.huertohogarkotlinx.ui.screens.catalog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.huertohogar.huertohogarkotlinx.data.model.ProductModel
import com.huertohogar.huertohogarkotlinx.data.remote.dto.MealDto
import com.huertohogar.huertohogarkotlinx.ui.theme.DarkGray
import com.huertohogar.huertohogarkotlinx.ui.theme.LightBrown
import com.huertohogar.huertohogarkotlinx.viewmodel.CartViewModel
import com.huertohogar.huertohogarkotlinx.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    navController: NavController,
    catalogViewModel: CatalogViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val uiState by catalogViewModel.uiState.collectAsState()
    val product = uiState.allProducts.find { it.id == productId }

    // --- EFECTOS PARA GESTIONAR LAS RECETAS ---
    LaunchedEffect(product) {
        product?.let { catalogViewModel.loadRecipesForProduct(it.name) }
    }

    DisposableEffect(Unit) {
        onDispose { catalogViewModel.clearRecipes() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: "Producto", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Volver") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = { product?.let { DetailBottomBar(product = it, onAddToCart = { cartViewModel.addItem(it) }) } }
    ) { paddingValues ->
        if (product == null) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Producto no encontrado. ID: $productId", color = MaterialTheme.colorScheme.error)
            }
        } else {
            ProductDetailContent(product, uiState.suggestedRecipes, paddingValues)
        }
    }
}

@Composable
fun ProductDetailContent(product: ProductModel, recipes: List<MealDto>, paddingValues: PaddingValues) {
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.fillMaxWidth().height(300.dp).background(product.color.copy(alpha = 0.3f))) {
            Image(painter = painterResource(id = product.imageResId), contentDescription = product.name, modifier = Modifier.fillMaxSize().padding(32.dp))
            if (product.isOffer) {
                Text(
                    text = "OFERTA 🔥", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.ExtraBold, modifier = Modifier.align(Alignment.TopEnd)
                        .background(MaterialTheme.colorScheme.error, RoundedCornerShape(bottomStart = 16.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = product.name, style = MaterialTheme.typography.headlineLarge, color = DarkGray, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "Categoría: ${product.category}", style = MaterialTheme.typography.titleMedium, color = LightBrown)
            Spacer(Modifier.height(16.dp))
            Text(text = "Descripción Detallada:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = product.description + "\n\nEste producto es cultivado de manera sostenible...", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))

            // --- SECCIÓN DE RECETAS SUGERIDAS ---
            if (recipes.isNotEmpty()) {
                SuggestedRecipesSection(recipes = recipes)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun SuggestedRecipesSection(recipes: List<MealDto>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Ideas para cocinar",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(recipes) { recipe ->
                RecipeCard(recipe = recipe)
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: MealDto) {
    Card(
        modifier = Modifier.width(160.dp), 
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(model = recipe.imageUrl),
                contentDescription = recipe.name,
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = recipe.name,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun DetailBottomBar(product: ProductModel, onAddToCart: () -> Unit) {
    Surface(shadowElevation = 10.dp, color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Precio Total:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "$${product.price} / ${product.unit}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            }
            Button(
                onClick = onAddToCart, modifier = Modifier.height(56.dp).weight(1f).padding(start = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Añadir al Carrito")
            }
        }
    }
}