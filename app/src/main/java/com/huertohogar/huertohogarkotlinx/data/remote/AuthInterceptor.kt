package com.huertohogar.huertohogarkotlinx.data.remote

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor de OkHttp que añade automáticamente el token de autenticación
 * a las cabeceras de todas las peticiones a la API.
 */
class AuthInterceptor : Interceptor {

    companion object {
        // El token se mantiene en memoria para un acceso rápido y síncrono.
        // Será actualizado por el AuthViewModel.
        var token: String? = null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        // 1. Clona la petición original para poder modificarla.
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // 2. Si tenemos un token, lo añadimos a la cabecera.
        token?.let {
            requestBuilder.header("Authorization", "Bearer $it")
        }

        // 3. Procede con la nueva petición (con o sin cabecera).
        return chain.proceed(requestBuilder.build())
    }
}