package com.android.sample.model.workout

//BodyWeight workout description
class BodyWeightWorkout(
    name: String,
    description: String,
    warmup: Boolean,
    val exercises: List<BodyWeightExercise> //List of exercises to do during the workout
) : Workout(name, description, warmup)

enum class BodyWeightExerciseType {
    PUSH_UPS,
    SQUATS,
    PLANK,
    CHAIR
}

data class BodyWeightExercise(
    val type: BodyWeightExerciseType,
    val detail: ExerciseDetail
)