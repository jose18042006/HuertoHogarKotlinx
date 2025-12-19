package com.huertohogar.huertohogarkotlinx.utils

import android.util.Base64
import org.json.JSONObject

object JwtUtils {

    private fun getDecodedPayload(token: String): JSONObject? {
        return try {
            val parts = token.split(".")
            if (parts.size == 3) {
                JSONObject(String(Base64.decode(parts[1], Base64.URL_SAFE)))
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getRoleFromToken(token: String): String? {
        return getDecodedPayload(token)?.optString("role", null)
    }

    // --- ¡NUEVO! ---
    fun getUsernameFromToken(token: String): String? {
        return getDecodedPayload(token)?.optString("sub", null)
    }
}