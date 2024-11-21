package com.android.sample.model.workout

import com.google.type.LatLng
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime
import kotlin.time.Duration

@JsonClass(generateAdapter = true)
/**
 * Represents a running workout session which includes a path and time.
 *
 * @param workoutId Unique identifier for the workout.
 * @param name Name of the workout.
 * @param description Brief description of the workout.
 * @param userIdSet Set of user IDs associated with this workout (defaults to an empty set).
 * @param date Date and Time of the workout.
 * @param path List of paths for the workout.
 * @param time Duration of the workout, using the Kotlin [Duration] class.
 */
class RunningWorkout(
    workoutId: String,
    name: String,
    description: String,
    userIdSet: MutableSet<String> = mutableSetOf(),
    date: LocalDateTime,
    path: List<List<LatLng>>,
    time: Duration
) : Workout(workoutId, name, description, false, userIdSet, mutableListOf(), date)
