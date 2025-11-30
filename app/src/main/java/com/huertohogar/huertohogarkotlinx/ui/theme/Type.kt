package com.huertohogar.huertohogarkotlinx.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.huertohogar.huertohogarkotlinx.R
import com.huertohogar.huertohogarkotlinx.ui.theme.TitleColor
import com.huertohogar.huertohogarkotlinx.ui.theme.OnBackgroundColor
import com.huertohogar.huertohogarkotlinx.ui.theme.DarkGray // Aseguramos que DarkGray se importe si se usa

// Fuentes (Asumimos que los archivos TTF existen en res/font/)
val Montserrat = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_bold, FontWeight.Bold)
)

val PlayfairDisplay = FontFamily(
    Font(R.font.playfair_display_regular, FontWeight.Normal),
    Font(R.font.playfair_display_bold, FontWeight.Bold)
)

// Definición de la escala tipográfica (AppTypography)
val AppTypography = Typography(
    // Encabezados con Playfair Display
    headlineLarge = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        color = TitleColor
    ),
    headlineMedium = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        color = TitleColor
    ),
    // Títulos y Subtítulos con Montserrat
    titleLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        color = OnBackgroundColor
    ),
    titleMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = OnBackgroundColor
    ),
    // Cuerpo de Texto con Montserrat
    bodyLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = OnBackgroundColor
    ),
    bodyMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = OnBackgroundColor
    ),
    // Etiquetas y Botones
    labelLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp,
        color = androidx.compose.ui.graphics.Color.White
    )
)