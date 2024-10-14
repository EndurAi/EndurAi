package com.android.sample.model.workout

import org.junit.Assert.assertEquals
import org.junit.Test

class BodyWeightWorkoutTest {
  val WORKOUT_ID = "001"
  val EX_ID = "ExoId"

  @Test
  fun testBodyWeightWorkoutCreation() {
    val workout =
        BodyWeightWorkout(
            workoutId = WORKOUT_ID,
            name = "Bodyweight Strength",
            description = "A workout focused on bodyweight exercises.",
            warmup = true)
    assertEquals(WORKOUT_ID, workout.workoutId)
    assertEquals("Bodyweight Strength", workout.name)
    assertEquals("A workout focused on bodyweight exercises.", workout.description)
    assertEquals(true, workout.warmup)
    assertEquals(0, workout.exercises.size) // Initially, exercises should be empty
  }

  @Test
  fun testAddBodyWeightExercise() {
    val workout =
        BodyWeightWorkout(
            workoutId = WORKOUT_ID,
            name = "Bodyweight Strength",
            description = "A workout focused on bodyweight exercises.",
            warmup = true)

    val pushUps =
        BodyWeightExercise(
            EX_ID, BodyWeightExerciseType.PUSH_UPS, ExerciseDetail.RepetitionBased(15))
    workout.addExercise(pushUps)

    assertEquals(1, workout.exercises.size) // Now should have 1 exercise
    assertEquals(pushUps, workout.exercises[0]) // Check if the exercise is correctly added
  }

  @Test
  fun testDeleteBodyWeightExercise() {
    val workout =
        BodyWeightWorkout(
            workoutId = WORKOUT_ID,
            name = "Bodyweight Strength",
            description = "A workout focused on bodyweight exercises.",
            warmup = true)

    val pushUps1 =
        BodyWeightExercise(
            "001", BodyWeightExerciseType.PUSH_UPS, ExerciseDetail.RepetitionBased(15))
    workout.addExercise(pushUps1)

    val pushUps2 =
        BodyWeightExercise(
            "002", BodyWeightExerciseType.PUSH_UPS, ExerciseDetail.RepetitionBased(15))
    workout.addExercise(pushUps2)

    assertEquals(2, workout.exercises.size) // Now should have 2 exercises
    workout.removeExerciseById("001") // Removing pushUps1 by its Id
    assertEquals(1, workout.exercises.size) // Now should have 2 exercises
    assertEquals(pushUps2, workout.exercises[0]) // Check if the exercise is correctly removed
  }
}
