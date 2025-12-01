package com.huertohogar.huertohogarkotlinx.ui.screens.profile

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.huertohogar.huertohogarkotlinx.R
import com.huertohogar.huertohogarkotlinx.data.model.FormModel
import com.huertohogar.huertohogarkotlinx.ui.theme.LightBrown
import com.huertohogar.huertohogarkotlinx.viewmodel.AuthUiState
import com.huertohogar.huertohogarkotlinx.viewmodel.AuthViewModel
import com.huertohogar.huertohogarkotlinx.viewmodel.SharedUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    sharedViewModel: SharedUserViewModel
) {
    val userData by sharedViewModel.formData.collectAsState()
    val authViewModel: AuthViewModel = viewModel()
    val authState = authViewModel.uiState
    val isLoggedIn = !authState.token.isNullOrEmpty()

    var showPictureDialog by remember { mutableStateOf(false) }

    // --- LÓGICA DE CÁMARA --- 
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap: Bitmap? -> sharedViewModel.onProfilePictureTaken(bitmap) }
    )
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean -> if (isGranted) cameraLauncher.launch(null) }
    )

    if (showPictureDialog) {
        AlertDialog(
            onDismissRequest = { showPictureDialog = false },
            title = { Text("Foto de Perfil") },
            text = { Text("Elige una opción") },
            confirmButton = {
                Column(Modifier.fillMaxWidth()) {
                    TextButton(onClick = {
                        showPictureDialog = false
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }) { Text("Tomar una nueva foto", color = MaterialTheme.colorScheme.primary) }
                    if (userData?.profileImageUri != null) {
                        TextButton(onClick = {
                            showPictureDialog = false
                            sharedViewModel.deleteProfilePicture()
                        }) { Text("Eliminar foto actual", color = MaterialTheme.colorScheme.error) }
                    }
                }
            },
            dismissButton = { TextButton(onClick = { showPictureDialog = false }) { Text("Cancelar", color = MaterialTheme.colorScheme.primary) } }
        )
    }

    Scaffold(
        topBar = { /* ... Tu TopAppBar sin cambios ... */ },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isLoggedIn) {
                AuthSection(authViewModel = authViewModel, state = authState)
            } else {
                val displayName = authState.userName ?: userData?.nombre ?: "Invitado"
                val displayEmail = userData?.email.orEmpty()
                val hasExtraFormData = userData != null && userData!!.nombre.isNotBlank()

                // ---------- CABECERA DE PERFIL MEJORADA ----------
                ProfileHeader(
                    name = displayName,
                    email = displayEmail,
                    profileImageUri = userData?.profileImageUri,
                    onImageClick = { showPictureDialog = true } // <-- CONECTADO
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (hasExtraFormData && userData != null) {
                    UserInfoCard(userData!!)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = { authViewModel.logout() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Filled.Logout, contentDescription = "Cerrar Sesión")
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar Sesión", style = MaterialTheme.typography.labelLarge)
                }

                Spacer(modifier = Modifier.height(24.dp))
                AccountOptionsList()
            }
        }
    }
}


@Composable
fun AuthSection(authViewModel: AuthViewModel, state: AuthUiState) {
    var selectedTab by remember { mutableStateOf(0) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // En la sección de login, no hay foto de perfil clicable.
        ProfileHeader(name = "Invitado", email = "", profileImageUri = null, onImageClick = {}) 
        Spacer(modifier = Modifier.height(24.dp))
        TabRow(selectedTabIndex = selectedTab, modifier = Modifier.fillMaxWidth()) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Iniciar sesión") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Registrarse") })
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
        state.errorMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
        state.successMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it)
        }
    }
}

// ... Tus LoginForm y RegisterForm sin cambios ...
@Composable
fun LoginForm(authViewModel: AuthViewModel, state: AuthUiState) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { authViewModel.login(email, password) }, enabled = !state.isLoading, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(8.dp)) {
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
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { authViewModel.register(name, email, password) }, enabled = !state.isLoading, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(8.dp)) {
            Text("Crear cuenta", style = MaterialTheme.typography.labelLarge)
        }
    }
}


// ---------- CABECERA DE PERFIL MODIFICADA ----------
@Composable
fun ProfileHeader(name: String, email: String, profileImageUri: String?, onImageClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomEnd) {
            val placeholderPainter = rememberVectorPainter(image = Icons.Default.AccountCircle)
            val painter = rememberAsyncImagePainter(model = profileImageUri, error = placeholderPainter, fallback = placeholderPainter)

            Image(
                painter = painter,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable(onClick = onImageClick),
                contentScale = ContentScale.Crop
            )
            // Solo mostrar el icono de la cámara si la imagen es clicable
            if (onImageClick != {}) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp).offset(x = (-4).dp, y = (-4).dp).background(MaterialTheme.colorScheme.surface, CircleShape).padding(4.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(text = name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        if (email.isNotBlank()) {
            Text(text = email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ... El resto de tus componentes (UserInfoCard, etc.) sin cambios ...
@Composable
fun UserInfoCard(data: FormModel) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Datos de Miembro", style = MaterialTheme.typography.titleLarge, color = LightBrown, modifier = Modifier.padding(bottom = 8.dp))
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
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
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
