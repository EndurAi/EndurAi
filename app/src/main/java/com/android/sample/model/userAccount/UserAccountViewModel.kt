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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class UserAccountViewModel(
    private val repository: UserAccountRepository,
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
    repository.init { Firebase.auth.currentUser?.let { user -> getUserAccount(user.uid) } }
  }

    fun getUserAccount(userId: String) {
        _isLoading.value = true

        viewModelScope.launch {
            // Start both the delay and the repository operation concurrently
            val delayJob = async {
                delay(minimumLoadingTime)
                true // Return true to indicate the delay has completed
            }

            val repositoryJob = async {
                var result = false
                repository.getUserAccount(
                    userId = userId,
                    onSuccess = {
                        _userAccount.value = it
                        result = true
                    },
                    onFailure = {
                        _userAccount.value = null
                        result = true
                    }
                )
                result // Return true after repository fetch completes
            }

            // Wait for both delay and repository operation to complete
            awaitAll(delayJob, repositoryJob)

            // After both are done, set isLoading to false
            _isLoading.value = false
        }
    }

  fun createUserAccount(userAccount: UserAccount) {
    repository.createUserAccount(
        userAccount, onSuccess = { _userAccount.value = userAccount }, onFailure = {})
  }

  fun updateUserAccount(userAccount: UserAccount) {
    repository.updateUserAccount(
        userAccount, onSuccess = { getUserAccount(userAccount.userId) }, onFailure = {})
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
          onSuccess = { getUserAccount(currentUser.userId) },
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

  fun getFriends(): List<UserAccount> {
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

  fun getSentRequests(): List<UserAccount> {
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

  fun getReceivedRequests(): List<UserAccount> {
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

  fun deleteAccount(context: Context, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val user = firebaseAuth?.currentUser ?: FirebaseAuth.getInstance().currentUser
    val userId = user?.uid

    if (userId != null) {
      // Delete user data from the repository
      repository.deleteUserAccount(
          userId = userId,
          onSuccess = {
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
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserAccountViewModel(
                UserAccountRepositoryFirestore(FirebaseFirestore.getInstance()),
                FirebaseAuth.getInstance())
                as T
          }
        }
  }
}
