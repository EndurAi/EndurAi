package com.android.sample.model.userAccount

import com.android.sample.viewmodel.UserAccountViewModel
import com.google.firebase.Timestamp
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
          profileImageUrl = "profile_image_url")

  @Before
  fun setUp() {
    userAccountRepository = mock(UserAccountRepository::class.java)
    userAccountViewModel = UserAccountViewModel(userAccountRepository)
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
    // Arrange: simulate failure callback in the repository
    `when`(userAccountRepository.getUserAccount(any(), any(), any())).thenAnswer {
      val onFailure = it.arguments[2] as (Exception) -> Unit
      onFailure(Exception("User not found"))
    }

    // Act: trigger the getUserAccount call
    userAccountViewModel.getUserAccount("1")

    // Assert
    verify(userAccountRepository).getUserAccount(eq("1"), any(), any())

    // Check that loading is set to false
    assertThat(userAccountViewModel.isLoading.first(), `is`(false))

    // Check that userAccount is null due to failure
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
}
