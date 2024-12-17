package com.android.sample.model.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.preferencesDataStore: DataStore<Preferences> by
preferencesDataStore(name = "preferences_cache")

class PreferencesLocalCache(private val context: Context) {
    private val gson = Gson()
    private val preferencesKey = stringPreferencesKey("preferences_data")

    fun getPreferences(): Flow<Preferences?> =
        context.preferencesDataStore.data
            .catch { exception ->
                when (exception) {
                    is IOException -> {
                        Log.e("PreferencesLocalCache", "Error reading cache, using default preferences.", exception)
                        emit(emptyPreferences())
                    }
                    else -> throw exception
                }
            }
            .map { preferences ->
                preferences[preferencesKey]?.let { json ->
                    gson.fromJson(json, Preferences::class.java)
                }
            }

    /**
     * Saves the user preferences to local cache.
     *
     * @param preferences The [Preferences] object to be saved.
     */
    suspend fun savePreferences(preferences: Preferences) {
        try {
            context.preferencesDataStore.edit { prefs ->
                prefs[preferencesKey] = gson.toJson(preferences)
            }
            Log.d("PreferencesLocalCache", "Successfully saved preferences to cache.")
        } catch (e: Exception) {
            Log.e("PreferencesLocalCache", "Error saving preferences to cache.", e)
        }
    }

    /**
     * Clears the preferences cache.
     */
    suspend fun clearPreferences() {
        context.preferencesDataStore.edit { it.clear() }
    }
}
