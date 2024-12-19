package com.android.sample.model.achievements

import com.android.sample.model.workout.WorkoutType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.DayOfWeek

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

  /** Get total workouts calories of the week */
  fun getCaloriesOfTheWeek(): Int {

    val mondayOfTheWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    val statsOfTheWeek =
        workoutStatisticsFlow.value.filter { stats ->
          stats.date.isAfter(mondayOfTheWeek.minusDays(1)) &&
              stats.date.isBefore(today.plusWeeks(1))
        }

    return statsOfTheWeek.sumOf { it.caloriesBurnt }
  }

  /** Get workouts calories of the week in a list */
  fun getCaloriesOfTheWeekToList(): List<Double> {

    val mondayOfTheWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    val caloriesPerDay = MutableList(7) { 0.0 }

    val statsOfTheWeek =
      workoutStatisticsFlow.value.filter { stats ->
        stats.date.isAfter(mondayOfTheWeek.minusDays(1)) &&
                stats.date.isBefore(today.plusWeeks(1))
      }
    statsOfTheWeek.forEach { stats -> caloriesPerDay[stats.date.dayOfWeek.value - 1] += stats.caloriesBurnt.toDouble() }

    return caloriesPerDay
  }

  /** Get the frequency of each workout type  */
  fun getWorkoutTypeFrequency(): Map<WorkoutType, Double> {


    val numberOfBodyweight = workoutStatisticsFlow.value.filter { stats -> stats.type == WorkoutType.BODY_WEIGHT }.size.toDouble()
    val numberOfYoga = workoutStatisticsFlow.value.filter { stats -> stats.type == WorkoutType.YOGA }.size.toDouble()
    val numberOfRunning = workoutStatisticsFlow.value.filter { stats -> stats.type == WorkoutType.RUNNING }.size.toDouble()




    return listOf(
      FrequencyWithWorkoutType(type = WorkoutType.BODY_WEIGHT, frequency = numberOfBodyweight),
      FrequencyWithWorkoutType(type = WorkoutType.YOGA, frequency = numberOfYoga),
      FrequencyWithWorkoutType(type = WorkoutType.RUNNING, frequency = numberOfRunning)).associate { it.type to it.frequency }
  }

  /** Get the distance of the week per day  */
  fun getDistanceOfTheWeekPerDay(): List<Double> {


    val mondayOfTheWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    val distancePerDay = MutableList(7) { 0.0 }

    val statsOfTheWeek =
      workoutStatisticsFlow.value.filter{stats -> stats.type == WorkoutType.RUNNING}.filter { stats ->
        stats.date.isAfter(mondayOfTheWeek.minusDays(1)) &&
                stats.date.isBefore(today.plusWeeks(1))
      }
    statsOfTheWeek.forEach { stats -> distancePerDay[stats.date.dayOfWeek.value - 1] += stats.distance }

    return distancePerDay
  }



}
data class FrequencyWithWorkoutType( val type: WorkoutType, val frequency : Double )
