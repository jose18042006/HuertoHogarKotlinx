package com.huertohogar.huertohogarkotlinx.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// Crea una única instancia de DataStore para toda la aplicación.
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "huerto_hogar_settings")
