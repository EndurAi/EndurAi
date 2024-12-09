package com.android.sample.model.userAccount

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class UserAccountRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val localCache: UserAccountLocalCache
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
      // Use the local cache first
      val cachedAccount = localCache.getUserAccount().firstOrNull()
      if (cachedAccount != null) {
        onSuccess(cachedAccount)
      } else {
        // Fetch from Firebase if not in the cache
        db.collection(collectionPath)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
              val userAccount = document.toObject(UserAccount::class.java)
              if (userAccount != null) {
                onSuccess(userAccount)
                // Save to cache
                CoroutineScope(Dispatchers.IO).launch { localCache.saveUserAccount(userAccount) }
              } else {
                onFailure(Exception("UserAccount not found"))
              }
            }
            .addOnFailureListener { exception ->
              Log.e("UserAccountRepo", "Error getting user account", exception)
              onFailure(exception)
            }
      }
    } catch (e: Exception) {
      onFailure(e)
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

    override fun removeFriend(
        userAccount: UserAccount,
        friendId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userRef = db.collection(collectionPath).document(userAccount.userId)
        val friendRef = db.collection(collectionPath).document(friendId)

        db.runTransaction { transaction ->
            val userSnapshot = transaction.get(userRef)
            val friendSnapshot = transaction.get(friendRef)

            val currentUser = userSnapshot.toObject(UserAccount::class.java)
                ?: throw Exception("User not found")
            val friend = friendSnapshot.toObject(UserAccount::class.java)
                ?: throw Exception("Friend not found")

            // Update only the friends list of both users
            val updatedUserFriends = currentUser.friends - friendId
            val updatedFriendFriends = friend.friends - userAccount.userId

            transaction.update(userRef, "friends", updatedUserFriends)
            transaction.update(friendRef, "friends", updatedFriendFriends)
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
        val fromUserRef = db.collection(collectionPath).document(fromUser.userId)
        val toUserRef = db.collection(collectionPath).document(toUserId)

        db.runTransaction { transaction ->
            // Get the latest snapshot of the sender's and receiver's documents
            val fromUserSnapshot = transaction.get(fromUserRef)
            val toUserSnapshot = transaction.get(toUserRef)

            val fromUserUpdated = fromUserSnapshot.toObject(UserAccount::class.java)
                ?: throw Exception("Sender user not found")
            val toUserUpdated = toUserSnapshot.toObject(UserAccount::class.java)
                ?: throw Exception("Receiver user not found")

            // Prevent duplicate entries by checking if the request already exists
            if (toUserId in fromUserUpdated.sentRequests) {
                throw Exception("Friend request already sent")
            }

            // Update only the necessary fields
            val newSentRequests = fromUserUpdated.sentRequests + toUserId
            val newReceivedRequests = toUserUpdated.receivedRequests + fromUser.userId

            transaction.update(fromUserRef, "sentRequests", newSentRequests)
            transaction.update(toUserRef, "receivedRequests", newReceivedRequests)
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
        val userRef = db.collection(collectionPath).document(userAccount.userId)
        val friendRef = db.collection(collectionPath).document(friendId)

        db.runTransaction { transaction ->
            val userSnapshot = transaction.get(userRef)
            val friendSnapshot = transaction.get(friendRef)

            val currentUser = userSnapshot.toObject(UserAccount::class.java)
                ?: throw Exception("User not found")
            val friend = friendSnapshot.toObject(UserAccount::class.java)
                ?: throw Exception("Friend not found")

            // Add friend and remove the friend request
            val updatedUserFriends = currentUser.friends + friendId
            val updatedUserReceivedRequests = currentUser.receivedRequests - friendId

            val updatedFriendFriends = friend.friends + userAccount.userId
            val updatedFriendSentRequests = friend.sentRequests - userAccount.userId

            transaction.update(userRef, "friends", updatedUserFriends)
            transaction.update(userRef, "receivedRequests", updatedUserReceivedRequests)

            transaction.update(friendRef, "friends", updatedFriendFriends)
            transaction.update(friendRef, "sentRequests", updatedFriendSentRequests)
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
        val userRef = db.collection(collectionPath).document(userAccount.userId)
        val friendRef = db.collection(collectionPath).document(friendId)

        db.runTransaction { transaction ->
            val userSnapshot = transaction.get(userRef)
            val friendSnapshot = transaction.get(friendRef)

            val currentUser = userSnapshot.toObject(UserAccount::class.java)
                ?: throw Exception("User not found")
            val friend = friendSnapshot.toObject(UserAccount::class.java)
                ?: throw Exception("Friend not found")

            // Remove the friend request
            val updatedUserReceivedRequests = currentUser.receivedRequests - friendId
            val updatedFriendSentRequests = friend.sentRequests - userAccount.userId

            transaction.update(userRef, "receivedRequests", updatedUserReceivedRequests)
            transaction.update(friendRef, "sentRequests", updatedFriendSentRequests)
        }
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }


  override fun searchUsers(
      query: String,
      onSuccess: (List<UserAccount>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val firestore = FirebaseFirestore.getInstance()
    Log.d("UserAccountRepo", "Searching users with query: $query")
    firestore
        .collection(collectionPath)
        .whereGreaterThanOrEqualTo("firstName", query)
        .whereLessThan("firstName", query + "\uf8ff") // Firebase query for prefix matching
        .get()
        .addOnSuccessListener { result ->
          val users = result.documents.mapNotNull { it.toObject(UserAccount::class.java) }
          Log.d("UserAccountRepo", "Found users: $users")
          onSuccess(users)
        }
        .addOnFailureListener { exception ->
          Log.e("UserAccountRepo", "Error searching users", exception)
          onFailure(exception)
        }
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


    override fun getFriendsFromFirestore(
        userId: String,
        onSuccess: (List<UserAccount>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(collectionPath).document(userId).get()
            .addOnSuccessListener { document ->
                val friendIds = document.toObject(UserAccount::class.java)?.friends ?: emptyList()
                Log.d("UserAccountRepof", "Friend IDs: $friendIds")
                val friendAccounts = mutableListOf<UserAccount>()

                val tasks = friendIds.map { friendId ->
                    Log.d("UserAccountRepof", "Fetching friend with ID: $friendId")
                    db.collection(collectionPath).document(friendId).get()
                        .addOnSuccessListener { friendDoc ->
                            friendDoc.toObject(UserAccount::class.java)?.let {
                                friendAccounts.add(it)
                                Log.d("UserAccountRepof", "Added friend account: $it")
                            }
                        }
                        .addOnFailureListener(onFailure)
                }

                Tasks.whenAllComplete(tasks).addOnSuccessListener {
                    Log.d("UserAccountRepof1", "Fetched friend accounts: $friendAccounts")
                    onSuccess(friendAccounts)
                }.addOnFailureListener(onFailure)
            }
    }


    override fun getSentRequestsFromFirestore(
        userId: String,
        onSuccess: (List<UserAccount>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(collectionPath).document(userId).get()
            .addOnSuccessListener { document ->
                val sentRequestIds = document.toObject(UserAccount::class.java)?.sentRequests ?: emptyList()
                Log.d("UserAccountRepof", "Sent Request IDs: $sentRequestIds")
                val sentRequestAccounts = mutableListOf<UserAccount>()

                val tasks = sentRequestIds.map { requestId ->
                    Log.d("UserAccountRepof", "Fetching sent request with ID: $requestId")
                    db.collection(collectionPath).document(requestId).get()
                        .addOnSuccessListener { requestDoc ->
                            requestDoc.toObject(UserAccount::class.java)?.let {
                                sentRequestAccounts.add(it)
                                Log.d("UserAccountRepof", "Added sent request account: $it")
                            }
                        }
                        .addOnFailureListener(onFailure)
                }

                Tasks.whenAllComplete(tasks).addOnSuccessListener {
                    Log.d("UserAccountRepof1", "Fetched sent request accounts: $sentRequestAccounts")
                    onSuccess(sentRequestAccounts)
                }.addOnFailureListener(onFailure)
            }
    }

    override fun getReceivedRequestsFromFirestore(
        userId: String,
        onSuccess: (List<UserAccount>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(collectionPath).document(userId).get()
            .addOnSuccessListener { document ->
                val receivedRequestIds = document.toObject(UserAccount::class.java)?.receivedRequests ?: emptyList()
                Log.d("UserAccountRepof", "Received Request IDs: $receivedRequestIds")
                val receivedRequestAccounts = mutableListOf<UserAccount>()

                val tasks = receivedRequestIds.map { requestId ->
                    Log.d("UserAccountRepof", "Fetching received request with ID: $requestId")
                    db.collection(collectionPath).document(requestId).get()
                        .addOnSuccessListener { requestDoc ->
                            requestDoc.toObject(UserAccount::class.java)?.let {
                                receivedRequestAccounts.add(it)
                                Log.d("UserAccountRepof", "Added received request account: $it")
                            }
                        }
                        .addOnFailureListener(onFailure)
                }

                Tasks.whenAllComplete(tasks).addOnSuccessListener {
                    Log.d("UserAccountRepof1", "Fetched received request accounts: $receivedRequestAccounts")
                    onSuccess(receivedRequestAccounts)
                }.addOnFailureListener(onFailure)
            }
    }


    private fun saveUserAccountToCache(userAccount: UserAccount) {
    CoroutineScope(Dispatchers.IO).launch { localCache.saveUserAccount(userAccount) }
  }
}
