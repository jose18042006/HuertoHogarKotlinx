package com.huertohogar.huertohogarkotlinx.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class AppSettingsDataStore(private val context: Context) {

    companion object {
        val HAS_SEEN_INTRO = booleanPreferencesKey("has_seen_intro")
    }

    fun getHasSeenIntro(): Flow<Boolean> {
        return context.dataStore.data
            .map { preferences ->
                preferences[HAS_SEEN_INTRO] ?: false
            }
    }

    suspend fun setHasSeenIntro(value: Boolean) {
        context.dataStore.edit { settings ->
            settings[HAS_SEEN_INTRO] = value
        }
    }
}