package com.android.sample.ui.authentication

import android.content.Context
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.userAccount.Gender
import com.android.sample.model.userAccount.HeightUnit
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.UserAccountLocalCache
import com.android.sample.model.userAccount.UserAccountRepository
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.model.userAccount.WeightUnit
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.google.firebase.Timestamp
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import java.util.Date
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

@RunWith(AndroidJUnit4::class)
class SignInTest : TestCase() {
  private lateinit var navigationActions: NavigationActions
  private lateinit var accountRepo: UserAccountRepository
  private lateinit var accountViewModel: UserAccountViewModel
  private lateinit var localCache: UserAccountLocalCache

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    runTest {
      // Mock the NavigationActions
      navigationActions = mock(NavigationActions::class.java)
      accountRepo = mock()
      val account =
          UserAccount(
              "1111",
              "Micheal",
              "Phelps",
              1.8f,
              HeightUnit.METER,
              70f,
              WeightUnit.KG,
              Gender.MALE,
              Timestamp(Date()),
              "")
      // Get application context for testing
      val context = ApplicationProvider.getApplicationContext<Context>()

      // Initialize localCache with the context
      localCache = UserAccountLocalCache(context)
      // Mock the current route
      `when`(navigationActions.currentRoute()).thenReturn(Route.MAIN)

      `when`(accountRepo.getUserAccount(any(), any(), any())).thenAnswer {
        val onSuccess = it.getArgument<(UserAccount) -> Unit>(1)
        onSuccess(account)
      }

      accountViewModel = UserAccountViewModel(accountRepo, localCache)
      composeTestRule.setContent { SignInScreen(accountViewModel, navigationActions) }
    }
  }

  @Test
  fun titleAndButtonAreCorrectlyDisplayed() {
    composeTestRule.onNodeWithTag("loginTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginTitle").assertTextEquals("Welcome")

    composeTestRule.onNodeWithTag("loginButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginButton").assertHasClickAction()
  }

  @Test
  fun googleSignInReturnsValidActivityResult() {
    composeTestRule.onNodeWithTag("loginButton").performClick()

    // assert that an Intent resolving to Google Mobile Services has been sent (for sign-in)
    // intended(toPackage("com.google.android.gms"))
  }
}
