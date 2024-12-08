package com.android.sample.model.achievements
import android.util.Log
import com.android.sample.model.workout.WorkoutRepositoryFirestore.LatLngAdapter
import com.android.sample.model.workout.WorkoutRepositoryFirestore.LatLngListAdapter
import com.android.sample.model.workout.WorkoutRepositoryFirestore.LocalDateTimeAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.zacsweers.moshix.sealed.reflect.MoshiSealedJsonAdapterFactory

/**
 * Repository implementation for storing and managing `workoutStatistics` documents in Firebase Firestore.
 *
 * @property db Firebase Firestore instance.
 */
open class StatisticsRepositoryFirestore(
    private val db: FirebaseFirestore
) : StatisticsRepository {
    private val moshi =
        Moshi.Builder()
            .add(MoshiSealedJsonAdapterFactory())
            .add(KotlinJsonAdapterFactory())
            .add(LocalDateTimeAdapter())
            .add(LatLngAdapter())
            .add(LatLngListAdapter(LatLngAdapter()))
            .build()
    private val adapter: JsonAdapter<WorkoutStatistics> = moshi.adapter(WorkoutStatistics::class.java)

    private val collectionName = "user_statistics"

    override fun init(onSuccess: () -> Unit) {
        Firebase.auth.addAuthStateListener {
            if (it.currentUser != null) {
                onSuccess()
            }
        }
    }

    /**
     * Retrieves the workouts statistics
     *
     * @return The list of workoutStatistics.
     * @throws Exception if something went wrong.
     */
    override fun getStatistics(
        onSuccess: (List<WorkoutStatistics>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = Firebase.auth.currentUser?.uid ?: return onFailure(Exception("User not authenticated"))
        db.collection(collectionName)
            .document(uid)
            .collection("workouts")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val stats =
                        task.result?.mapNotNull { document ->
                            val json = moshi.adapter(Map::class.java).toJson(document.data)
                            adapter.fromJson(json)
                        } ?: emptyList()

                    onSuccess(stats)
                } else {
                    task.exception?.let { e ->
                        Log.e("WorkoutRepositoryFirestore", "Error getting workout IDs Document", e)
                        onFailure(e)
                    }
                }
        }
    }

/**
 * Adds a workout statistics to the current repository list.
 *
 * @param workout  The workout statistics to be stored.
 * @param onSuccess Callback triggered upon successful addition.
 * @param onFailure Callback triggered upon failure with the exception.
 */
    override fun addWorkout(
        workout: WorkoutStatistics,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = Firebase.auth.currentUser?.uid ?: return onFailure(Exception("User not authenticated"))
        val json = adapter.toJson(workout)

        val dataMap: Map<String, Any> =
            (moshi.adapter(Map::class.java).fromJson(json) as? Map<String, Any>) ?: emptyMap()

        db.collection(collectionName)
            .document(uid)
            .collection("workouts")
            .document(workout.id)
            .set(dataMap)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}
