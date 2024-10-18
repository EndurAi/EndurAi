package com.android.sample.model.workout

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*

open class WorkoutViewModel<out T : Workout>(private val repository: WorkoutRepository<T>) :
    ViewModel() {

  private val workouts_ = MutableStateFlow<List<T>>(emptyList())
  val workouts: StateFlow<List<T>> = workouts_

  private val selectedWorkout_ = MutableStateFlow<T?>(null)
  open val selectedWorkout: StateFlow<T?> = selectedWorkout_

  init {
    repository.init { getWorkouts() }
  }

  /**
   * Generates a new unique ID.
   *
   * @return A new unique ID.
   */
  fun getNewUid(): String {
    return repository.getNewUid()
  }

  /** Gets all Workout documents. */
  fun getWorkouts() {
    repository.getDocuments(onSuccess = { workouts_.value = it }, onFailure = {})
  }

  /**
   * Adds a Workout document.
   *
   * @param workout The Workout document to be added.
   */
  fun addWorkout(workout: @UnsafeVariance T) {
    repository.addDocument(obj = workout, onSuccess = { getWorkouts() }, onFailure = {})
  }

  /**
   * Updates a Workout document.
   *
   * @param workout The Workout document to be updated.
   */
  fun updateWorkout(workout: @UnsafeVariance T) {
    repository.updateDocument(obj = workout, onSuccess = { getWorkouts() }, onFailure = {})
  }

  /**
   * Deletes a Workout document by its ID.
   *
   * @param id The ID of the Workout document to be deleted.
   */
  fun deleteWorkoutById(id: String) {
    repository.deleteDocument(id = id, onSuccess = { getWorkouts() }, onFailure = {})
  }

  /**
   * Selects a Workout document.
   *
   * @param workout The Workout document to be selected.
   */
  fun selectWorkout(workout: @UnsafeVariance T) {
    selectedWorkout_.value = workout
  }
}
