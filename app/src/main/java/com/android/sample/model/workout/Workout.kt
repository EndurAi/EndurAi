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
    val exercises: MutableList<Exercise> = mutableListOf(),
    val date: LocalDateTime = LocalDateTime.now()
) {

  fun addUserById(id: String) {
    userIdSet.add(id)
  }

  /**
   * Removes a user from the workout by their ID.
   *
   * @param id The ID of the user to be removed from the workout.
   */
  fun removeUserById(id: String) {
    userIdSet.removeAll { it == id }
  }

  fun addExercise(exercise: Exercise) {
    exercises.add(exercise)
  }

  /**
   * Removes a Exercise from the workout by its exercise Id.
   *
   * @param exerciseId The unique ID of the exercise to remove.
   */
  fun removeExerciseById(exerciseId: String) {
    exercises.removeAll { it.id == exerciseId }
  }
}

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
  WARMUP,
  RUNNING;

  override fun toString(): String {
    return when (this) {
      BODY_WEIGHT -> "Body-weight"
      YOGA -> "Yoga"
      RUNNING -> "Running"
      WARMUP -> "Warm-up"
    }
  }
}
