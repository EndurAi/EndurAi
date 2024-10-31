package com.android.sample.model.workout

import com.squareup.moshi.JsonClass

/**
 * Represents a bodyweight workout session which includes a list of bodyweight exercises.
 *
 * @param workoutId Unique identifier for the workout.
 * @param name Name of the workout.
 * @param description Brief description of the workout.
 * @param warmup Boolean indicating if the workout includes a warmup.
 * @param userIdSet Set of user IDs associated with this workout (defaults to an empty set).
 * @param exercises List of exercises included in the workout (defaults to an empty list).
 */
@JsonClass(generateAdapter = true)
class BodyWeightWorkout(
    workoutId: String,
    name: String,
    description: String,
    warmup: Boolean,
    userIdSet: MutableSet<String> = mutableSetOf(),
    val exercises: MutableList<BodyWeightExercise> = mutableListOf() // Default to an empty list
) : Workout(workoutId, name, description, warmup, userIdSet) {

  companion object {
    const val DOCUMENT_NAME = "bodyweightWorkout"
  }

  /**
   * Adds a [BodyWeightExercise] to the list of exercises in the workout.
   *
   * @param exercise The [BodyWeightExercise] to be added.
   */
  fun addExercise(exercise: BodyWeightExercise) {
    exercises.add(exercise)
  }

  /**
   * Removes a [BodyWeightExercise] from the workout by its [exerciseId].
   *
   * @param exerciseId The unique ID of the exercise to remove.
   */
  fun removeExerciseById(exerciseId: String) {
    exercises.removeAll { it.exerciseId == exerciseId }
  }

  /**
   * Adds a user to the workout by their ID.
   *
   * @param id The ID of the user to be added to the workout.
   */
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
}

/** Enum class representing various types of bodyweight exercises. */
enum class BodyWeightExerciseType : ExerciseType {
  PUSH_UPS,
  SQUATS,
  PLANK,
  CHAIR;

  override fun toString(): String {
    return when (this) {
      PUSH_UPS -> "Push-ups"
      SQUATS -> "Squats"
      PLANK -> "Plank"
      CHAIR -> "Chair"
    }
  }
}

/**
 * Data class representing a specific bodyweight exercise.
 *
 * @param exerciseId Unique identifier for the exercise.
 * @param type Type of the bodyweight exercise, represented by [BodyWeightExerciseType].
 * @param detail Additional details about the exercise, stored in [ExerciseDetail].
 */
data class BodyWeightExercise(
    val exerciseId: String,
    val type: BodyWeightExerciseType,
    val detail: ExerciseDetail
) : Exercise(exerciseId, type, detail)
