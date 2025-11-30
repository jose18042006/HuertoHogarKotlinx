package com.huertohogar.huertohogarkotlinx.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CatalogRepositoryTest {

    private lateinit var catalogRepository: CatalogRepository

    @Before
    fun setUp() {
        catalogRepository = CatalogRepository()
    }

    @Test
    fun `getProducts returns a non-empty list of products`() = runTest {
        // When
        val products = catalogRepository.getProducts()

        // Then
        assertTrue(products.isNotEmpty())
        assertEquals(8, products.size)
    }

    @Test
    fun `getProducts returns list with correct data`() = runTest {
        // When
        val products = catalogRepository.getProducts()

        // Then
        val firstProduct = products.first()
        assertEquals(1, firstProduct.id)
        assertEquals("Tomates Orgánicos", firstProduct.name)
        assertEquals("Verduras", firstProduct.category)
    }
}