package com.android.sample.model.workout

import com.squareup.moshi.JsonClass

/**
 * Represents a bodyweight workout session which includes a list of bodyweight exercises.
 *
 * @param workoutId Unique identifier for the workout.
 * @param name Name of the workout.
 * @param description Brief description of the workout.
 * @param userIdSet Set of user IDs associated with this workout (defaults to an empty set).
 * @param exercises List of exercises included in the workout (defaults to an empty list).
 */
@JsonClass(generateAdapter = true)
class WarmUp(
    workoutId: String,
    name: String,
    description: String,
    userIdSet: MutableSet<String> = mutableSetOf(),
    exercises: MutableList<Exercise> = mutableListOf() // Default to an empty list
) : Workout(workoutId, name, description, warmup = true, userIdSet, exercises) {

  companion object {
    const val DOCUMENT_NAME = "warmUp"
  }
}
