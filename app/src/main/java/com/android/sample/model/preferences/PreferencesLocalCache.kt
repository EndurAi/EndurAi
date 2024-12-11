package com.android.sample.model.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences_cache")

class PreferencesLocalCache(private val context: Context) {
    private val gson = Gson()
    private val preferencesKey = stringPreferencesKey("preferences_data")

    fun getPreferences(): Flow<Preferences?> = context.preferencesDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[preferencesKey]?.let { json ->
                gson.fromJson(json, Preferences::class.java)
            }
        }

    suspend fun savePreferences(preferences: Preferences) {
        context.preferencesDataStore.edit { prefs ->
            prefs[preferencesKey] = gson.toJson(preferences)
        }
    }

    suspend fun clearPreferences() {
        context.preferencesDataStore.edit { it.clear() }
    }
}