package com.android.sample.model.workout

import com.squareup.moshi.JsonClass
import dev.zacsweers.moshix.sealed.annotations.TypeLabel
import java.time.LocalDateTime

abstract class Workout(
    val workoutId: String, // Uniquely identifies the workout among all others
    val name: String,
    val description: String,
    val warmup: Boolean, // Whether the user want to do a warmup
    val userIdSet:
        MutableSet<String>, // Set of userId that represent the User linked to a specific workout
    val date : LocalDateTime
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

enum class WorkoutType {
  BODY_WEIGHT,
  YOGA,
  RUNNING;

  override fun toString(): String {
    return when (this) {
      BODY_WEIGHT -> "Body-weight"
      YOGA -> "Yoga"
      RUNNING -> "Running"
    }
  }
}
