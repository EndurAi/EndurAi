package com.android.sample.ui.workout

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Exercise
import com.android.sample.model.workout.ExerciseDetail
import com.android.sample.model.workout.ExerciseType
import com.android.sample.model.workout.WarmUp
import com.android.sample.model.workout.WarmUpViewModel
import com.android.sample.model.workout.WorkoutRepository
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import java.time.LocalDateTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

class WorkoutScreenTest {
  private lateinit var navigationActions: NavigationActions
  private lateinit var bodyWeightViewModel: WorkoutViewModel<BodyWeightWorkout>
  private lateinit var yogaViewModel: WorkoutViewModel<YogaWorkout>
  private lateinit var warmUpViewModel: WarmUpViewModel
  private lateinit var yogaRepo: WorkoutRepository<YogaWorkout>
  private lateinit var bodyWeightRepo: WorkoutRepository<BodyWeightWorkout>
  private lateinit var warmUpRepo: WorkoutRepository<WarmUp>

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    bodyWeightRepo = mock()
    yogaRepo = mock()
    warmUpRepo = mock()

    val exerciseList =
        mutableListOf(
            Exercise(
                id = "1",
                type = ExerciseType.SQUATS,
                ExerciseDetail.RepetitionBased(repetitions = 3)),
            Exercise(
                id = "2",
                type = ExerciseType.PLANK,
                ExerciseDetail.TimeBased(durationInSeconds = 30, sets = 1)),
            Exercise(
                id = "3",
                type = ExerciseType.SQUATS,
                ExerciseDetail.RepetitionBased(repetitions = 3)),
        )

    val bodyWeightWorkouts =
        listOf(
            BodyWeightWorkout(
                "2",
                "MyWorkout",
                "Hold for 60 seconds",
                false,
                exercises = exerciseList,
                date = LocalDateTime.of(2024, 11, 10, 2, 1)))
    val yogaWorkouts: List<YogaWorkout> = listOf()

    `when`(bodyWeightRepo.getDocuments(any(), any())).then {
      it.getArgument<(List<BodyWeightWorkout>) -> Unit>(0)(bodyWeightWorkouts)
    }

    `when`(yogaRepo.getDocuments(any(), any())).then {
      it.getArgument<(List<YogaWorkout>) -> Unit>(0)(yogaWorkouts)
    }

    `when`(yogaRepo.getDocuments(any(), any())).then {
      it.getArgument<(List<YogaWorkout>) -> Unit>(0)(listOf())
    }

    bodyWeightViewModel = WorkoutViewModel(bodyWeightRepo)
    yogaViewModel = WorkoutViewModel(yogaRepo)

    warmUpViewModel = WarmUpViewModel(repository = warmUpRepo)

    // Mock the NavigationActions
    navigationActions = mock(NavigationActions::class.java)

