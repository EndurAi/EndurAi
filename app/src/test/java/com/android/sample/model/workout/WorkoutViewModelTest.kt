package com.android.sample.model.workout

import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class WorkoutViewModelTest {

  private lateinit var repository: WorkoutRepository<BodyWeightWorkout>
  private lateinit var localCache: WorkoutLocalCache
  private lateinit var workoutViewModel: WorkoutViewModel<BodyWeightWorkout>
  private val testDispatcher = StandardTestDispatcher()

  private val workout1 =
      BodyWeightWorkout(
          workoutId = "workout-1",
          name = "Morning Workout",
          description = "A great way to start your day!",
          date = LocalDateTime.of(2024, 11, 1, 0, 42),
          warmup = true)

  private val workout2 =
      BodyWeightWorkout(
          workoutId = "workout-2",
          name = "Workout after Bugnion lesson",
          description = "A great way to change my mind!",
          date = LocalDateTime.of(2024, 11, 1, 0, 42),
          warmup = true)

  @Before
  fun setUp() {
    runTest {
      Dispatchers.setMain(testDispatcher)

      repository =
          mock(WorkoutRepository::class.java as Class<WorkoutRepository<BodyWeightWorkout>>)
      localCache = mock(WorkoutLocalCache::class.java)
      `when`(localCache.getWorkouts()).thenReturn(flowOf(listOf(workout1, workout2)))

      workoutViewModel = WorkoutViewModel(repository, localCache, BodyWeightWorkout::class.java)
    }
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain() // Reset the main dispatcher to the original Main dispatcher
  }

  /** Verifies that the workouts flow starts empty. */
  @Test
  fun workoutsFlowShouldStartEmpty() = runBlocking {
    val workouts = workoutViewModel.workouts.first()
    assertThat(workouts, `is`(emptyList<Workout>()))
  }

  /**
   * Verifies that calling [getWorkouts] on the view model invokes the corresponding method on the
   * repository.
   */
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun getWorkoutsCallsRepository() = runTest {
    // Mock repository to simulate success
    `when`(repository.getDocuments(any(), any())).thenAnswer {
      val onSuccess = it.arguments[0] as (List<BodyWeightWorkout>) -> Unit
      onSuccess(emptyList()) // Simulate an empty repository
    }

    // Call the method to test
    workoutViewModel.getWorkouts()

    // Verify the interaction
    advanceUntilIdle() // Ensure all coroutines complete
    verify(repository).getDocuments(any(), any())
  }

  /**
   * Verifies that calling [addWorkout] on the view model invokes the corresponding method on the
   * repository with the correct parameters.
   */
  @Test
  fun addWorkoutCallsRepository() {
    workoutViewModel.addWorkout(workout1)
    verify(repository).addDocument(eq(workout1), any(), any())
  }

  /**
   * Verifies that calling [updateWorkout] on the view model invokes the corresponding method on the
   * repository with the correct parameters.
   */
  @Test
  fun updateWorkoutCallsRepository() {
    workoutViewModel.updateWorkout(workout1)
    verify(repository).updateDocument(eq(workout1), any(), any())
  }

  /**
   * Verifies that calling [deleteWorkoutById] on the view model invokes the corresponding method on
   * the repository with the correct parameters.
   */
  @Test
  fun deleteWorkoutByIdCallsRepository() {
    workoutViewModel.deleteWorkoutById(workout1.workoutId)
    verify(repository).deleteDocument(eq(workout1.workoutId), any(), any())
  }

  /** Verifies that selecting a workout updates the selected workout state in the view model. */
  @Test
  fun selectWorkoutUpdatesSelectedWorkout() {
    workoutViewModel.selectWorkout(workout1)
    val selectedWorkout = workoutViewModel.selectedWorkout.value!!
    assertThat(selectedWorkout, `is`(workout1))
  }

  /**
   * Verifies that calling [getWorkouts] updates the workouts flow with the list of workouts
   * returned by the repository.
   */
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun getWorkoutsUpdatesWorkoutsFlow() = runTest {
    // Mock repository behavior to return two workouts
    `when`(repository.getDocuments(any(), any())).thenAnswer {
      val onSuccess = it.arguments[0] as (List<BodyWeightWorkout>) -> Unit
      onSuccess(listOf(workout1, workout2)) // Return workouts
    }

    // Call the method to test
    workoutViewModel.getWorkouts()

    // Wait for flow to update
    advanceUntilIdle() // Ensure all coroutines complete

    // Verify the flow updates
    val workouts = workoutViewModel.workouts.first()
    assertThat(workouts, `is`(listOf(workout1, workout2)))
  }

  /** Verifies that adding a workout refreshes the workouts flow in the view model. */
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun addWorkoutRefreshesWorkoutsFlow() = runTest {
    // Initial cache state
    `when`(localCache.getWorkouts()).thenReturn(flowOf(listOf(workout1)))

    // Repository returns updated workouts
    `when`(repository.getDocuments(any(), any())).thenAnswer {
      val onSuccess = it.arguments[0] as (List<BodyWeightWorkout>) -> Unit
      onSuccess(listOf(workout1, workout2)) // Updated repository state
    }

    // Mock addWorkout behavior
    `when`(repository.addDocument(eq(workout2), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as () -> Unit
      onSuccess() // Simulate success
    }

    // Call the method to add a workout
    workoutViewModel.addWorkout(workout2)

    // Wait for flow to update
    advanceUntilIdle()

    // Verify flow reflects new state
    val updatedWorkouts = workoutViewModel.workouts.first()
    assertThat(updatedWorkouts, `is`(listOf(workout1, workout2)))
  }

  /**
   * Verifies that calling [transferWorkoutToDone] on the view model invokes the corresponding
   * method on the repository with the correct parameters and updates the flows.
   */
  @Test
  fun transferWorkoutToDoneUpdatesFlows() = runBlocking {
    // Mock the repository methods
    `when`(repository.transferDocumentToDone(eq(workout2.workoutId), any(), any())).thenAnswer {
      val onSuccess = {
        workoutViewModel.getWorkouts()
        workoutViewModel.getDoneWorkouts()
      }
      onSuccess() // Simulate success
    }
    `when`(repository.getDocuments(any(), any())).thenAnswer {
      val onSuccess = it.arguments[0] as (List<Workout>) -> Unit
      onSuccess(emptyList()) // No workouts remaining
    }
    `when`(repository.getDoneDocuments(any(), any())).thenAnswer {
      val onSuccess = it.arguments[0] as (List<Workout>) -> Unit
      onSuccess(listOf(workout1, workout2)) // Both workouts in "done"
    }

    // Call the method
    workoutViewModel.transferWorkoutToDone(workout2.workoutId)

    // Collect the flows to verify updates
    val workouts = workoutViewModel.workouts.first()
    val doneWorkouts = workoutViewModel.doneWorkouts.first()

    // Verify the assertions
    assertThat(workouts, `is`(emptyList()))
    assertThat(doneWorkouts, `is`(listOf(workout1, workout2)))

    // Verify repository interactions
    verify(repository).transferDocumentToDone(eq(workout2.workoutId), any(), any())
  }

  /**
   * Verifies that calling [importWorkoutFromDone] on the view model invokes the corresponding
   * method on the repository with the correct parameters and updates the flows.
   */
  @Test
  fun importWorkoutFromDoneUpdatesFlows() = runBlocking {
    // Mock the repository methods
    `when`(repository.importDocumentFromDone(eq(workout1.workoutId), any(), any())).thenAnswer {
      val onSuccess = {
        workoutViewModel.getWorkouts() // Refresh workouts flow
        workoutViewModel.getDoneWorkouts() // Refresh doneWorkouts flow
      }
      onSuccess() // Simulate success
    }

    `when`(repository.getDocuments(any(), any())).thenAnswer {
      val onSuccess = it.arguments[0] as (List<Workout>) -> Unit
      onSuccess(listOf(workout1, workout2)) // Updated workouts list
    }

    `when`(repository.getDoneDocuments(any(), any())).thenAnswer {
      val onSuccess = it.arguments[0] as (List<Workout>) -> Unit
      onSuccess(emptyList()) // No workouts left in "done"
    }

    // Call the method
    workoutViewModel.importWorkoutFromDone(workout1.workoutId)

    // Collect the flows to verify updates
    val workouts = workoutViewModel.workouts.first() // Collect current workouts
    val doneWorkouts = workoutViewModel.doneWorkouts.first() // Collect current doneWorkouts

    // Verify the assertions
    assertThat(workouts, `is`(listOf(workout1, workout2)))
    assertThat(doneWorkouts, `is`(emptyList()))

    // Verify repository interactions
    verify(repository).importDocumentFromDone(eq(workout1.workoutId), any(), any())
  }
}
