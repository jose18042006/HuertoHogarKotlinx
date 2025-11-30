package com.huertohogar.huertohogarkotlinx.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar // <--- IMPORTACIÓN CRÍTICA
import androidx.compose.material3.TopAppBarDefaults // <--- IMPORTACIÓN CRÍTICA
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.huertohogar.huertohogarkotlinx.viewmodel.SharedUserViewModel
import com.huertohogar.huertohogarkotlinx.ui.theme.LightBrown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    sharedViewModel: SharedUserViewModel = viewModel()
) {
    val formData by sharedViewModel.formData.collectAsState()

    Scaffold(
        // Usamos TopAppBar de Material 3
        topBar = {
            TopAppBar(
                title = { Text("Resumen de Datos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                // Usamos TopAppBarDefaults.topAppBarColors de Material 3
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (formData == null || formData!!.nombre.isBlank()) {
            EmptyState(paddingValues)
        } else {
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
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Detalles del Registro",
                            style = MaterialTheme.typography.headlineMedium,
                            color = LightBrown,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        DetailItem(label = "Nombre de Usuario:", value = formData!!.nombre)
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        DetailItem(label = "Correo Electrónico:", value = formData!!.email)
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        DetailItem(label = "Edad:", value = formData!!.edad.toString())
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        DetailItem(
                            label = "¿Aceptó Términos?",
                            value = if (formData!!.aceptaTerminos) "Sí, aceptado" else "No aceptado",
                            valueColor = if (formData!!.aceptaTerminos) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        if (formData!!.aceptaTerminos) {
                            DetailItem(
                                label = "Comentario Adicional:",
                                value = if (formData!!.comentario.isNullOrBlank()) "No se añadió comentario" else formData!!.comentario!!
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = LightBrown,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = valueColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun EmptyState(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Error,
            contentDescription = "Sin Datos",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Sin Datos de Formulario",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Navega de vuelta y envía el formulario primero para ver los detalles.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}