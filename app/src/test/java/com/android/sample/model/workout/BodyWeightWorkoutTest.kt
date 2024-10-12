package com.android.sample.model.workout

import org.junit.Assert.assertEquals
import org.junit.Test

class BodyWeightWorkoutTest {

  @Test
  fun testBodyWeightWorkoutCreation() {
    val workout =
        BodyWeightWorkout(
            name = "Bodyweight Strength",
            description = "A workout focused on bodyweight exercises.",
            warmup = true)

    assertEquals("Bodyweight Strength", workout.name)
    assertEquals("A workout focused on bodyweight exercises.", workout.description)
    assertEquals(true, workout.warmup)
    assertEquals(0, workout.exercises.size) // Initially, exercises should be empty
  }

  @Test
  fun testAddExercise() {
    val workout =
        BodyWeightWorkout(
            name = "Bodyweight Strength",
            description = "A workout focused on bodyweight exercises.",
            warmup = true)

    val pushUps =
        BodyWeightExercise(BodyWeightExerciseType.PUSH_UPS, ExerciseDetail.RepetitionBased(15))
    workout.addExercise(pushUps)

    assertEquals(1, workout.exercises.size) // Now should have 1 exercise
    assertEquals(pushUps, workout.exercises[0]) // Check if the exercise is correctly added
  }
}
