package com.android.sample.model.workout

/**
 * Interface defining the contract for a Firestore-based repository that handles workout data.
 *
 * @param T The type of workout that extends the [Workout] class.
 */
interface WorkoutRepository<T : Workout> {

  /**
   * Generates a new unique identifier (UID) for a workout entree in Firestore.
   *
   * @return A unique identifier for a new workout entree.
   */
  fun getNewUid(): String

  /**
   * Initializes the repository and performs necessary setup actions when the user is authenticated.
   *
   * @param onSuccess A callback function that is invoked once the user is authenticated.
   */
  fun init(onSuccess: () -> Unit)

  /**
   * Retrieves a document from the Firestore database corresponding to the workout.
   *
   * @param onSuccess A callback function that is invoked with the workout data upon successful
   *   retrieval.
   * @param onFailure A callback function that is invoked in case of an error during the retrieval.
   */
  fun getDocuments(onSuccess: (List<T>) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Add a workout document in Firestore.
   *
   * @param obj The workout object that will be serialized and sent to Firestore.
   * @param onSuccess A callback function that is invoked when the document is successfully added to
   *   Firestore.
   * @param onFailure A callback function that is invoked in case of an error during the update.
   */
  fun addDocument(obj: T, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Updates an existing workout document in Firestore.
   *
   * @param obj The workout object that will be serialized and sent to Firestore.
   * @param onSuccess A callback function that is invoked upon successful update.
   * @param onFailure A callback function that is invoked in case of an error during the update.
   */
  fun updateDocument(obj: T, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Deletes the workout document from Firestore.
   *
   * @param onSuccess A callback function that is invoked upon successful deletion.
   * @param onFailure A callback function that is invoked in case of an error during the deletion.
   */
  fun deleteDocument(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
