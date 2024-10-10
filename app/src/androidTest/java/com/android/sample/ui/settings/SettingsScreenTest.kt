package com.android.sample.ui.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SettingsScreenTest {

  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
  }

  @Test
  fun displayAllComponents() {
    // Set up the SettingsScreen for testing
    composeTestRule.setContent { SettingsScreen(navigationActions) }

    // Verify all essential components are displayed
    composeTestRule.onNodeWithTag("settingsScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("userDataButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("preferencesButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("deleteAccountButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("logoutButton").assertIsDisplayed()
  }

  @Test
  fun logoutButtonNavigatesToAuthScreen() {
    composeTestRule.setContent { SettingsScreen(navigationActions) }

    // Perform click on the logout button
    composeTestRule.onNodeWithTag("logoutButton").performClick()

    // Verify navigation to the authentication screen
    verify(navigationActions).navigateTo("Auth Screen")
  }
}
