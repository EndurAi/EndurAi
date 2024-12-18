package com.android.sample.screen

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.android.sample.mlUtils.CoachFeedback
import com.android.sample.mlUtils.ExerciseFeedBackUnit
import com.android.sample.mlUtils.exercisesCriterions.PlankExerciseCriterions
import com.android.sample.model.camera.CameraViewModel
import com.android.sample.ui.mlFeedback.CoachFeedbackScreen
import com.android.sample.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class CoachFeedBackScreenTest {
  private lateinit var mockCameraViewModel: CameraViewModel
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock()
    mockCameraViewModel = mock()

    `when`(mockCameraViewModel.feedback)
        .thenReturn(
            listOf(
                CoachFeedback(
                    emptySet(), 100f, 10, ExerciseFeedBackUnit.SECONDS, PlankExerciseCriterions)))
  }

  @Test
  fun allComponentsAreDisplayed() {
    composeTestRule.setContent {
      CoachFeedbackScreen(
          cameraViewModel = mockCameraViewModel, navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("coachFeedBackScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("coachImage").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("animatedText").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("rankCircle").performScrollTo().assertIsDisplayed().assertHasClickAction()
    composeTestRule
        .onNodeWithTag("rankText")
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals("S")
    composeTestRule.onNodeWithTag("exerciseCard").performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("exerciseName")
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals("Plank")
    composeTestRule
        .onNodeWithTag("exerciseDuration")
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextContains("10 s", substring = true)
    composeTestRule
        .onNodeWithTag("doneButton")
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
      //Test the info dialogue
      composeTestRule.onNodeWithTag("rankButton")
          .performScrollTo()
          .assertHasClickAction()
          .assertIsDisplayed()
          .performClick()
      composeTestRule.onNodeWithTag("infoDialogue").assertIsDisplayed()
  }
}
