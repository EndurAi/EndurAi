package com.android.sample.model.userAccount

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserAccountRepositoryFirestore(private val db: FirebaseFirestore, private val localCache: UserAccountLocalCache
) : UserAccountRepository {

  private val collectionPath = "userAccounts"

  override fun init(onSuccess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override suspend fun getUserAccount(
      userId: String,
      onSuccess: (UserAccount) -> Unit,
      onFailure: (Exception) -> Unit
  ) {

      try {
          // Check local cache first
          localCache.getUserAccount().collect { cachedAccount ->
              if (cachedAccount != null) {
                  onSuccess(cachedAccount)
              } else {
                  // If not cached, fetch from Firebase
                  db.collection(collectionPath)
                      .document(userId)
                      .get()
                      .addOnSuccessListener { document ->
                          document.toObject(UserAccount::class.java)?.let { userAccount ->
                              onSuccess(userAccount)
                              // Save to cache
                              saveUserAccountToCache(userAccount)
                          } ?: onFailure(Exception("UserAccount not found"))
                      }
                      .addOnFailureListener { exception ->
                          Log.e("UserAccountRepo", "Error getting user account", exception)
                          onFailure(exception)
                      }
              }
          }
      } catch (exception: Exception) {
          onFailure(exception)
      }
  }

  override fun createUserAccount(
      userAccount: UserAccount,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath)
        .document(userAccount.userId)
        .set(userAccount)
        .addOnSuccessListener {
            onSuccess()
            saveUserAccountToCache(userAccount) // Cache locally
        }
        .addOnFailureListener { exception ->
          Log.e("UserAccountRepo", "Error creating user account", exception)
          onFailure(exception)
        }
  }

  override fun updateUserAccount(
      userAccount: UserAccount,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath)
        .document(userAccount.userId)
        .set(userAccount)
        .addOnSuccessListener {
            onSuccess()
            saveUserAccountToCache(userAccount) // Cache locally
        }
        .addOnFailureListener { exception ->
          Log.e("UserAccountRepo", "Error updating user account", exception)
          onFailure(exception)
        }
  }

  // Remove a friend in an immutable way
  override fun removeFriend(
      userAccount: UserAccount,
      friendId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val updatedUserAccount = userAccount.copy(friends = userAccount.friends - friendId)
    val friendRef = db.collection(collectionPath).document(friendId)

    db.runTransaction { transaction ->
          val friendSnapshot = transaction.get(friendRef)
          val friend =
              friendSnapshot.toObject(UserAccount::class.java)
                  ?: throw Exception("Friend not found")

          val updatedFriend = friend.copy(friends = friend.friends - userAccount.userId)
          transaction.set(friendRef, updatedFriend)
          transaction.set(
              db.collection(collectionPath).document(userAccount.userId), updatedUserAccount)
        }
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
  }

  override fun sendFriendRequest(
      fromUser: UserAccount,
      toUserId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Update fromUser's sentRequests and toUser's receivedRequests
    val updatedFromUser = fromUser.copy(sentRequests = fromUser.sentRequests + toUserId)
    val toUserRef = db.collection(collectionPath).document(toUserId)

    db.runTransaction { transaction ->
          val toUserSnapshot = transaction.get(toUserRef)
          val toUser =
              toUserSnapshot.toObject(UserAccount::class.java) ?: throw Exception("User not found")

          val updatedToUser =
              toUser.copy(receivedRequests = toUser.receivedRequests + fromUser.userId)
          transaction.set(toUserRef, updatedToUser)
          transaction.set(db.collection(collectionPath).document(fromUser.userId), updatedFromUser)
        }
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
  }

  override fun acceptFriendRequest(
      userAccount: UserAccount,
      friendId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val updatedUserAccount =
        userAccount.copy(
            friends = userAccount.friends + friendId,
            receivedRequests = userAccount.receivedRequests - friendId)
    val friendRef = db.collection(collectionPath).document(friendId)

    db.runTransaction { transaction ->
          val friendSnapshot = transaction.get(friendRef)
          val friend =
              friendSnapshot.toObject(UserAccount::class.java)
                  ?: throw Exception("Friend not found")

          val updatedFriend =
              friend.copy(
                  friends = friend.friends + userAccount.userId,
                  sentRequests = friend.sentRequests - userAccount.userId)
          transaction.set(friendRef, updatedFriend)
          transaction.set(
              db.collection(collectionPath).document(userAccount.userId), updatedUserAccount)
        }
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
  }

  override fun rejectFriendRequest(
      userAccount: UserAccount,
      friendId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val updatedUserAccount =
        userAccount.copy(receivedRequests = userAccount.receivedRequests - friendId)
    val friendRef = db.collection(collectionPath).document(friendId)

    db.runTransaction { transaction ->
          val friendSnapshot = transaction.get(friendRef)
          val friend =
              friendSnapshot.toObject(UserAccount::class.java)
                  ?: throw Exception("Friend not found")

          val updatedFriend = friend.copy(sentRequests = friend.sentRequests - userAccount.userId)
          transaction.set(friendRef, updatedFriend)
          transaction.set(
              db.collection(collectionPath).document(userAccount.userId), updatedUserAccount)
        }
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
  }

  override fun deleteUserAccount(
      userId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath)
        .document(userId)
        .delete()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception ->
          Log.e("UserAccountRepo", "Error deleting user account", exception)
          onFailure(exception)
        }
  }

    private fun saveUserAccountToCache(userAccount: UserAccount) {
        CoroutineScope(Dispatchers.IO).launch {
            localCache.saveUserAccount(userAccount)
        }
    }
}
