package com.android.sample.ui.mainscreen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.WorkoutLocalCache
import com.android.sample.model.workout.WorkoutRepository
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.navigation.NavigationActions
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import java.time.LocalDateTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any

class ViewAllScreenTest {

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
              listOf(
                  BodyWeightWorkout(
                      "1",
                      "NopainNogain",
                      "Do 20 push-ups",
                      false,
                      date = LocalDateTime.of(2024, 11, 1, 0, 42)
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

          // Mock local cache behavior
          `when`(workoutLocalCache.getWorkouts()).thenReturn(flowOf(bodyWeightWorkouts))

          bodyWeightViewModel = WorkoutViewModel(bodyWeightRepo, workoutLocalCache)
          yogaViewModel = WorkoutViewModel(yogaRepo, workoutLocalCache)

          navigationActions = mock(NavigationActions::class.java)

          composeTestRule.setContent {
              ViewAllScreen(
                  navigationActions = navigationActions,
                  bodyWeightViewModel = bodyWeightViewModel,
                  yogaViewModel = yogaViewModel
              )
          }
      }
  }

  @Test
  fun testDisplayAllTags() {
    bodyWeightViewModel.getWorkouts()
    yogaViewModel.getWorkouts()
    composeTestRule.onNodeWithTag("ViewAllScreen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("ScreenTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ArrowBackButton").assertIsDisplayed()

    composeTestRule.onNodeWithTag("BodyTab").assertIsDisplayed()
    composeTestRule.onNodeWithTag("YogaTab").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RunningTab").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("WorkoutCard").assertCountEquals(2)

    composeTestRule.onNodeWithTag("YogaTab").performClick()
    composeTestRule.onNodeWithTag("emptyWorkoutPrompt").assertIsDisplayed()
  }
}
