package com.android.sample.model.workout

// Yoga workout description
class YogaWorkout(
    name: String,
    description: String,
    warmup: Boolean,
    val exercises: MutableList<YogaExercise> = mutableListOf() // Default to an empty list
) : Workout(name, description, warmup) {

  // Function to add an exercise
  fun addExercise(exercise: YogaExercise) {
    exercises.add(exercise) // Correctly adds the exercise to the list
  }
}

enum class YogaExerciseType {
  DOWNWARD_DOG,
  TREE_POSE,
  SUN_SALUTATION,
  WARRIOR_II
}

data class YogaExercise(val type: YogaExerciseType, val detail: ExerciseDetail)
