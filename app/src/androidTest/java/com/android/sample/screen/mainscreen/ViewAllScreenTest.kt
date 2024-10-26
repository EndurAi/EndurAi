package com.android.sample.ui.mainscreen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.WorkoutRepository
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.navigation.NavigationActions
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

  @Before
  fun setUp() {
    bodyWeightRepo = mock()
    yogaRepo = mock()

    val bodyWeightWorkouts =
        listOf(
            BodyWeightWorkout("1", "NopainNogain", "Do 20 push-ups", false),
            BodyWeightWorkout("2", "NightSes", "Hold for 60 seconds", false))
    val yogaWorkouts: List<YogaWorkout> = listOf()

    `when`(bodyWeightRepo.getDocuments(any(), any())).then {
      it.getArgument<(List<BodyWeightWorkout>) -> Unit>(0)(bodyWeightWorkouts)
    }

    `when`(yogaRepo.getDocuments(any(), any())).then {
      it.getArgument<(List<YogaWorkout>) -> Unit>(0)(yogaWorkouts)
    }

    bodyWeightViewModel = WorkoutViewModel(bodyWeightRepo)
    yogaViewModel = WorkoutViewModel(yogaRepo)

    navigationActions = mock(NavigationActions::class.java)

    composeTestRule.setContent {
      ViewAllScreen(
          navigationActions = navigationActions,
          bodyWeightViewModel = bodyWeightViewModel,
          yogaViewModel = yogaViewModel)
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
