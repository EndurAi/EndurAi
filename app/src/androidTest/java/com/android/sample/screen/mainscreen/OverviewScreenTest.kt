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
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`

class OverviewScreenTest {

    private lateinit var navigationActions: NavigationActions

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        // Mock the NavigationActions
        navigationActions = mock(NavigationActions::class.java)

        // Mock the current route
        `when`(navigationActions.currentRoute()).thenReturn(Route.MAIN)

        // Set the content of the screen for testing
        composeTestRule.setContent {
            OverviewScreen(navigationActions)
        }
    }

    @Test
    fun testOverviewScreenDisplaysProfileSection() {
        // Check that the profile picture is displayed
        composeTestRule.onNodeWithTag("ProfilePicture").assertIsDisplayed()

        // Check that the welcome text is displayed
        composeTestRule.onNodeWithTag("WelcomeText")
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
        composeTestRule.onNodeWithText("View all").assertIsDisplayed()

        // Simulate clicking on "View all" and verify the expected behavior
        composeTestRule.onNodeWithText("View all").performClick()

        // Add verification if navigation or action is expected after clicking "View all"
        verifyNoMoreInteractions(navigationActions) // Modify this based on your "View all" implementation
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
        // Check that the New Workout Plan section is displayed
        composeTestRule.onNodeWithText("New workout plan").assertIsDisplayed()

        // Check that the New Workout icon is displayed
        composeTestRule.onNodeWithContentDescription("New Workout").assertIsDisplayed()

        // Simulate clicking on the New Workout Plan section and verify navigation
        composeTestRule.onNodeWithText("New workout plan").performClick()

        // Verify that the appropriate navigation was triggered (if required)
        verifyNoMoreInteractions(navigationActions) // Modify this based on your New Workout Plan implementation
    }

    @Test
    fun testBottomNavigationBarIsDisplayed() {
        // Check that the BottomNavigationBar is displayed
        composeTestRule.onNodeWithTag("BottomNavigationBar").assertIsDisplayed()

        // Optionally, simulate bottom navigation actions
        // Assuming that navigation logic is triggered via BottomNavigationMenu
        verifyNoMoreInteractions(navigationActions) // Modify based on your bottom navigation logic
    }
}