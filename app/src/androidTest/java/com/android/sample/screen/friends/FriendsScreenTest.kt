package com.android.sample.screen.friends

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.sample.model.userAccount.UserAccountRepository
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.ui.friends.FriendsScreen
import com.android.sample.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class FriendsScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mock navigation actions
  private lateinit var navigationActions: NavigationActions

  @Mock private lateinit var userAccountRepository: UserAccountRepository

  private lateinit var userAccountViewModel: UserAccountViewModel

  @Before
  fun setUp() {
    // Initialize mock navigation actions

    MockitoAnnotations.openMocks(this)
    // Initialize mock navigation actions
    navigationActions = Mockito.mock(NavigationActions::class.java)
    userAccountViewModel = UserAccountViewModel(userAccountRepository)
    `when`(userAccountRepository.getUserAccount(any(), any(), any())).thenAnswer {}

    // Set the content of the test to the FriendsScreen
    composeTestRule.setContent {
      FriendsScreen(navigationActions = navigationActions, userAccountViewModel)
    }
  }

  @Test
  fun friendsScreenDisplaysCorrectly() {
    composeTestRule.onNodeWithTag("friendsScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchBarRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addFriendButton").assertIsDisplayed().assertHasClickAction()
  }
}
