package com.android.sample.model.workout

open class Workout(
    val name: String,
    val description: String,
    val warmup: Boolean // Whether the user want to do a warmup
)

// Detail of the exercise based on its type
sealed class ExerciseDetail {
  data class TimeBased(val durationInSeconds: Int, val sets: Int) : ExerciseDetail()

  data class RepetitionBased(val repetitions: Int) : ExerciseDetail()
}
