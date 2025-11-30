package com.huertohogar.huertohogarkotlinx.ui.screens.form

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.huertohogar.huertohogarkotlinx.ui.navigation.Screen // Importación necesaria de Screen
import com.huertohogar.huertohogarkotlinx.viewmodel.FormViewModel
import com.huertohogar.huertohogarkotlinx.viewmodel.SharedUserViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla principal del formulario interactivo.
 * Este archivo se usa como ejemplo del flujo de autenticación/registro.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    navController: NavController,
    formViewModel: FormViewModel,
    sharedViewModel: SharedUserViewModel
) {
    val formState by formViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val buttonElevation by animateDpAsState(
        targetValue = if (formState.validationState.isFormValid) 8.dp else 2.dp,
        label = "ButtonElevationAnimation"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Registro Orgánico", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver al inicio")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        LaunchedEffect(formState.showSnackbar) {
            if (formState.showSnackbar) {
                snackbarHostState.showSnackbar(
                    message = formState.snackbarMessage,
                    actionLabel = "Cerrar",
                    duration = SnackbarDuration.Short
                )
                formViewModel.dismissSnackbar()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .animateContentSize(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Datos de Cultivador",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // 1. Campo Nombre (Reactivo y con Validación)
                    OutlinedTextField(
                        value = formState.data.nombre,
                        onValueChange = { formViewModel.onNombreChange(it) },
                        label = { Text("Nombre Completo") },
                        isError = formState.validationState.nameError != null,
                        supportingText = {
                            if (formState.validationState.nameError != null) {
                                Text(formState.validationState.nameError!!)
                            }
                        },
                        trailingIcon = {
                            if (formState.validationState.nameError != null) {
                                Icon(Icons.Filled.Warning, "Error", tint = MaterialTheme.colorScheme.error)
                            } else if (formState.data.nombre.isNotBlank()) {
                                Icon(Icons.Filled.Check, "Válido", tint = MaterialTheme.colorScheme.primary)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )

                    // 2. Campo Email
                    OutlinedTextField(
                        value = formState.data.email,
                        onValueChange = { formViewModel.onEmailChange(it) },
                        label = { Text("Correo Electrónico") },
                        isError = formState.validationState.emailError != null,
                        supportingText = {
                            if (formState.validationState.emailError != null) {
                                Text(formState.validationState.emailError!!)
                            }
                        },
                        trailingIcon = {
                            if (formState.validationState.emailError != null) {
                                Icon(Icons.Filled.Warning, "Error", tint = MaterialTheme.colorScheme.error)
                            } else if (formState.data.email.isNotBlank() && formState.validationState.emailError == null) {
                                Icon(Icons.Filled.Check, "Válido", tint = MaterialTheme.colorScheme.primary)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    // 3. Campo Edad
                    OutlinedTextField(
                        value = if (formState.data.edad > 0) formState.data.edad.toString() else "",
                        onValueChange = { formViewModel.onEdadChange(it.filter { char -> char.isDigit() }) },
                        label = { Text("Edad (18-99)") },
                        isError = formState.validationState.ageError != null && formState.data.edad > 0,
                        supportingText = {
                            if (formState.validationState.ageError != null) {
                                Text(formState.validationState.ageError!!)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // 4. Checkbox de Términos (Regla de Aceptación)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = formState.data.aceptaTerminos,
                            onCheckedChange = { formViewModel.onAceptaTerminosChange(it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Acepto los términos y condiciones.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (formState.validationState.termsError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // 5. Campo Comentario (Dependencia y Animación)
                    AnimatedVisibility(visible = formState.data.aceptaTerminos) {
                        OutlinedTextField(
                            value = formState.data.comentario ?: "",
                            onValueChange = { formViewModel.onComentarioChange(it) },
                            label = { Text("Comentario Adicional (Opcional)") },
                            isError = formState.validationState.commentError != null,
                            supportingText = {
                                if (formState.validationState.commentError != null) {
                                    Text(formState.validationState.commentError!!)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // 6. Botón de Envío
                    Button(
                        onClick = {
                            formViewModel.submitForm { finalData ->
                                // 1. Persistir datos en el Shared ViewModel (Room)
                                sharedViewModel.saveFormData(finalData)
                                // 2. CORRECCIÓN: Volver a la pantalla anterior (Perfil)
                                navController.popBackStack()
                            }
                        },
                        enabled = formState.validationState.isFormValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = buttonElevation)
                    ) {
                        Text(
                            text = if (formState.validationState.isFormValid) "Guardar Registro" else "Corregir Errores",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}