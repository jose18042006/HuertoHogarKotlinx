package com.huertohogar.huertohogarkotlinx.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.huertohogar.huertohogarkotlinx.R
import com.huertohogar.huertohogarkotlinx.data.model.FormModel
import com.huertohogar.huertohogarkotlinx.ui.theme.LightBrown
import com.huertohogar.huertohogarkotlinx.viewmodel.AuthUiState
import com.huertohogar.huertohogarkotlinx.viewmodel.AuthViewModel
import com.huertohogar.huertohogarkotlinx.viewmodel.SharedUserViewModel

/**
 * PERFIL
 * - Si NO hay sesión (token nulo): muestra login / registro con API REST.
 * - Si hay sesión (token != null): muestra datos de usuario + opciones.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    sharedViewModel: SharedUserViewModel
) {
    val userData by sharedViewModel.formData.collectAsState()

    // ViewModel de autenticación (API REST)
    val authViewModel: AuthViewModel = viewModel()
    val authState = authViewModel.uiState
    val isLoggedIn = !authState.token.isNullOrEmpty()

    val displayName = authState.userName ?: userData?.nombre ?: "Invitado"
    // Como AuthUiState no tiene email, usamos solo el del formulario local
    val displayEmail = userData?.email.orEmpty()
    val hasExtraFormData = userData != null && userData!!.nombre.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo Huerto Hogar",
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 8.dp)
                        )
                        Text("Mi Cuenta Huerto Hogar")
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

            if (!isLoggedIn) {
                // ---------- NO LOGUEADO: mostrar login / registro ----------
                AuthSection(authViewModel = authViewModel, state = authState)
            } else {
                // ---------- LOGUEADO: mostrar perfil ----------
                ProfileHeader(
                    name = displayName,
                    email = displayEmail,
                    isLoggedIn = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (hasExtraFormData && userData != null) {
                    UserInfoCard(userData!!)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        authViewModel.logout() // asumiendo que ya lo tienes
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar Sesión", style = MaterialTheme.typography.labelLarge)
                }

                Spacer(modifier = Modifier.height(24.dp))

                AccountOptionsList()
            }
        }
    }
}

/* ---------- SECCIÓN LOGIN / REGISTER ---------- */

@Composable
fun AuthSection(authViewModel: AuthViewModel, state: AuthUiState) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = login, 1 = register

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ProfileHeader(
            name = "Invitado",
            email = "",
            isLoggedIn = false
        )

        Spacer(modifier = Modifier.height(24.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Iniciar sesión") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Registrarse") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            LoginForm(authViewModel = authViewModel, state = state)
        } else {
            RegisterForm(authViewModel = authViewModel, state = state)
        }

        if (state.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        state.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        state.successMessage?.let { msg ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = msg)
        }
    }
}

@Composable
fun LoginForm(authViewModel: AuthViewModel, state: AuthUiState) {
    // AuthUiState no guarda email; lo manejamos solo aquí
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { authViewModel.login(email, password) },
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Entrar", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun RegisterForm(authViewModel: AuthViewModel, state: AuthUiState) {
    var name by remember { mutableStateOf(state.userName ?: "") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { authViewModel.register(name, email, password) },
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Crear cuenta", style = MaterialTheme.typography.labelLarge)
        }
    }
}

/* ---------- COMPONENTES REUTILIZABLES (perfil logueado) ---------- */

@Composable
fun ProfileHeader(name: String, email: String, isLoggedIn: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(96.dp),
            shape = CircleShape,
            color = if (isLoggedIn) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(
                Icons.Filled.AccountCircle,
                contentDescription = "Perfil de Usuario",
                tint = if (isLoggedIn) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (isLoggedIn && email.isNotBlank()) {
            Text(
                text = email,
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

            TextButton(onClick = { /* Navegar a edición de perfil (tu FormScreen) */ }) {
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
fun AccountOptionsList() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Configuración",
            style = MaterialTheme.typography.titleMedium,
            color = LightBrown,
            modifier = Modifier.padding(bottom = 8.dp)
        )

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
        leadingContent = {
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = { Icon(Icons.Default.ArrowForward, contentDescription = null) },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
    )
    Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.5.dp)
}
