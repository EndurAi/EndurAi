package com.android.sample.screen.mainscreen

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.model.calendar.CalendarViewModel
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.WorkoutRepository
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.calendar.DayCalendarScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import java.time.LocalDateTime
import kotlinx.datetime.toKotlinLocalDate
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

// Check the implementation of the daily calendar screen
class DayCalendarScreenTest {
  private lateinit var navigationActions: NavigationActions
  private lateinit var bodyWeightViewModel: WorkoutViewModel<BodyWeightWorkout>
  private lateinit var yogaViewModel: WorkoutViewModel<YogaWorkout>
  private lateinit var yogaRepo: WorkoutRepository<YogaWorkout>
  private lateinit var bodyWeightRepo: WorkoutRepository<BodyWeightWorkout>
  private lateinit var calendarViewModel: CalendarViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    bodyWeightRepo = mock()
    yogaRepo = mock()
    calendarViewModel = CalendarViewModel()

    val bodyWeightWorkouts =
        listOf(
            BodyWeightWorkout(
                "1",
                "Afternoon Push-up Session",
                "Hold for 60 seconds",
                false,
                date = LocalDateTime.now().withHour(0)),
            BodyWeightWorkout(
                "1",
                "Afternoon Push-up Session",
                "Hold for 60 seconds",
                false,
                date = LocalDateTime.now().withHour(1)))

    val workoutDate = bodyWeightWorkouts[0].date.toLocalDate()

    calendarViewModel.updateSelectedDate(workoutDate.toKotlinLocalDate())

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

    navigationActions = mock(NavigationActions::class.java)

    bodyWeightViewModel.getWorkouts()

    composeTestRule.setContent {
      DayCalendarScreen(
          navigationActions = navigationActions,
          bodyworkoutViewModel = bodyWeightViewModel,
          yogaworkoutViewModel = yogaViewModel,
          calendarViewModel = calendarViewModel)
    }
  }

  @Test
  fun navigationGoBack() {
    composeTestRule.onNodeWithTag("ArrowBackButton").assertIsDisplayed().performClick()
    verify(navigationActions).goBack()
  }

  @Test
  fun navigationToWorkout() {
    composeTestRule.onAllNodesWithTag("WorkoutCard")[0].performClick()
    Mockito.verify(navigationActions).navigateTo(Screen.BODY_WEIGHT_OVERVIEW)
  }

  @Test
  fun testEverythingDisplayed() {
    composeTestRule.onNodeWithTag("TopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Categories").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("legendItem").assertCountEquals(3)
    composeTestRule.onNodeWithTag("Date").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Hours").assertIsDisplayed()
    assert(composeTestRule.onAllNodesWithTag("hour").fetchSemanticsNodes().isNotEmpty())
    composeTestRule.onAllNodesWithTag("WorkoutCard").assertCountEquals(2)
  }
}
