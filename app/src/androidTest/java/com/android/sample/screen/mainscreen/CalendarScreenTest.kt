package com.android.sample.screen.mainscreen

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.calendar.CalendarViewModel
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.WorkoutLocalCache
import com.android.sample.model.workout.WorkoutRepository
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.calendar.CalendarScreen
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

class CalendarScreenTest {
  private lateinit var navigationActions: NavigationActions
  private lateinit var bodyWeightViewModel: WorkoutViewModel<BodyWeightWorkout>
  private lateinit var yogaViewModel: WorkoutViewModel<YogaWorkout>
  private lateinit var yogaRepo: WorkoutRepository<YogaWorkout>
  private lateinit var bodyWeightRepo: WorkoutRepository<BodyWeightWorkout>
  private lateinit var calendarViewModel: CalendarViewModel

    @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
      runTest {
          bodyWeightRepo = mock()
          yogaRepo = mock()

          // Get application context for testing (just like in your working snippet)
          val context = ApplicationProvider.getApplicationContext<Context>()

          // Use a real WorkoutLocalCache with a real Context
          // This ensures no NullPointerException from null context.
          val workoutLocalCache = WorkoutLocalCache(context)

          val bodyWeightWorkouts =
              listOf(
                  BodyWeightWorkout(
                      "0",
                      "NightSes",
                      "Hold for 60 seconds",
                      false,
                      date = LocalDateTime.now().plusDays(1)
                  ),
                  BodyWeightWorkout(
                      "1",
                      "NightSes",
                      "Hold for 60 seconds",
                      false,
                      date = LocalDateTime.now().plusDays(1)
                  ),
                  BodyWeightWorkout(
                      "2",
                      "NightSes",
                      "Hold for 60 seconds",
                      false,
                      date = LocalDateTime.now().plusDays(1)
                  )
              )
          val yogaWorkouts: List<YogaWorkout> =
              listOf(
                  YogaWorkout(
                      "2",
                      "NightSes",
                      "Hold for 60 seconds",
                      false,
                      date = LocalDateTime.now().plusDays(1)
                  )
              )


          `when`(bodyWeightRepo.getDocuments(any(), any())).then {
              it.getArgument<(List<BodyWeightWorkout>) -> Unit>(0)(bodyWeightWorkouts)
          }

          `when`(bodyWeightRepo.deleteDocument(any(), any(), any())).then {
              it.getArgument<(List<BodyWeightWorkout>) -> Unit>(0)(emptyList())
          }

          `when`(yogaRepo.getDocuments(any(), any())).then {
              it.getArgument<(List<YogaWorkout>) -> Unit>(0)(yogaWorkouts)
          }

          bodyWeightViewModel = WorkoutViewModel(bodyWeightRepo, workoutLocalCache)
          yogaViewModel = WorkoutViewModel(yogaRepo, workoutLocalCache)
          calendarViewModel = CalendarViewModel()

          // Mock the NavigationActions
          navigationActions = mock(NavigationActions::class.java)

          bodyWeightViewModel.getWorkouts()
      }
  }

  @Test
  fun testNavigationToDayCalendar() {
    composeTestRule.setContent {
      CalendarScreen(
          navigationActions = navigationActions,
          bodyworkoutViewModel = bodyWeightViewModel,
          yogaworkoutViewModel = yogaViewModel,
          calendarViewModel = calendarViewModel)
    }

    composeTestRule.onAllNodesWithTag("daySection")[0].performClick()

    verify(navigationActions).navigateTo(Screen.DAY_CALENDAR)
  }

  @Test
  fun testDisplayMoreThan1Day() {
    composeTestRule.setContent {
      CalendarScreen(navigationActions, bodyWeightViewModel, yogaViewModel, calendarViewModel)
    }

    // Find all nodes with the testTag "daySection"
    val dayNodes = composeTestRule.onAllNodesWithTag("daySection", useUnmergedTree = true)

    // Assert that there are 3 day sections displayed
    assert(1 < dayNodes.fetchSemanticsNodes().size)
  }

  @Test
  fun testAllComponentsDisplayed() {
    composeTestRule.setContent {
      CalendarScreen(navigationActions, bodyWeightViewModel, yogaViewModel, calendarViewModel)
    }

    composeTestRule.onNodeWithTag("Categories").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lazyColumn").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("legendItem").assertCountEquals(3)
    assert(composeTestRule.onAllNodesWithTag("daySection").fetchSemanticsNodes().isNotEmpty())
    assert(composeTestRule.onAllNodesWithTag("Divider").fetchSemanticsNodes().isNotEmpty())
    composeTestRule.onAllNodesWithTag("workoutItem").assertCountEquals(3)
  }
}
