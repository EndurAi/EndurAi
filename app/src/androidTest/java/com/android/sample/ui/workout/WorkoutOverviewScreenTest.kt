package com.android.sample.ui.workout

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
import com.android.sample.ui.navigation.Screen
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import java.time.LocalDateTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class WorkoutOverviewScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var bodyWeightViewModel: WorkoutViewModel<BodyWeightWorkout>
  private lateinit var yogaViewModel: WorkoutViewModel<YogaWorkout>
  private lateinit var navigationActions: NavigationActions
  private lateinit var bodyWeightRepo: WorkoutRepository<BodyWeightWorkout>
  private lateinit var yogaRepo: WorkoutRepository<YogaWorkout>
    private lateinit var workoutLocalCache: WorkoutLocalCache


    @Before
  fun setUp() {
      runTest {
          bodyWeightRepo = mock()
          yogaRepo = mock()
          workoutLocalCache = mock()

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
                              "2", ExerciseType.JUMPING_JACKS, ExerciseDetail.RepetitionBased(10)
                          )
                      )
                  ),
                  BodyWeightWorkout(
                      "2",
                      "NightSes",
                      "Hold for 60 seconds",
                      false,
                      date = LocalDateTime.of(2024, 11, 1, 0, 43)
                  )
              )
          val yogaWorkouts: List<YogaWorkout> = listOf()

          `when`(bodyWeightRepo.getDocuments(any(), any())).then {
              it.getArgument<(List<BodyWeightWorkout>) -> Unit>(0)(bodyWeightWorkouts)
          }

          `when`(yogaRepo.getDocuments(any(), any())).then {
              it.getArgument<(List<YogaWorkout>) -> Unit>(0)(yogaWorkouts)
          }

          `when`(workoutLocalCache.getWorkouts()).thenReturn(flowOf(bodyWeightWorkouts))

          `when`(bodyWeightRepo.getNewUid()).thenReturn("mocked-bodyweight-uid")
          `when`(bodyWeightRepo.addDocument(any(), any(), any())).then {
              val workout = it.getArgument<BodyWeightWorkout>(0)
              bodyWeightWorkouts.add(2, workout)
          }
          bodyWeightViewModel = WorkoutViewModel(bodyWeightRepo, workoutLocalCache)
          yogaViewModel = WorkoutViewModel(yogaRepo, workoutLocalCache)

          navigationActions = mock(NavigationActions::class.java)
          bodyWeightViewModel.getWorkouts()
      }
  }

  @Test
  fun testWorkoutOverviewScreenDisplaysCorrectly() {
    // Select the first workout
    bodyWeightViewModel.selectWorkout(bodyWeightViewModel.workouts.value.first())

    composeTestRule.setContent {
      WorkoutOverviewScreen(
          navigationActions = navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          workoutTye = WorkoutType.BODY_WEIGHT)
    }
    composeTestRule.onNodeWithTag("WorkoutOverviewScreen").assertIsDisplayed()
    // Verify the workout name is displayed
    composeTestRule.onNodeWithText("NopainNogain").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editButton").assertHasClickAction()

    // Verify the warmup icon is displayed and correct
    composeTestRule.onNodeWithTag("warmupCard").assertIsDisplayed()
    composeTestRule.onNodeWithText("Warmup").assertIsDisplayed()
    composeTestRule.onNodeWithTag("warmupGreenIcon").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("warmupRedIcon").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("exerciseCard").assertCountEquals(2)
    composeTestRule.onNodeWithTag("startButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("startButton").assertTextEquals("Start")
    composeTestRule.onNodeWithTag("startButton").assertHasClickAction()
  }

  @Test
  fun testStartButtonNavigatesToBodyWeightWorkout() {
    // Select the first workout
    bodyWeightViewModel.selectWorkout(bodyWeightViewModel.workouts.value.first())

    composeTestRule.setContent {
      WorkoutOverviewScreen(
          navigationActions = navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          workoutTye = WorkoutType.BODY_WEIGHT)
    }

    // Click the start button
    composeTestRule.onNodeWithTag("startButton").performClick()

    // Verify navigation to BODY_WEIGHT_WORKOUT
    verify(navigationActions).navigateTo(Screen.BODY_WEIGHT_WORKOUT)
  }

  @Test
  fun testEditButtonNavigatesToBodyWeightEdit() {
    // Select the first workout
    bodyWeightViewModel.selectWorkout(bodyWeightViewModel.workouts.value.first())

    composeTestRule.setContent {
      WorkoutOverviewScreen(
          navigationActions = navigationActions,
          bodyweightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel,
          workoutTye = WorkoutType.BODY_WEIGHT)
    }

    // Click the edit button
    composeTestRule.onNodeWithTag("editButton").performClick()

    // Verify navigation to BODY_WEIGHT_EDIT
    verify(navigationActions).navigateTo(Screen.BODY_WEIGHT_EDIT)
  }
}
