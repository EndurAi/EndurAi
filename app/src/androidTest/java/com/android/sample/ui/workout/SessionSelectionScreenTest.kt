package com.android.sample.ui.workout

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SessionSelectionScreenTest {

  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
  }

  /**
   * Verifies that all essential components of the session selection screen, including session
   * cards, are displayed correctly.
   */
  @Test
  fun displayAllComponents() {
    // Set up the SessionSelectionScreen for testing
    composeTestRule.setContent { SessionSelectionScreen(navigationActions) }

    // Verify that the screen and all session cards are displayed
    composeTestRule.onNodeWithTag("sessionSelectionScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("sessionCard_Body weight").assertIsDisplayed()
    composeTestRule.onNodeWithTag("sessionCard_Running").assertIsDisplayed()
    composeTestRule.onNodeWithTag("sessionCard_Yoga").assertIsDisplayed()

    // Check if correct titles are displayed in session cards
    composeTestRule.onNodeWithText("Body weight").assertIsDisplayed()
    composeTestRule.onNodeWithText("Running").assertIsDisplayed()
    composeTestRule.onNodeWithText("Yoga").assertIsDisplayed()
  }

  /**
   * Verifies that clicking the back button on the top app bar triggers a navigation action to go
   * back to the previous screen.
   */
  @Test
  fun backButtonNavigatesToPreviousScreen() {
    composeTestRule.setContent { SessionSelectionScreen(navigationActions) }

    // Perform click on the back button
    composeTestRule.onNodeWithTag("ArrowBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()

    // Verify navigation back
    verify(navigationActions).goBack()
  }

  /**
   * Verifies that clicking on the "Body weight" session card triggers a navigation action to the
   * ImportOrCreate screen.
   */
  @Test
  fun bodyWeightCardNavigatesToImportOrCreateScreen() {
    composeTestRule.setContent { SessionSelectionScreen(navigationActions) }

    // Perform click on the "Body weight" session card
    composeTestRule.onNodeWithTag("sessionCard_Body weight").assertIsDisplayed()
    composeTestRule.onNodeWithTag("sessionCard_Body weight").performClick()

    // Verify navigation to Import or Create screen
    verify(navigationActions).navigateTo(Screen.IMPORTORCREATE)
  }

  /**
   * Verifies that clicking on the "Yoga" session card triggers a navigation action to the
   * ImportOrCreate screen.
   */
  @Test
  fun yogaCardNavigatesToImportOrCreateScreen() {
    composeTestRule.setContent { SessionSelectionScreen(navigationActions) }

    // Perform click on the "Yoga" session card
    composeTestRule.onNodeWithTag("sessionCard_Yoga").assertIsDisplayed()
    composeTestRule.onNodeWithTag("sessionCard_Yoga").performClick()

    // Verify navigation to Import or Create screen
    verify(navigationActions).navigateTo(Screen.IMPORTORCREATE)
  }

  /** Verifies that each session card (Body weight, Running, Yoga) is clickable. */
  @Test
  fun sessionCardsAreClickable() {
    composeTestRule.setContent { SessionSelectionScreen(navigationActions) }

    // Check if each session card is clickable
    composeTestRule.onNodeWithTag("sessionCard_Body weight").assertHasClickAction()
    composeTestRule.onNodeWithTag("sessionCard_Running").assertHasClickAction()
    composeTestRule.onNodeWithTag("sessionCard_Yoga").assertHasClickAction()
  }
}
