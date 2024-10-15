package com.android.sample.model.workout

abstract class Workout(
    val workoutId: String, // Uniquely identifies the workout among all others
    val name: String,
    val description: String,
    val warmup: Boolean, // Whether the user want to do a warmup
    val userIdSet:
        MutableSet<String> // Set of userId that represent the User linked to a specific workout
) {
  abstract val documentName: String
}

// Detail of the exercise based on its type
sealed class ExerciseDetail {
  data class TimeBased(val durationInSeconds: Int, val sets: Int) : ExerciseDetail()

  data class RepetitionBased(val repetitions: Int) : ExerciseDetail()
}
