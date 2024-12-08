package com.android.sample.model.achievements

import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Exercise
import com.android.sample.model.workout.ExerciseDetail
import com.android.sample.model.workout.ExerciseType
import com.android.sample.model.workout.WorkoutType
import com.android.sample.ui.workout.ExerciseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import java.time.LocalDateTime

class StatisticsViewModelTest {

    private lateinit var repository: StatisticsRepository
    private lateinit var statisticsViewModel: StatisticsViewModel
    private lateinit var userAccountViewModel: UserAccountViewModel

    private val workout = BodyWeightWorkout(
        workoutId = "1",
        name = "Morning Routine",
        description = "A short bodyweight workout to start the day.",
        warmup = true,
        userIdSet = mutableSetOf(),
        date = LocalDateTime.of(2024, 12, 8, 7, 30)
    )

    private val exerciseStateList = listOf(
        ExerciseState(Exercise("1", ExerciseType.PUSH_UPS, ExerciseDetail.RepetitionBased(10)), isDone = true),
        ExerciseState(Exercise("2", ExerciseType.SQUATS, ExerciseDetail.RepetitionBased(10)), isDone = true)
    )

    private val workoutStats = WorkoutStatistics(
        id = "1",
        date = LocalDateTime.of(2024, 12, 8, 7, 30),
        caloriesBurnt = 3,
        type = WorkoutType.BODY_WEIGHT
    )

    private val workoutsStats = listOf(
        workoutStats,
        WorkoutStatistics(
            id = "2",
            date = LocalDateTime.of(2024, 12, 7, 18, 0),
            caloriesBurnt = 4,
            type = WorkoutType.RUNNING
        )
    )

    private val account = UserAccount(
        userId = "user123",
        firstName = "John",
        lastName = "Doe",
        height = 180f,
        weight = 75f
    )

    @Before
    fun setUp() {
        // Mock the repository
        repository = mock(StatisticsRepository::class.java)
        `when`(repository.getStatistics(any(), any())).thenAnswer { invocation ->
            val onSuccess = invocation.arguments[0] as (List<WorkoutStatistics>) -> Unit
            onSuccess(workoutsStats)
        }

        `when`(repository.addWorkout(eq(workoutStats), any(), any())).thenAnswer { invocation ->
            val onSuccess = invocation.arguments[1] as () -> Unit
            onSuccess()
        }

        // Mock the UserAccountViewModel
        userAccountViewModel = mock(UserAccountViewModel::class.java)
        `when`(userAccountViewModel.userAccount).thenReturn(MutableStateFlow(account))

        // Initialize the ViewModel with the mocked repository
        statisticsViewModel = StatisticsViewModel(repository)
    }

    @Test
    fun getWorkoutStatisticsCallsRepository() = runBlocking {
        statisticsViewModel.getWorkoutStatistics()
        verify(repository).getStatistics(any(), any())
    }

    @Test
    fun workoutStatisticsFlowShouldStartWithCorrectList() = runBlocking {
        statisticsViewModel.getWorkoutStatistics()
        val workoutStatistics = statisticsViewModel.workoutStatistics.value
        assert(workoutStatistics == workoutsStats)
    }

    @Test
    fun addWorkoutStatisticsCallsRepository() = runBlocking {
        statisticsViewModel.addWorkoutStatistics(workoutStats)
        verify(repository).addWorkout(eq(workoutStats), any(), any())
    }

    @Test
    fun computeWorkoutStatisticsReturnsCorrectWorkoutStatistics() = runBlocking {
        val computedStats = statisticsViewModel.computeWorkoutStatistics(
            workout,
            exerciseStateList,
            userAccountViewModel
        )
        assert(computedStats.id == workoutStats.id)
        assert(computedStats.duration == workoutStats.duration)
        assert(computedStats.caloriesBurnt == workoutStats.caloriesBurnt)
        assert(computedStats.type == workoutStats.type)
        assert(computedStats.date == workoutStats.date)
    }
}
