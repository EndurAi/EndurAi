package com.android.sample.endToend

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import com.android.sample.MainActivity
import com.android.sample.mlUtils.CoachFeedback
import com.android.sample.mlUtils.ExerciseFeedBackUnit
import com.android.sample.mlUtils.exercisesCriterions.PlankExerciseCriterions
import com.android.sample.model.camera.CameraViewModel
import com.android.sample.ui.navigation.Route
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class MLEndToEndTest {
  private lateinit var mockCameraViewModel: CameraViewModel
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val grantPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.POST_NOTIFICATIONS,
          Manifest.permission.CAMERA)

  @Before
  fun setUp() {
    val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
    intent.putExtra("START_DESTINATION", Route.MAIN)
    val context = ApplicationProvider.getApplicationContext<Context>()
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
    mockCameraViewModel = mock()
    `when`(mockCameraViewModel.feedback)
        .thenReturn(
            listOf(
                CoachFeedback(
                    emptySet(),
                    100f,
                    10,
                    ExerciseFeedBackUnit.SECONDS,
                    true,
                    PlankExerciseCriterions)))
  }
  /** This test is an end-to-end test that tests the ML flow of the app. */
  @OptIn(ExperimentalTestApi::class)
  @Test
  fun mlEndToEndTest() {
    // Starting from the main screen, go to the learnings screen
    composeTestRule.onNodeWithTag("Video").assertIsDisplayed().performClick()
    // We need to wait for the video to load and animation to end
    composeTestRule.waitUntilExactlyOneExists(
        matcher = hasTestTag("coachText"), timeoutMillis = 5000)
    // Check if the coach text and button are displayed
    composeTestRule.onNodeWithTag("coachText").assertIsDisplayed()
    // Go to the ML screen
    composeTestRule.onNodeWithTag("coachButton").assertIsDisplayed().performClick()
    // Plank is selected by default, so we can just press the start button
    composeTestRule.onNodeWithTag("saveButton").assertIsDisplayed().performClick()
    // Press the record button twice to simulate a good plank
    composeTestRule
        .onNodeWithTag("recordButton")
        .assertIsDisplayed()
        .performClick()
        .performClick()
        .performClick()

    // Check that everything is displayed in the feedback screen
    composeTestRule.onNodeWithTag("coachFeedBackScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("coachImage").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("animatedText").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("rankCircle").performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("rankText", useUnmergedTree = true)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals("X") // Rank should be X, since nothing was detected
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
        .assertTextContains(
            "0 s", substring = true) // Duration should be 0s since nothing was detected
    composeTestRule
        .onNodeWithTag("doneButton")
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
    // Test the info dialogue
    composeTestRule
        .onNodeWithTag("rankButton")
        .performScrollTo()
        .assertHasClickAction()
        .assertIsDisplayed()
        .performClick()
    composeTestRule.onNodeWithTag("infoDialogue").assertIsDisplayed()
  }
}
