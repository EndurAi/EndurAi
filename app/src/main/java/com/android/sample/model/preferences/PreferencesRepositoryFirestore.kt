package com.android.sample.model.preferences

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class PreferencesRepositoryFirestore(private val db: FirebaseFirestore) : PreferencesRepository {

  val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

  val preferencesAdapter = moshi.adapter(Preferences::class.java)

  private val collectionPath: String
    get() {
      val uid = Firebase.auth.currentUser?.uid
      return if (uid != null) {
        "$uid"
      } else {
        throw IllegalStateException("The user is not registered")
      }
    }

  private val documentName = "preferences"

  override fun init(onSuccess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override fun getPreferences(onSuccess: (Preferences) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionPath)
        .document(documentName)
        .get()
        .addOnSuccessListener { documents ->
          val preferences = documentSnapshotToPreferences(documents)
          onSuccess(preferences)
        }
        .addOnFailureListener { e -> onFailure(e) }
  }

  override fun updatePreferences(
      prefs: Preferences,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val json = preferencesAdapter.toJson(prefs)

    val preferencesData: Map<String, Any> =
        (moshi.adapter(Map::class.java).fromJson(json) as Map<String, Any>?)!!

    db.collection(collectionPath)
        .document(documentName)
        .set(preferencesData)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onFailure(e) }
  }

  override fun deletePreferences(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionPath)
        .document(documentName)
        .delete()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onFailure(e) }
  }

  private fun documentSnapshotToPreferences(doc: DocumentSnapshot): Preferences {
    if (!doc.exists()) {
      Log.e("DEB", "The document does not exist")
      throw Exception()
    }
    try {

      val json = moshi.adapter(Map::class.java).toJson(doc.data)

      val prefs = preferencesAdapter.fromJson(json)

      return prefs ?: throw IllegalArgumentException("Conversion error in Preferences")
    } catch (e: JsonDataException) {
      Log.e("Moshi", "Data error JSON : ${e.message}")
      throw e
    } catch (e: JsonEncodingException) {
      Log.e("Moshi", "Encoding error JSON : ${e.message}")
      throw e
    } catch (e: Exception) {
      Log.e("Moshi", "Conversion error : ${e.message}")
      throw e
    }
  }
}
