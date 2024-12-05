package com.android.sample.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.model.userAccount.WeightUnit
import com.android.sample.model.workout.ExerciseDetail
import com.android.sample.model.workout.ExerciseType
import com.android.sample.ui.theme.Green
import com.android.sample.ui.theme.LightGrey
import com.android.sample.ui.theme.Red
import com.android.sample.ui.workout.ExerciseState

/**
 * Composable function that displays a summary screen for a workout.
 *
 * This screen shows a summary of the completed workout, including:
 * - A card indicating whether a warm-up was done.
 * - A list of exercise cards, each displaying the exercise type and a checkmark if completed.
 * - A finish button to navigate away from the summary screen.
 *
 * @param hasWarmUp A boolean value indicating whether the workout included a warm-up.
 * @param exerciseList A list of [ExerciseState] objects representing the exercises in the workout.
 * @param onfinishButtonClicked A lambda function to be invoked when the finish button is clicked.
 */
@Composable
fun WorkoutSummaryScreen(
    hasWarmUp: Boolean,
    exerciseList: List<ExerciseState>,
    onfinishButtonClicked: () -> Unit,
    userAccountViewModel: UserAccountViewModel
) {
  val userAccount by userAccountViewModel.userAccount.collectAsState()
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Top,
      modifier = Modifier.testTag("WorkoutSummaryScreen")) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = LightGrey),
            modifier = Modifier.fillMaxWidth(0.5f).padding(vertical = 8.dp).testTag("warmupCard")) {
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(16.dp)) {
                    Text(text = "Warmup", modifier = Modifier.weight(2f), fontSize = 18.sp)
                    if (hasWarmUp) {
                      Icon(
                          Icons.Default.Check,
                          contentDescription = "Checkmark",
                          tint = Green, // Green color for completed state
                          modifier = Modifier.testTag("warmupGreenIcon"))
                    } else {
                      Icon(
                          Icons.Default.Close,
                          contentDescription = "Close",
                          tint = Red, // Red color for skipped state
                          modifier = Modifier.testTag("warmupRedIcon"))
                    }
                  }
            }

        exerciseList.forEach { exerciseState ->
          if (exerciseState.isDone) {
            ExerciseCard(
                exerciseState.exercise,
                onCardClick = {},
                onDetailClick = {},
                modifier = Modifier.testTag("ExerciseCardID${exerciseState.exercise.id}"),
                innerModifier =
                    Modifier.testTag("InnerTextExerciseCardID${exerciseState.exercise.id}"))
          } else {
            var currTextValue by remember { mutableStateOf("Skipped") }
            ExerciseCard(
                exerciseState.exercise,
                innerColor = Red,
                textToDisplay = currTextValue,
                onDetailClick = { currTextValue = if (currTextValue.isBlank()) "Skipped" else "" },
                onCardClick = {},
                modifier = Modifier.testTag("ExerciseCardID${exerciseState.exercise.id}"),
                innerModifier =
                    Modifier.testTag("InnerTextExerciseCardID${exerciseState.exercise.id}"))
          }
        }
      }

  Spacer(Modifier.size(25.dp))

  Row(modifier = Modifier.testTag("Calories")) {
    Image(
        painter = painterResource(id = R.drawable.calories),
        contentDescription = "Calories Image",
        modifier = Modifier.size(25.dp))
    Text(
        text =
            stringResource(
                R.string.CaloriesMessage, Calories.computeCalories(exerciseList, userAccount)))
  }

  Button(
      onClick = { onfinishButtonClicked() },
      modifier = Modifier.width(200.dp).height(50.dp).padding().testTag("FinishButton"),
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA9B0FF)),
      shape = RoundedCornerShape(size = 11.dp)) {
        Text("Finish", color = Color.Black, fontSize = 20.sp)
      }
}

/**
 * Object that calculates the number of calories burned during bodyweight exercises. It provides
 * methods to compute the calories burned based on time-based and repetition-based exercises, taking
 * into account the user's weight. If the user weight is not provided, it uses an average weight.
 */
object Calories {
  private const val AVERAGE_WEIGHT = 70f
  private const val LBS_TO_KG = 0.453592f
  private const val SECONDS_IN_ONE_MINUTE = 60.0
  private const val MINUTES_IN_ONE_HOUR = 60.0
  private const val SEC_PER_PUSH_UP = 2.0
  private const val SEC_PER_SQUAT = 2.0

