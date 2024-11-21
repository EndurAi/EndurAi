package com.android.sample.model.workout

import com.google.type.LatLng
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

/**
 * Represents a running workout session which includes a path and time.
 *
 * @param workoutId Unique identifier for the workout.
 * @param name Name of the workout.
 * @param description Brief description of the workout.
 * @param userIdSet Set of user IDs associated with this workout (defaults to an empty set).
 * @param date Date and Time of the workout.
 * @param path List of paths for the workout.
 * @param timeMs Duration of the workout, in milliseconds.
 */
@JsonClass(generateAdapter = true)
class RunningWorkout(
    workoutId: String,
    name: String,
    description: String,
    warmup: Boolean = false,
    userIdSet: MutableSet<String> = mutableSetOf(),
    exercises: MutableList<Exercise> = mutableListOf(),
    date: LocalDateTime,
    @Json(name = "path") val path: List<LatLng>,
    @Json(name = "time") val timeMs: Long
) : Workout(workoutId, name, description, false, userIdSet, mutableListOf(), date) {
  companion object {
    const val DOCUMENT_NAME = "runningWorkout"
  }
}
