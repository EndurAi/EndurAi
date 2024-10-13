package com.android.sample.model.workout

// Yoga workout description
class YogaWorkout(
    workoutId : String,
    name: String,
    description: String,
    warmup: Boolean,
    val exercises: MutableList<YogaExercise> = mutableListOf() // Default to an empty list
) : Workout(workoutId,name, description, warmup) {

  // Function to add an exercise
  fun addExercise(exercise: YogaExercise) {
    exercises.add(exercise) // Correctly adds the exercise to the list
  }

// Function to remove an exercise of the workout using the exercise Id
fun removeExerciseById(exerciseId: String) {
    exercises.removeAll{ it.exerciseId == exerciseId }
}

}

enum class YogaExerciseType {
  DOWNWARD_DOG,
  TREE_POSE,
  SUN_SALUTATION,
  WARRIOR_II
}

data class YogaExercise(val exerciseId: String,val type: YogaExerciseType, val detail: ExerciseDetail)
