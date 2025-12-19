package com.huertohogar.huertohogarkotlinx.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.huertohogar.huertohogarkotlinx.R
import com.huertohogar.huertohogarkotlinx.viewmodel.SharedUserViewModel

@Composable
fun WelcomePopup(
    sharedViewModel: SharedUserViewModel,
    onNavigateToProfile: () -> Unit
) {
    val popupChecked by sharedViewModel.popupChecked.collectAsState()

    Dialog(onDismissRequest = { sharedViewModel.dismissWelcomePopup() }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier.size(80.dp)
                )
                Text(
                    text = "¡Bienvenido a HuertoHogar!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Para disfrutar de una experiencia completa y guardar tus preferencias, te recomendamos crear una cuenta.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = popupChecked,
                        onCheckedChange = { isChecked -> sharedViewModel.setPopupChecked(isChecked) }
                    )
                    Text(
                        text = "No volver a mostrar este mensaje",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TextButton(onClick = {
                        sharedViewModel.handlePopupDismissal(popupChecked, onNavigateToProfile)
                    }) {
                        Text("Crear Cuenta")
                    }
                    Button(onClick = { 
                        sharedViewModel.handlePopupDismissal(popupChecked, {}) 
                    }) {
                        Text("Más Tarde")
                    }
                }
            }
        }
    }
}