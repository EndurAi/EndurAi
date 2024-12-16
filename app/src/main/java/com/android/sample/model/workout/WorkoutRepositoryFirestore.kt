package com.android.sample.model.workout

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.LatLng
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
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

/**
 * Repository implementation for storing and managing `Workout` documents in Firebase Firestore.
 * Handles CRUD operations with Firestore using Moshi for JSON serialization/deserialization.
 *
 * @param T The type of workout model, constrained to classes that extend `Workout`.
 * @property db Firebase Firestore instance.
 * @property clazz Class reference for the generic workout model.
 */
open class WorkoutRepositoryFirestore<T : Workout>(
    private val db: FirebaseFirestore,
    private val clazz: Class<T>
) : WorkoutRepository<T> {

  /**
   * Retrieves the document name for the specified workout model. Expects a companion object
   * property named `DOCUMENT_NAME` in the workout model class.
   *
   * @return The document name for the model.
   * @throws IllegalStateException if `DOCUMENT_NAME` is not found.
   */
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
          .add(LatLngAdapter())
          .add(LatLngListAdapter(LatLngAdapter()))
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
    private val doneDocumentName = "doneWorkouts"

  /**
   * Generates a new unique ID for a workout document in the Firestore.
   *
   * @return A new document ID.
   */
  override fun getNewUid(): String {
    return db.collection(mainDocumentName).document().id
  }

  override fun init(onSuccess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  /**
   * Adds a workout document to the Firestore database.
   *
   * @param obj Workout object to add.
   * @param onSuccess Callback triggered upon successful addition.
   * @param onFailure Callback triggered upon failure with the exception.
   */
  override fun addDocument(obj: T, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val jsonWorkout = adapter.toJson(obj)

    val dataMapWorkout: Map<String, Any> =
        (moshi.adapter(Map::class.java).fromJson(jsonWorkout) as Map<String, Any>?)!!

    val workoutById = WorkoutID(workoutid = obj.workoutId)
    val jsonWorkoutId = adapterWorkoutID.toJson(workoutById)

    val dataMapWorkoutID: Map<String, Any> =
        (moshi.adapter(Map::class.java).fromJson(jsonWorkoutId) as Map<String, Any>?)!!

    // add the workout id in user document

    db.collection(collectionPath)
        .document(documentToCollectionName)
        .collection(documentName)
        .document(obj.workoutId)
        .set(dataMapWorkoutID)
        .addOnFailureListener { e -> onFailure(e) }

    // add the workout content in allworkouts document

    db.collection(mainDocumentName)
        .document(obj.workoutId)
        .set(dataMapWorkout)
        .addOnFailureListener { e -> onFailure(e) }
        .addOnSuccessListener { onSuccess() }
  }

  /**
   * Retrieves all workout documents for the authenticated user.
   *
   * @param onSuccess Callback triggered with the list of workout documents on success.
   * @param onFailure Callback triggered with an exception on failure.
   */
  override fun getDocuments(onSuccess: (List<T>) -> Unit, onFailure: (Exception) -> Unit) {

    // we first get document'ids from user document
    db.collection(collectionPath)
        .document(documentToCollectionName)
        .collection(documentName)
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            val workoutids =
                task.result?.mapNotNull { document ->
                  documentSnapshotToObject(document, adapterWorkoutID) { it?.workoutid } as? String
                } ?: emptyList()
            val workouts = mutableListOf<T>()
            val tasks = mutableListOf<Task<DocumentSnapshot>>()

            // for each id we get the content of the workout in "allworkouts" document

            for (id in workoutids) {
              val task =
                  db.collection(mainDocumentName)
                      .document(id)
                      .get()
                      .addOnSuccessListener { document ->
                        val workout = documentSnapshotToObject(document, adapter) as T
                        workout.let { workouts.add(workout) }
                      }
                      .addOnFailureListener { e ->
                        Log.e("WorkoutRepositoryFirestore", "Error getting workout document", e)
                        onFailure(e)
                      }
              tasks.add(task)
            }

            // Wait for all tasks to complete and give all the workouts to "onSuccess"
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

  /**
   * Updates a workout document in the Firestore.
   *
   * @param obj Workout object to update.
   * @param onSuccess Callback triggered upon successful update.
   * @param onFailure Callback triggered upon failure with the exception.
   */
  override fun updateDocument(obj: T, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val json = adapter.toJson(obj)

    val dataMap: Map<String, Any> =
        (moshi.adapter(Map::class.java).fromJson(json) as Map<String, Any>?)!!

    // we just need to update in the "allworkouts" document

    db.collection(mainDocumentName)
        .document(obj.workoutId)
        .set(dataMap)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onFailure(e) }
  }

  /**
   * Deletes a workout document from the Firestore.
   *
   * @param id The ID of the workout document to delete.
   * @param onSuccess Callback triggered upon successful deletion.
   * @param onFailure Callback triggered upon failure with the exception.
   */
  override fun deleteDocument(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {

    // we delete the id in user document

    db.collection(collectionPath)
        .document(documentToCollectionName)
        .collection(documentName)
        .document(id)
        .delete()
        .addOnFailureListener { e -> onFailure(e) }

    // we delete the content of the workout in "allworkouts" document

    db.collection(mainDocumentName)
        .document(id)
        .delete()
        .addOnFailureListener { e -> onFailure(e) }
        .addOnSuccessListener { onSuccess() }
  }

    /**
     * Transfer a finished workout to a done collection
     *
     * @param id The id of the workout to transfer.
     * @param onSuccess A callback function that is invoked upon successful deletion.
     * @param onFailure A callback function that is invoked in case of an error during the deletion.
     */
    override fun transferDocumentToDone(
        id: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val workoutById = WorkoutID(workoutid = id)
        val jsonWorkoutId = adapterWorkoutID.toJson(workoutById)

        val dataMapWorkoutID: Map<String, Any> =
            (moshi.adapter(Map::class.java).fromJson(jsonWorkoutId) as Map<String, Any>?)!!

        // Get document from all workout
        db.collection(mainDocumentName).document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val workoutData = document.data ?: emptyMap()
                    // Add the workout data to the collection "done"
                    db.collection(doneDocumentName).document(id).set(workoutData)
                        .addOnSuccessListener {
                            // Delete the document from "allworkouts"
                            deleteDocument(id = id, onSuccess = {
                                //Add the workout id in the done list of the user
                                db.collection(collectionPath)
                                    .document(doneDocumentName)
                                    .collection(documentName)
                                    .document(id)
                                    .set(dataMapWorkoutID)
                                    .addOnSuccessListener { onSuccess() }
                                    .addOnFailureListener { onFailure(it) }
                            }, onFailure = onFailure)
                        }
                        .addOnFailureListener { onFailure(it) }
                } else {
                    onFailure(Exception("Document with ID $id does not exist in allworkouts"))
                }
            }
            .addOnFailureListener { onFailure(it) }

    }

    /**
     * Import a done workout to the current collection.
     *
     * @param id The id of the workout to transfer.
     * @param onSuccess A callback function that is invoked upon successful deletion.
     * @param onFailure A callback function that is invoked in case of an error during the deletion.
     */
    override fun importDocumentFromDone(
        id: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Get the document from done
        db.collection(doneDocumentName).document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Add the document to "allworkouts"
                    db.collection(mainDocumentName).document(id).set(document)
                        .addOnSuccessListener {
                            // Delete the document from done
                            db.collection(doneDocumentName).document(id).delete()
                                .addOnSuccessListener {
                                    val workoutById = WorkoutID(workoutid = id)
                                    val jsonWorkoutId = adapterWorkoutID.toJson(workoutById)

                                    val dataMapWorkoutID: Map<String, Any> =
                                        (moshi.adapter(Map::class.java).fromJson(jsonWorkoutId) as Map<String, Any>?)!!

                                    // Add the id to the user workout list
                                    db.collection(collectionPath)
                                        .document(documentToCollectionName)
                                        .collection(documentName)
                                        .document(id)
                                        .set(dataMapWorkoutID)
                                        .addOnFailureListener { e -> onFailure(e) }
                                }
                                .addOnFailureListener { onFailure(it) }
                        }
                        .addOnFailureListener { onFailure(it) }
                } else {
                    onFailure(Exception("Document with ID $id does not exist in done"))
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Retrieves done documents from the Firestore.
     *
     * @param onSuccess A callback function that is invoked with the workout data upon successful
     *   retrieval.
     * @param onFailure A callback function that is invoked in case of an error during the retrieval.
     */
    override fun getDoneDocuments(onSuccess: (List<T>) -> Unit, onFailure: (Exception) -> Unit) {
        // we first get document'ids from user document
        db.collection(collectionPath)
            .document(doneDocumentName)
            .collection(documentName)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val workoutids =
                        task.result?.mapNotNull { document ->
                            documentSnapshotToObject(document, adapterWorkoutID) { it?.workoutid } as? String
                        } ?: emptyList()
                    val workouts = mutableListOf<T>()
                    val tasks = mutableListOf<Task<DocumentSnapshot>>()

                    // for each id we get the content of the workout in "doneWorkouts" document

                    for (id in workoutids) {
                        val task =
                            db.collection(doneDocumentName)
                                .document(id)
                                .get()
                                .addOnSuccessListener { document ->
                                    val workout = documentSnapshotToObject(document, adapter) as T
                                    workout.let { workouts.add(workout) }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("WorkoutRepositoryFirestore", "Error getting done workout document", e)
                                    onFailure(e)
                                }
                        tasks.add(task)
                    }

                    // Wait for all tasks to complete and give all the done workouts to "onSuccess"
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

    /**
   * Converts a Firestore `DocumentSnapshot` to an object of type `R`. Serializes the document data
   * using the provided `JsonAdapter`.
   *
   * @param doc The Firestore `DocumentSnapshot`.
   * @param adapter JsonAdapter for serializing the document data.
   * @param mapResult Function to map the deserialized object if needed.
   * @return The mapped object or null if the document does not exist.
   */
  private fun <R> documentSnapshotToObject(
      doc: DocumentSnapshot,
      adapter: JsonAdapter<R>,
      mapResult: (R) -> Any? = { it }
  ): Any? {
    if (!doc.exists()) {
      Log.e("DEB", "The document does not exist")
      return null
    }
    return try {
      val json = moshi.adapter(Map::class.java).toJson(doc.data)
      val result = adapter.fromJson(json)
      result?.let { mapResult(it) }
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

  /** Adapter for serializing and deserializing `LocalDateTime` instances to/from JSON strings. */
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

  data class LatLngJson(val latitude: Double, val longitude: Double)

  class LatLngAdapter {
    @ToJson
    fun toJson(latLng: LatLng): LatLngJson {
      return LatLngJson(latLng.latitude, latLng.longitude)
    }

    @FromJson
    fun fromJson(latLngJson: LatLngJson): LatLng {
      return LatLng.newBuilder()
          .setLatitude(latLngJson.latitude)
          .setLongitude(latLngJson.longitude)
          .build()
    }
  }

  class LatLngListAdapter(private val latLngAdapter: LatLngAdapter) {

    @ToJson
    fun toJson(list: List<LatLng>): List<Map<String, Double>> {
      return list.map { latLng ->
        mapOf("latitude" to latLng.latitude, "longitude" to latLng.longitude)
      }
    }

    @FromJson
    fun fromJson(jsonList: List<Map<String, Double>>): List<LatLng> {
      return jsonList.map { json ->
        LatLng.newBuilder()
            .setLatitude(json["latitude"] ?: throw JsonDataException("Missing latitude"))
            .setLongitude(json["longitude"] ?: throw JsonDataException("Missing longitude"))
            .build()
      }
    }
  }
}
