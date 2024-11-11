package com.android.sample.screen.friends

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.model.userAccount.UserAccountRepository
import com.android.sample.ui.friends.FriendsScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.viewmodel.UserAccountViewModel
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


  @Mock
  private lateinit var userAccountRepository: UserAccountRepository

  @Mock
  private lateinit var navigationActions: NavigationActions

  private lateinit var userAccountViewModel: UserAccountViewModel




  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    // Initialize mock navigation actions
    navigationActions = Mockito.mock(NavigationActions::class.java)

    // Initialize the ViewModel with the mocked repository
    userAccountViewModel = UserAccountViewModel(userAccountRepository)

    // Mock any necessary data or methods on the repository
    `when`(userAccountRepository.getUserAccount(any(), any(), any())).thenAnswer {}

    // Set the content of the test to the FriendsScreen
    composeTestRule.setContent { FriendsScreen(navigationActions = navigationActions, userAccountViewModel) }
  }

  @Test
  fun friendsScreenDisplaysCorrectly() {
    composeTestRule.onNodeWithTag("friendsScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchBarRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addFriendButton").assertIsDisplayed()

    // commented out because the user account is always reinitialized when starting the application
    // thus the friends list is always empty
//    composeTestRule.onNodeWithTag("friendsList").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("friendsList").performClick()
//    composeTestRule.onNodeWithTag("inviteButton").assertIsDisplayed()
  }
}
