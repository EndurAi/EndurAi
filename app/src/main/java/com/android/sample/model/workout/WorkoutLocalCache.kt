package com.android.sample.model.workout

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.android.sample.model.workout.WorkoutRepositoryFirestore.LatLngAdapter
import com.android.sample.model.workout.WorkoutRepositoryFirestore.LatLngListAdapter
import com.android.sample.model.workout.WorkoutRepositoryFirestore.LocalDateTimeAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.zacsweers.moshix.sealed.reflect.MoshiSealedJsonAdapterFactory
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.workoutDataStore: DataStore<Preferences> by preferencesDataStore(name = "workout_cache")

open class WorkoutLocalCache(private val context: Context) {
  private val moshi =
      Moshi.Builder()
          .add(
              PolymorphicJsonAdapterFactory.of(Workout::class.java, "type")
                  .withSubtype(BodyWeightWorkout::class.java, "BodyWeightWorkout")
                  .withSubtype(YogaWorkout::class.java, "YogaWorkout")
                  .withSubtype(WarmUp::class.java, "WarmUp")
                  .withSubtype(RunningWorkout::class.java, "RunningWorkout"))
          .add(MoshiSealedJsonAdapterFactory())
          .add(KotlinJsonAdapterFactory())
          .add(LocalDateTimeAdapter())
          .add(LatLngAdapter())
          .add(LatLngListAdapter(LatLngAdapter()))
          .build()

  // Create a Moshi adapter for a list of Workouts
  private val workoutListAdapter =
      moshi.adapter<List<Workout>>(
          com.squareup.moshi.Types.newParameterizedType(List::class.java, Workout::class.java))

  private val workoutKey = stringPreferencesKey("workout_data")

  // Get workouts from cache
  fun getWorkouts(): Flow<List<Workout>> =
      context.workoutDataStore.data
          .catch { exception ->
            when (exception) {
              is IOException -> {
                Log.e("WorkoutLocalCache", "Error reading cache, returning empty list.", exception)
                emit(emptyPreferences())
              }
              else -> throw exception
            }
          }
          .map { preferences ->
            val json = preferences[workoutKey]
            if (json != null) {
              try {
                val workouts = workoutListAdapter.fromJson(json)
                if (workouts != null) {
                  Log.d(
                      "WorkoutLocalCache",
                      "Successfully loaded ${workouts.size} workouts from cache.")
                  workouts
                } else {
                  Log.w("WorkoutLocalCache", "Decoded workouts list is null, returning empty list.")
                  emptyList()
                }
              } catch (e: Exception) {
                Log.e("WorkoutLocalCache", "Error deserializing workouts: $json", e)
                emptyList()
              }
            } else {
              Log.d("WorkoutLocalCache", "No workouts found in cache.")
              emptyList()
            }
          }

  // Save workouts to cache using Moshi
  suspend fun saveWorkouts(workouts: List<Workout>) {
    try {
      val jsonWorkouts = workoutListAdapter.toJson(workouts)
      context.workoutDataStore.edit { preferences -> preferences[workoutKey] = jsonWorkouts }
      Log.d("WorkoutLocalCache", "Saved ${workouts.size} workouts to cache.")
    } catch (e: Exception) {
      Log.e("WorkoutLocalCache", "Error saving workouts to cache.", e)
    }
  }

  // Clear cached workouts
  suspend fun clearWorkouts() {
    context.workoutDataStore.edit { it.remove(workoutKey) }
    Log.d("WorkoutLocalCache", "Cleared all cached workouts.")
  }
}
