package com.android.sample.screen

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.ExerciseType
import com.android.sample.model.workout.WorkoutRepository
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.workout.WorkoutCreationScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class WorkoutCreationScreenTest {
  private lateinit var mockYogaWorkoutViewModel: WorkoutViewModel<YogaWorkout>
  private lateinit var mockBodyWeightWorkoutViewModel: WorkoutViewModel<BodyWeightWorkout>
  private lateinit var mockNavHostController: NavigationActions
  private lateinit var mockYogaWorkoutRepository: WorkoutRepository<YogaWorkout>
  private lateinit var mockBodyWeightWorkoutRepository: WorkoutRepository<BodyWeightWorkout>

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    // Mock the WorkoutRepositories
    mockYogaWorkoutRepository =
        mock(WorkoutRepository::class.java as Class<WorkoutRepository<YogaWorkout>>)
    mockBodyWeightWorkoutRepository =
        mock(WorkoutRepository::class.java as Class<WorkoutRepository<BodyWeightWorkout>>)
    `when`(mockYogaWorkoutRepository.getNewUid()).thenReturn("mocked-yoga-uid")
    `when`(mockBodyWeightWorkoutRepository.getNewUid()).thenReturn("mocked-bodyweight-uid")

    // Mock the ViewModels and NavigationActions
    mockYogaWorkoutViewModel =
        WorkoutViewModel(mockYogaWorkoutRepository) as WorkoutViewModel<YogaWorkout>
    mockBodyWeightWorkoutViewModel =
        WorkoutViewModel(mockBodyWeightWorkoutRepository) as WorkoutViewModel<BodyWeightWorkout>
    mockNavHostController = mock(NavigationActions::class.java)
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

    composeTestRule.onNodeWithTag("workoutTopBar").assertIsDisplayed()
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
    composeTestRule.onNodeWithTag("exerciseTypeSUN_SALUTATION").performClick()
    composeTestRule
        .onNodeWithTag("selectedExerciseType")
        .assertTextEquals("Selected Exercise: ${ExerciseType.SUN_SALUTATION}")
    composeTestRule.onNodeWithTag("timeBasedButton").assertIsDisplayed().assertHasClickAction()
    composeTestRule
        .onNodeWithTag("repetitionBasedButton")
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule.onNodeWithTag("timeBasedButton").performClick()
    composeTestRule.onNodeWithTag("durationTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("setsTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("repetitionBasedButton").performClick()
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
  fun displayAllComponentsForBodyWeightWorkout() {
    composeTestRule.setContent {
      WorkoutCreationScreen(
          navigationActions = mockNavHostController,
          workoutType = WorkoutType.BODY_WEIGHT,
          workoutViewModel = mockBodyWeightWorkoutViewModel,
          isImported = false)
    }

    composeTestRule.onNodeWithTag("workoutTopBar").assertIsDisplayed()
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
    composeTestRule.onNodeWithTag("timeBasedButton").assertIsDisplayed().assertHasClickAction()
    composeTestRule
        .onNodeWithTag("repetitionBasedButton")
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule.onNodeWithTag("timeBasedButton").performClick()
    composeTestRule.onNodeWithTag("durationTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("setsTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("repetitionBasedButton").performClick()
    composeTestRule.onNodeWithTag("repetitionsTextField").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("addExerciseConfirmButton")
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule.onNodeWithTag("addExerciseConfirmButton").performClick()
    composeTestRule.onNodeWithTag("exerciseCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("saveButton").assertIsDisplayed().assertHasClickAction()
  }
}
