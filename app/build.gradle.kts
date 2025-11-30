plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.huertohogar.huertohogarkotlinx"
    compileSdk = 35 // CAMBIO OBLIGATORIO: Subir a SDK 35 para las nuevas librerías

    defaultConfig {
        applicationId = "com.huertohogar.huertohogarkotlinx"
        minSdk = 24
        targetSdk = 35 // También subimos el target SDK
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// app/build.gradle.kts (SECCIÓN DE DEPENDENCIES COMPLETA)

dependencies {

    // Dependencias base de Android y Kotlin
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")

    // COIL PARA CARGA DE IMÁGENES
    implementation("io.coil-kt:coil-compose:2.5.0")

    // ---------------------------------------------------------------------------------------------
    // JETPACK COMPOSE & MATERIAL DESIGN 3
    // ---------------------------------------------------------------------------------------------
    // BOM - Mantenemos la referencia
    implementation(platform("androidx.compose:compose-bom:2023.10.00"))

    // FORZAMOS LA VERSIÓN COMPLETA DE CADA MODULO CRÍTICO A 1.6.0/1.2.1:
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.ui:ui-graphics:1.6.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0")
    implementation("androidx.compose.material3:material3:1.2.1")

    // **CRÍTICO PARA ANIMATEITEMPLACEMENT**
    implementation("androidx.compose.foundation:foundation:1.6.0")
    // **CRÍTICO PARA TWEEN**
    implementation("androidx.compose.animation:animation:1.6.0")
    implementation("androidx.compose.animation:animation-core:1.6.0")


    implementation("androidx.compose.material3:material3-window-size-class:1.1.2")
    implementation("androidx.compose.material:material-icons-extended")


    // ---------------------------------------------------------------------------------------------
    // ARQUITECTURA MVVM & REACTIVIDAD (Lifecycle, ViewModel, Navigation)
    // ---------------------------------------------------------------------------------------------
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    // ---------------------------------------------------------------------------------------------
    // PERSISTENCIA (Room y DataStore)
    // ---------------------------------------------------------------------------------------------
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.datastore:datastore-preferences:1.0.0")


    // ---------------------------------------------------------------------------------------------
    // DEBUG Y TESTEO
    // ---------------------------------------------------------------------------------------------
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.8") // MockK para mocks en tests
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3") // Para testear coroutines
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}