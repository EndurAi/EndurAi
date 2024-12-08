package com.android.sample.model.achievements

import com.android.sample.model.workout.WorkoutType
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

/***
 * Class to compute some statistics based on a list of the workoutStatistics of the user.
 * @param workoutStatisticsFlow the stateflow of the list of workout statistics used for computations
 */
class Statistics(
    private val workoutStatisticsFlow: StateFlow<List<WorkoutStatistics>>
) {

    /** Get the total number of workouts. */
    fun getTotalWorkouts(): Int {
        return workoutStatisticsFlow.value.size
    }

    /** Get a list of all workout dates. */
    fun getDates(): List<LocalDateTime> {
        return workoutStatisticsFlow.value.map { it.date }
    }

    /** Get the total calories burnt across all workouts. */
    fun getTotalCalories(): Int {
        return workoutStatisticsFlow.value.sumOf { it.caloriesBurnt }
    }

    /** Get workouts grouped by type (e.g., BODY_WEIGHT, YOGA, etc.). */
    fun getWorkoutsByType(): Map<WorkoutType, List<WorkoutStatistics>> {
        return workoutStatisticsFlow.value.groupBy { it.type }
    }
}
