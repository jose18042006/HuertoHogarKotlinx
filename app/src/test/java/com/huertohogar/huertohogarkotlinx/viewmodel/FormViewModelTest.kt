package com.huertohogar.huertohogarkotlinx.viewmodel

import com.huertohogar.huertohogarkotlinx.data.model.FormModel
import com.huertohogar.huertohogarkotlinx.data.repository.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FormViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: FormViewModel

    @Before
    fun setUp() {
        viewModel = FormViewModel()
    }

    @Test
    fun `initial state shows validation errors`() = runTest {
        val validationState = viewModel.uiState.first().validationState

        assertFalse(validationState.isFormValid)
        assertNotNull(validationState.nameError)
        assertNotNull(validationState.emailError)
        assertNotNull(validationState.ageError)
        assertNotNull(validationState.termsError)
    }

    @Test
    fun `onNombreChange updates name and validation`() = runTest {
        viewModel.onNombreChange("John Doe")
        val state = viewModel.uiState.first()

        assertEquals("John Doe", state.data.nombre)
        assertNull(state.validationState.nameError)
    }

    @Test
    fun `onEmailChange with invalid email shows error`() = runTest {
        viewModel.onEmailChange("invalid-email")
        val state = viewModel.uiState.first()

        assertEquals("invalid-email", state.data.email)
        assertNotNull(state.validationState.emailError)
        assertEquals("Formato de correo inválido.", state.validationState.emailError)
    }

    @Test
    fun `onEdadChange with valid age updates state`() = runTest {
        viewModel.onEdadChange("30")
        val state = viewModel.uiState.first()

        assertEquals(30, state.data.edad)
        assertNull(state.validationState.ageError)
    }

    @Test
    fun `onAceptaTerminosChange updates terms and validation`() = runTest {
        viewModel.onAceptaTerminosChange(true)
        val state = viewModel.uiState.first()

        assertTrue(state.data.aceptaTerminos)
        assertNull(state.validationState.termsError)
    }

    @Test
    fun `submitForm fails when form is invalid`() = runTest {
        var wasOnSuccessCalled = false
        viewModel.submitForm { wasOnSuccessCalled = true }

        val state = viewModel.uiState.first()
        assertFalse(wasOnSuccessCalled)
        assertTrue(state.showSnackbar)
        assertEquals("Por favor, corrige los errores del formulario.", state.snackbarMessage)
    }

    @Test
    fun `submitForm succeeds when form is valid`() = runTest {
        // Given: Fill the form with valid data
        viewModel.onNombreChange("Jane Doe")
        viewModel.onEmailChange("jane.doe@example.com")
        viewModel.onEdadChange("25")
        viewModel.onAceptaTerminosChange(true)

        // When: Submit the form
        var receivedData: FormModel? = null
        viewModel.submitForm { data -> receivedData = data }

        // Then: Check success state
        val state = viewModel.uiState.first()
        assertNotNull(receivedData)
        assertEquals("Jane Doe", receivedData?.nombre)
        assertTrue(state.showSnackbar)
        assertEquals("¡Formulario enviado con éxito!", state.snackbarMessage)
    }

    @Test
    fun `dismissSnackbar hides the snackbar`() = runTest {
        // First, trigger snackbar
        viewModel.submitForm { }
        assertTrue(viewModel.uiState.first().showSnackbar)

        // Then, dismiss it
        viewModel.dismissSnackbar()
        assertFalse(viewModel.uiState.first().showSnackbar)
    }
}