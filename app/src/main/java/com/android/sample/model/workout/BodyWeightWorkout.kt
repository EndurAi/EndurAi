package com.android.sample.model.workout

// BodyWeight workout description
class BodyWeightWorkout(
    workoutId: String,
    name: String,
    description: String,
    warmup: Boolean,
    val exercises: MutableList<BodyWeightExercise> = mutableListOf() // Default to an empty list
) : Workout(workoutId, name, description, warmup) {

  // Function to add an exercise
  fun addExercise(exercise: BodyWeightExercise) {
    exercises.add(exercise)
  }

  // Function to remove an exercise of the workout using the exercise Id
  fun removeExerciseById(exerciseId: String) {
    exercises.removeAll { it.exerciseId == exerciseId }
  }
}

enum class BodyWeightExerciseType {
  PUSH_UPS,
  SQUATS,
  PLANK,
  CHAIR
}

data class BodyWeightExercise(
    val exerciseId: String,
    val type: BodyWeightExerciseType,
    val detail: ExerciseDetail
)
