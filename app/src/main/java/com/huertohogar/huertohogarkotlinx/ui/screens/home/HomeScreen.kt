package com.huertohogar.huertohogarkotlinx.ui.screens.home

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale // <-- IMPORTACIÓN CLAVE
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.huertohogar.huertohogarkotlinx.data.model.ProductModel
import com.huertohogar.huertohogarkotlinx.ui.navigation.Screen
import com.huertohogar.huertohogarkotlinx.ui.theme.LightBrown
import com.huertohogar.huertohogarkotlinx.ui.theme.DarkGray
import com.huertohogar.huertohogarkotlinx.viewmodel.CatalogViewModel
import com.huertohogar.huertohogarkotlinx.viewmodel.CatalogUiState

/**
 * Pestaña de INICIO: Rediseño con Banner Fijo, Acceso Rápido y Pestañas Anidadas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    catalogViewModel: CatalogViewModel,
    navController: NavController
) {
    val uiState: CatalogUiState by catalogViewModel.uiState.collectAsState()

    val selectedTabIndex = remember { mutableStateOf(0) }
    val tabs = listOf("Ofertas", "Conócenos")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Huerto Hogar",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White, // Rojo ,
                    fontWeight = FontWeight.Bold ,
                    textAlign = TextAlign.Center ,
                    modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    Icon(
                        Icons.Filled.Eco,
                        contentDescription = "Logo Huerto Hogar",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(start = 8.dp).size(50.dp)

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

            // 1. Banner Destacado
            FeaturedCallToActionBanner()

            // 2. SECCIÓN DE ACCESO RÁPIDO A CATEGORÍAS
            QuickCategoryAccess(navController, catalogViewModel)

            // 3. TabRow (Pestañas de Navegación Interna)
            TabRow(
                selectedTabIndex = selectedTabIndex.value,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex.value == index,
                        onClick = { selectedTabIndex.value = index },
                        text = { Text(title) }
                    )
                }
            }

            // 4. Contenido basado en la Pestaña Seleccionada
            when (selectedTabIndex.value) {
                0 -> OffersContent(uiState, navController)
                1 -> AboutUsContent()
            }
        }
    }
}

// --- Componentes ---

@Composable
fun FeaturedCallToActionBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(16.dp),
        // Usamos el Amarillo Mostaza Suave como fondo
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.LocalFireDepartment,
                contentDescription = "Ofertas",
                // El tinte del ícono está bien en secondary (Amarillo)
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(50.dp).padding(end = 12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "¡LA COSECHA DE LA SEMANA!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    // CORRECCIÓN: Usamos onSecondaryContainer (Marrón Oscuro) para alto contraste
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Descuentos frescos de hasta 30%. ¡Stock Limitado!",
                    style = MaterialTheme.typography.bodyLarge,
                    // CORRECCIÓN: Usamos onSecondaryContainer para alto contraste
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun QuickCategoryAccess(navController: NavController, catalogViewModel: CatalogViewModel) {
    val categories = listOf(
        Pair("Verduras", Icons.Filled.Nature),
        Pair("Frutas", Icons.Filled.WaterDrop),
        Pair("Otros", Icons.Filled.Category)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) { (label, icon) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(80.dp)
                    .clickable {
                        navController.navigate(Screen.Catalog.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                        catalogViewModel.filterProducts(label)
                    }
            ) {
                Card(
                    shape = androidx.compose.foundation.shape.CircleShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            icon,
                            contentDescription = label,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = DarkGray
                )
            }
        }
    }
    Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
fun OffersContent(uiState: CatalogUiState, navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

        Text(
            text = "Explora nuestros productos orgánicos más vendidos:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            color = DarkGray
        )

        when {
            uiState.isLoading -> LoadingState()
            uiState.errorMessage != null -> ErrorState(uiState.errorMessage!!)
            uiState.offers.isEmpty() -> EmptyOffersState()
            else -> OffersCarousel(uiState.offers, navController)
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun OffersCarousel(offers: List<ProductModel>, navController: NavController) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(offers) { product ->
            OfferCard(product = product, navController = navController)
        }
    }
}

@Composable
fun OfferCard(product: ProductModel, navController: NavController) {
    Card(
        onClick = { navController.navigate(Screen.ProductDetail.createRoute(product.id)) },
        modifier = Modifier
            .width(180.dp) // Un poco más estrecho
            .height(260.dp),
        shape = RoundedCornerShape(16.dp), // Esquinas más redondeadas
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Fondo blanco para que destaque el color del producto
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 1. IMAGEN GRANDE
            Box(
                modifier = Modifier
                    .height(140.dp) // La mitad del espacio es para la imagen
                    .fillMaxWidth()
                    .background(product.color.copy(alpha = 0.3f)), // Fondo de color suave del producto
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = product.imageResId),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop // <-- CORRECCIÓN CLAVE: La imagen llena el contenedor
                )
                if (product.isOffer) {
                    Text(
                        "OFERTA",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(MaterialTheme.colorScheme.error, RoundedCornerShape(bottomStart = 8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // 2. CONTENIDO Y PRECIO
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))

                // Precio Grande
                Text(
                    text = "$${product.price} / ${product.unit}",
                    style = MaterialTheme.typography.headlineSmall, // Precio más grande
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


@Composable
fun AboutUsContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "🌿 Nuestra Filosofía",
            style = MaterialTheme.typography.headlineMedium,
            color = LightBrown,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Text(
            text = "En Huerto Hogar, nuestra misión es sencilla: conectar tu cocina con la naturaleza de forma sostenible y justa. Eliminamos intermediarios para que recibas productos frescos en horas, no en días. Valoramos el trabajo del agricultor y la salud de nuestros clientes.",
            style = MaterialTheme.typography.bodyLarge,
            color = DarkGray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "🤝 Comercio Justo y Local",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Cada compra apoya directamente a pequeñas granjas locales y promueve prácticas agrícolas orgánicas que benefician nuestro planeta. ¡Gracias por ser parte del cambio!",
            style = MaterialTheme.typography.bodyLarge,
            color = DarkGray
        )
    }
}

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxWidth().height(280.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun ErrorState(message: String) {
    Text("Error de carga: $message", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
}

@Composable
fun EmptyOffersState() {
    Text("No hay ofertas destacadas esta semana.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(16.dp))
}