package com.android.sample.model.achievements

import com.android.sample.model.workout.WorkoutType
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class StatisticsTest {
    private lateinit var statistics: Statistics

    private val mockWorkoutStatistics = listOf(
        WorkoutStatistics(
            id = "1",
            date = LocalDateTime.of(2024, 12, 1, 10, 0),
            caloriesBurnt = 200,
            type = WorkoutType.BODY_WEIGHT
        ),
        WorkoutStatistics(
            id = "2",
            date = LocalDateTime.of(2024, 12, 3, 15, 30),
            caloriesBurnt = 300,
            type = WorkoutType.RUNNING
        ),
        WorkoutStatistics(
            id = "3",
            date = LocalDateTime.of(2024, 12, 5, 18, 0),
            caloriesBurnt = 400,
            type = WorkoutType.YOGA
        )
    )

    @Before
    fun setUp() {
        val statisticsStateFlow = MutableStateFlow(mockWorkoutStatistics)
        statistics = Statistics(statisticsStateFlow)
    }

    @Test
    fun testGetTotalWorkouts() {
        val totalWorkouts = statistics.getTotalWorkouts()
        assert(3 == totalWorkouts)
    }

    @Test
    fun testGetDates() {
        val dates = statistics.getDates()
        assert(
            listOf(
                LocalDateTime.of(2024, 12, 1, 10, 0),
                LocalDateTime.of(2024, 12, 3, 15, 30),
                LocalDateTime.of(2024, 12, 5, 18, 0)
            ) == dates)
    }

    @Test
    fun testGetTotalCalories() {
        val totalCalories = statistics.getTotalCalories()
        assert(900 ==  totalCalories)
    }

    @Test
    fun testGetWorkoutsByType() {
        val workoutsByType = statistics.getWorkoutsByType()
        val expectedWorkoutsByType = mapOf(
            WorkoutType.BODY_WEIGHT to listOf(mockWorkoutStatistics[0]),
            WorkoutType.RUNNING to listOf(mockWorkoutStatistics[1]),
            WorkoutType.YOGA to listOf(mockWorkoutStatistics[2])
        )
        assert(expectedWorkoutsByType == workoutsByType)
    }
}
