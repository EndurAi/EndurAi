package com.android.sample.model.userAccount

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class UserAccountViewModel(private val repository: UserAccountRepository) : ViewModel() {

  private val _userAccount = MutableStateFlow<UserAccount?>(null)
  val userAccount: StateFlow<UserAccount?>
    get() = _userAccount.asStateFlow()

  // Loading state
  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean>
    get() = _isLoading.asStateFlow()

  init {
    repository.init { Firebase.auth.currentUser?.let { user -> getUserAccount(user.uid) } }
  }

  fun getUserAccount(userId: String) {
    _isLoading.value = true
    repository.getUserAccount(
        userId = userId,
        onSuccess = {
          _userAccount.value = it
          _isLoading.value = false
        },
        onFailure = {
          _userAccount.value = null
          _isLoading.value = false
        })
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

  // Factory for creating instances of the ViewModel
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserAccountViewModel(
                UserAccountRepositoryFirestore(FirebaseFirestore.getInstance()))
                as T
          }
        }
  }
}
