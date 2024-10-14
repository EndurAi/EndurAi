package com.android.sample.model.workout

/**
 * Represents a Yoga workout session which includes a list of yoga exercises.
 *
 * @param workoutId Unique identifier for the workout.
 * @param name Name of the workout.
 * @param description Brief description of the workout.
 * @param warmup Boolean indicating if the workout includes a warmup.
 * @param userIdSet Set of user IDs associated with this workout (defaults to an empty set).
 * @param exercises List of exercises included in the workout (defaults to an empty list).
 */
class YogaWorkout(
    workoutId: String,
    name: String,
    description: String,
    warmup: Boolean,
    userIdSet: MutableSet<String> = mutableSetOf(),
    val exercises: MutableList<YogaExercise> = mutableListOf() // Default to an empty list
) : Workout(workoutId, name, description, warmup, userIdSet) {

  /**
   * Adds a [YogaExercise] to the list of exercises in the workout.
   *
   * @param exercise The [YogaExercise] to be added.
   */
  fun addExercise(exercise: YogaExercise) {
    exercises.add(exercise) // Correctly adds the exercise to the list
  }

  /**
   * Removes a [YogaExercise] from the workout by its [exerciseId].
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

/** Enum class representing various types of yoga exercises. */
enum class YogaExerciseType {
  DOWNWARD_DOG,
  TREE_POSE,
  SUN_SALUTATION,
  WARRIOR_II
}

/**
 * Data class representing a specific yoga exercise.
 *
 * @param exerciseId Unique identifier for the exercise.
 * @param type Type of the yoga exercise, represented by [YogaExerciseType].
 * @param detail Additional details about the exercise, stored in [ExerciseDetail].
 */
data class YogaExercise(
    val exerciseId: String,
    val type: YogaExerciseType,
    val detail: ExerciseDetail
)
