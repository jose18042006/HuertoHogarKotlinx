package com.huertohogar.huertohogarkotlinx.ui.screens.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.huertohogar.huertohogarkotlinx.data.model.CartItemModel
import com.huertohogar.huertohogarkotlinx.data.model.ProductModel
import com.huertohogar.huertohogarkotlinx.ui.navigation.Screen
import com.huertohogar.huertohogarkotlinx.ui.theme.DarkGray
import com.huertohogar.huertohogarkotlinx.viewmodel.CartViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(cartViewModel: CartViewModel, navController: NavController) { 
    val uiState by cartViewModel.uiState.collectAsState()
    val cartItems = uiState.items
    val totalAmount by cartViewModel.totalAmount.collectAsState(initial = 0.0)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val df = remember { DecimalFormat("0.00") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Icon(
                        Icons.Filled.Eco,
                        contentDescription = "Logo Huerto Hogar",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(start = 8.dp).size(28.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
                // PAPELERA SUPERIOR ELIMINADA
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = cartItems.isNotEmpty(),
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                CartSummaryBar(
                    total = totalAmount,
                    df = df,
                    onCheckout = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Implementar la pasarela de pago para ${df.format(totalAmount)} €",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    onClearCart = cartViewModel::clearCart
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            EmptyCartState(
                modifier = Modifier.padding(paddingValues),
                onBrowseCatalog = { 
                    navController.navigate(Screen.Catalog.route) { 
                        popUpTo(navController.graph.startDestinationId)
                    }
                }
            )
        } else {
            CartItemList(
                modifier = Modifier.padding(paddingValues).fillMaxSize(),
                cartItems = cartItems,
                onAdd = cartViewModel::addItem,
                onRemove = cartViewModel::removeItem
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CartItemList(
    modifier: Modifier = Modifier,
    cartItems: List<CartItemModel>,
    onAdd: (ProductModel) -> Unit,
    onRemove: (ProductModel) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = cartItems,
            key = { it.product.id }
        ) { cartItem ->
            CartItemCard(
                cartItem = cartItem,
                onAdd = { onAdd(cartItem.product) },
                onRemove = { onRemove(cartItem.product) },
                modifier = Modifier.animateItemPlacement(animationSpec = tween(durationMillis = 300))
            )
            Divider(modifier = Modifier.padding(top = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItemModel,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val product = cartItem.product
    val df = remember { DecimalFormat("0.00") }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = product.imageResId),
            contentDescription = product.name,
            modifier = Modifier.size(60.dp).background(product.color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DarkGray)
            Text(text = "$${df.format(product.price * cartItem.quantity)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onRemove, enabled = cartItem.quantity > 0, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Filled.Remove, contentDescription = "Quitar uno", tint = MaterialTheme.colorScheme.error)
            }
            Text(text = "${cartItem.quantity}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(onClick = onAdd, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir uno", tint = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(Modifier.width(16.dp))
        Text(text = "$${df.format(product.price)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.width(80.dp))
    }
}

@Composable
fun CartSummaryBar(
    total: Double,
    df: DecimalFormat,
    onCheckout: () -> Unit,
    onClearCart: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Total a Pagar:", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                Text(text = "$${df.format(total)}", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onCheckout,
                    modifier = Modifier.weight(1.8f).height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.ShoppingCartCheckout, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Pagar Ahora", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onClearCart,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Vaciar Carrito", modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Vaciar")
                }
            }
        }
    }
}

@Composable
fun EmptyCartState(modifier: Modifier = Modifier, onBrowseCatalog: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ShoppingCart,
            contentDescription = "Carrito Vacío",
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Tu carrito está esperando",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "¡Añade productos frescos para empezar a llenarlo!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onBrowseCatalog,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
        ) {
            Text("Explorar Catálogo", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}