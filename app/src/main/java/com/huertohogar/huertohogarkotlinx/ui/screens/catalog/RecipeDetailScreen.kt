package com.huertohogar.huertohogarkotlinx.ui.screens.catalog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.huertohogar.huertohogarkotlinx.data.remote.repository.recipe.RecipeRepositoryImpl
import com.huertohogar.huertohogarkotlinx.viewmodel.RecipeDetailViewModel

class RecipeDetailViewModelFactory(private val recipeId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val recipeRepository = RecipeRepositoryImpl()
        return RecipeDetailViewModel(recipeRepository).apply {
            loadRecipeDetails(recipeId)
        } as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    navController: NavController
) {
    val viewModel: RecipeDetailViewModel = viewModel(factory = RecipeDetailViewModelFactory(recipeId))
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.recipe?.name ?: "Cargando receta...") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Volver") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
            }
            uiState.recipe != null -> {
                val recipe = uiState.recipe!!
                Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState())) {
                    Image(
                        painter = rememberAsyncImagePainter(model = recipe.imageUrl), // <-- CORREGIDO
                        contentDescription = recipe.name,
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(recipe.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Ingredientes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        recipe.getIngredients().forEach {
                            Text("• ${it.first} - ${it.second}")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Instrucciones", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(recipe.instructions, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