  /**
   * Computes the total calories burned for a list of exercises, based on the user's weight and the
   * exercise details. The function will sum the calories burned for each exercise in the list,
   * either using time-based or repetition-based methods.
   *
   * @param exerciseList List of exercise states to compute the calories for.
   * @param userAccount The user's account containing weight and weight unit. If null or invalid,
   *   average weight is used.
   * @return The total calories burned, rounded to the nearest integer.
   */
  fun computeCalories(exerciseList: List<ExerciseState>, userAccount: UserAccount?): Int {
    val weightInKg: Float =
        if (userAccount?.weight != null && userAccount.weight > 0) {
          when (userAccount.weightUnit) {
            WeightUnit.KG -> userAccount.weight
            WeightUnit.LBS -> userAccount.weight * LBS_TO_KG
          }
        } else {
          AVERAGE_WEIGHT
        }

    return exerciseList
        .filter { it.isDone }
        .sumOf { exerciseState ->
          val caloriesPerExercise =
              when (val detail = exerciseState.exercise.detail) {
                is ExerciseDetail.TimeBased -> {
                  val hours =
                      detail.durationInSeconds * detail.sets /
                          (SECONDS_IN_ONE_MINUTE * MINUTES_IN_ONE_HOUR)
                  caloriesBurnedForTimeBased(exerciseState.exercise.type, weightInKg, hours)
                }
                is ExerciseDetail.RepetitionBased -> {
                  caloriesBurnedForRepetitionBased(
                      exerciseState.exercise.type, weightInKg, detail.repetitions)
                }
              }
          caloriesPerExercise.toInt()
        }
  }

  /**
   * Calculates the calories burned for time-based exercises. This method multiplies the hours of
   * exercise by the MET value of the exercise type and the user's weight.
   *
   * @param exerciseType The type of exercise performed.
   * @param weight The weight of the user in kilograms.
   * @param hour The duration of the exercise in hours.
   * @return The number of calories burned for the time-based exercise.
   */
  private fun caloriesBurnedForTimeBased(
      exerciseType: ExerciseType,
      weight: Float,
      hour: Double
  ): Double {
    return hour * weight * metValues.getOrDefault(exerciseType, 0.0)
  }

  /**
   * Calculates the calories burned for repetition-based exercises. The time taken for each
   * repetition is factored in to calculate the calories burned per exercise.
   *
   * @param exerciseType The type of exercise performed.
   * @param weight The weight of the user in kilograms.
   * @param repetitions The number of repetitions performed.
   * @return The number of calories burned for the repetition-based exercise.
   */
  private fun caloriesBurnedForRepetitionBased(
      exerciseType: ExerciseType,
      weight: Float,
      repetitions: Int
  ): Double {
    val factor =
        when (exerciseType) {
          ExerciseType.PUSH_UPS ->
              (SEC_PER_PUSH_UP * repetitions) / (SECONDS_IN_ONE_MINUTE * MINUTES_IN_ONE_HOUR) *
                  weight
          ExerciseType.SQUATS ->
              (SEC_PER_SQUAT * repetitions) / (SECONDS_IN_ONE_MINUTE * MINUTES_IN_ONE_HOUR) * weight
          else -> 0.0
        }

    return factor * metValues.getOrDefault(exerciseType, 0.0)
  }

  /**
   * A map of MET (Metabolic Equivalent of Task) values for each type of exercise. These values
   * define the number of calories burned per kilogram of body weight per hour of exercise (kcal /
   * hour * kg).
   */
  private val metValues: Map<ExerciseType, Double> =
      mapOf(
          ExerciseType.PUSH_UPS to 4.0,
          ExerciseType.SQUATS to 5.0,
          ExerciseType.PLANK to 5.0,
          ExerciseType.CHAIR to 4.5,

          // Yoga exercises
          ExerciseType.DOWNWARD_DOG to 2.5,
          ExerciseType.TREE_POSE to 2.0,
          ExerciseType.UPWARD_FACING_DOG to 3.5,
          ExerciseType.WARRIOR_II to 2.8)
}
