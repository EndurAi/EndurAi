package com.android.sample.model.workout

import com.squareup.moshi.JsonClass
import dev.zacsweers.moshix.sealed.annotations.TypeLabel

abstract class Workout(
    val workoutId: String, // Uniquely identifies the workout among all others
    val name: String,
    val description: String,
    val warmup: Boolean, // Whether the user want to do a warmup
    val userIdSet:
        MutableSet<String> // Set of userId that represent the User linked to a specific workout
) {}

// Detail of the exercise based on its type
@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed class ExerciseDetail {

  @TypeLabel("TimeBased")
  @JsonClass(generateAdapter = true)
  data class TimeBased(val durationInSeconds: Int, val sets: Int) : ExerciseDetail()

  @TypeLabel("RepetitionBased")
  @JsonClass(generateAdapter = true)
  data class RepetitionBased(val repetitions: Int) : ExerciseDetail()
}