    bodyWeightViewModel.getWorkouts()
    bodyWeightViewModel.selectWorkout(bodyWeightViewModel.workouts.value[0])
  }

  @Test
  fun presentationIsDisplayed() {
    composeTestRule.setContent {
      WorkoutScreen(
          navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          warmUpViewModel = warmUpViewModel,
          workoutType = WorkoutType.BODY_WEIGHT,
      )
    }
    // ArrowBack
    composeTestRule.onNodeWithTag("ArrowBackButton").assertIsDisplayed()

    // Test if the presentation screen is well displayed
    // Workout Name
    composeTestRule.onNodeWithTag("WorkoutName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("WorkoutName").assertTextEquals("MyWorkout")
    // Exercise name
    composeTestRule.onNodeWithTag("ExerciseName").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("ExerciseName")
        .assertTextEquals(
            bodyWeightViewModel.selectedWorkout.value?.exercises?.get(0)?.type.toString())
    // Exercise description
    composeTestRule.onNodeWithTag("ExerciseDescription").assertIsDisplayed()
    bodyWeightViewModel.selectedWorkout.value?.exercises?.get(0)?.type?.let {
      composeTestRule.onNodeWithTag("ExerciseDescription").assertTextEquals(it.getInstruction())
    }

    // Goal Icon and value

    composeTestRule.onNodeWithTag("GoalIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("GoalValue").assertIsDisplayed()
    composeTestRule.onNodeWithTag("GoalValue").assertTextEquals("3 Rep.")

    // Skip and start button are displayed

    composeTestRule.onNodeWithTag("SkipButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SkipButton").assertTextEquals("Skip")

    composeTestRule.onNodeWithTag("StartButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("StartButton").assertTextEquals("Start")
    composeTestRule.onNodeWithTag("recordSwitch").assertIsDisplayed()

    // VideoPlayer
    composeTestRule.onNodeWithTag("VideoPlayer").assertIsDisplayed()

    // ClickOnStart to start the 1st activity
    composeTestRule.onNodeWithTag("StartButton").performClick()
  }

  @Test
  fun startingExerciseIsDisplayed() {
    composeTestRule.setContent {
      WorkoutScreen(
          navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          warmUpViewModel = warmUpViewModel,
          workoutType = WorkoutType.BODY_WEIGHT)
    }

    // ClickOnStart to start the 1st activity
    composeTestRule.onNodeWithTag("StartButton").performClick()

    // check that permanent composable are still there
    // ArrowBack
    composeTestRule.onNodeWithTag("ArrowBackButton").assertIsDisplayed()
    // Workout Name
    composeTestRule.onNodeWithTag("WorkoutName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("WorkoutName").assertTextEquals("MyWorkout")
    // Exercise name
    composeTestRule.onNodeWithTag("ExerciseName").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("ExerciseName")
        .assertTextEquals(
            bodyWeightViewModel.selectedWorkout.value?.exercises?.get(0)?.type.toString())
    // Exercise description
    composeTestRule.onNodeWithTag("ExerciseDescription").assertIsDisplayed()
    bodyWeightViewModel.selectedWorkout.value?.exercises?.get(0)?.type?.let {
      composeTestRule.onNodeWithTag("ExerciseDescription").assertTextEquals(it.getInstruction())
    }

    // Check that the presentation specific components are hidden
    // startButton is not displayed
    composeTestRule.onNodeWithTag("StartButton").assertIsNotDisplayed()
    // VideoPlayer is not displayed
    composeTestRule.onNodeWithTag("VideoPlayer").assertIsNotDisplayed()

    // As it rep based, show the ExercisetypeIcon and not the timer
    composeTestRule.onNodeWithTag("ExerciseTypeIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("CountDownTimer").assertIsNotDisplayed()
  }

  @Test
  fun skipButtonGoesToNextExercise() {
    composeTestRule.setContent {
      WorkoutScreen(
          navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          warmUpViewModel = warmUpViewModel,
          workoutType = WorkoutType.BODY_WEIGHT)
    }

    // Exercise name
    composeTestRule.onNodeWithTag("ExerciseName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ExerciseName").assertTextEquals(ExerciseType.SQUATS.toString())

    // Skip the 1st activity
    composeTestRule.onNodeWithTag("SkipButton").performClick()

    // Exercise name is correctly updated
    composeTestRule.onNodeWithTag("ExerciseName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ExerciseName").assertTextEquals(ExerciseType.PLANK.toString())

    // the video box is there again
    composeTestRule.onNodeWithTag("VideoPlayer").assertIsDisplayed()

    // Check that the goal is well updated

    composeTestRule.onNodeWithTag("GoalValue").assertIsDisplayed()
    composeTestRule.onNodeWithTag("GoalValue").assertTextEquals("00:30")
  }

  @Test
  fun skipButtonWorkDuringExercise() {
    composeTestRule.setContent {
      WorkoutScreen(
          navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          warmUpViewModel = warmUpViewModel,
          workoutType = WorkoutType.BODY_WEIGHT)
    }

    // Exercise name
    composeTestRule.onNodeWithTag("ExerciseName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ExerciseName").assertTextEquals(ExerciseType.SQUATS.toString())

    // Start 1st exercise
    composeTestRule.onNodeWithTag("StartButton").performClick()
    // Then skip 1st exercise
    composeTestRule.onNodeWithTag("SkipButton").performClick()
    // Exercise name
    composeTestRule.onNodeWithTag("ExerciseName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ExerciseName").assertTextEquals(ExerciseType.PLANK.toString())

    // the video box is there again
    composeTestRule.onNodeWithTag("VideoPlayer").assertIsDisplayed()

    // Check that the goal is well updated

    composeTestRule.onNodeWithTag("GoalValue").assertIsDisplayed()
    composeTestRule.onNodeWithTag("GoalValue").assertTextEquals("00:30")
  }

  @Test
  fun finishWorkoutCallsNavigation() {
    composeTestRule.setContent {
      WorkoutScreen(
          navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          warmUpViewModel = warmUpViewModel,
          workoutType = WorkoutType.BODY_WEIGHT)
    }
    // ex1
    composeTestRule.onNodeWithTag("StartButton").performClick()
    composeTestRule.onNodeWithTag("FinishButton").performClick()
    // ex2
    composeTestRule.onNodeWithTag("StartButton").performClick()
    composeTestRule.onNodeWithTag("FinishButton").performClick()
    // ex3 (last one)
    composeTestRule.onNodeWithTag("StartButton").performClick()
    composeTestRule.onNodeWithTag("FinishButton").performClick()

    verify(navigationActions).navigateTo(Screen.MAIN)
  }
}
