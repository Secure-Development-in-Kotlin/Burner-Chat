package com.example.burnerchat.preferences
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Creamos un objeto para manejar el acceso a DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prefs_usuario")

class ThemePreferences(private val context: Context) {

    // Usamos BooleanPreferencesKey para manejar el valor de noche
    private val NIGHT_MODE_KEY = booleanPreferencesKey("night_mode")

    // Lee el valor de night_mode desde DataStore
    val isNightMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[NIGHT_MODE_KEY] ?: false // Por defecto es falso (modo claro)
        }

    // Guarda el valor de night_mode en DataStore
    suspend fun saveNightMode(isNight: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NIGHT_MODE_KEY] = isNight
        }
    }
}