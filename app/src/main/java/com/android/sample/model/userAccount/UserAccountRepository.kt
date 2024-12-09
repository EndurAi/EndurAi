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
  suspend fun getUserAccount(
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

  /**
   * Removes a friend from the user account.
   *
   * @param userAccount The UserAccount object to be updated.
   * @param friendId The ID of the friend to be removed.
   * @param onSuccess Callback function to be invoked when the update is successful.
   * @param onFailure Callback function to be invoked with an Exception if the update fails.
   */
  fun removeFriend(
      userAccount: UserAccount,
      friendId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  /** Sends a friend request from one user to another. */
  fun sendFriendRequest(
      fromUser: UserAccount,
      toUserId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  /** Accepts a friend request. */
  fun acceptFriendRequest(
      userAccount: UserAccount,
      friendId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  /** Rejects a friend request. */
  fun rejectFriendRequest(
      userAccount: UserAccount,
      friendId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  /** Searches for users based on the given query. */
  fun searchUsers(
      query: String,
      onSuccess: (List<UserAccount>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Deletes a user account.
   *
   * @param userId The ID of the user whose account is to be deleted.
   * @param onSuccess Callback function to be invoked when the deletion is successful.
   * @param onFailure Callback function to be invoked with an Exception if the deletion fails.
   */
  fun deleteUserAccount(userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Retrieves the friends of the user from Firestore.
   *
   * @param uid The ID of the user whose friends are to be retrieved.
   * @param onSuccess Callback function to be invoked with the list of friends.
   * @param onFailure Callback function to be invoked with an Exception if the retrieval fails.
   */
  fun getFriendsFromFirestore(uid: String, onSuccess: (List<UserAccount>) -> Unit, onFailure: (Exception) -> Unit)
/**
   * Retrieves the sent friend requests of the user from Firestore.
   *
   * @param userId The ID of the user whose sent requests are to be retrieved.
   * @param onSuccess Callback function to be invoked with the list of sent requests.
   * @param onFailure Callback function to be invoked with an Exception if the retrieval fails.
   */
  fun getSentRequestsFromFirestore(
        userId: String,
        onSuccess: (List<UserAccount>) -> Unit,
        onFailure: (Exception) -> Unit
    )
  /**
   * Retrieves the received friend requests of the user from Firestore.
   *
   * @param userId The ID of the user whose received requests are to be retrieved.
   * @param onSuccess Callback function to be invoked with the list of received requests.
   * @param onFailure Callback function to be invoked with an Exception if the retrieval fails.
   */
  fun getReceivedRequestsFromFirestore(
        userId: String,
        onSuccess: (List<UserAccount>) -> Unit,
        onFailure: (Exception) -> Unit
    )
}
