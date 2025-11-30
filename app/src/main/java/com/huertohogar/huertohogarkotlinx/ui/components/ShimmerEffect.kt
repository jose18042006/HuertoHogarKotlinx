package com.huertohogar.huertohogarkotlinx.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Plantilla de Tarjeta Shimmer para simular la carga de productos en cuadrícula.
 */
@Composable
fun ShimmerCardPlaceholder(brush: Brush) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        // Placeholder de imagen
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(brush, RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Placeholder de línea de texto 1 (Título)
        Spacer(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(20.dp)
                .background(brush, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Placeholder de línea de texto 2 (Precio)
        Spacer(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(16.dp)
                .background(brush, RoundedCornerShape(4.dp))
        )
    }
}

/**
 * Componente reutilizable que aplica el efecto Shimmer (esqueleto de carga).
 */
@Composable
fun ShimmerAnimation(
    isLoading: Boolean,
    contentAfterLoading: @Composable () -> Unit,
    loadingContent: @Composable (brush: Brush) -> Unit
) {
    if (isLoading) {
        val transition = rememberInfiniteTransition(label = "shimmerTransition")
        val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1000,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmerTranslation"
        )

        val shimmerColors = listOf(
            Color.LightGray.copy(alpha = 0.9f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.9f),
        )

        val brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(10f, 10f),
            end = Offset(translateAnim, translateAnim)
        )

        loadingContent(brush)

    } else {
        contentAfterLoading()
    }
}

/**
 * Componente que envuelve el Shimmer para el grid de productos.
 * ESTA FUNCIÓN DEBE RESOLVER EL ERROR DE IMPORTACIÓN
 */
@Composable
fun ShimmerLoadingGrid() {
    ShimmerAnimation(
        isLoading = true,
        contentAfterLoading = { /* No usado aquí */ },
        loadingContent = { brush ->
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(6) {
                    ShimmerCardPlaceholder(brush)
                }
            }
        }
    )
}