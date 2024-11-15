package com.android.sample.ui.workout

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.sample.model.workout.WorkoutType
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify

class ImportOrCreateScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun displaysScreenCorrectly() {
    val navigationActions = mock(NavigationActions::class.java)

    // Set the content of the screen
    composeTestRule.setContent {
      ImportOrCreateScreen(
        navigationActions = navigationActions, workoutType = WorkoutType.BODY_WEIGHT)
    }
    // Check that the screen is displayed
    composeTestRule.onNodeWithTag("ImportOrCreateScreen").assertIsDisplayed()
  }

  @Test
  fun displaysTitleCorrectly() {
    val navigationActions = mock(NavigationActions::class.java)

    // Set the content of the screen
    composeTestRule.setContent {
      ImportOrCreateScreen(
        navigationActions = navigationActions, workoutType = WorkoutType.BODY_WEIGHT)
    }
    // Check that the title is displayed correctly
    composeTestRule.onNodeWithText("New Session").assertExists()
  }

  @Test
  fun displaysPromptMessageCorrectly() {
    val navigationActions = mock(NavigationActions::class.java)

    // Set the content of the screen
    composeTestRule.setContent {
      ImportOrCreateScreen(navigationActions = navigationActions, workoutType = WorkoutType.YOGA)
    }
    // Check that the prompt message is displayed correctly
    composeTestRule
      .onNodeWithText("Do you want to create a new program from scratch or from an existing one?")
      .assertExists()
  }

  @Test
  fun navigatesToChooseBodyweightScreenOnImportClick() {
    val navigationActions = mock(NavigationActions::class.java)

    // Set the content of the screen
    composeTestRule.setContent {
      ImportOrCreateScreen(
        navigationActions = navigationActions, workoutType = WorkoutType.BODY_WEIGHT)
    }
    // Perform click on the "Import" button
    composeTestRule.onNodeWithText("Import").performClick()
    // Verify that the navigation action is called
    verify(navigationActions).navigateTo(Screen.CHOOSE_BODYWEIGHT)
  }

  @Test
  fun navigatesToBodyweightCreationScreenOnCreateFromScratchClick() {
    val navigationActions = mock(NavigationActions::class.java)

    // Set the content of the screen
    composeTestRule.setContent {
      ImportOrCreateScreen(
        navigationActions = navigationActions, workoutType = WorkoutType.BODY_WEIGHT)
    }
    // Perform click on the "Create from scratch" button
    composeTestRule.onNodeWithText("Create from scratch").performClick()
    // Verify that the navigation action is called
    verify(navigationActions).navigateTo(Screen.BODY_WEIGHT_CREATION)
  }

  @Test
  fun navigatesToChooseYogaScreenOnImportClick() {
    val navigationActions = mock(NavigationActions::class.java)

    // Set the content of the screen
    composeTestRule.setContent {
      ImportOrCreateScreen(navigationActions = navigationActions, workoutType = WorkoutType.YOGA)
    }
    // Perform click on the "Import" button
    composeTestRule.onNodeWithText("Import").performClick()
    // Verify that the navigation action is called
    verify(navigationActions).navigateTo(Screen.CHOOSE_YOGA)
  }

  @Test
  fun navigatesToYogaCreationScreenOnCreateFromScratchClick() {
    val navigationActions = mock(NavigationActions::class.java)

    // Set the content of the screen
    composeTestRule.setContent {
      ImportOrCreateScreen(navigationActions = navigationActions, workoutType = WorkoutType.YOGA)
    }
    // Perform click on the "Create from scratch" button
    composeTestRule.onNodeWithText("Create from scratch").performClick()
    // Verify that the navigation action is called
    verify(navigationActions).navigateTo(Screen.YOGA_CREATION)
  }
}