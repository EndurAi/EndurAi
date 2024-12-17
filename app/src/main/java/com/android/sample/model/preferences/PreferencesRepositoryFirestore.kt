package com.android.sample.model.preferences

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

open class PreferencesRepositoryFirestore(private val db: FirebaseFirestore, private val localCache: PreferencesLocalCache) :
    PreferencesRepository {

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
      CoroutineScope(Dispatchers.IO).launch {
          // First check the local cache
          val cachedPreferences = localCache.getPreferences().firstOrNull()
          if (cachedPreferences != null) {
              Log.d("PreferencesRepository", "Loaded preferences from cache.")
              onSuccess(cachedPreferences)
          } else {
              // Fetch from Firestore if no cache exists
              db.collection(collectionPath)
                  .document(documentName)
                  .get()
                  .addOnSuccessListener { document ->
                      val preferences = documentSnapshotToPreferences(document)
                      onSuccess(preferences)
                      // Save to local cache
                      CoroutineScope(Dispatchers.IO).launch {
                          localCache.savePreferences(preferences)
                      }
                  }
                  .addOnFailureListener { e ->
                      Log.e("PreferencesRepository", "Error fetching preferences from Firestore", e)
                      onFailure(e)
                  }
          }
      }
  }

    override fun updatePreferences(
        prefs: Preferences,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val json = preferencesAdapter.toJson(prefs)
        val preferencesData = moshi.adapter(Map::class.java).fromJson(json) as Map<String, Any>

        db.collection(collectionPath)
            .document(documentName)
            .set(preferencesData)
            .addOnSuccessListener {
                Log.d("PreferencesRepository", "Preferences updated in Firestore.")
                onSuccess()
                // Save to cache
                CoroutineScope(Dispatchers.IO).launch { localCache.savePreferences(prefs) }
            }
            .addOnFailureListener { e ->
                Log.e("PreferencesRepository", "Error updating preferences in Firestore", e)
                onFailure(e)
            }
    }

    override fun deletePreferences(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(collectionPath)
            .document(documentName)
            .delete()
            .addOnSuccessListener {
                Log.d("PreferencesRepository", "Preferences deleted from Firestore.")
                onSuccess()
                CoroutineScope(Dispatchers.IO).launch { localCache.clearPreferences() }
            }
            .addOnFailureListener { e ->
                Log.e("PreferencesRepository", "Error deleting preferences", e)
                onFailure(e)
            }
    }

  open fun documentSnapshotToPreferences(doc: DocumentSnapshot): Preferences {
    if (!doc.exists()) {
      Log.e("DEB", "The document does not exist")
      return PreferencesViewModel.defaultPreferences
    }
    try {

      val json = moshi.adapter(Map::class.java).toJson(doc.data)

      val prefs = preferencesAdapter.fromJson(json)

      return prefs ?: throw IllegalArgumentException("Conversion error in Preferences")
    } catch (e: Error) {
      Log.e("Moshi", "Moshi error : ${e.message}")
      throw e
    }
  }
}
