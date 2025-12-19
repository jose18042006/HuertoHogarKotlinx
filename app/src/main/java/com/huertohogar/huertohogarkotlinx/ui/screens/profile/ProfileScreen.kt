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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.huertohogar.huertohogarkotlinx.ui.navigation.Screen
import com.huertohogar.huertohogarkotlinx.viewmodel.AuthUiState
import com.huertohogar.huertohogarkotlinx.viewmodel.AuthViewModel
import com.huertohogar.huertohogarkotlinx.viewmodel.SharedUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    sharedViewModel: SharedUserViewModel,
    authViewModel: AuthViewModel
) {
    val userData by sharedViewModel.formData.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val isLoggedIn = !authState.token.isNullOrEmpty()

    var showPictureDialog by remember { mutableStateOf(false) }

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
            dismissButton = { TextButton(onClick = { showPictureDialog = false }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isLoggedIn) {
                AuthSection(authViewModel = authViewModel, state = authState)
            } else {
                val displayName = authState.userName ?: "Invitado"
                // --- ¡LÍNEA CORREGIDA! ---
                val displayEmail = userData?.email.orEmpty()

                ProfileHeader(
                    name = displayName,
                    email = displayEmail,
                    profileImageUri = userData?.profileImageUri,
                    onImageClick = { showPictureDialog = true }
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                UserInfoCard(displayName, displayEmail, authState.userRole ?: "Desconocido")
                
                Spacer(modifier = Modifier.height(24.dp))

                if (authState.userRole == "ROLE_ADMIN" || authState.userRole == "ROLE_EMPLOYEE") {
                    AdminPanelButton(navController = navController)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                AccountOptionsList()
                
                Spacer(modifier = Modifier.height(24.dp))

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
            }
        }
    }
}

@Composable
fun UserInfoCard(username: String, email: String, role: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Información de la Cuenta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Divider()
            ProfileInfoRow(label = "Nombre de usuario", value = username)
            ProfileInfoRow(label = "Email", value = email)
            ProfileInfoRow(label = "Rol", value = role, isLast = true)
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String, isLast: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value)
    }
    if (!isLast) {
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun AccountOptionsList() {
    Column {
        Text("General", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        OptionItem(label = "Mis Pedidos", icon = Icons.Default.ReceiptLong, onClick = { /* TODO */ })
        OptionItem(label = "Editar Perfil", icon = Icons.Default.Edit, onClick = { /* TODO */ })
        OptionItem(label = "Configuración", icon = Icons.Default.Settings, onClick = { /* TODO */ })
        OptionItem(label = "Ayuda y Soporte", icon = Icons.Default.HelpOutline, onClick = { /* TODO */ })
    }
}

@Composable
fun OptionItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        onClick = onClick
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
fun AuthSection(authViewModel: AuthViewModel, state: AuthUiState) {
    var selectedTab by remember { mutableStateOf(0) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
            RegisterForm(
                authViewModel = authViewModel, 
                state = state,
                onRegisterSuccess = { selectedTab = 0 }
            )
        }
        
        if (state.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        } else {
            state.errorMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            state.successMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = it, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun LoginForm(authViewModel: AuthViewModel, state: AuthUiState) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { authViewModel.login(username, password) }, enabled = !state.isLoading, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(8.dp)) {
            Text("Entrar", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun RegisterForm(
    authViewModel: AuthViewModel, 
    state: AuthUiState, 
    onRegisterSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage?.contains("exitosamente") == true) {
            name = ""
            email = ""
            password = ""
            onRegisterSuccess()
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre de Usuario") }, modifier = Modifier.fillMaxWidth())
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
                    .clickable(onClick = onImageClick, enabled = onImageClick != {}),
                contentScale = ContentScale.Crop
            )
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

@Composable
fun AdminPanelButton(navController: NavController) {
    Button(
        onClick = { navController.navigate(Screen.AdminDashboard.route) },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(Icons.Filled.AdminPanelSettings, contentDescription = "Panel de Administración")
        Spacer(Modifier.width(8.dp))
        Text("Panel de Administración", style = MaterialTheme.typography.labelLarge)
    }
}