package com.android.sample.model.workout

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.zacsweers.moshix.sealed.reflect.MoshiSealedJsonAdapterFactory
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance

class WorkoutRepositoryFirestore<T : Workout>(
    private val db: FirebaseFirestore,
    private val clazz: Class<T>
) : WorkoutRepository<T> {

    fun getDocumentName(): String {
        val companionObject = clazz.kotlin.companionObjectInstance
        val documentNameProperty = clazz.kotlin.companionObject?.members?.find { it.name == "DOCUMENT_NAME" }
        return documentNameProperty?.call(companionObject) as? String
            ?: throw IllegalStateException("DOCUMENT_NAME not found for class ${clazz.simpleName}")
    }

  private val moshi = Moshi.Builder()
      .add(MoshiSealedJsonAdapterFactory())
      .add(KotlinJsonAdapterFactory())
      .build()

  private val adapter = moshi.adapter(clazz)

  private val collectionPath: String
    get() {
      val uid = Firebase.auth.currentUser?.uid
      return uid ?: throw IllegalStateException("The user is not registered")
    }

  private val documentToCollectionName: String = "workout"

  private val documentName: String = getDocumentName()

  override fun getNewUid(): String {
    return db
        .collection(collectionPath)
        .document(documentToCollectionName)
        .collection(documentName).document().id
  }

  override fun init(onSuccess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override fun addDocument(obj: T, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val json = adapter.toJson(obj)

    val dataMap: Map<String, Any> =
        (moshi.adapter(Map::class.java).fromJson(json) as Map<String, Any>?)!!

    db.collection(collectionPath)
        .document(documentToCollectionName)
        .collection(documentName)
        .document(obj.workoutId)
        .set(dataMap)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onFailure(e) }
  }

  override fun getDocuments(onSuccess: (List<T>) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionPath)
        .document(documentToCollectionName)
        .collection(documentName)
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            val workouts =
                task.result?.mapNotNull { document -> documentSnapshotToObject(document) }
                    ?: emptyList()
            onSuccess(workouts)
          } else {
            task.exception?.let { e ->
              Log.e("WorkoutRepositoryFirestore", "Error getting documents", e)
              onFailure(e)
            }
          }
        }
  }

  override fun updateDocument(obj: T, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val json = adapter.toJson(obj)

    val dataMap: Map<String, Any> =
        (moshi.adapter(Map::class.java).fromJson(json) as Map<String, Any>?)!!

    db.collection(collectionPath)
        .document(documentToCollectionName)
        .collection(documentName)
        .document(obj.workoutId)
        .set(dataMap)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onFailure(e) }
  }

  override fun deleteDocument(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionPath)
        .document(documentToCollectionName)
        .collection(documentName)
        .document(id)
        .delete()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onFailure(e) }
  }

  private fun documentSnapshotToObject(doc: DocumentSnapshot): T? {
    if (!doc.exists()) {
      Log.e("DEB", "The document does not exist")
      return null
    }
    return try {
      val json = moshi.adapter(Map::class.java).toJson(doc.data)
      adapter.fromJson(json)
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
