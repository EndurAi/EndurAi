package com.android.sample.model.achievements

import androidx.lifecycle.ViewModel
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.RunningWorkout
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.composables.Calories.computeCalories
import com.android.sample.ui.googlemap.calculateDistance
import com.android.sample.ui.workout.ExerciseState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class StatisticsViewModel(private val repository: StatisticsRepository) : ViewModel() {

  private val workoutStatistics_ = MutableStateFlow<List<WorkoutStatistics>>(emptyList())
  val workoutStatistics: StateFlow<List<WorkoutStatistics>> = workoutStatistics_

  private val friendWorkoutStatistics_ = MutableStateFlow<List<WorkoutStatistics>>(emptyList())
  open val friendWorkoutStatistics: StateFlow<List<WorkoutStatistics>>
    get() = friendWorkoutStatistics_.asStateFlow()

  init {
    // Initialize by fetching all statistics
    repository.init { getWorkoutStatistics() }
  }

  /** Fetch all workout statistics from the repository. */
  fun getWorkoutStatistics() {
    repository.getStatistics(onSuccess = { workoutStatistics_.value = it }, onFailure = {})
  }

  fun getFriendWorkoutStatistics(friendId: String) {
    repository.getFriendStatistics(
        friendId = friendId, onSuccess = { friendWorkoutStatistics_.value = it }, onFailure = {})
  }

  /**
   * Add a new workout statistics document to the repository.
   *
   * @param stats The WorkoutStatistics object to be added.
   */
  fun addWorkoutStatistics(stats: WorkoutStatistics) {
    repository.addWorkoutStatistics(
        workout = stats, onSuccess = { getWorkoutStatistics() }, onFailure = {})
  }

  /**
   * Computes statistics for a given workout.
   *
   * @param workout The completed workout.
   * @param exerciseList The list of exercises performed during the workout.
   * @param userAccountViewModel The UserAccountViewModel to fetch user details.
   * @return A new [WorkoutStatistics] object.
   */
  fun computeWorkoutStatistics(
      workout: Workout,
      exerciseList: List<ExerciseState>,
      userAccountViewModel: UserAccountViewModel
  ): WorkoutStatistics {

    // Calculate total calories burnt using userAccount details
    val caloriesBurnt = computeCalories(exerciseList, userAccountViewModel.userAccount.value)
    val workoutType =
        when (workout) {
          is BodyWeightWorkout -> WorkoutType.BODY_WEIGHT
          is YogaWorkout -> WorkoutType.YOGA
          else -> WorkoutType.RUNNING
        }
      var distance = 0.0

      if (workoutType == WorkoutType.RUNNING) {
          val workout = workout as RunningWorkout
          distance =
              calculateDistance(
                  workout.path.map { latLng -> LatLng(latLng.latitude, latLng.longitude) })
      }

    // Construct the WorkoutStatistics object
    return WorkoutStatistics(
        id = workout.workoutId,
        date = workout.date,
        caloriesBurnt = caloriesBurnt,
        type = workoutType,
        distance = distance)
  }
}
