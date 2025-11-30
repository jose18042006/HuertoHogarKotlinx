package com.huertohogar.huertohogarkotlinx.viewmodel

import com.huertohogar.huertohogarkotlinx.data.model.ProductModel
import com.huertohogar.huertohogarkotlinx.data.repository.CatalogRepository
import com.huertohogar.huertohogarkotlinx.data.repository.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CatalogViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @RelaxedMockK
    private lateinit var catalogRepository: CatalogRepository

    private lateinit var viewModel: CatalogViewModel

    // Test data
    private val testProducts = listOf(
        ProductModel(1, "Tomate", "", 2.5, "Kg", 0, "Verduras", isOffer = true, color = androidx.compose.ui.graphics.Color.Red),
        ProductModel(2, "Lechuga", "", 1.3, "C/u", 0, "Verduras", color = androidx.compose.ui.graphics.Color.Green),
        ProductModel(3, "Manzana", "", 3.5, "Kg", 0, "Frutas", color = androidx.compose.ui.graphics.Color.Yellow)
    )

    @Before
    fun setUp() {
        coEvery { catalogRepository.getProducts() } returns testProducts
        viewModel = CatalogViewModel(catalogRepository)
    }

    @Test
    fun `state is updated correctly on successful product load`() = runTest {
        val uiState = viewModel.uiState.first()

        assertFalse(uiState.isLoading)
        assertNull(uiState.errorMessage)
        assertEquals(3, uiState.allProducts.size)
        assertEquals(1, uiState.offers.size)
        assertEquals("Tomate", uiState.allProducts.first().name)
    }

    @Test
    fun `state reflects error when repository throws exception`() = runTest {
        // Given
        val errorMessage = "Network Error"
        val expectedMessage = "Error al cargar el catálogo: $errorMessage"
        coEvery { catalogRepository.getProducts() } throws RuntimeException(errorMessage)

        // When
        viewModel = CatalogViewModel(catalogRepository) // Re-initialize to trigger load

        // Then
        val uiState = viewModel.uiState.first()
        assertFalse(uiState.isLoading)
        assertEquals(expectedMessage, uiState.errorMessage)
    }

    @Test
    fun `filterProducts updates selected category`() = runTest {
        // When
        viewModel.filterProducts("Frutas")

        // Then
        val uiState = viewModel.uiState.first()
        assertEquals("Frutas", uiState.selectedCategory)
    }

    @Test
    fun `updateSearchQuery updates the search query`() = runTest {
        // When
        viewModel.updateSearchQuery("Tomate")

        // Then
        val uiState = viewModel.uiState.first()
        assertEquals("Tomate", uiState.searchQuery)
    }

    @Test
    fun `getFilteredProducts returns products by category`() = runTest {
        // Given
        viewModel.filterProducts("Frutas")

        // When
        val filtered = viewModel.getFilteredProducts()

        // Then
        assertEquals(1, filtered.size)
        assertEquals("Manzana", filtered.first().name)
    }

    @Test
    fun `getFilteredProducts returns products by search query`() = runTest {
        // Given
        viewModel.updateSearchQuery("lechuga")

        // When
        val filtered = viewModel.getFilteredProducts()

        // Then
        assertEquals(1, filtered.size)
        assertEquals("Lechuga", filtered.first().name)
    }

    @Test
    fun `getFilteredProducts returns empty list for no match`() = runTest {
        // Given
        viewModel.updateSearchQuery("patata")

        // When
        val filtered = viewModel.getFilteredProducts()

        // Then
        assertTrue(filtered.isEmpty())
    }
}