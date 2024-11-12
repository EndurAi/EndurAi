package com.android.sample.ui.authentication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.model.userAccount.*
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.TopLevelDestinations
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class AddAccountScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Mock private lateinit var userAccountRepository: UserAccountRepository

  @Mock private lateinit var navigationActions: NavigationActions

  private lateinit var userAccountViewModel: UserAccountViewModel

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize the ViewModel with the mocked repository
    userAccountViewModel = UserAccountViewModel(userAccountRepository)

    // Mock any necessary data or methods on the repository
    `when`(userAccountRepository.getUserAccount(any(), any(), any())).thenAnswer {}
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent {
      AddAccount(
          userAccountViewModel = userAccountViewModel,
          navigationActions = navigationActions,
          false,
          userId = "testUserId")
    }

    composeTestRule.waitForIdle()
    Thread.sleep(1000) // 1 second delay to ensure UI is fully rendered

    // Check if all the necessary components are displayed
    composeTestRule.onNodeWithTag("addScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileImage").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("firstName").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("lastName").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("height").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("heightUnit").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("weight").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("weightUnit").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("gender").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("birthday").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("submit").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun enterValidDataAndSubmit() {
    composeTestRule.setContent {
      AddAccount(
          userAccountViewModel = userAccountViewModel,
          navigationActions = navigationActions,
          false,
          userId = "testUserId")
    }

    composeTestRule.waitForIdle()
    Thread.sleep(1000) // 1 second delay to ensure UI is fully rendered

    // Interact with the UI elements to simulate valid data entry
    composeTestRule.onNodeWithTag("firstName").performTextInput("John")
    composeTestRule.onNodeWithTag("lastName").performTextInput("Doe")
    composeTestRule.onNodeWithTag("height").performTextInput("180")
    composeTestRule.onNodeWithTag("weight").performTextInput("75")
    composeTestRule.onNodeWithTag("birthday").performTextInput("01/01/1990")

    `when`(userAccountRepository.createUserAccount(any(), any(), any())).then {}

    composeTestRule.onNodeWithText("Submit").performScrollTo().performClick()

    composeTestRule.waitForIdle() // Wait for any pending actions to complete

    verify(navigationActions).navigateTo(TopLevelDestinations.MAIN)
  }

  @Test
  fun doesNotSubmitWithIncompleteData() {
    composeTestRule.setContent {
      AddAccount(
          userAccountViewModel = userAccountViewModel,
          navigationActions = navigationActions,
          false,
          userId = "testUserId")
    }

    // Only input first name and leave other fields blank
    composeTestRule.onNodeWithText("First Name").performTextInput("John")
    composeTestRule.onNodeWithText("Submit").performScrollTo().performClick()

    // Verify that createUserAccount is never called due to incomplete data
    verify(userAccountRepository, never()).createUserAccount(any(), any(), any())
  }
}
