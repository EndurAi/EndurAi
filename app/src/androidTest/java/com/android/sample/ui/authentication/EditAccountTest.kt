package com.android.sample.ui.authentication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.model.userAccount.*
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.viewmodel.UserAccountViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
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
      EditAccount(
          userAccountViewModel = userAccountViewModel, navigationActions = navigationActions)
    }

    // Introduce a delay to ensure all components are rendered
    composeTestRule.waitForIdle()
    Thread.sleep(1000) // 1 second delay

    composeTestRule.onNodeWithTag("addScreen").assertExists()
    composeTestRule.onNodeWithTag("profileImage").assertExists()
    composeTestRule.onNodeWithTag("firstName").assertExists()
    composeTestRule.onNodeWithTag("lastName").assertExists()
    composeTestRule.onNodeWithTag("height").assertExists()
    composeTestRule.onNodeWithTag("heightUnit").assertExists()
    composeTestRule.onNodeWithTag("weight").assertExists()
    composeTestRule.onNodeWithTag("weightUnit").assertExists()
    composeTestRule.onNodeWithTag("gender").assertExists()
    composeTestRule.onNodeWithTag("birthday").assertExists()
    composeTestRule.onNodeWithTag("saveChanges").assertExists()
  }

  @Test
  fun inputsHaveInitialValue() {
    composeTestRule.setContent {
      EditAccount(
          userAccountViewModel = userAccountViewModel, navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("firstName").assertTextContains(userAccount.firstName)
    composeTestRule.onNodeWithTag("lastName").assertTextContains(userAccount.lastName)
    composeTestRule.onNodeWithTag("height").assertTextContains(userAccount.height.toString())
    composeTestRule.onNodeWithTag("weight").assertTextContains(userAccount.weight.toString())
  }

  @Test
  fun updatesUserAccountAndNavigatesBackOnSaveClick() {
    composeTestRule.setContent {
      EditAccount(
          userAccountViewModel = userAccountViewModel, navigationActions = navigationActions)
    }

    composeTestRule.waitForIdle()
    Thread.sleep(1000) // 1 second delay to ensure UI is fully rendered

    composeTestRule.onNodeWithTag("firstName").performTextClearance()
    composeTestRule.onNodeWithTag("firstName").performTextInput("Jane")
    composeTestRule.onNodeWithTag("lastName").performTextClearance()
    composeTestRule.onNodeWithTag("lastName").performTextInput("Smith")
    composeTestRule.onNodeWithTag("height").performTextClearance()
    composeTestRule.onNodeWithTag("height").performTextInput("170")
    composeTestRule.onNodeWithTag("weight").performTextClearance()
    composeTestRule.onNodeWithTag("weight").performTextInput("65")

    composeTestRule.onNodeWithText("Save Changes").performClick()

    composeTestRule.waitForIdle() // Wait for any pending actions to complete

    verify(navigationActions).goBack()
  }

  @Test
  fun doesNotNavigateBackWhenDataIsInvalid() {
    composeTestRule.setContent {
      EditAccount(
          userAccountViewModel = userAccountViewModel, navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("firstName").performTextClearance()
    composeTestRule.onNodeWithText("Save Changes").performClick()

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

  fun setUserAccount(account: UserAccount?) {
    this.userAccount = account
  }
}
