package com.android.sample.model.userAccount

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UserAccountViewModelTest {

  @Mock private lateinit var userAccountRepository: UserAccountRepository
  @Mock private lateinit var userAccountViewModel: UserAccountViewModel
  @Mock private lateinit var mockFirebaseAuth: FirebaseAuth
  @Mock private lateinit var mockFirebaseUser: FirebaseUser

  private val userAccount =
      UserAccount(
          userId = "1",
          firstName = "John",
          lastName = "Doe",
          height = 180f,
          weight = 75f,
          birthDate = Timestamp.now(),
          profileImageUrl = "profile_image_url",
          friends = listOf("2", "3"),
          sentRequests = listOf("4"),
          receivedRequests = listOf("5"))

  private val friendAccount1 = UserAccount(userId = "2", firstName = "Friend1", lastName = "Last1")
  private val friendAccount2 = UserAccount(userId = "3", firstName = "Friend2", lastName = "Last2")
  private val sentRequestAccount =
      UserAccount(userId = "4", firstName = "Sent", lastName = "Request")
  private val receivedRequestAccount =
      UserAccount(userId = "5", firstName = "Received", lastName = "Request")

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    if (FirebaseApp.getApps(context).isEmpty()) {
      val options =
          FirebaseOptions.Builder()
              .setApplicationId("1:1234567890:android:abcde12345") // Fake App ID for testing
              .setApiKey("fake-api-key") // Fake API key
              .setProjectId("test-project-id") // Fake Project ID
              .build()
      FirebaseApp.initializeApp(context, options)
    }

    mockFirebaseAuth = mock(FirebaseAuth::class.java)
    mockFirebaseUser = mock(FirebaseUser::class.java)

    `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
    `when`(mockFirebaseUser.uid).thenReturn("testUserId")

    userAccountRepository = mock(UserAccountRepository::class.java)
    userAccountViewModel = UserAccountViewModel(userAccountRepository)

    `when`(userAccountRepository.getUserAccount(eq("2"), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (UserAccount) -> Unit
      onSuccess(friendAccount1)
    }
    `when`(userAccountRepository.getUserAccount(eq("3"), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (UserAccount) -> Unit
      onSuccess(friendAccount2)
    }
    `when`(userAccountRepository.getUserAccount(eq("4"), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (UserAccount) -> Unit
      onSuccess(sentRequestAccount)
    }
    `when`(userAccountRepository.getUserAccount(eq("5"), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (UserAccount) -> Unit
      onSuccess(receivedRequestAccount)
    }

    // Use reflection to set the private _userAccount property
    val userAccountProperty =
        UserAccountViewModel::class.declaredMemberProperties.first { it.name == "_userAccount" }
    userAccountProperty.isAccessible = true
    (userAccountProperty.get(userAccountViewModel) as MutableStateFlow<UserAccount?>).value =
        userAccount
    userAccountViewModel = UserAccountViewModel(userAccountRepository, mockFirebaseAuth)
  }

  @Test
  fun `getUserAccount updates userAccount`() = runTest {
    `when`(userAccountRepository.getUserAccount(any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (UserAccount) -> Unit
      onSuccess(userAccount)
    }

    assertThat(userAccountViewModel.isLoading.first(), `is`(false))

    userAccountViewModel.getUserAccount("1")

    verify(userAccountRepository).getUserAccount(eq("1"), any(), any())
    assertThat(userAccountViewModel.userAccount.first(), `is`(userAccount))
  }


  @Test
  fun `createUserAccount calls repository and updates userAccount`() = runTest {
    `when`(userAccountRepository.createUserAccount(any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as () -> Unit
      onSuccess()
    }

    userAccountViewModel.createUserAccount(userAccount)

    verify(userAccountRepository).createUserAccount(eq(userAccount), any(), any())
    assertThat(userAccountViewModel.userAccount.first(), `is`(userAccount))
  }

  @Test
  fun `updateUserAccount calls repository and reloads userAccount`() = runTest {
    `when`(userAccountRepository.updateUserAccount(any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as () -> Unit
      onSuccess()
    }
    `when`(userAccountRepository.getUserAccount(any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (UserAccount) -> Unit
      onSuccess(userAccount)
    }

    userAccountViewModel.updateUserAccount(userAccount)

    verify(userAccountRepository).updateUserAccount(eq(userAccount), any(), any())
    assertThat(userAccountViewModel.userAccount.first(), `is`(userAccount))
  }

  @Test
  fun `removeFriend updates userAccount`() = runTest {
    `when`(userAccountRepository.removeFriend(any(), any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[2] as () -> Unit
      onSuccess()
    }
    `when`(userAccountRepository.getUserAccount(any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (UserAccount) -> Unit
      onSuccess(userAccount)
    }

    userAccountViewModel.getUserAccount("1")
    userAccountViewModel.removeFriend("friendId")

    verify(userAccountRepository).removeFriend(eq(userAccount), eq("friendId"), any(), any())
    assertThat(userAccountViewModel.userAccount.first(), `is`(userAccount))
  }

  @Test
  fun `acceptFriendRequest calls repository and reloads userAccount`() = runTest {
    `when`(userAccountRepository.acceptFriendRequest(any(), any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[2] as () -> Unit
      onSuccess()
    }
    `when`(userAccountRepository.getUserAccount(any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (UserAccount) -> Unit
      onSuccess(userAccount)
    }

    userAccountViewModel.getUserAccount("1")
    userAccountViewModel.acceptFriendRequest("friendId")

    verify(userAccountRepository).acceptFriendRequest(eq(userAccount), eq("friendId"), any(), any())
    assertThat(userAccountViewModel.userAccount.first(), `is`(userAccount))
  }

  @Test
  fun `rejectFriendRequest calls repository and reloads userAccount`() = runTest {
    `when`(userAccountRepository.rejectFriendRequest(any(), any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[2] as () -> Unit
      onSuccess()
    }
    `when`(userAccountRepository.getUserAccount(any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (UserAccount) -> Unit
      onSuccess(userAccount)
    }

    userAccountViewModel.getUserAccount("1")
    userAccountViewModel.rejectFriendRequest("friendId")

    verify(userAccountRepository).rejectFriendRequest(eq(userAccount), eq("friendId"), any(), any())
    assertThat(userAccountViewModel.userAccount.first(), `is`(userAccount))
  }

  @Test
  fun `sendFriendRequest calls repository and reloads userAccount`() = runTest {
    `when`(userAccountRepository.sendFriendRequest(any(), any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[2] as () -> Unit
      onSuccess()
    }
    `when`(userAccountRepository.getUserAccount(any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (UserAccount) -> Unit
      onSuccess(userAccount)
    }

    userAccountViewModel.getUserAccount("1")
    userAccountViewModel.sendFriendRequest("friendId")

    verify(userAccountRepository).sendFriendRequest(eq(userAccount), eq("friendId"), any(), any())
    assertThat(userAccountViewModel.userAccount.first(), `is`(userAccount))
  }

  @Test
  fun `deleteAccount calls deleteUserAccount method on repository`() = runTest {
    val mockContext = mock(Context::class.java)
    val mockDeleteTask: Task<Void> = Tasks.forResult(null) // Successful delete task

    `when`(mockFirebaseUser.delete()).thenReturn(mockDeleteTask)

    `when`(userAccountRepository.deleteUserAccount(any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as () -> Unit
      onSuccess()
    }

    userAccountViewModel.deleteAccount(context = mockContext, onSuccess = {}, onFailure = {})

    verify(userAccountRepository).deleteUserAccount(eq("testUserId"), any(), any())
  }

  @Test
  fun `deleteAccount calls onFailure when deletion fails`() = runTest {
    val mockContext = mock(Context::class.java)
    val exception = RuntimeException("Failed to delete user account")
    val mockDeleteTask: Task<Void> = Tasks.forException(exception) // Failed delete task

    `when`(mockFirebaseUser.delete()).thenReturn(mockDeleteTask)

    `when`(userAccountRepository.deleteUserAccount(any(), any(), any())).thenAnswer {
      val onFailure = it.arguments[2] as (Exception) -> Unit
      onFailure(exception)
    }

    var failureCalled = false

    userAccountViewModel.deleteAccount(
        context = mockContext,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    verify(userAccountRepository).deleteUserAccount(eq("testUserId"), any(), any())
    assertTrue("Failure callback should be called", failureCalled)
  }
}
