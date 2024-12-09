package com.android.sample.screen.friends

import android.content.Context
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.userAccount.Gender
import com.android.sample.model.userAccount.HeightUnit
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.UserAccountLocalCache
import com.android.sample.model.userAccount.UserAccountRepository
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.model.userAccount.WeightUnit
import com.android.sample.ui.authentication.FakeUserAccountRepository
import com.android.sample.ui.friends.FriendsScreen
import com.android.sample.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class FriendsScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mock navigation actions
  private lateinit var navigationActions: NavigationActions

  @Mock private lateinit var userAccountRepository: UserAccountRepository

  private lateinit var userAccountViewModel: UserAccountViewModel
  private lateinit var localCache: UserAccountLocalCache
  private val userAccount =
      UserAccount(
          userId = "testUserId",
          firstName = "John",
          lastName = "Doe",
          height = 180f,
          heightUnit = HeightUnit.CM,
          weight = 75f,
          weightUnit = WeightUnit.KG,
          gender = Gender.MALE,
          birthDate = com.google.firebase.Timestamp.now(),
          profileImageUrl = "content://path/to/image")

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    val context = ApplicationProvider.getApplicationContext<Context>()

    // Initialize localCache with the context
    localCache = UserAccountLocalCache(context)
    userAccountRepository = FakeUserAccountRepository()
    navigationActions = mock(NavigationActions::class.java)

    userAccountViewModel = UserAccountViewModel(userAccountRepository, localCache)

    // Initialize the fake repository with a user account for the tests
    (userAccountRepository as FakeUserAccountRepository).setUserAccount(userAccount)

    // Call getUserAccount to initialize the state
    userAccountViewModel.getUserAccount(userAccount.userId)

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
