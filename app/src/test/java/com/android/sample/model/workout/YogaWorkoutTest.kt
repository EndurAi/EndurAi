package com.android.sample.model.workout

import org.junit.Assert.assertEquals
import org.junit.Test

class YogaWorkoutTest {

  @Test
  fun testYogaWorkoutCreation() {
    val workout =
        YogaWorkout(
            name = "Morning Yoga Flow",
            description = "A calming yoga routine to start your day.",
            warmup = false)

    assertEquals("Morning Yoga Flow", workout.name)
    assertEquals("A calming yoga routine to start your day.", workout.description)
    assertEquals(false, workout.warmup)
    assertEquals(0, workout.exercises.size) // Initially, exercises should be empty
  }

  @Test
  fun testAddYogaExercise() {
    val workout =
        YogaWorkout(
            name = "Morning Yoga Flow",
            description = "A calming yoga routine to start your day.",
            warmup = false)

    val downwardDog = YogaExercise(YogaExerciseType.DOWNWARD_DOG, ExerciseDetail.TimeBased(60, 1))
    workout.addExercise(downwardDog)

    assertEquals(1, workout.exercises.size) // Now should have 1 exercise
    assertEquals(downwardDog, workout.exercises[0]) // Check if the exercise is correctly added
  }
}
