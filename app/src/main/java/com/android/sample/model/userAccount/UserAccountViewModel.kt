package com.android.sample.model.userAccount

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.sample.ui.settings.signOut
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class UserAccountViewModel(
    private val repository: UserAccountRepository,
    private val localCache: UserAccountLocalCache,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

  private val _userAccount = MutableStateFlow<UserAccount?>(null)
  val userAccount: StateFlow<UserAccount?>
    get() = _userAccount.asStateFlow()

  // Loading state
  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean>
    get() = _isLoading.asStateFlow()

  // Minimum display time for loading dialog in milliseconds
  private val minimumLoadingTime = 1500L // 1,5 seconds but could be changed

  init {
    loadCachedUserAccount()
  }

  private fun loadCachedUserAccount() {
    viewModelScope.launch {
      localCache.getUserAccount().collect { cachedAccount ->
        if (cachedAccount != null) {
          _userAccount.value = cachedAccount
        } else {
          // Fetch from repository if no cache exists
          Firebase.auth.currentUser?.uid?.let { userId -> getUserAccount(userId) }
        }
      }
    }
  }

  fun getUserAccount(userId: String) {
    viewModelScope.launch {
      _isLoading.value = true

      // Start the delay for a minimum loading time
      val delayJob = async { delay(minimumLoadingTime) }

      // Properly handle the repository job
      val repositoryJob = launch {
        try {
          repository.getUserAccount(
              userId = userId,
              onSuccess = { userAccount -> _userAccount.value = userAccount },
              onFailure = { exception ->
                Log.e("UserAccountViewModel", "Failed to fetch user account", exception)
                _userAccount.value = null
              })
        } catch (e: Exception) {
          Log.e("UserAccountViewModel", "Unexpected error fetching user account", e)
          _userAccount.value = null
        }
      }

      // Wait for both the delay and repository job to finish
      delayJob.await()
      repositoryJob.join()

      // Set loading to false after both tasks are complete
      _isLoading.value = false
    }
  }

  fun createUserAccount(userAccount: UserAccount) {
    viewModelScope.launch {
      repository.createUserAccount(
          userAccount, onSuccess = { _userAccount.value = userAccount }, onFailure = {})
    }
  }

  fun updateUserAccount(userAccount: UserAccount) {
    viewModelScope.launch {
      repository.updateUserAccount(
          userAccount, onSuccess = { getUserAccount(userAccount.userId) }, onFailure = {})
    }
  }

  fun uploadProfileImage(
      uri: Uri,
      userId: String,
      onSuccess: (String) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val storageRef = FirebaseStorage.getInstance().reference
    val profileImageRef = storageRef.child("profile_images/${userId}/${UUID.randomUUID()}.jpg")

    profileImageRef
        .putFile(uri)
        .addOnSuccessListener {
          profileImageRef.downloadUrl.addOnSuccessListener { downloadUri ->
            onSuccess(downloadUri.toString())
          }
        }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  fun removeFriend(friendId: String) {
    userAccount.value?.let { currentUser ->
      repository.removeFriend(
          userAccount = currentUser,
          friendId = friendId,
          onSuccess = { getUserAccount(currentUser.userId) },
          onFailure = { exception ->
            Log.e("UserAccountViewModel", "Failed to remove friend", exception)
          })
    }
  }

  fun sendFriendRequest(toUserId: String) {
    userAccount.value?.let { currentUser ->
      repository.sendFriendRequest(
          fromUser = currentUser,
          toUserId = toUserId,
          onSuccess = {  },
          onFailure = { exception ->
            Log.e("UserAccountViewModel", "Failed to send friend request", exception)
          })
    }
  }

  fun acceptFriendRequest(friendId: String) {
    userAccount.value?.let { currentUser ->
      repository.acceptFriendRequest(
          userAccount = currentUser,
          friendId = friendId,
          onSuccess = { getUserAccount(currentUser.userId) },
          onFailure = { exception ->
            Log.e("UserAccountViewModel", "Failed to accept friend request", exception)
          })
    }
  }

  fun rejectFriendRequest(friendId: String) {
    userAccount.value?.let { currentUser ->
      repository.rejectFriendRequest(
          userAccount = currentUser,
          friendId = friendId,
          onSuccess = { getUserAccount(currentUser.userId) },
          onFailure = { exception ->
            Log.e("UserAccountViewModel", "Failed to reject friend request", exception)
          })
    }
  }

  // synchronous friends
  suspend fun getFriends(): List<UserAccount> {
    val friends = mutableListOf<UserAccount>()
    userAccount.value?.friends?.forEach { friendId ->
      repository.getUserAccount(
          friendId,
          onSuccess = { friends.add(it) },
          onFailure = { exception ->
            Log.e("UserAccountViewModel", "Failed to get the list of friends", exception)
          })
    }
    return friends
  }

  fun searchUsers(
      query: String,
      onResult: (List<UserAccount>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    if (query.isBlank()) {
      onResult(emptyList()) // If query is blank, return no results
      return
    }

    repository.searchUsers(
        query = query,
        onSuccess = { userList -> onResult(userList) },
        onFailure = { exception ->
          Log.e("UserAccountViewModel", "Failed to search users", exception)
          onFailure(exception)
        })
  }

  suspend fun getSentRequests(): List<UserAccount> {
    val sentRequests = mutableListOf<UserAccount>()
    userAccount.value?.sentRequests?.forEach { requestId ->
      repository.getUserAccount(
          requestId,
          onSuccess = { sentRequests.add(it) },
          onFailure = { exception ->
            Log.e("UserAccountViewModel", "Failed to get the list of sent requests", exception)
          })
    }
    return sentRequests
  }

  suspend fun getReceivedRequests(): List<UserAccount> {
    val receivedRequests = mutableListOf<UserAccount>()
    userAccount.value?.receivedRequests?.forEach { requestId ->
      repository.getUserAccount(
          requestId,
          onSuccess = { receivedRequests.add(it) },
          onFailure = { exception ->
            Log.e("UserAccountViewModel", "Failed to get the list of sent requests", exception)
          })
    }
    return receivedRequests
  }

  // asynchronous friends

  private val _friends = MutableStateFlow<List<UserAccount>>(emptyList())
  val friends: StateFlow<List<UserAccount>>
    get() = _friends.asStateFlow()

  private val _sentRequests = MutableStateFlow<List<UserAccount>>(emptyList())
  val sentRequests: StateFlow<List<UserAccount>>
    get() = _sentRequests.asStateFlow()

  private val _receivedRequests = MutableStateFlow<List<UserAccount>>(emptyList())
  val receivedRequests: StateFlow<List<UserAccount>>
    get() = _receivedRequests.asStateFlow()

  private suspend fun getUserAccountAsync(userId: String): UserAccount? {
    val deferred = CompletableDeferred<UserAccount?>()
    repository.getUserAccount(
        userId, onSuccess = { deferred.complete(it) }, onFailure = { deferred.complete(null) })
    return deferred.await()
  }

//  fun fetchFriends() {
//    viewModelScope.launch {
//      userAccount.value?.let { currentUser ->
//        val friendsList =
//            currentUser.friends
//                .map { friendId -> async { getUserAccountAsync(friendId) } }
//                .awaitAll()
//                .filterNotNull()
//
//        _friends.value = friendsList
//        Log.d("UserAccountViewModel", "Fetched friends list: $friendsList")
//      }
//    }
//  }

    fun fetchFriends() {
        viewModelScope.launch {
            FirebaseAuth.getInstance().currentUser?.let { currentUser ->
                repository.getFriendsFromFirestore(currentUser.uid,
                    onSuccess = { friendsList ->
                        _friends.value = friendsList
                        Log.d("UserAccountViewModel", "Friends list: $friendsList")
                    },
                    onFailure = { exception ->
                        Log.e("UserAccountViewModel", "Error fetching friends: $exception")
                    }
                )
            }
        }
    }



    fun fetchSentRequests() {
        viewModelScope.launch {
            FirebaseAuth.getInstance().currentUser?.let { currentUser ->
                repository.getSentRequestsFromFirestore(currentUser.uid,
                    onSuccess = { sentRequestsList ->
                        _sentRequests.value = sentRequestsList
                        Log.d("UserAccountViewModel", "Sent requests list: $sentRequestsList")
                    },
                    onFailure = { exception ->
                        Log.e("UserAccountViewModel", "Error fetching sent requests: $exception")
                    }
                )
            }
        }
    }


    fun fetchReceivedRequests() {
        viewModelScope.launch {
            FirebaseAuth.getInstance().currentUser?.let { currentUser ->
                repository.getReceivedRequestsFromFirestore(currentUser.uid,
                    onSuccess = { receivedRequestsList ->
                        _receivedRequests.value = receivedRequestsList
                        Log.d("UserAccountViewModel", "Received requests list: $receivedRequestsList")
                    },
                    onFailure = { exception ->
                        Log.e("UserAccountViewModel", "Error fetching received requests: $exception")
                    }
                )
            }
        }
    }

//
//    fun fetchSentRequests() {
//    viewModelScope.launch {
//      userAccount.value?.let { currentUser ->
//        _sentRequests.value = emptyList()
//        val sentRequestsList =
//            currentUser.sentRequests
//                .map { requestId -> async { getUserAccountAsync(requestId) } }
//                .awaitAll()
//                .filterNotNull()
//        _sentRequests.value = sentRequestsList
//        Log.d("UserAccountViewModel", "Fetched sent requests list: $sentRequestsList")
//        Log.d("UserAccountViewModel", "Fetched sent requests list length: ${sentRequestsList.size}")
//      }
//    }
//  }
//
//  fun fetchReceivedRequests() {
//    viewModelScope.launch {
//      userAccount.value?.let { currentUser ->
//        val receivedRequestsList =
//            currentUser.receivedRequests
//                .map { requestId -> async { getUserAccountAsync(requestId) } }
//                .awaitAll()
//                .filterNotNull()
//        _receivedRequests.value = receivedRequestsList
//        Log.d("UserAccountViewModel", "Fetched received requests list: $receivedRequestsList")
//      }
//    }
//  }

  fun deleteAccount(context: Context, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val user = firebaseAuth?.currentUser ?: FirebaseAuth.getInstance().currentUser
    val userId = user?.uid

    if (userId != null) {
      // Delete user data from the repository
      repository.deleteUserAccount(
          userId = userId,
          onSuccess = {
            // Clear the local cache and Firebase Auth account
            clearCacheOnLogout()
            // Attempt to delete the Firebase Auth account
            user
                .delete()
                .addOnSuccessListener {
                  signOut(context) // Log out from Firebase Auth
                  onSuccess()
                }
                .addOnFailureListener { error ->
                  // Check if the error is due to needing re-authentication
                  if (error is FirebaseAuthRecentLoginRequiredException) {
                    // Re-authenticate and retry deletion if necessary
                    reAuthenticateUser(context, user) { reAuthError ->
                      if (reAuthError == null) {
                        // Retry deletion after re-authentication
                        user
                            .delete()
                            .addOnSuccessListener {
                              signOut(context)
                              onSuccess()
                            }
                            .addOnFailureListener(onFailure)
                      } else {
                        onFailure(reAuthError)
                      }
                    }
                  } else {
                    onFailure(error)
                  }
                }
          },
          onFailure = onFailure)
    } else {
      onFailure(Exception("User not logged in"))
    }
  }

  // Clears the local cache on logout
  fun clearCacheOnLogout() {
    viewModelScope.launch {
      localCache.clearUserAccount()
      _userAccount.value = null // Reset the userAccount in ViewModel
    }
  }

  private fun reAuthenticateUser(
      context: Context,
      user: FirebaseUser,
      callback: (Exception?) -> Unit
  ) {
    val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context)
    val idToken = googleSignInAccount?.idToken
    val accessToken = googleSignInAccount?.serverAuthCode
    val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, accessToken)

    user.reauthenticate(credential).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        callback(null) // Re-authentication successful
      } else {
        callback(task.exception) // Pass the error to the callback
      }
    }
  }

  // Factory for creating instances of the ViewModel
  companion object {
    fun provideFactory(context: Context): ViewModelProvider.Factory {
      return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          val firestore = FirebaseFirestore.getInstance()
          val repository = UserAccountRepositoryFirestore(firestore, UserAccountLocalCache(context))
          return UserAccountViewModel(repository, UserAccountLocalCache(context)) as T
        }
      }
    }
  }
}
