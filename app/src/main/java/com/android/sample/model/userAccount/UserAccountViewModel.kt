package com.android.sample.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.UserAccountRepository
import com.android.sample.model.userAccount.UserAccountRepositoryFirestore
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


    fun addFriend(userAccount: UserAccount, friendId: String) {
        repository.addFriend(userAccount, friendId, onSuccess = { getUserAccount(userAccount.userId) }, onFailure = {})
    }
    fun removeFriend(userAccount: UserAccount, friendId: String) {
        repository.removeFriend(userAccount, friendId, onSuccess = { getUserAccount(userAccount.userId) }, onFailure = {})
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
