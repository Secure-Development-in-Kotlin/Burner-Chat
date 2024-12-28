package com.example.burnerchat.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "prefs_usuario")

class AppPreferences(private val context: Context) {

    // claves para las preferencias
    companion object {
        val CLAVE_TEMA = booleanPreferencesKey("nightMode")
        val CLAVE_IDIOMA = stringPreferencesKey("language")
    }

    val preferencesDataClass: Flow<PreferenciasDataClass> =
        context.dataStore.data
            .map { prefs ->
                PreferenciasDataClass(
                    prefs[CLAVE_TEMA] ?: false,
                    prefs[CLAVE_IDIOMA] ?: "en"
                )
            }

    // Cambiamos para que sólo haya un método para guardar las preferencias
    suspend fun savePreferences(nightMode: Boolean, lang: String) {
        context.dataStore.edit { prefs ->
            prefs[CLAVE_TEMA] = nightMode
            prefs[CLAVE_IDIOMA] = lang
        }
    }
}