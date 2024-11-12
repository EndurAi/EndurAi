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
