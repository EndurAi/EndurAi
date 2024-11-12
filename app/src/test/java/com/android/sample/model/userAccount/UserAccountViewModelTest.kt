package com.android.sample.model.userAccount

import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

class UserAccountViewModelTest {

  private lateinit var userAccountRepository: UserAccountRepository
  private lateinit var userAccountViewModel: UserAccountViewModel

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
  private val sentRequestAccount = UserAccount(userId = "4", firstName = "Sent", lastName = "Request")
  private val receivedRequestAccount = UserAccount(userId = "5", firstName = "Received", lastName = "Request")
  @Before
  fun setUp() {
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
    val userAccountProperty = UserAccountViewModel::class.declaredMemberProperties
      .first { it.name == "_userAccount" }
    userAccountProperty.isAccessible = true
    (userAccountProperty.get(userAccountViewModel) as MutableStateFlow<UserAccount?>).value = userAccount
  }

  @Test
  fun `getUserAccount updates userAccount and isLoading`() = runTest {
    `when`(userAccountRepository.getUserAccount(any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (UserAccount) -> Unit
      onSuccess(userAccount)
    }

    userAccountViewModel.getUserAccount("1")

    verify(userAccountRepository).getUserAccount(eq("1"), any(), any())
    assertThat(userAccountViewModel.isLoading.first(), `is`(false))
    assertThat(userAccountViewModel.userAccount.first(), `is`(userAccount))
  }

  @Test
  fun `getUserAccount sets isLoading to false on failure`() = runTest {
    `when`(userAccountRepository.getUserAccount(any(), any(), any())).thenAnswer {
      val onFailure = it.arguments[2] as (Exception) -> Unit
      onFailure(Exception("User not found"))
    }

    userAccountViewModel.getUserAccount("1")

    verify(userAccountRepository).getUserAccount(eq("1"), any(), any())

    assertThat(userAccountViewModel.isLoading.first(), `is`(false))

    assertThat(userAccountViewModel.userAccount.first(), nullValue())
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
  fun `removeFriend updates userAccount and isLoading`() = runTest {
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
    assertThat(userAccountViewModel.isLoading.first(), `is`(false))
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
}
