package com.huertohogar.huertohogarkotlinx.ui.screen

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.huertohogar.huertohogarkotlinx.viewmodel.SharedUserViewModel

@Composable
fun ProfileScreen(viewModel: SharedUserViewModel) {
    val userData by viewModel.formData.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap: Bitmap? -> viewModel.onProfilePictureTaken(bitmap) }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean -> if (isGranted) cameraLauncher.launch(null) }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Foto de Perfil") },
            text = { Text("Elige una opción") },
            confirmButton = {
                Column(Modifier.fillMaxWidth()) {
                    TextButton(onClick = {
                        showDialog = false
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }) { Text("Tomar una nueva foto", color = MaterialTheme.colorScheme.primary) } // <-- COLOR AÑADIDO
                    if (userData?.profileImageUri != null) {
                        TextButton(onClick = {
                            showDialog = false
                            viewModel.deleteProfilePicture()
                        }) { Text("Eliminar foto actual", color = MaterialTheme.colorScheme.error) }
                    }
                }
            },
            dismissButton = { 
                TextButton(onClick = { showDialog = false }) { 
                    Text("Cancelar", color = MaterialTheme.colorScheme.primary) // <-- COLOR AÑADIDO
                } 
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Box(contentAlignment = Alignment.BottomEnd) {
                    val placeholderPainter = rememberVectorPainter(image = Icons.Default.AccountCircle)
                    val painter = rememberAsyncImagePainter(
                        model = userData?.profileImageUri,
                        error = placeholderPainter,
                        fallback = placeholderPainter
                    )
                    Image(
                        painter = painter,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(150.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant).border(2.dp, MaterialTheme.colorScheme.primary, CircleShape).clickable { showDialog = true },
                        contentScale = ContentScale.Crop
                    )
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).offset(x = (-8).dp, y = (-8).dp).background(MaterialTheme.colorScheme.surface, CircleShape).padding(4.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = userData?.nombre ?: "Nombre no disponible",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = userData?.email ?: "Email no disponible",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(24.dp))
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
            }

            item { ProfileOption(icon = Icons.Default.ShoppingCart, text = "Mis Pedidos") { /* TODO */ } }
            item { ProfileOption(icon = Icons.Default.Favorite, text = "Mis Favoritos") { /* TODO */ } }
            item { ProfileOption(icon = Icons.Default.CardGiftcard, text = "Únete al Club HuertoHogar") { /* TODO */ } }
            item { ProfileOption(icon = Icons.Default.Settings, text = "Configuración") { /* TODO */ } }
        }

        ProfileOption(icon = Icons.Default.Logout, text = "Cerrar Sesión", isDestructive = true) {
            // TODO: Implementar lógica de cierre de sesión
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ProfileOption(icon: ImageVector, text: String, isDestructive: Boolean = false, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                color = if (isDestructive) MaterialTheme.colorScheme.error else LocalContentColor.current,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}