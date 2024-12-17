package com.android.sample.ui.settings

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.preferences.PreferencesLocalCache
import com.android.sample.model.preferences.PreferencesRepository
import com.android.sample.model.preferences.PreferencesViewModel
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.ui.navigation.NavigationActions
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify

class SettingsScreenTest {
  private lateinit var userAccountViewModel: UserAccountViewModel
  private lateinit var mockPreferencesRepository: PreferencesRepository
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var mockContext: Context

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {

    // Get application context for testing
    val context = ApplicationProvider.getApplicationContext<Context>()
    
    val preferencesLocalCache = PreferencesLocalCache(context)
    mockPreferencesRepository = mock(PreferencesRepository::class.java)

    preferencesViewModel = PreferencesViewModel(mockPreferencesRepository, preferencesLocalCache)

    navigationActions = mock(NavigationActions::class.java)
    userAccountViewModel = mock(UserAccountViewModel::class.java)
    mockContext = mock(Context::class.java)
  }

  @Test
  fun displayAllComponents() {
    // Set up the SettingsScreen for testing
    composeTestRule.setContent {
      SettingsScreen(navigationActions, preferencesViewModel, userAccountViewModel)
    }

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

    composeTestRule.setContent { SettingsScreen(navigationActions, preferencesViewModel) }

    // Perform click on the logout button
    composeTestRule.onNodeWithTag("logoutButton").performClick()

    // Verify navigation to the authentication screen
    verify(navigationActions).navigateTo("Auth Screen")
  }

  @Test
  fun deleteAccountButtonCallsDeleteFunction() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()

    // Set up the SettingsScreen for testing
    composeTestRule.setContent {
      SettingsScreen(navigationActions, preferencesViewModel, userAccountViewModel)
    }

    // Perform click on the delete account button
    composeTestRule.onNodeWithTag("deleteAccountButton").performClick()

    // TODO: Find a way to verify the deleteAccount function is called without having an unfinished
  }

  @After
  fun tearDown() {
    reset(navigationActions)
    reset(userAccountViewModel)
  }
}
