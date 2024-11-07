package com.android.sample.model.workout

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.zacsweers.moshix.sealed.reflect.MoshiSealedJsonAdapterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance

class WorkoutRepositoryFirestore<T : Workout>(
    private val db: FirebaseFirestore,
    private val clazz: Class<T>
) : WorkoutRepository<T> {

  fun getDocumentName(): String {
    val companionObject = clazz.kotlin.companionObjectInstance
    val documentNameProperty =
        clazz.kotlin.companionObject?.members?.find { it.name == "DOCUMENT_NAME" }
    return documentNameProperty?.call(companionObject) as? String
        ?: throw IllegalStateException("DOCUMENT_NAME not found for class ${clazz.simpleName}")
  }

  private val moshi =
      Moshi.Builder()
          .add(MoshiSealedJsonAdapterFactory())
          .add(KotlinJsonAdapterFactory())
          .add(LocalDateTimeAdapter())
          .build()

  private val adapter = moshi.adapter(clazz)
    private val adapterWorkoutID = moshi.adapter(WorkoutID::class.java)

  private val collectionPath: String
    get() {
      val uid = Firebase.auth.currentUser?.uid
      return uid ?: throw IllegalStateException("The user is not registered")
    }

  private val documentToCollectionName: String = "workout"

  private val documentName: String = getDocumentName()

    private val mainDocumentName = "allworkouts"

  override fun getNewUid(): String {
    return db.collection(mainDocumentName)
        .document()
        .id
  }

  override fun init(onSuccess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override fun addDocument(obj: T, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val jsonWorkout = adapter.toJson(obj)

    val dataMapWorkout: Map<String, Any> =
        (moshi.adapter(Map::class.java).fromJson(jsonWorkout) as Map<String, Any>?)!!

      val workoutById = WorkoutID(workoutid = obj.workoutId)
      val jsonWorkoutId = adapterWorkoutID.toJson(workoutById)

      val dataMapWorkoutID: Map<String, Any> =
          (moshi.adapter(Map::class.java).fromJson(jsonWorkoutId) as Map<String, Any>?)!!


      db.collection(collectionPath)
          .document(documentToCollectionName)
          .collection(documentName)
          .document(obj.workoutId)
          .set(dataMapWorkoutID)
          .addOnFailureListener { e -> onFailure(e) }

      db.collection(mainDocumentName)
          .document(obj.workoutId)
          .set(dataMapWorkout)
          .addOnFailureListener { e -> onFailure(e) }
          .addOnSuccessListener { onSuccess() }

  }

  override fun getDocuments(onSuccess: (List<T>) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionPath)
        .document(documentToCollectionName)
        .collection(documentName)
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
              val workoutids = task.result?.mapNotNull { document -> documentSnapshotToObjectIds(document) } ?: emptyList()
              val workouts = mutableListOf<T>()
              val tasks = mutableListOf<Task<DocumentSnapshot>>()

              for (id in workoutids) {
                  val task = db.collection(mainDocumentName)
                      .document(id)
                      .get()
                      .addOnSuccessListener { document ->
                          documentSnapshotToObject(document)?.let { workouts.add(it) }
                      }
                      .addOnFailureListener { e ->
                          Log.e("WorkoutRepositoryFirestore", "Error getting workout document", e)
                          onFailure(e)
                      }
                  tasks.add(task)
              }

              // Wait for all tasks to complete
              Tasks.whenAllComplete(tasks)
                  .addOnSuccessListener { onSuccess(workouts) }
                  .addOnFailureListener { e -> onFailure(e) }

          } else {
            task.exception?.let { e ->
              Log.e("WorkoutRepositoryFirestore", "Error getting workout IDs Document", e)
              onFailure(e)
            }
          }
        }
  }

  override fun updateDocument(obj: T, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val json = adapter.toJson(obj)

    val dataMap: Map<String, Any> =
        (moshi.adapter(Map::class.java).fromJson(json) as Map<String, Any>?)!!

    db.collection(mainDocumentName)
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
        .addOnFailureListener { e -> onFailure(e) }

      db.collection(mainDocumentName)
          .document(id)
          .delete()
          .addOnFailureListener { e -> onFailure(e) }
          .addOnSuccessListener { onSuccess() }
  }

  private fun documentSnapshotToObjectIds(doc: DocumentSnapshot): String? {
    if (!doc.exists()) {
      Log.e("DEB", "The document does not exist")
      return null
    }
    return try {
      val json = moshi.adapter(Map::class.java).toJson(doc.data)
        adapterWorkoutID.fromJson(json)?.workoutid
    } catch (e: JsonDataException) {
      Log.e("Moshi", "Data id error JSON : ${e.message}")
      throw e
    } catch (e: JsonEncodingException) {
      Log.e("Moshi", "Encoding id error JSON : ${e.message}")
      throw e
    } catch (e: Exception) {
      Log.e("Moshi", "Conversion id error : ${e.message}")
      throw e
    }
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

class LocalDateTimeAdapter {
  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  @ToJson
  fun toJson(dateTime: LocalDateTime): String {
    return dateTime.format(formatter)
  }

  @FromJson
  fun fromJson(dateTimeString: String): LocalDateTime {
    return LocalDateTime.parse(dateTimeString, formatter)
  }
}
