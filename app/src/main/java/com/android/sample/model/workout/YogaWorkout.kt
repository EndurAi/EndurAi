package com.android.sample.model.workout

//Yoga workout description
class YogaWorkout(
    name: String,
    description: String,
    warmup: Boolean,
    val exercises: List<YogaExercise> ////List of exercises to do during the workout
)

enum class YogaExerciseType {
    DOWNWARD_DOG,
    TREE_POSE,
    SUN_SALUTATION,
    WARRIOR_II
}

data class YogaExercise(
    val type: YogaExerciseType,
    val detail: ExerciseDetail
)