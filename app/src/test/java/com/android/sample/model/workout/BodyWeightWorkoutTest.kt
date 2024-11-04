package com.android.sample.model.workout

import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class BodyWeightWorkoutTest {
  val WORKOUT_ID = "001"
  val EX_ID = "ExoId"
  val USER_ID_1 = "UID1"
  val USER_ID_2 = "UID2"

  @Test
  fun testBodyWeightWorkoutCreation() {
    val workout =
        BodyWeightWorkout(
            workoutId = WORKOUT_ID,
            name = "Bodyweight Strength",
            description = "A workout focused on bodyweight exercises.",
            warmup = true,
            userIdSet = mutableSetOf(USER_ID_1),
            date = LocalDateTime.of(2024, 11, 1, 0, 42))
    assertEquals(WORKOUT_ID, workout.workoutId)
    assertEquals("Bodyweight Strength", workout.name)
    assertEquals("A workout focused on bodyweight exercises.", workout.description)
    assertEquals(true, workout.warmup)
    assertEquals(0, workout.exercises.size) // Initially, exercises should be empty
    assertEquals(mutableSetOf(USER_ID_1), workout.userIdSet)
  }

  @Test
  fun testAddBodyWeightExercise() {
    val workout =
        BodyWeightWorkout(
            workoutId = WORKOUT_ID,
            name = "Bodyweight Strength",
            description = "A workout focused on bodyweight exercises.",
            date = LocalDateTime.of(2024, 11, 1, 0, 42),
            warmup = true)

    val pushUps =
        Exercise(
            EX_ID, ExerciseType.PUSH_UPS, ExerciseDetail.RepetitionBased(15))
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
            date = LocalDateTime.of(2024, 11, 1, 0, 42),
            warmup = true)

    val pushUps1 =
        Exercise(
            "001", ExerciseType.PUSH_UPS, ExerciseDetail.RepetitionBased(15))
    workout.addExercise(pushUps1)

    val pushUps2 =
        Exercise(
            "002", ExerciseType.PUSH_UPS, ExerciseDetail.RepetitionBased(15))
    workout.addExercise(pushUps2)

    assertEquals(2, workout.exercises.size) // Now should have 2 exercises
    workout.removeExerciseById("001") // Removing pushUps1 by its Id
    assertEquals(1, workout.exercises.size) // Now should have 2 exercises
    assertEquals(pushUps2, workout.exercises[0]) // Check if the exercise is correctly removed
  }

  @Test
  fun testAddUserIdIntoBodyWeightWorkout() {
    val workout =
        BodyWeightWorkout(
            workoutId = WORKOUT_ID,
            name = "Bodyweight Strength",
            description = "A workout focused on bodyweight exercises.",
            date = LocalDateTime.of(2024, 11, 1, 0, 42),
            warmup = true) // UserId Set is initially empty

    workout.addUserById(USER_ID_1)
    assertEquals(1, workout.userIdSet.size)
    workout.addUserById(USER_ID_2)
    assertEquals(2, workout.userIdSet.size)
    assertEquals(mutableSetOf(USER_ID_1, USER_ID_2), workout.userIdSet)
  }

  @Test
  fun testRemoveUserIdFromBodyWeightWorkout() {
    val workout =
        BodyWeightWorkout(
            workoutId = WORKOUT_ID,
            name = "Bodyweight Strength",
            description = "A workout focused on bodyweight exercises.",
            warmup = true,
            date = LocalDateTime.of(2024, 11, 1, 0, 42),
            userIdSet = mutableSetOf(USER_ID_1, USER_ID_2)) // UserId Set is initially empty

    workout.removeUserById(USER_ID_1)
    assertEquals(1, workout.userIdSet.size)
    workout.removeUserById(USER_ID_2)
    assertEquals(0, workout.userIdSet.size)
    assertEquals(mutableSetOf<String>(), workout.userIdSet)
  }
}
