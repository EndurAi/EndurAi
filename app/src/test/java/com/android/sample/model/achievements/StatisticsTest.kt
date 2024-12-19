package com.android.sample.model.achievements

import com.android.sample.model.workout.WorkoutType
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Test

/** Unit tests for the `Statistics` class to validate its functionality and ensure correctness. */
class StatisticsTest {
  private lateinit var statistics: Statistics

  // Mock workout statistics data used for testing
  private val mockWorkoutStatistics =
      listOf(
          WorkoutStatistics(
              id = "1",
              date = LocalDateTime.of(2024, 12, 1, 10, 0),
              caloriesBurnt = 200,
              type = WorkoutType.BODY_WEIGHT,
              distance = 0.0),
          WorkoutStatistics(
              id = "2",
              date = LocalDateTime.of(2024, 12, 3, 15, 30),
              caloriesBurnt = 300,
              type = WorkoutType.RUNNING,
              distance = 0.0),
          WorkoutStatistics(
              id = "3",
              date = LocalDateTime.of(2024, 12, 5, 18, 0),
              caloriesBurnt = 400,
              type = WorkoutType.YOGA,
              distance = 0.0))

  /** Sets up the test environment by initializing the `Statistics` instance with mock data. */
  @Before
  fun setUp() {
    val statisticsStateFlow = MutableStateFlow(mockWorkoutStatistics)
    statistics = Statistics(statisticsStateFlow)
  }

  /**
   * Tests the `getTotalWorkouts` method to ensure it correctly calculates the total number of
   * workouts in the dataset.
   */
  @Test
  fun testGetTotalWorkouts() {
    val totalWorkouts = statistics.getTotalWorkouts()
    assert(3 == totalWorkouts)
  }

  /** Tests the `getDates` method to ensure it returns the correct list of workout dates. */
  @Test
  fun testGetDates() {
    val dates = statistics.getDates()
    assert(
        listOf(
            LocalDateTime.of(2024, 12, 1, 10, 0),
            LocalDateTime.of(2024, 12, 3, 15, 30),
            LocalDateTime.of(2024, 12, 5, 18, 0)) == dates)
  }

  /**
   * Tests the `getTotalCalories` method to ensure it correctly calculates the total calories burnt
   * across all workouts.
   */
  @Test
  fun testGetTotalCalories() {
    val totalCalories = statistics.getTotalCalories()
    assert(900 == totalCalories)
  }

  /** Tests the `getWorkoutsByType` method to ensure it groups workouts correctly by their type. */
  @Test
  fun testGetWorkoutsByType() {
    val workoutsByType = statistics.getWorkoutsByType()
    val expectedWorkoutsByType =
        mapOf(
            WorkoutType.BODY_WEIGHT to listOf(mockWorkoutStatistics[0]),
            WorkoutType.RUNNING to listOf(mockWorkoutStatistics[1]),
            WorkoutType.YOGA to listOf(mockWorkoutStatistics[2]))
    assert(expectedWorkoutsByType == workoutsByType)
  }
}
