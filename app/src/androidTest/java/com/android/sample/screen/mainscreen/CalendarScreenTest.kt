package com.android.sample.screen.mainscreen

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.WorkoutRepository
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.calendar.CalendarScreen
import com.android.sample.ui.navigation.NavigationActions
import java.time.LocalDateTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class CalendarScreenTest {
  private lateinit var navigationActions: NavigationActions
  private lateinit var bodyWeightViewModel: WorkoutViewModel<BodyWeightWorkout>
  private lateinit var yogaViewModel: WorkoutViewModel<YogaWorkout>
  private lateinit var yogaRepo: WorkoutRepository<YogaWorkout>
  private lateinit var bodyWeightRepo: WorkoutRepository<BodyWeightWorkout>

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    bodyWeightRepo = mock()
    yogaRepo = mock()

    val bodyWeightWorkouts =
        listOf(
            BodyWeightWorkout(
                "2",
                "NightSes",
                "Hold for 60 seconds",
                false,
                date = LocalDateTime.now().plusDays(1)))
    val yogaWorkouts: List<YogaWorkout> = listOf()

    `when`(bodyWeightRepo.getDocuments(any(), any())).then {
      it.getArgument<(List<BodyWeightWorkout>) -> Unit>(0)(bodyWeightWorkouts)
    }

    `when`(bodyWeightRepo.deleteDocument(any(), any(), any())).then {
      it.getArgument<(List<BodyWeightWorkout>) -> Unit>(0)(emptyList())
    }

    `when`(yogaRepo.getDocuments(any(), any())).then {
      it.getArgument<(List<YogaWorkout>) -> Unit>(0)(yogaWorkouts)
    }

    bodyWeightViewModel = WorkoutViewModel(bodyWeightRepo)
    yogaViewModel = WorkoutViewModel(yogaRepo)

    // Mock the NavigationActions
    navigationActions = mock(NavigationActions::class.java)

    bodyWeightViewModel.getWorkouts()
  }

  /*
    @Test
    fun displayAllComponents() {
      composeTestRule.setContent {
        CalendarScreen(navigationActions, bodyWeightViewModel, yogaViewModel)
      }

      sleep(10000)

      // Check if the top bar, legend, and lazy column are displayed
      composeTestRule.onNodeWithTag("workoutItem").assertIsDisplayed()
      composeTestRule.onNodeWithTag("TopBar").assertIsDisplayed()
      composeTestRule.onNodeWithTag("legendYoga").assertIsDisplayed()
      composeTestRule.onNodeWithTag("legendBodyweight").assertIsDisplayed()
      composeTestRule.onNodeWithTag("lazyColumn").assertIsDisplayed()
    }
  */

  @Test
  fun testNavigationOnBack() {
    composeTestRule.setContent {
      CalendarScreen(
          navigationActions = navigationActions,
          bodyworkoutViewModel = bodyWeightViewModel,
          yogaworkoutViewModel = yogaViewModel)
    }

    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()

    verify(navigationActions).goBack()
  }

  /*@Test
  fun testWorkoutClickShowsDialog() {

    composeTestRule.setContent {
      CalendarScreen(navigationActions, bodyWeightViewModel, yogaViewModel)
    }

    bodyWeightViewModel.getWorkouts()

    // Simulate a workout click and ensure the dialog shows up
    composeTestRule.onNodeWithTag("workoutItem").performClick()
    composeTestRule.onNodeWithTag("alertDialog").assertIsDisplayed()

    // Simulate click on the 'Edit' button
    composeTestRule.onNodeWithTag("editButton").performClick()

    // Verify that the dialog is dismissed
    composeTestRule.onNodeWithTag("alertDialog").assertDoesNotExist()
  }*/

  @Test
  fun testDisplayMoreThan3Days() {
    composeTestRule.setContent {
      CalendarScreen(navigationActions, bodyWeightViewModel, yogaViewModel)
    }

    // Find all nodes with the testTag "daySection"
    val dayNodes = composeTestRule.onAllNodesWithTag("daySection", useUnmergedTree = true)

    // Assert that there are 3 day sections displayed
    assert(3 < dayNodes.fetchSemanticsNodes().size)
  }
}
