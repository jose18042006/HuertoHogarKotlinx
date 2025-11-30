// build.gradle.kts (Nivel de Proyecto/Raíz)

plugins {
    // CAMBIO OBLIGATORIO: Usar AGP 8.3.0 o superior para estabilidad con SDK 34/35.
    // Usaremos una versión que soporta el SDK 35 (aunque la advertencia pida 8.6.0, 8.3.0 es más estable para muchos entornos)
    id("com.android.application") version "8.13.0" apply false

    // Mantenemos Kotlin y KSP en las mismas versiones
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}