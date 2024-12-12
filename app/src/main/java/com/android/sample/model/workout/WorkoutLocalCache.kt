package com.android.sample.model.workout

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.datetime.LocalDateTime
import java.io.IOException

val Context.workoutDataStore: DataStore<Preferences> by preferencesDataStore(name = "workout_cache")

open class WorkoutLocalCache(private val context: Context) {
    private val gson = GsonBuilder()
        .registerTypeAdapter(java.time.LocalDateTime::class.java, LocalDateTimeAdapter())
        .registerTypeAdapter(Workout::class.java, WorkoutTypeAdapter())
        .create()
    private val workoutKey = stringPreferencesKey("workout_data")

    // Get workouts from cache
    fun getWorkouts(): Flow<List<Workout>> = context.workoutDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[workoutKey]?.let { json ->
                val workoutListType = object : com.google.gson.reflect.TypeToken<Array<Workout>>() {}.type
                gson.fromJson<Array<Workout>>(json, workoutListType)?.toList() ?: emptyList()
            } ?: emptyList()
        }

    // Save workouts to cache
    suspend fun saveWorkouts(workouts: List<Workout>) {
        val workoutListType = object : com.google.gson.reflect.TypeToken<List<Workout>>() {}.type
        val jsonWorkouts = gson.toJson(workouts, workoutListType)
        context.workoutDataStore.edit { preferences ->
            preferences[workoutKey] = jsonWorkouts
        }
    }

    // Clear cached workouts
    suspend fun clearWorkouts() {
        context.workoutDataStore.edit { it.remove(workoutKey) }
    }
}