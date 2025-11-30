package com.huertohogar.huertohogarkotlinx.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huertohogar.huertohogarkotlinx.viewmodel.SharedUserViewModel
import com.huertohogar.huertohogarkotlinx.ui.theme.LightBrown

@Composable
fun WelcomePopup(
    sharedViewModel: SharedUserViewModel,
    onNavigateToProfile: () -> Unit
) {
    val isChecked by sharedViewModel.popupChecked.collectAsState()

    AlertDialog(
        onDismissRequest = {
            sharedViewModel.handlePopupDismissal(
                shouldNeverShowAgain = isChecked,
                navigateToProfile = {}
            )
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Eco,
                    contentDescription = "Logo Huerto Hogar",
                    tint = LightBrown,
                    modifier = Modifier.size(32.dp).padding(end = 8.dp)
                )
                Text(
                    "¡Únete al Club Huerto Hogar!",
                    fontWeight = FontWeight.Bold,
                    color = LightBrown
                )
            }
        },
        text = {
            Column {
                Text(
                    "Obtén increíbles ofertas, descuentos exclusivos y acceso a preventas de temporada solo para miembros registrados.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { sharedViewModel.setPopupChecked(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "No volver a mostrar este mensaje",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    sharedViewModel.handlePopupDismissal(
                        shouldNeverShowAgain = isChecked,
                        navigateToProfile = onNavigateToProfile
                    )
                }
            ) {
                Text("Crear Cuenta")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    sharedViewModel.handlePopupDismissal(
                        shouldNeverShowAgain = isChecked,
                        navigateToProfile = {}
                    )
                }
            ) {
                Text(
                    "No me interesa",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}