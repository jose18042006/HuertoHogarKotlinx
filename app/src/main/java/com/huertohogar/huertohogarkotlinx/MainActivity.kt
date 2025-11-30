package com.huertohogar.huertohogarkotlinx

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.huertohogar.huertohogarkotlinx.ui.navigation.AppWindowSizeClass
import com.huertohogar.huertohogarkotlinx.ui.screens.MainScreen
import com.huertohogar.huertohogarkotlinx.ui.theme.AppComposeTheme
import com.huertohogar.huertohogarkotlinx.viewmodel.SharedUserViewModel

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Calcula el tamaño de la ventana para el diseño adaptable
            val windowSizeClass = calculateWindowSizeClass(this)
            val appWindowSizeClass = AppWindowSizeClass(windowSizeClass.widthSizeClass)

            // Aplica el Tema (que ahora controla el color de la barra de estado)
            AppComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppSetup(appWindowSizeClass)
                }
            }
        }
    }
}

/**
 * Función Composable que inicializa el NavController y la Factory de ViewModels.
 */
@Composable
fun AppSetup(appWindowSizeClass: AppWindowSizeClass) {
    val navController = rememberNavController()

    // 1. Obtención de Factory para ViewModels con inyección de Room/DataStore
    val context = LocalContext.current
    // Obtenemos la aplicación para la ViewModelFactory
    val application = context.applicationContext as Application
    val sharedViewModelFactory = SharedUserViewModel.Factory(application)

    // 2. La pantalla MainScreen es el nuevo NavHost y punto de inicio
    MainScreen(
        appNavHostController = navController,
        windowSizeClass = appWindowSizeClass,
        sharedViewModelFactory = sharedViewModelFactory
    )
}