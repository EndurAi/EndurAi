package com.android.sample.model.achievements

import com.android.sample.model.workout.WorkoutType
import java.time.LocalDateTime
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * Class to compute some statistics based on a list of the workoutStatistics of the user.
 *
 * @param workoutStatisticsFlow the stateflow of the list of workout statistics used for
 *   computations
 */
class Statistics(private val workoutStatisticsFlow: StateFlow<List<WorkoutStatistics>>) {

  val today = LocalDateTime.now()

  /** Get the total number of workouts. */
  fun getTotalWorkouts(): Int {
    return workoutStatisticsFlow.value.size
  }

  /** Get a list of all workout dates. */
  fun getDates(): List<LocalDate> {
    return workoutStatisticsFlow.value.map { it.date.toLocalDate() }
  }

  /** Get the total calories burnt across all workouts. */
  fun getTotalCalories(): Int {
    return workoutStatisticsFlow.value.sumOf { it.caloriesBurnt }
  }

  /** Get workouts grouped by type (e.g., BODY_WEIGHT, YOGA, etc.). */
  fun getWorkoutsByType(): Map<WorkoutType, List<WorkoutStatistics>> {
    return workoutStatisticsFlow.value.groupBy { it.type }
  }

  /** Get workouts calories of the week */
  fun getCaloriesOfTheWeek(): Int {

    val mondayOfTheWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))




    val statsOfTheWeek = workoutStatisticsFlow.value.filter { stats -> stats.date.isAfter(mondayOfTheWeek.minusDays(1)) && stats.date.isBefore(today.plusWeeks(1)) }

    return statsOfTheWeek.sumOf{it.caloriesBurnt}
  }


}
