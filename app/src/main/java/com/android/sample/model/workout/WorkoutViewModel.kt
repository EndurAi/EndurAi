package com.android.sample.model.workout

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*

open class WorkoutViewModel<out T : Workout>(private val repository: WorkoutRepository<T>) :
    ViewModel() {

  val workouts_ = MutableStateFlow<List<@UnsafeVariance T>>(emptyList())
  val workouts: StateFlow<List<T>> = workouts_

    val doneWorkouts_ = MutableStateFlow<List<@UnsafeVariance T>>(emptyList())
    val doneWorkouts: StateFlow<List<T>> = doneWorkouts_

  private val selectedWorkout_ = MutableStateFlow<T?>(null)
  open val selectedWorkout: StateFlow<T?> = selectedWorkout_

  init {
    repository.init { getWorkouts()
    getDoneWorkouts()}
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
    /** Gets all Workout documents. */
    fun getDoneWorkouts(){
        repository.getDoneDocuments(onSuccess = { doneWorkouts_.value = it }, onFailure = {})
    }

  /**
   * Adds a Workout document.
   *
   * @param workout The Workout document to be added.
   */
  fun addWorkout(workout: @UnsafeVariance T) {
    repository.addDocument(obj = workout, onSuccess = { getWorkouts() }, onFailure = {})
  }

    fun transferWorkoutToDone(id: String){
        repository.transferDocumentToDone(id = id, onSuccess = {
            getWorkouts()
            getDoneWorkouts()
        }, onFailure = {})
    }

    fun importWorkoutFromDone(id: String){
        repository.importDocumentFromDone(id = id, onSuccess = {
            getWorkouts()
            getDoneWorkouts()
        }, onFailure = {})
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

  /**
   * Copies a workout object and returns a new instance with all the same properties except for the
   * ID, which is a newly generated unique identifier.
   *
   * @param workout The workout object to be copied. Must be a subclass of [Workout].
   */
  fun <T : Workout> copyOf(workout: T): T {
    return when (workout) {
      is BodyWeightWorkout ->
          BodyWeightWorkout(
              getNewUid(),
              workout.name,
              workout.description,
              workout.warmup,
              workout.userIdSet,
              workout.exercises,
              workout.date)
              as T
      is YogaWorkout ->
          YogaWorkout(
              getNewUid(),
              workout.name,
              workout.description,
              workout.warmup,
              workout.userIdSet,
              workout.exercises,
              workout.date)
              as T
      else -> TODO()
    }
  }
}
