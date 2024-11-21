package com.android.sample.ui.authentication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.model.userAccount.*
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class EditAccountScreenTest {

  private lateinit var userAccountRepository: UserAccountRepository
  private lateinit var userAccountViewModel: UserAccountViewModel
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

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
    userAccountRepository = FakeUserAccountRepository()
    navigationActions = mock(NavigationActions::class.java)
    userAccountViewModel = UserAccountViewModel(userAccountRepository)

    // Initialize the fake repository with a user account for the tests
    (userAccountRepository as FakeUserAccountRepository).setUserAccount(userAccount)

    // Call getUserAccount to initialize the state
    userAccountViewModel.getUserAccount(userAccount.userId)
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent {
      AddAccount(
          userAccountViewModel = userAccountViewModel,
          navigationActions = navigationActions,
          true,
          userId = "testUserId")
    }

    // Introduce a delay to ensure all components are rendered
    composeTestRule.waitForIdle()
    Thread.sleep(1000) // 1 second delay

    composeTestRule.onNodeWithTag("editScreen").assertIsDisplayed()
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
  fun inputsHaveInitialValue() {
    composeTestRule.setContent {
      AddAccount(
          userAccountViewModel = userAccountViewModel,
          navigationActions = navigationActions,
          true,
          userId = "testUserId")
    }

    composeTestRule
        .onNodeWithTag("firstName")
        .performScrollTo()
        .assertTextContains(userAccount.firstName)
    composeTestRule
        .onNodeWithTag("lastName")
        .performScrollTo()
        .assertTextContains(userAccount.lastName)
    composeTestRule
        .onNodeWithTag("height")
        .performScrollTo()
        .assertTextContains(userAccount.height.toString())
    composeTestRule
        .onNodeWithTag("weight")
        .performScrollTo()
        .assertTextContains(userAccount.weight.toString())
  }

  @Test
  fun updatesUserAccountAndNavigatesBackOnSaveClick() {
    composeTestRule.setContent {
      AddAccount(
          userAccountViewModel = userAccountViewModel,
          navigationActions = navigationActions,
          true,
          userId = "testUserId")
    }

    composeTestRule.waitForIdle()
    Thread.sleep(1000) // 1 second delay to ensure UI is fully rendered

    composeTestRule.onNodeWithTag("firstName").performScrollTo().performTextClearance()
    composeTestRule.onNodeWithTag("firstName").performTextInput("Jane")
    composeTestRule.onNodeWithTag("lastName").performScrollTo().performTextClearance()
    composeTestRule.onNodeWithTag("lastName").performTextInput("Smith")
    composeTestRule.onNodeWithTag("height").performScrollTo().performTextClearance()
    composeTestRule.onNodeWithTag("height").performTextInput("170")
    composeTestRule.onNodeWithTag("weight").performScrollTo().performTextClearance()
    composeTestRule.onNodeWithTag("weight").performTextInput("65")

    composeTestRule.onNodeWithText("Save Changes").performScrollTo().performClick()

    composeTestRule.waitForIdle() // Wait for any pending actions to complete

    verify(navigationActions).goBack()
  }

  @Test
  fun doesNotNavigateBackWhenDataIsInvalid() {
    composeTestRule.setContent {
      AddAccount(
          userAccountViewModel = userAccountViewModel,
          navigationActions = navigationActions,
          true,
          userId = "testUserId")
    }

    composeTestRule.onNodeWithTag("firstName").performScrollTo().performTextClearance()
    composeTestRule.onNodeWithText("Save Changes").performScrollTo().performClick()

    verify(navigationActions, never()).goBack()
  }
}

class FakeUserAccountRepository : UserAccountRepository {
  private var userAccount: UserAccount? = null
  private var error: Exception? = null

  override fun init(onSuccess: () -> Unit) {
    onSuccess()
  }

  override fun getUserAccount(
      userId: String,
      onSuccess: (UserAccount) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    if (error != null) {
      onFailure(error!!)
    } else {
      userAccount?.let { onSuccess(it) }
    }
  }

  override fun createUserAccount(
      userAccount: UserAccount,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    this.userAccount = userAccount
    onSuccess()
  }

  override fun updateUserAccount(
      userAccount: UserAccount,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    this.userAccount = userAccount
    onSuccess()
  }

  override fun sendFriendRequest(
      fromUser: UserAccount,
      toUserId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // not needed here
  }

  override fun acceptFriendRequest(
      userAccount: UserAccount,
      friendId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // not needed here
  }

  override fun removeFriend(
      userAccount: UserAccount,
      friendId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // not needed here
  }

  override fun rejectFriendRequest(
      userAccount: UserAccount,
      friendId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // not needed here
  }

  override fun deleteUserAccount(
      userId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    if (userAccount != null && userAccount?.userId == userId) {
      userAccount = null
      onSuccess()
    } else {
      onFailure(Exception("UserAccount not found"))
    }
  }

  fun setUserAccount(account: UserAccount?) {
    this.userAccount = account
  }
}
