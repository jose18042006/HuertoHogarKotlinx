package com.huertohogar.huertohogarkotlinx.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.huertohogar.huertohogarkotlinx.data.model.UserDto
import com.huertohogar.huertohogarkotlinx.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    navController: NavController,
    adminViewModel: AdminViewModel // <-- ¡CORREGIDO! Recibe el ViewModel
) {
    val uiState by adminViewModel.uiState.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<UserDto?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Carga los usuarios solo una vez cuando la pantalla se muestra por primera vez
    LaunchedEffect(Unit) {
        adminViewModel.loadAllUsers()
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            showEditDialog = false
            snackbarHostState.showSnackbar(it)
            adminViewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Usuarios") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Volver") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                selectedUser = null
                showEditDialog = true 
            }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Usuario")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                else -> {
                    UserList(
                        users = uiState.users,
                        onEditClick = { user ->
                            selectedUser = user
                            showEditDialog = true
                        },
                        onDeleteClick = { user ->
                            selectedUser = user
                            showDeleteConfirmation = true
                        }
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        UserEditDialog(
            user = selectedUser,
            onDismiss = { showEditDialog = false },
            onConfirm = {
                if (selectedUser == null) {
                    adminViewModel.createUser(it)
                } else {
                    adminViewModel.updateUser(it.id, it)
                }
            }
        )
    }

    if (showDeleteConfirmation) {
        DeleteConfirmationDialog(
            user = selectedUser!!,
            onDismiss = { showDeleteConfirmation = false },
            onConfirm = {
                adminViewModel.deleteUser(selectedUser!!.id)
                showDeleteConfirmation = false
            }
        )
    }
}

// ... (El resto de los componentes del archivo no necesitan cambios)
@Composable
private fun UserList(users: List<UserDto>, onEditClick: (UserDto) -> Unit, onDeleteClick: (UserDto) -> Unit) { /*...*/ }
@Composable
private fun UserCard(user: UserDto, onEditClick: (UserDto) -> Unit, onDeleteClick: (UserDto) -> Unit) { /*...*/ }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserEditDialog(user: UserDto?, onDismiss: () -> Unit, onConfirm: (UserDto) -> Unit) { /*...*/ }
@Composable
private fun DeleteConfirmationDialog(user: UserDto, onDismiss: () -> Unit, onConfirm: () -> Unit) { /*...*/ }
