package com.android.sample.model.workout

// BodyWeight workout description
class BodyWeightWorkout(
    name: String,
    description: String,
    warmup: Boolean,
    val exercises: MutableList<BodyWeightExercise> = mutableListOf() // Default to an empty list
) : Workout(name, description, warmup) {

  // Function to add an exercise
  fun addExercise(exercise: BodyWeightExercise) {
    exercises.add(exercise)
  }
}

enum class BodyWeightExerciseType {
  PUSH_UPS,
  SQUATS,
  PLANK,
  CHAIR
}

data class BodyWeightExercise(val type: BodyWeightExerciseType, val detail: ExerciseDetail)
