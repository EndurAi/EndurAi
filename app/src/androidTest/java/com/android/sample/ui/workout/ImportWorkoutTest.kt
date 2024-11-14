package com.android.sample.ui.workout

import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Exercise
import com.android.sample.model.workout.ExerciseDetail
import com.android.sample.model.workout.ExerciseType
import com.android.sample.model.workout.WorkoutRepository
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.navigation.NavigationActions
import java.time.LocalDateTime
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

class ImportWorkoutTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var bodyWeightViewModel: WorkoutViewModel<BodyWeightWorkout>
  private lateinit var yogaViewModel: WorkoutViewModel<YogaWorkout>
  private lateinit var navigationActions: NavigationActions
  private lateinit var bodyWeightRepo: WorkoutRepository<BodyWeightWorkout>
  private lateinit var yogaRepo: WorkoutRepository<YogaWorkout>

  @Before
  fun setUp() {
    bodyWeightRepo = mock()
    yogaRepo = mock()

    val bodyWeightWorkouts =
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
                            "2", ExerciseType.JUMPING_JACKS, ExerciseDetail.RepetitionBased(10)))),
            BodyWeightWorkout(
                "2",
                "NightSes",
                "Hold for 60 seconds",
                false,
                date = LocalDateTime.of(2024, 11, 1, 0, 43)))
    val yogaWorkouts: List<YogaWorkout> = listOf()

    `when`(bodyWeightRepo.getDocuments(any(), any())).then {
      it.getArgument<(List<BodyWeightWorkout>) -> Unit>(0)(bodyWeightWorkouts)
    }

    `when`(yogaRepo.getDocuments(any(), any())).then {
      it.getArgument<(List<YogaWorkout>) -> Unit>(0)(yogaWorkouts)
    }
    `when`(bodyWeightRepo.getNewUid()).thenReturn("mocked-bodyweight-uid")
    `when`(bodyWeightRepo.addDocument(any(), any(), any())).then {
      val workout = it.getArgument<BodyWeightWorkout>(0)
      bodyWeightWorkouts.add(2, workout)
    }
    bodyWeightViewModel = WorkoutViewModel(bodyWeightRepo)
    yogaViewModel = WorkoutViewModel(yogaRepo)

    navigationActions = mock(NavigationActions::class.java)
    bodyWeightViewModel.getWorkouts()
  }

  // Test the workout selection screen
  @Test
  fun testWorkoutSelectionScreen() {
    // Set the content of the screen
    composeTestRule.setContent {
      WorkoutSelectionScreen(
          navigationActions = navigationActions,
          viewModel = bodyWeightViewModel,
      )
    }
    // Check that the screen is displayed
    composeTestRule.onNodeWithTag("WorkoutSelectionScreen").assertIsDisplayed()
    // Check that the body weight workout is displayed
    composeTestRule.onAllNodesWithTag("WorkoutCard").assertCountEquals(2)
    // Check that the body weight workout is clickable
    composeTestRule.onAllNodesWithTag("WorkoutCard").assertAll(hasClickAction())
  }

  // Test that you can delete an exercise from the workout
  @Test
  fun testDeleteWorkout() {
    // Set the content of the screen
    bodyWeightViewModel.selectWorkout(bodyWeightViewModel.workouts.value[0])
    composeTestRule.setContent {
      WorkoutCreationScreen(
          navigationActions = navigationActions,
          workoutViewModel = bodyWeightViewModel,
          workoutType = WorkoutType.BODY_WEIGHT,
          isImported = true)
    }
    composeTestRule.onNodeWithTag("nextButton").performClick()
    // Check that the body weight workout is displayed
    composeTestRule.onAllNodesWithTag("exerciseCard").assertCountEquals(2)
    // Check that the body weight workout is clickable
    composeTestRule.onAllNodesWithTag("exerciseCard").assertAll(hasClickAction())
    // Click on the first workout
    composeTestRule.onAllNodesWithTag("exerciseCard")[0].performClick()
    // Check that the delete button is displayed
    composeTestRule.onNodeWithTag("deleteExerciseButton").assertIsDisplayed()
    // Click on the delete button
    composeTestRule.onNodeWithTag("deleteExerciseButton").performClick()
    // Check that the workout is deleted
    composeTestRule.onAllNodesWithTag("exerciseCard").assertCountEquals(1)
  }

  @Test
  fun testEditExercise() {
    bodyWeightViewModel.selectWorkout(bodyWeightViewModel.workouts.value[0])
    composeTestRule.setContent {
      WorkoutCreationScreen(
          navigationActions = navigationActions,
          workoutViewModel = bodyWeightViewModel,
          workoutType = WorkoutType.BODY_WEIGHT,
          isImported = true)
    }
    composeTestRule.onNodeWithTag("nextButton").performClick()
    composeTestRule.onAllNodesWithTag("exerciseCard").assertAll(hasClickAction())
    composeTestRule.onAllNodesWithTag("exerciseCard")[0].performClick()
    composeTestRule.onNodeWithTag("repetitionsTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("repetitionsTextField").performTextClearance()
    composeTestRule.onNodeWithTag("repetitionsTextField").performTextInput("30")
    composeTestRule.onNodeWithTag("addExerciseConfirmButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addExerciseConfirmButton").performClick()
    composeTestRule.onNodeWithTag("saveButton").performClick()
    bodyWeightViewModel.getWorkouts()
    // Check that the workout is indeed updated
    assertThat(
        (bodyWeightViewModel.workouts.value[2].exercises[0].detail
                as ExerciseDetail.RepetitionBased)
            .repetitions,
        `is`(CoreMatchers.not(CoreMatchers.equalTo(20))))
  }
}
