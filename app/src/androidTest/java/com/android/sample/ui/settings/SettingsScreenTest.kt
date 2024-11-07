package com.android.sample.ui.settings

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.viewmodel.UserAccountViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.kotlin.eq

class SettingsScreenTest {
  private lateinit var userAccountViewModel: UserAccountViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var mockContext: Context

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    userAccountViewModel = mock(UserAccountViewModel::class.java)
    mockContext = mock(Context::class.java)
  }

  @Test
  fun displayAllComponents() {
    // Set up the SettingsScreen for testing
    composeTestRule.setContent { SettingsScreen(navigationActions, userAccountViewModel) }

    // Verify all essential components are displayed
    composeTestRule.onNodeWithTag("settingsScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("userDataButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("preferencesButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("deleteAccountButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("logoutButton").assertIsDisplayed()
  }

  @Test
  fun buttonLogoutNavigatesToAuthScreen() {
    reset(navigationActions)

    composeTestRule.setContent { SettingsScreen(navigationActions) }

    // Perform click on the logout button
    composeTestRule.onNodeWithTag("logoutButton").performClick()

    // Verify navigation to the authentication screen
    verify(navigationActions).navigateTo("Auth Screen")
  }

  @Test
  fun deleteAccountButtonCallsDeleteFunction() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()

    // Set up the SettingsScreen for testing
    composeTestRule.setContent { SettingsScreen(navigationActions, userAccountViewModel) }

    // Perform click on the delete account button
    composeTestRule.onNodeWithTag("deleteAccountButton").performClick()

    // Verify that the deleteAccount function is called
    verify(userAccountViewModel).deleteAccount(eq(context), onSuccess = {}, onFailure = {})
  }
}
