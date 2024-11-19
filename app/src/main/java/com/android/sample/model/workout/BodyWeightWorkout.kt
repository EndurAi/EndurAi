package com.android.sample.model.workout

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

/**
 * Represents a bodyweight workout session which includes a list of bodyweight exercises.
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
class BodyWeightWorkout(
    workoutId: String,
    name: String,
    description: String,
    warmup: Boolean,
    userIdSet: MutableSet<String> = mutableSetOf(),
    exercises: MutableList<Exercise> = mutableListOf(), // Default to an empty list
    date: LocalDateTime
) : Workout(workoutId, name, description, warmup, userIdSet, exercises, date) {

  companion object {
    const val DOCUMENT_NAME = "bodyweightWorkout"

    val WARMUP_WORKOUT =
        BodyWeightWorkout(
            workoutId = "WARMUP_WORKOUT",
            name = "Warmup Workout",
            description = "A quick warmup workout.",
            warmup = true,
            exercises = mutableListOf(),
            date = LocalDateTime.now())

    val WORKOUT_PUSH_UPS =
        BodyWeightWorkout(
            workoutId = "WORKOUT_PUSH_UPS",
            name = "Basic Push Ups",
            description = "A basic quick push up workout.",
            warmup = false,
            exercises =
                mutableListOf(
                    Exercise(
                        id = "PUSH_UPS_1",
                        type = ExerciseType.PUSH_UPS,
                        detail = ExerciseType.PUSH_UPS.detail)),
            date = LocalDateTime.now())

    val QUICK_BODY_WEIGHT_WORKOUT =
        BodyWeightWorkout(
            workoutId = "QUICK_BODY_WEIGHT_WORKOUT",
            name = "Quick Bodyweight Workout",
            description = "A quick bodyweight workout for beginners.",
            warmup = false,
            exercises =
                mutableListOf(
                    Exercise(
                        id = "BODY_WEIGHT_1",
                        type = ExerciseType.PUSH_UPS,
                        detail = ExerciseType.PUSH_UPS.detail),
                    Exercise(
                        id = "BODY_WEIGHT_2",
                        type = ExerciseType.SQUATS,
                        detail = ExerciseType.SQUATS.detail),
                    Exercise(
                        id = "BODY_WEIGHT_3",
                        type = ExerciseType.PLANK,
                        detail = ExerciseType.PLANK.detail),
                    Exercise(
                        id = "BODY_WEIGHT_4",
                        type = ExerciseType.CHAIR,
                        detail = ExerciseType.CHAIR.detail)),
            date = LocalDateTime.now())
  }
}
