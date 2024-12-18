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

  /**
   * Retrieves the user preferences from local cache.
   *
   * @return A [Flow] of [Preferences] object.
   */
  fun getPreferences(): Flow<com.android.sample.model.preferences.Preferences?> =
      context.preferencesDataStore.data
          .catch { exception ->
            when (exception) {
              is IOException -> {
                Log.e(
                    "PreferencesLocalCache",
                    "Error reading cache, using default preferences.",
                    exception)
                emit(emptyPreferences())
              }
              else -> throw exception
            }
          }
          .map { preferences ->
            val json = preferences[preferencesKey]
            if (json != null) {
              try {
                val result =
                    gson.fromJson(
                        json, com.android.sample.model.preferences.Preferences::class.java)
                Log.d("PreferencesLocalCache", "Successfully loaded preferences: $json")
                result
              } catch (e: Exception) {
                Log.e("PreferencesLocalCache", "Error deserializing preferences: $json", e)
                null
              }
            } else {
              Log.d("PreferencesLocalCache", "No preferences found in cache, returning null.")
              null
            }
          }

  /**
   * Saves the user preferences to local cache.
   *
   * @param preferences The [Preferences] object to be saved.
   */
  suspend fun savePreferences(preferences: com.android.sample.model.preferences.Preferences) {
    try {
      context.preferencesDataStore.edit { prefs ->
        prefs[preferencesKey] = gson.toJson(preferences)
      }
      Log.d("PreferencesLocalCache", "Successfully saved preferences to cache.")
    } catch (e: Exception) {
      Log.e("PreferencesLocalCache", "Error saving preferences to cache.", e)
    }
  }

  /** Clears the preferences cache. */
  suspend fun clearPreferences() {
    try {
      context.preferencesDataStore.edit { it.clear() }
      Log.d("PreferencesLocalCache", "Cleared preferences cache.")
    } catch (e: Exception) {
      Log.e("PreferencesLocalCache", "Error clearing preferences cache.", e)
    }
  }
}
