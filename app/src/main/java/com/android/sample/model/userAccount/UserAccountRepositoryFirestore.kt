package com.android.sample.model.userAccount

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class UserAccountRepositoryFirestore(private val db: FirebaseFirestore) : UserAccountRepository {

  private val collectionPath = "userAccounts"

  override fun init(onSuccess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override fun getUserAccount(
      userId: String,
      onSuccess: (UserAccount) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath)
        .document(userId)
        .get()
        .addOnSuccessListener { document ->
          document.toObject(UserAccount::class.java)?.let { userAccount -> onSuccess(userAccount) }
              ?: onFailure(Exception("UserAccount not found"))
        }
        .addOnFailureListener { exception ->
          Log.e("UserAccountRepo", "Error getting user account", exception)
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
        .addOnSuccessListener { onSuccess() }
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
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception ->
          Log.e("UserAccountRepo", "Error updating user account", exception)
          onFailure(exception)
        }
  }
}
