package com.android.sample.model.workout

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

/**
 * Represents a Yoga workout session which includes a list of yoga exercises.
 *
 * @param workoutId Unique identifier for the workout.
 * @param name Name of the workout.
 * @param description Brief description of the workout.
 * @param warmup Boolean indicating if the workout includes a warmup.
 * @param userIdSet Set of user IDs associated with this workout (defaults to an empty set).
 * @param date Date and Time of the workout.
 * @param exercises List of exercises included in the workout (defaults to an empty list).
 */
@JsonClass(generateAdapter = true)
class YogaWorkout(
    workoutId: String,
    name: String,
    description: String,
    warmup: Boolean,
    userIdSet: MutableSet<String> = mutableSetOf(),
    exercises: MutableList<Exercise> // Default to an empty list
) : Workout(workoutId, name, description, warmup, userIdSet, exercises) {

  companion object {
    const val DOCUMENT_NAME = "yogaWorkout"
  }
}
