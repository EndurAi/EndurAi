package com.android.sample.model.userAccount

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import junit.framework.TestCase.fail
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UserAccountViewModelListsTest {

  @Mock private lateinit var userAccountRepository: UserAccountRepository
  @Mock private lateinit var userAccountViewModel: UserAccountViewModel
  @Mock private lateinit var mockFirebaseAuth: FirebaseAuth
  @Mock private lateinit var mockFirebaseUser: FirebaseUser
  @Mock private lateinit var localCache: UserAccountLocalCache

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
    runTest {
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
      localCache = mock(UserAccountLocalCache::class.java)

      // Return a valid Flow for the local cache
      `when`(localCache.getUserAccount()).thenReturn(flowOf(null))

      userAccountViewModel = UserAccountViewModel(userAccountRepository, localCache)

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
    }
  }

  @Test
  fun `getFriends returns list of friends`() = runTest {
    val friends = userAccountViewModel.getFriends()
    assertThat(friends.size, `is`(2))
    assertThat(friends[0], `is`(friendAccount1))
    assertThat(friends[1], `is`(friendAccount2))
  }

  @Test
  fun `getSentRequests returns list of sent requests`() = runTest {
    val sentRequests = userAccountViewModel.getSentRequests()
    assertThat(sentRequests.size, `is`(1))
    assertThat(sentRequests[0], `is`(sentRequestAccount))
  }

  @Test
  fun `getReceivedRequests returns list of received requests`() = runTest {
    val receivedRequests = userAccountViewModel.getReceivedRequests()
    assertThat(receivedRequests.size, `is`(1))
    assertThat(receivedRequests[0], `is`(receivedRequestAccount))
  }

  @Test
  fun `fetchFriends populates friends list`() = runTest {
    userAccountViewModel.fetchFriends()
    val friends = userAccountViewModel.friends.first()
    assertThat(friends.size, `is`(2))
    assertThat(friends[0], `is`(friendAccount1))
    assertThat(friends[1], `is`(friendAccount2))
  }

  @Test
  fun `fetchSentRequests populates sent requests list`() = runTest {
    userAccountViewModel.fetchSentRequests()
    val sentRequests = userAccountViewModel.sentRequests.first()
    assertThat(sentRequests.size, `is`(1))
    assertThat(sentRequests[0], `is`(sentRequestAccount))
  }

  @Test
  fun `fetchReceivedRequests populates received requests list`() = runTest {
    userAccountViewModel.fetchReceivedRequests()
    val receivedRequests = userAccountViewModel.receivedRequests.first()
    assertThat(receivedRequests.size, `is`(1))
    assertThat(receivedRequests[0], `is`(receivedRequestAccount))
  }

  @Test
  fun `searchUsers returns list of matching users when query is valid`() = runTest {
    val query = "John"
    val expectedUsers = listOf(friendAccount1, friendAccount2)
    `when`(userAccountRepository.searchUsers(eq(query), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (List<UserAccount>) -> Unit
      onSuccess(expectedUsers)
    }

    var result: List<UserAccount>? = null
    userAccountViewModel.searchUsers(
        query = query, onResult = { result = it }, onFailure = { fail("Unexpected failure") })

    assertThat(result, `is`(expectedUsers))
  }

  @Test
  fun `searchUsers returns empty list when query is blank`() = runTest {
    var result: List<UserAccount>? = null
    userAccountViewModel.searchUsers(
        query = "", onResult = { result = it }, onFailure = { fail("Unexpected failure") })

    assertThat(result, `is`(emptyList()))
  }

  @Test
  fun `searchUsers calls onFailure when repository fails`() = runTest {
    val query = "Invalid"
    val exception = RuntimeException("Search error")
    `when`(userAccountRepository.searchUsers(eq(query), any(), any())).thenAnswer {
      val onFailure = it.arguments[2] as (Exception) -> Unit
      onFailure(exception)
    }

    var failure: Exception? = null
    userAccountViewModel.searchUsers(
        query = query, onResult = { fail("Unexpected success") }, onFailure = { failure = it })

    assertThat(failure, `is`(exception))
  }
}
