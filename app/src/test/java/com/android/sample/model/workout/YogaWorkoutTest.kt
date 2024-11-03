package com.android.sample.model.workout

import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class YogaWorkoutTest {
  val WORKOUT_ID = "001"
  val EX_ID = "ExoId"
  val USER_ID_1 = "UID1"
  val USER_ID_2 = "UID2"

  @Test
  fun testYogaWorkoutCreation() {
    val workout =
        YogaWorkout(
            workoutId = WORKOUT_ID,
            name = "Morning Yoga Flow",
            description = "A calming yoga routine to start your day.",
            warmup = false,
            date = LocalDateTime.of(2024, 11, 1, 0, 42),
            userIdSet = mutableSetOf(USER_ID_1, USER_ID_2))
    assertEquals(WORKOUT_ID, workout.workoutId)
    assertEquals("Morning Yoga Flow", workout.name)
    assertEquals("A calming yoga routine to start your day.", workout.description)
    assertEquals(false, workout.warmup)
    assertEquals(0, workout.exercises.size) // Initially, exercises should be empty
    assertEquals(mutableSetOf(USER_ID_2, USER_ID_1), workout.userIdSet)
  }

  @Test
  fun testAddYogaExercise() {
    val workout =
        YogaWorkout(
            workoutId = WORKOUT_ID,
            name = "Morning Yoga Flow",
            description = "A calming yoga routine to start your day.",
            date = LocalDateTime.of(2024, 11, 1, 0, 42),
            warmup = false)

    val downwardDog =
        YogaExercise(EX_ID, YogaExerciseType.DOWNWARD_DOG, ExerciseDetail.TimeBased(60, 1))
    workout.addExercise(downwardDog)

    assertEquals(1, workout.exercises.size) // Now should have 1 exercise
    assertEquals(downwardDog, workout.exercises[0]) // Check if the exercise is correctly added
  }

  @Test
  fun testRemoveYogaExerciseById() {
    val workout =
        YogaWorkout(
            workoutId = WORKOUT_ID,
            name = "Morning Yoga Flow",
            description = "A calming yoga routine to start your day.",
            date = LocalDateTime.of(2024, 11, 1, 0, 42),
            warmup = false)

    val downwardDog1 =
        YogaExercise("001", YogaExerciseType.DOWNWARD_DOG, ExerciseDetail.TimeBased(60, 1))
    workout.addExercise(downwardDog1)
    val downwardDog2 =
        YogaExercise("002", YogaExerciseType.DOWNWARD_DOG, ExerciseDetail.TimeBased(60, 1))
    workout.addExercise(downwardDog2)

    assertEquals(2, workout.exercises.size) // Now should have 2 exercises

    workout.removeExerciseById("001") // remove downwardDog1 from the workout
    assertEquals(1, workout.exercises.size) // Now should have 1 exercise
    assertEquals(downwardDog2, workout.exercises[0]) // Check if the exercise is correctly removed
  }

  @Test
  fun testAddUserIdIntoYogaWorkout() {
    val workout =
        YogaWorkout(
            workoutId = WORKOUT_ID,
            name = "Morning Yoga Flow",
            description = "A calming yoga routine to start your day.",
            date = LocalDateTime.of(2024, 11, 1, 0, 42),
            warmup = false)
    workout.addUserById(USER_ID_1)
    assertEquals(1, workout.userIdSet.size)
    workout.addUserById(USER_ID_2)
    assertEquals(2, workout.userIdSet.size)
    assertEquals(mutableSetOf(USER_ID_1, USER_ID_2), workout.userIdSet)
  }

  @Test
  fun testRemoveUserIdFromYogaWorkout() {
    val workout =
        YogaWorkout(
            workoutId = WORKOUT_ID,
            name = "Morning Yoga Flow",
            description = "A calming yoga routine to start your day.",
            date = LocalDateTime.of(2024, 11, 1, 0, 42),
            userIdSet = mutableSetOf(USER_ID_1, USER_ID_2),
            warmup = false)

    workout.removeUserById(USER_ID_1)
    assertEquals(1, workout.userIdSet.size)
    workout.removeUserById(USER_ID_2)
    assertEquals(0, workout.userIdSet.size)
    assertEquals(mutableSetOf<String>(), workout.userIdSet)
  }
}
