package com.android.sample.screen

import android.content.Context
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Exercise
import com.android.sample.model.workout.ExerciseDetail
import com.android.sample.model.workout.ExerciseType
import com.android.sample.model.workout.WorkoutLocalCache
import com.android.sample.model.workout.WorkoutRepository
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.workout.WorkoutCreationScreen
import java.time.LocalDateTime
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class WorkoutCreationScreenTest {
  private lateinit var mockYogaWorkoutViewModel: WorkoutViewModel<YogaWorkout>
  private lateinit var mockBodyWeightWorkoutViewModel: WorkoutViewModel<BodyWeightWorkout>
  private lateinit var mockNavHostController: NavigationActions
  private lateinit var mockYogaWorkoutRepository: WorkoutRepository<YogaWorkout>
  private lateinit var mockBodyWeightWorkoutRepository: WorkoutRepository<BodyWeightWorkout>
  private lateinit var bodyWeightWorkouts: MutableList<BodyWeightWorkout>

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    runTest {
      // Mock the WorkoutRepositories
      mockYogaWorkoutRepository = mock()
      mockBodyWeightWorkoutRepository = mock()

      // Get application context for testing (just like in your working snippet)
      val context = ApplicationProvider.getApplicationContext<Context>()

      // Use a real WorkoutLocalCache with a real Context
      // This ensures no NullPointerException from null context.
      val workoutLocalCache = WorkoutLocalCache(context)

      bodyWeightWorkouts =
          mutableListOf(
              BodyWeightWorkout(
                  "1",
                  "NopainNogain",
                  "Do 20 push-ups",
                  false,
                  date = LocalDateTime.of(2024, 11, 1, 0, 42),
                  exercises =
                      mutableListOf(
                          Exercise("1", ExerciseType.PUSH_UPS, ExerciseDetail.RepetitionBased(20)),
                          Exercise(
                              "2",
                              ExerciseType.JUMPING_JACKS,
                              ExerciseDetail.RepetitionBased(10)))),
              BodyWeightWorkout(
                  "2",
                  "NightSes",
                  "Hold for 60 seconds",
                  false,
                  date = LocalDateTime.of(2024, 11, 1, 0, 43)))

      `when`(mockBodyWeightWorkoutRepository.getDocuments(any(), any())).then {
        it.getArgument<(List<BodyWeightWorkout>) -> Unit>(0)(bodyWeightWorkouts)
      }
      `when`(mockBodyWeightWorkoutRepository.addDocument(any(), any(), any())).then {
        val workout = it.getArgument<BodyWeightWorkout>(0)
        bodyWeightWorkouts.add(2, workout)
      }
      `when`(mockBodyWeightWorkoutRepository.updateDocument(any(), any(), any())).then {}

      `when`(mockYogaWorkoutRepository.getNewUid()).thenReturn("mocked-yoga-uid")
      `when`(mockBodyWeightWorkoutRepository.getNewUid()).thenReturn("mocked-bodyweight-uid")

      // Mock the ViewModels and NavigationActions
      mockYogaWorkoutViewModel =
          WorkoutViewModel(mockYogaWorkoutRepository, workoutLocalCache, YogaWorkout::class.java)
      mockBodyWeightWorkoutViewModel =
          WorkoutViewModel(
              mockBodyWeightWorkoutRepository, workoutLocalCache, BodyWeightWorkout::class.java)
      mockNavHostController = mock(NavigationActions::class.java)
    }
  }

  @Test
  fun displayAllComponentsForYogaWorkout() {
    composeTestRule.setContent {
      WorkoutCreationScreen(
          navigationActions = mockNavHostController,
          workoutType = WorkoutType.YOGA,
          workoutViewModel = mockYogaWorkoutViewModel,
          isImported = false)
    }

    composeTestRule.onNodeWithTag("TopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nameTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("descriptionTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nextButton").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithTag("nextButton").assertTextEquals("Next")
    composeTestRule.onNodeWithTag("nextButton").performClick()
    composeTestRule.onNodeWithTag("addExerciseButton").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithTag("addExerciseButton").performClick()
    composeTestRule
        .onNodeWithTag("selectExerciseTypeButton")
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule.onNodeWithTag("selectExerciseTypeButton").performClick()
    for (exerciseType in ExerciseType.entries.filter { it.workoutType == WorkoutType.YOGA }) {
      composeTestRule.onNodeWithTag("exerciseType${exerciseType.name}").assertIsDisplayed()
    }
    composeTestRule.onNodeWithTag("exerciseTypeUPWARD_FACING_DOG").performClick()
    composeTestRule
        .onNodeWithTag("selectedExerciseType")
        .assertTextEquals("Selected Exercise: ${ExerciseType.UPWARD_FACING_DOG}")

    composeTestRule.onNodeWithTag("durationTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("setsTextField").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("addExerciseConfirmButton")
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule.onNodeWithTag("addExerciseConfirmButton").performClick()
    composeTestRule.onNodeWithTag("exerciseCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("saveButton").assertIsDisplayed().assertHasClickAction()
  }

  @Test
  fun displayAllComponentsForBodyWeightWorkout() {
    composeTestRule.setContent {
      WorkoutCreationScreen(
          navigationActions = mockNavHostController,
          workoutType = WorkoutType.BODY_WEIGHT,
          workoutViewModel = mockBodyWeightWorkoutViewModel,
          isImported = false)
    }

    composeTestRule.onNodeWithTag("TopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nameTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("descriptionTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nextButton").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithTag("nextButton").assertTextEquals("Next")
    composeTestRule.onNodeWithTag("nextButton").performClick()
    composeTestRule.onNodeWithTag("addExerciseButton").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithTag("addExerciseButton").performClick()
    composeTestRule
        .onNodeWithTag("selectExerciseTypeButton")
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule.onNodeWithTag("selectExerciseTypeButton").performClick()
    for (exerciseType in
        ExerciseType.entries.filter { it.workoutType == WorkoutType.BODY_WEIGHT }) {
      composeTestRule.onNodeWithTag("exerciseType${exerciseType.name}").assertIsDisplayed()
    }
    composeTestRule.onNodeWithTag("exerciseTypePUSH_UPS").performClick()
    composeTestRule
        .onNodeWithTag("selectedExerciseType")
        .assertTextEquals("Selected Exercise: ${ExerciseType.PUSH_UPS}")

    // check that PushUps is a repetition based exercise
    // check default value
    composeTestRule.onNodeWithTag("repetitionsTextField").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("addExerciseConfirmButton")
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule.onNodeWithTag("addExerciseConfirmButton").performClick()
    composeTestRule.onNodeWithTag("exerciseCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("saveButton").assertIsDisplayed().assertHasClickAction()
  }

  @Test
  fun createDefaultBodyWeightExerciseWhenNoChangesRequested() {
    composeTestRule.setContent {
      WorkoutCreationScreen(
          navigationActions = mockNavHostController,
          workoutType = WorkoutType.BODY_WEIGHT,
          workoutViewModel = mockBodyWeightWorkoutViewModel,
          isImported = false)
    }

    composeTestRule.onNodeWithTag("nextButton").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithTag("nextButton").assertTextEquals("Next")
    composeTestRule.onNodeWithTag("nextButton").performClick()

    composeTestRule.onNodeWithTag("addExerciseButton").performClick()

    ExerciseType.entries
        .filter { it.workoutType == WorkoutType.BODY_WEIGHT }
        .forEach { exerciseType ->
          composeTestRule.onNodeWithTag("selectExerciseTypeButton").performClick()
          // Select the exercise
          composeTestRule.onNodeWithTag("exerciseType${exerciseType.name}").performClick()
          composeTestRule
              .onNodeWithTag("selectedExerciseType")
              .assertTextEquals("Selected Exercise: ${exerciseType.toString()}")

          // check that default value are displayed
          when (exerciseType.detail) {
            is ExerciseDetail.RepetitionBased -> {
              composeTestRule
                  .onNodeWithTag("repetitionsTextField")
                  .assertIsDisplayed()
                  .assertTextContains(
                      (exerciseType.detail as ExerciseDetail.RepetitionBased)
                          .repetitions
                          .toString()) // default value of the repetition
            }
            is ExerciseDetail.TimeBased -> {
              composeTestRule
                  .onNodeWithTag("durationTextField")
                  .assertIsDisplayed()
                  .assertTextContains(
                      (exerciseType.detail as ExerciseDetail.TimeBased)
                          .durationInSeconds
                          .toString()) // default value of the duration in second
              composeTestRule
                  .onNodeWithTag("setsTextField")
                  .assertIsDisplayed()
                  .assertTextContains(
                      (exerciseType.detail as ExerciseDetail.TimeBased)
                          .sets
                          .toString()) // default value of the duration in second
            }
          }
        }
  }

  @Test
  fun createDefaultYogaExerciseWhenNoChangesRequested() {
    composeTestRule.setContent {
      WorkoutCreationScreen(
          navigationActions = mockNavHostController,
          workoutType = WorkoutType.YOGA,
          workoutViewModel = mockYogaWorkoutViewModel,
          isImported = false)
    }

    composeTestRule.onNodeWithTag("nextButton").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithTag("nextButton").assertTextEquals("Next")
    composeTestRule.onNodeWithTag("nextButton").performClick()

    composeTestRule.onNodeWithTag("addExerciseButton").performClick()

    ExerciseType.entries
        .filter { it.workoutType == WorkoutType.YOGA }
        .forEach { exerciseType ->
          composeTestRule.onNodeWithTag("selectExerciseTypeButton").performClick()
          // Select the  exercise
          composeTestRule.onNodeWithTag("exerciseType${exerciseType.name}").performClick()
          composeTestRule
              .onNodeWithTag("selectedExerciseType")
              .assertTextEquals("Selected Exercise: ${exerciseType.toString()}")

          // check that default value are displayed
          when (exerciseType.detail) {
            is ExerciseDetail.RepetitionBased -> {
              composeTestRule
                  .onNodeWithTag("repetitionsTextField")
                  .assertIsDisplayed()
                  .assertTextContains(
                      (exerciseType.detail as ExerciseDetail.RepetitionBased)
                          .repetitions
                          .toString()) // default value of the repetition
            }
            is ExerciseDetail.TimeBased -> {
              composeTestRule
                  .onNodeWithTag("durationTextField")
                  .assertIsDisplayed()
                  .assertTextContains(
                      (exerciseType.detail as ExerciseDetail.TimeBased)
                          .durationInSeconds
                          .toString()) // default value of the duration in second
              composeTestRule
                  .onNodeWithTag("setsTextField")
                  .assertIsDisplayed()
                  .assertTextContains(
                      (exerciseType.detail as ExerciseDetail.TimeBased)
                          .sets
                          .toString()) // default value of the duration in second
            }
          }
        }
  }

  @Test
  fun testEditWorkoutCallsUpdateWorkout() {
    // Set the content of the screen
    mockBodyWeightWorkoutViewModel.selectWorkout(bodyWeightWorkouts[0])
    composeTestRule.setContent {
      WorkoutCreationScreen(
          navigationActions = mockNavHostController,
          workoutViewModel = mockBodyWeightWorkoutViewModel,
          workoutType = WorkoutType.BODY_WEIGHT,
          isImported = true,
          editing = true)
    }

    composeTestRule.onNodeWithTag("saveButton").performClick()
    // Check that the updateWorkout function is called
    verify(mockBodyWeightWorkoutRepository).updateDocument(any(), any(), any())
  }

  @Test
  fun testDeleteWorkoutCallsDeleteWorkout() {
    // Set the content of the screen
    mockBodyWeightWorkoutViewModel.selectWorkout(bodyWeightWorkouts[0])
    composeTestRule.setContent {
      WorkoutCreationScreen(
          navigationActions = mockNavHostController,
          workoutViewModel = mockBodyWeightWorkoutViewModel,
          workoutType = WorkoutType.BODY_WEIGHT,
          isImported = true,
          editing = true)
    }

    composeTestRule
        .onNodeWithTag("deleteButton")
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
        .performClick()
    // Check that the deleteDocument function is called
    verify(mockBodyWeightWorkoutRepository).deleteDocument(any(), any(), any())
  }
}
