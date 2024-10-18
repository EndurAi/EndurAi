package com.android.sample.model.userAccount

interface UserAccountRepository {
  /**
   * Initializes the repository.
   *
   * @param onSuccess Callback function to be invoked when the initialization is successful.
   */
  fun init(onSuccess: () -> Unit)

  /**
   * Retrieves the user account for the given user ID.
   *
   * @param userId The ID of the user whose account is to be retrieved.
   * @param onSuccess Callback function to be invoked with the retrieved UserAccount object.
   * @param onFailure Callback function to be invoked with an Exception if the retrieval fails.
   */
  fun getUserAccount(
      userId: String,
      onSuccess: (UserAccount) -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Creates a new user account.
   *
   * @param userAccount The UserAccount object to be created.
   * @param onSuccess Callback function to be invoked when the creation is successful.
   * @param onFailure Callback function to be invoked with an Exception if the creation fails.
   */
  fun createUserAccount(
      userAccount: UserAccount,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Updates an existing user account.
   *
   * @param userAccount The UserAccount object to be updated.
   * @param onSuccess Callback function to be invoked when the update is successful.
   * @param onFailure Callback function to be invoked with an Exception if the update fails.
   */
  fun updateUserAccount(
      userAccount: UserAccount,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )
}
