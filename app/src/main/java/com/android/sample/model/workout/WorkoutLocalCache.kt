package com.android.sample.model.workout

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

val Context.workoutDataStore: DataStore<Preferences> by preferencesDataStore(name = "workout_cache")

class WorkoutLocalCache(private val context: Context) {
    private val gson = Gson()
    private val workoutKey = stringPreferencesKey("workout_data")

    fun getWorkouts(): Flow<List<Workout>?> = context.workoutDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[workoutKey]?.let { json ->
                gson.fromJson(json, Array<Workout>::class.java)?.toList()
            }
        }

    suspend fun saveWorkouts(workouts: List<Workout>) {
        context.workoutDataStore.edit { preferences ->
            preferences[workoutKey] = gson.toJson(workouts)
        }
    }

    suspend fun clearWorkouts() {
        context.workoutDataStore.edit { it.clear() }
    }
}