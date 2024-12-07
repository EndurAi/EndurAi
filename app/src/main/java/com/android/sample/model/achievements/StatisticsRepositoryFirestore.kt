package com.android.sample.model.achievements
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class StatisticsRepositoryFirestore(
    private val db: FirebaseFirestore
) : StatisticsRepository {

    private val auth = FirebaseAuth.getInstance()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val adapter: JsonAdapter<WorkoutStatistics> = moshi.adapter(WorkoutStatistics::class.java)

    private val collectionName = "user_statistics"

    override fun init(onSuccess: () -> Unit) {
        auth.addAuthStateListener {
            if (auth.currentUser != null) {
                onSuccess()
            }
        }
    }

    override fun getStatistics(
        onSuccess: (List<WorkoutStatistics>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return onFailure(Exception("User not authenticated"))

        db.collection(collectionName)
            .document(uid)
            .collection("workouts")
            .get()
            .addOnSuccessListener { result ->
                val stats = result.mapNotNull { document ->
                    try {
                        adapter.fromJson(document.data.toString())
                    } catch (e: Exception) {
                        null
                    }
                }
                onSuccess(stats)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    override fun addWorkout(
        workout: WorkoutStatistics,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return onFailure(Exception("User not authenticated"))
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
