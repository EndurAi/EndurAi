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
    exercises: MutableList<Exercise> = mutableListOf(), // Default to an empty list
    date: LocalDateTime
) : Workout(workoutId, name, description, warmup, userIdSet, exercises, date) {

  companion object {
    const val DOCUMENT_NAME = "yogaWorkout"
    val QUICK_YOGA_WORKOUT =
        YogaWorkout(
            workoutId = "QUICK_YOGA_WORKOUT",
            name = "Quick Yoga Workout",
            description = "A quick yoga workout for beginners.",
            warmup = false,
            exercises =
                mutableListOf(
                    Exercise(
                        id = "YOGA_1",
                        type = ExerciseType.DOWNWARD_DOG,
                        detail = ExerciseType.DOWNWARD_DOG.detail),
                    Exercise(
                        id = "YOGA_2",
                        type = ExerciseType.TREE_POSE,
                        detail = ExerciseType.TREE_POSE.detail),
                    Exercise(
                        id = "YOGA_3",
                        type = ExerciseType.UPWARD_FACING_DOG,
                        detail = ExerciseType.UPWARD_FACING_DOG.detail),
                    Exercise(
                        id = "YOGA_4",
                        type = ExerciseType.WARRIOR_II,
                        detail = ExerciseType.WARRIOR_II.detail)),
            date = LocalDateTime.now())
  }
}
