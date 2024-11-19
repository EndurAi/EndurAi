package com.android.sample.ui.composables

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.model.userAccount.WeightUnit
import com.android.sample.model.workout.ExerciseDetail
import com.android.sample.model.workout.ExerciseType
import com.android.sample.model.workout.WorkoutType
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
    Text(text = stringResource(R.string.CaloriesMessage, ComputeCalories(exerciseList, userAccount)))

  Button(
      onClick = { onfinishButtonClicked() },
      modifier = Modifier.width(200.dp).height(50.dp).padding().testTag("FinishButton"),
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA9B0FF)),
      shape = RoundedCornerShape(size = 11.dp)) {
        Text("Finish", color = Color.Black, fontSize = 20.sp)
      }
}

fun ComputeCalories(exerciseList: List<ExerciseState>, userAccount: UserAccount?): Int {

    val weightInKg = if (userAccount == null) {
        70f // Default mean weight
    } else {
        when (userAccount.weightUnit) {
            WeightUnit.KG -> userAccount.weight
            WeightUnit.LBS -> userAccount.weight * 0.453592
        }
    }

    return exerciseList.filter { it.isDone }.sumOf { exerciseState ->
        val caloriesPerExercise = when (exerciseState.exercise.detail) {
            is ExerciseDetail.TimeBased -> {
                val minutes = exerciseState.exercise.detail.durationInSeconds / 60.0
                // Calories brûlées par minute pour les exercices basés sur le temps
                minutes * caloriesBurnedPerMinute(exerciseState.exercise.type, weightInKg)
            }
            is ExerciseDetail.RepetitionBased -> {
                // Calories par répétition pour les exercices basés sur les répétitions
                exerciseState.exercise.detail.repetitions * caloriesBurnedPerRepetition(exerciseState.exercise.type, weightInKg)
            }
        }
        caloriesPerExercise.toInt()
    }
}

// Calcule les calories par minute en fonction du type d'exercice
fun caloriesBurnedPerMinute(exerciseType: ExerciseType, weight: Float): Double {
    return when (exerciseType.workoutType) {
        WorkoutType.YOGA -> 3.0 * weight / 60.0 // Exemple: 3 MET pour le yoga
        WorkoutType.BODY_WEIGHT -> 6.0 * weight / 60.0 // Exemple: 6 MET pour exercices au poids du corps
        WorkoutType.WARMUP -> 4.0 * weight / 60.0 // Exemple: 4 MET pour échauffement
        WorkoutType.RUNNING -> TODO()
    }
}

// Calcule les calories par répétition en fonction du type d'exercice
fun caloriesBurnedPerRepetition(exerciseType: ExerciseType, weight: Float): Double {
    return when (exerciseType.workoutType) {
        WorkoutType.YOGA -> 0.1 * weight // Exemple: moins intense
        WorkoutType.BODY_WEIGHT -> 0.5 * weight // Exemple: plus intense
        WorkoutType.WARMUP -> 0.2 * weight // Exemple: modéré
        WorkoutType.RUNNING -> TODO()
    }
}

