package com.huertohogar.huertohogarkotlinx.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta de Colores Base (RGB, en formato ARGB Hexadecimal 0xFFRRGGBB)
val LightGreen = Color(0xFF2E8B57) // Verde Esmeralda (Primary)
val DarkGreen = Color(0xFF226742)
val LightMustard = Color(0xFFFFD700) // Amarillo Mostaza (Secondary)
val DarkMustard = Color(0xFFCCAA00)
val SoftWhite = Color(0xFFF7F7F7) // Fondo Principal antes del Terroso

// Colores de Texto y Acentos Terrosos (que resolvían las referencias)
val DarkGray = Color(0xFF333333) // Texto Principal
val MediumGray = Color(0xFF666666) // Texto Secundario
val LightBrown = Color(0xFF8B4513) // Marrón Claro (Acentos Terrosos - TitleColor)
val ErrorRed = Color(0xFFB00020)

// -----------------
// Variables de Temas (Mapeo a Material 3)
// -----------------

val PrimaryColor = LightGreen
val OnPrimaryColor = Color.White
val PrimaryContainerColor = Color(0xFFB0FFD3)
val OnPrimaryContainerColor = DarkGreen

val SecondaryColor = LightMustard
val OnSecondaryColor = DarkGray
val SecondaryContainerColor = Color(0xFFFFFAD6)
val OnSecondaryContainerColor = DarkMustard

val TertiaryColor = LightBrown
val OnTertiaryColor = Color.White
val TertiaryContainerColor = Color(0xFFFFDBCA)
val OnTertiaryContainerColor = LightBrown

val ErrorColor = ErrorRed
val OnErrorColor = Color.White

val BackgroundColor = SoftWhite // Fondo Blanco original
val OnBackgroundColor = DarkGray // Texto sobre fondo

val SurfaceColor = Color.White // Tarjetas, Bottom Bar
val OnSurfaceColor = DarkGray // Texto sobre superficie

val SurfaceVariantColor = Color(0xFFEFEFEF) // Gris muy suave
val OnSurfaceVariantColor = MediumGray

// Color específico para Tipografía (usado en Type.kt)
val TitleColor = LightBrown