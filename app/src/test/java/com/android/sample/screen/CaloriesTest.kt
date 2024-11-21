package com.android.sample.screen

import com.android.sample.model.userAccount.*
import com.android.sample.model.workout.*
import com.android.sample.ui.composables.Calories
import com.android.sample.ui.workout.ExerciseState
import org.junit.Assert.assertEquals
import org.junit.Test

class CaloriesTest {

  @Test
  fun computeCaloriesForUserWithValidWeight() {
    // UserAccount with valid weight
    val user =
        UserAccount(
            userId = "1",
            firstName = "John",
            lastName = "Doe",
            weight = 75f,
            weightUnit = WeightUnit.KG)

    // List of exercises
    val exercises =
        listOf(
            ExerciseState(
                Exercise("1", ExerciseType.PUSH_UPS, ExerciseDetail.RepetitionBased(60)), true),
            ExerciseState(
                Exercise("2", ExerciseType.SQUATS, ExerciseDetail.RepetitionBased(120)), true),
            ExerciseState(
                Exercise("3", ExerciseType.PLANK, ExerciseDetail.TimeBased(60, sets = 4)), true))

    val expectedCalories = 10 + 25 + 25

    // Compute calories
    val actualCalories = Calories.computeCalories(exercises, user)

    // Assert the expected and actual values
    assertEquals(expectedCalories, actualCalories)
  }

  @Test
  fun computeCaloriesForUserWithDefaultWeight() {
    // UserAccount with no weight provided
    val user = UserAccount(userId = "2", firstName = "Jane", lastName = "Doe")

    // List of exercises
    val exercises =
        listOf(
            ExerciseState(
                Exercise("2", ExerciseType.DOWNWARD_DOG, ExerciseDetail.TimeBased(60, sets = 12)),
                true))

    val expectedCalories = 35

    // Compute calories
    val actualCalories = Calories.computeCalories(exercises, user)

    // Assert the expected and actual values
    assertEquals(expectedCalories, actualCalories)
  }

  @Test
  fun computeCaloriesForNoExercises() {
    // UserAccount
    val user =
        UserAccount(
            userId = "3",
            firstName = "Alice",
            lastName = "Smith",
            weight = 60f,
            weightUnit = WeightUnit.KG)

    // Empty list of exercises
    var exercises = emptyList<ExerciseState>()

    val expectedCalories = 0

    // Compute calories
    var actualCalories = Calories.computeCalories(exercises, user)

    // Assert the expected and actual values
    assertEquals(expectedCalories, actualCalories)

    exercises =
        listOf(
            ExerciseState(
                Exercise("2", ExerciseType.CHAIR, ExerciseDetail.TimeBased(60, sets = 4)), false))

    actualCalories = Calories.computeCalories(exercises, user)

    // Assert the expected and actual values
    assertEquals(expectedCalories, actualCalories)
  }
}
