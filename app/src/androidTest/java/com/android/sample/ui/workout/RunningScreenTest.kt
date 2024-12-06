package com.android.sample.ui.workout

import android.Manifest
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.rule.GrantPermissionRule
import com.android.sample.model.workout.RunningWorkout
import com.android.sample.model.workout.WorkoutRepository
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.ui.googlemap.RunningScreen
import com.android.sample.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class RunningScreenTest {

  private lateinit var mockRunningWorkoutViewModel: WorkoutViewModel<RunningWorkout>
  private lateinit var mockNavHostController: NavigationActions
  private lateinit var mockRunningWorkoutRepository: WorkoutRepository<RunningWorkout>

  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule
  val grantPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.POST_NOTIFICATIONS)

  @Before
  fun setUp() {
    // Mock the WorkoutRepositories
    mockRunningWorkoutRepository = mock()

    // Mock the ViewModels and NavigationActions
    mockRunningWorkoutViewModel = WorkoutViewModel(mockRunningWorkoutRepository)
    mockNavHostController = mock(NavigationActions::class.java)

    `when`(mockRunningWorkoutViewModel.getNewUid()).thenReturn("mocked-running-uid")
  }

  @Test
  fun runningScreen_displaysStartButtonInitially() {
    composeTestRule.setContent {
      RunningScreen(
          navigationActions = mockNavHostController,
          runningWorkoutViewModel = mockRunningWorkoutViewModel)
    }

    composeTestRule.onNodeWithTag("StartButton").assertIsDisplayed()
  }

  @Test
  fun startButton_clickStartsRunning() {
    composeTestRule.setContent {
      RunningScreen(
          navigationActions = mockNavHostController,
          runningWorkoutViewModel = mockRunningWorkoutViewModel)
    }

    composeTestRule.onNodeWithTag("StartButton").performClick()
    composeTestRule.onNodeWithTag("PauseButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("LocationButton").assertIsDisplayed()
  }

  @Test
  fun pauseButton_clickPausesRunning() {
    composeTestRule.setContent {
      RunningScreen(
          navigationActions = mockNavHostController,
          runningWorkoutViewModel = mockRunningWorkoutViewModel)
    }

    composeTestRule.onNodeWithTag("StartButton").performClick()
    composeTestRule.onNodeWithTag("PauseButton").performClick()
    composeTestRule.onNodeWithTag("ResumeButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("FinishButton").assertIsDisplayed()
  }

  @Test
  fun resumeButton_clickResumesRunning() {
    composeTestRule.setContent {
      RunningScreen(
          navigationActions = mockNavHostController,
          runningWorkoutViewModel = mockRunningWorkoutViewModel)
    }

    composeTestRule.onNodeWithTag("StartButton").performClick()
    composeTestRule.onNodeWithTag("PauseButton").performClick()
    composeTestRule.onNodeWithTag("ResumeButton").performClick()
    composeTestRule.onNodeWithTag("PauseButton").assertIsDisplayed()
  }

  @Test
  fun finishButton_clickFinishesRunning() {
    composeTestRule.setContent {
      RunningScreen(
          navigationActions = mockNavHostController,
          runningWorkoutViewModel = mockRunningWorkoutViewModel)
    }

    composeTestRule.onNodeWithTag("StartButton").performClick()
    composeTestRule.onNodeWithTag("PauseButton").performClick()
    composeTestRule.onNodeWithTag("FinishButton").performClick()
    composeTestRule.onNodeWithTag("FinishButton").performScrollTo().assertHasClickAction()
  }

  @Test
  fun saveToggleButton_isDisplayedWhenSaving() {
    composeTestRule.setContent {
      RunningScreen(
          navigationActions = mockNavHostController,
          runningWorkoutViewModel = mockRunningWorkoutViewModel)
    }

    composeTestRule.onNodeWithTag("StartButton").performClick()
    composeTestRule.onNodeWithTag("PauseButton").performClick()
    composeTestRule.onNodeWithTag("FinishButton").performClick()

    composeTestRule.onNodeWithTag("Save Running switchToggle").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun saveButton_isDisplayedWhenToggleClicked() {
    composeTestRule.setContent {
      RunningScreen(
          navigationActions = mockNavHostController,
          runningWorkoutViewModel = mockRunningWorkoutViewModel)
    }

    composeTestRule.onNodeWithTag("StartButton").performClick()
    composeTestRule.onNodeWithTag("PauseButton").performClick()
    composeTestRule.onNodeWithTag("FinishButton").performClick()
    composeTestRule.onNodeWithTag("Save Running switchToggle").performScrollTo().performClick()

    composeTestRule.onNodeWithTag("SaveButton").assertExists()
  }

  @Test
  fun descriptionTextFieldAndNameTextField_isDisplayedWhenSaving() {
    composeTestRule.setContent {
      RunningScreen(
          navigationActions = mockNavHostController,
          runningWorkoutViewModel = mockRunningWorkoutViewModel)
    }

    composeTestRule.onNodeWithTag("StartButton").performClick()
    composeTestRule.onNodeWithTag("PauseButton").performClick()
    composeTestRule.onNodeWithTag("FinishButton").performClick()
    composeTestRule.onNodeWithTag("Save Running switchToggle").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("nameTextField").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("descriptionTextField").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun locationButton_clickDisplayStats() {
    composeTestRule.setContent {
      RunningScreen(
          navigationActions = mockNavHostController,
          runningWorkoutViewModel = mockRunningWorkoutViewModel)
    }

    composeTestRule.onNodeWithTag("StartButton").performClick()
    composeTestRule.onNodeWithTag("LocationButton").performClick()
    composeTestRule.onNodeWithText("TIME").assertIsDisplayed()
    composeTestRule.onNodeWithText("AVG PACE").assertIsDisplayed()
    composeTestRule.onNodeWithText("DISTANCE").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DistanceValueText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PaceValueText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TimeValueText").assertIsDisplayed()
  }

  @Test
  fun statsScreenBottomBarWorks() {
    composeTestRule.setContent {
      RunningScreen(
          navigationActions = mockNavHostController,
          runningWorkoutViewModel = mockRunningWorkoutViewModel)
    }

    composeTestRule.onNodeWithTag("StartButton").performClick()
    composeTestRule.onNodeWithTag("LocationButton").performClick()
    composeTestRule.onNodeWithTag("PauseButtonStats").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("PauseButtonStats").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("FinishButtonStats").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ResumeButtonStats").assertIsDisplayed()
  }

  @Test
  fun locationButtonCanSwitchScreen() {
    composeTestRule.setContent {
      RunningScreen(
          navigationActions = mockNavHostController,
          runningWorkoutViewModel = mockRunningWorkoutViewModel)
    }

    composeTestRule.onNodeWithTag("StartButton").performClick()
    composeTestRule.onNodeWithTag("LocationButton").performClick()
    composeTestRule.onNodeWithTag("StatsScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("LocationButtonStats").performClick()
    composeTestRule.onNodeWithTag("MainRunningScreen").assertIsDisplayed()
  }
}
