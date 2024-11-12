package com.android.sample.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.UserAccountLocalCache
import com.android.sample.model.userAccount.UserAccountRepository
import com.android.sample.model.userAccount.UserAccountRepositoryFirestore
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
                    Firebase.auth.currentUser?.uid?.let { userId ->
                        getUserAccount(userId)
                    }
                }
            }
        }
    }

    fun getUserAccount(userId: String) {
        viewModelScope.launch {
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
                }
            )
        }
  }

  fun createUserAccount(userAccount: UserAccount) {
      viewModelScope.launch {
          repository.createUserAccount(
              userAccount,
              onSuccess = { _userAccount.value = userAccount },
              onFailure = {}
          )
      }
  }

  fun updateUserAccount(userAccount: UserAccount) {
      viewModelScope.launch {
          repository.updateUserAccount(
              userAccount,
              onSuccess = {
                  getUserAccount(userAccount.userId)
              },
              onFailure = {}
          )
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
