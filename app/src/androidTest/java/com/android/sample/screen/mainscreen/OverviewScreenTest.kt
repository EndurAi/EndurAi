package com.android.sample.screen.mainscreen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.ui.mainscreen.OverviewScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class OverviewScreenTest {

  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    // Mock the NavigationActions
    navigationActions = mock(NavigationActions::class.java)

    // Mock the current route
    `when`(navigationActions.currentRoute()).thenReturn(Route.MAIN)

    // Set the content of the screen for testing
    composeTestRule.setContent { OverviewScreen(navigationActions) }
  }

  @Test
  fun testOverviewScreenDisplaysProfileSection() {
    // Check that the profile picture is displayed
    composeTestRule.onNodeWithTag("ProfilePicture").assertIsDisplayed()

    // Check that the welcome text is displayed
    composeTestRule
        .onNodeWithTag("WelcomeText")
        .assertIsDisplayed()
        .assertTextContains("What's up Micheal?")

    // Check that the settings button is displayed
    composeTestRule.onNodeWithTag("SettingsButton").assertIsDisplayed()

    // Simulate a click on the settings button and verify the navigation
    composeTestRule.onNodeWithTag("SettingsButton").performClick()

    // Verify that navigateTo for SETTINGS was called
    verify(navigationActions).navigateTo(Screen.SETTINGS)
  }

  @Test
  fun testOverviewScreenDisplaysWorkoutSessionsSection() {
    // Check that the workout section is displayed
    composeTestRule.onNodeWithTag("WorkoutSection").assertIsDisplayed()

    // Check that two workout cards are displayed
    composeTestRule.onAllNodesWithTag("WorkoutCard").assertCountEquals(2)

    // Check that the "View all" button is displayed
    composeTestRule.onNodeWithTag("ViewAllButton").assertIsDisplayed()

    // Simulate clicking on "View all"
    composeTestRule.onNodeWithTag("ViewAllButton").performClick()
    // Future test can be write here when view all will navigate to an other screen
  }

  @Test
  fun testOverviewScreenDisplaysQuickWorkoutSection() {
    // Check that the Quick Workout section is displayed
    composeTestRule.onNodeWithTag("QuickSection").assertIsDisplayed()

    // Check that four quick workout buttons are displayed
    composeTestRule.onAllNodesWithTag("QuickWorkoutButton").assertCountEquals(4)
  }

  @Test
  fun testOverviewScreenDisplaysNewWorkoutPlanSection() {
    // Check that the New Workout button is displayed
    composeTestRule.onNodeWithTag("NewWorkoutButton").assertIsDisplayed()

    // Simulate clicking on the New Workout Plan section
    composeTestRule.onNodeWithTag("NewWorkoutButton").performClick()
    // Future test can be written here when it will navigate to creation screen
  }

  @Test
  fun testBottomNavigationBarIsDisplayed() {
    // Check that the BottomNavigationBar is displayed
    composeTestRule.onNodeWithTag("BottomNavigationBar").assertIsDisplayed()
  }

  @Test
  fun testOverviewScreenDiplays() {
    // Check that the BottomNavigationBar is displayed
    composeTestRule.onNodeWithTag("BottomNavigationBar").assertIsDisplayed()
  }
}
