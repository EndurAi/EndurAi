package com.android.sample.model.userAccount

interface UserAccountRepository {
  fun init(onSuccess: () -> Unit)

  fun getUserAccount(
      userId: String,
      onSuccess: (UserAccount) -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun createUserAccount(
      userAccount: UserAccount,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun updateUserAccount(
      userAccount: UserAccount,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )
}
