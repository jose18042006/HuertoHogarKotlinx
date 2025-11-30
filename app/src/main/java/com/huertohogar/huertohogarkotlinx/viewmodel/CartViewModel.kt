package com.huertohogar.huertohogarkotlinx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarkotlinx.data.model.CartItemModel
import com.huertohogar.huertohogarkotlinx.data.model.ProductModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.map // Importación necesaria para el operador map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Estado que representa el contenido del Carrito y el total.
 */
data class CartUiState(
    val items: List<CartItemModel> = emptyList()
    // El total ya no es necesario aquí, se calcula reactivamente abajo
)

/**
 * ViewModel para gestionar la lógica del carrito de compras.
 */
class CartViewModel : ViewModel() {

    // 1. Estado principal reactivo
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    // 2. Propiedad de lectura: CANTIDAD TOTAL DE ÍTEMS EN EL CARRITO (CORRECCIÓN DEL CONTADOR)
    val totalQuantity: StateFlow<Int> = _uiState
        .map { state -> state.items.sumOf { it.quantity } } // Suma la propiedad 'quantity' de todos los ítems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )

    // 3. Propiedad de lectura: MONTO TOTAL A PAGAR (re-implementado de forma reactiva)
    val totalAmount: StateFlow<Double> = _uiState
        .map { state -> state.items.sumOf { it.product.price * it.quantity } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0.00
        )


    /** Añade un producto al carrito, o incrementa la cantidad si ya existe. */
    fun addItem(product: ProductModel) {
        _uiState.update { currentState ->
            val existingItem = currentState.items.find { it.product.id == product.id }

            val newItems = if (existingItem != null) {
                // Mapeamos la lista para reemplazar el ítem existente (garantiza inmutabilidad)
                currentState.items.map { item ->
                    if (item.product.id == product.id) {
                        item.copy(quantity = item.quantity + 1)
                    } else {
                        item
                    }
                }
            } else {
                currentState.items + CartItemModel(product = product, quantity = 1)
            }
            // Esto dispara la actualización en totalQuantity y totalAmount
            currentState.copy(items = newItems)
        }
    }

    /** Disminuye la cantidad de un ítem o lo elimina si la cantidad es 1. */
    fun removeItem(product: ProductModel) {
        _uiState.update { currentState ->
            val existingItem = currentState.items.find { it.product.id == product.id }
            val newItems = if (existingItem != null) {
                if (existingItem.quantity > 1) {
                    // Disminuir cantidad
                    currentState.items.map { item ->
                        if (item.product.id == product.id) item.copy(quantity = item.quantity - 1) else item
                    }
                } else {
                    // Eliminar ítem completamente
                    currentState.items.filter { it.product.id != product.id }
                }
            } else {
                currentState.items
            }
            currentState.copy(items = newItems)
        }
    }

    /** Elimina todos los ítems del carrito. */
    fun clearCart() {
        _uiState.update { CartUiState() }
    }
}
