package com.huertohogar.huertohogarkotlinx.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Importación para incluir Eco
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.huertohogar.huertohogarkotlinx.R
import com.huertohogar.huertohogarkotlinx.data.model.FormModel
import com.huertohogar.huertohogarkotlinx.ui.theme.LightBrown
import com.huertohogar.huertohogarkotlinx.viewmodel.SharedUserViewModel

/**
 * Pestaña de PERFIL: Muestra los datos del usuario logueado o un prompt para registrarse/iniciar sesión.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    sharedViewModel: SharedUserViewModel
) {
    val userData by sharedViewModel.formData.collectAsState()
    val isUserRegistered = userData != null && userData!!.nombre.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // CAMBIO CLAVE: Reemplazamos Icon(Icons.Filled.Eco...) por Image
                        Image(
                            painter = painterResource(id = R.drawable.logo), // <-- ¡Tu logo aquí!
                            contentDescription = "Logo Huerto Hogar",
                            modifier = Modifier.size(32.dp).padding(end = 8.dp)
                        )
                        Text(
                            "¡Únete al Club Huerto Hogar!",
                            // ...
                        )
                    }
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ProfileHeader(userData, isUserRegistered)

            Spacer(modifier = Modifier.height(24.dp))

            if (isUserRegistered) {
                UserInfoCard(userData!!)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { /* Lógica de cerrar sesión */ },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar Sesión", style = MaterialTheme.typography.labelLarge)
                }

            } else {
                RegistrationPrompt(navController)
            }

            Spacer(modifier = Modifier.height(24.dp))
            AccountOptionsList()
        }
    }
}

// --- Componentes Reutilizables ---

@Composable
fun ProfileHeader(userData: FormModel?, isUserRegistered: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(96.dp),
            shape = CircleShape,
            color = if (isUserRegistered) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(
                Icons.Filled.AccountCircle,
                contentDescription = "Perfil de Usuario",
                tint = if (isUserRegistered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = if (isUserRegistered) userData!!.nombre else "Invitado",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (isUserRegistered) {
            Text(
                text = userData!!.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun UserInfoCard(data: FormModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Datos de Miembro",
                style = MaterialTheme.typography.titleLarge,
                color = LightBrown,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)

            ProfileInfoRow("Edad", data.edad.toString())
            ProfileInfoRow("Aceptó Términos", if (data.aceptaTerminos) "Sí" else "No")
            ProfileInfoRow("Comentario", data.comentario ?: "N/A", isLast = true)

            TextButton(onClick = { /* Navegar a edición de perfil */ }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Editar Perfil", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String, isLast: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
    if (!isLast) {
        Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.5.dp)
    }
}

@Composable
fun RegistrationPrompt(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "¡Beneficios del Club Huerto Hogar!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Regístrate para guardar tu historial de compras, acceder a descuentos y gestionar tu huerto virtual.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { /* Lógica de Navegación a la pantalla de registro completa */ },
                modifier = Modifier.fillMaxWidth().height(40.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Crear Cuenta / Iniciar Sesión", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun AccountOptionsList() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Configuración", style = MaterialTheme.typography.titleMedium, color = LightBrown, modifier = Modifier.padding(bottom = 8.dp))

        OptionItem(label = "Mis Pedidos", onClick = { /* Navegación a historial */ })
        OptionItem(label = "Direcciones de Envío", onClick = { /* Navegación a direcciones */ })
        OptionItem(label = "Métodos de Pago", onClick = { /* Navegación a pagos */ })
    }
}

@Composable
fun OptionItem(label: String, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(label) },
        leadingContent = { Icon(Icons.Default.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        trailingContent = { Icon(Icons.Default.ArrowForward, contentDescription = null) },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
    )
    Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.5.dp)
}