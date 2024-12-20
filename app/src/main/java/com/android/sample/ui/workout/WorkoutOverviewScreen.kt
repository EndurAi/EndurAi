package com.android.sample.ui.workout

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.composables.NextButton
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.BlueGradient
import com.android.sample.ui.theme.DarkGreen
import com.android.sample.ui.theme.Dimensions
import com.android.sample.ui.theme.FontSizes.SubtitleFontSize
import com.android.sample.ui.theme.LightBackground
import com.android.sample.ui.theme.LightBlue2
import com.android.sample.ui.theme.Line
import com.android.sample.ui.theme.NeutralGrey
import com.android.sample.ui.theme.OpenSans
import com.android.sample.ui.theme.Orange
import com.android.sample.ui.theme.TitleBlue
import com.android.sample.ui.theme.White

/**
 * Displays the workout overview screen with the selected workout details. This screen displays the
 * workout name, warmup status, exercises, and a start button to begin the workout. The user can
 * also edit the workout by clicking the edit button.
 *
 * @param navigationActions the navigation actions to be performed.
 * @param bodyweightViewModel the view model for the bodyweight workout.
 * @param yogaViewModel the view model for the yoga workout.
 * @param workoutTye the type of workout to display.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun WorkoutOverviewScreen(
    navigationActions: NavigationActions,
    bodyweightViewModel: WorkoutViewModel<BodyWeightWorkout>,
    yogaViewModel: WorkoutViewModel<YogaWorkout>,
    workoutTye: WorkoutType
) {
  val selectedWorkout =
      when (workoutTye) {
        WorkoutType.BODY_WEIGHT -> bodyweightViewModel.selectedWorkout.value
        WorkoutType.YOGA -> yogaViewModel.selectedWorkout.value
        WorkoutType.WARMUP -> TODO()
        WorkoutType.RUNNING -> TODO()
      }

  Scaffold(
      modifier = Modifier.testTag("WorkoutOverviewScreen"),
      topBar = {
        when (workoutTye) {
          WorkoutType.BODY_WEIGHT -> TopBar(navigationActions, R.string.BodyWeightWorkoutTitle)
          WorkoutType.YOGA -> TopBar(navigationActions, R.string.YogaWorkoutTitle)
          WorkoutType.WARMUP -> TODO()
          WorkoutType.RUNNING -> TODO()
        }
      },
      content = { pd ->
        Box(modifier = Modifier.fillMaxSize().padding(pd)) {
          Column(
              modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Top) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top) {
                      item {
                        // Section avec le nom de l'entraînement et le bouton d'édition
                        Row(
                            modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center) {
                              Text(
                                  text = selectedWorkout?.name ?: "BodyWeight Plan",
                                  modifier = Modifier.padding(end = 8.dp).testTag("workoutName"),
                                  fontSize = 20.sp,
                                  color = TitleBlue,
                                  fontFamily = OpenSans,
                                  fontWeight = FontWeight.Bold // Makes the text bold
                                  )
                              IconButton(
                                  onClick = {
                                    when (workoutTye) {
                                      WorkoutType.BODY_WEIGHT ->
                                          navigationActions.navigateTo(Screen.BODY_WEIGHT_EDIT)
                                      WorkoutType.YOGA ->
                                          navigationActions.navigateTo(Screen.YOGA_EDIT)
                                      WorkoutType.WARMUP -> TODO()
                                      WorkoutType.RUNNING -> TODO()
                                    }
                                  },
                                  modifier = Modifier.size(40.dp).testTag("editButton"),
                              ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = NeutralGrey)
                              }
                            }
                      }

                      item {
                        Divider(
                            color = Line,
                            thickness = 0.5.dp,
                            modifier =
                                Modifier.padding(horizontal = 25.dp, vertical = 1.dp)
                                    .padding(bottom = 10.dp)
                                    .shadow(1.dp))
                      }
                      // Section Warmup avec l'icône activé/désactivé
                      item {
                        Card(
                            shape = RoundedCornerShape(50.dp),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = LightBlue2.copy(alpha = 0.7f)),
                            modifier =
                                Modifier.fillMaxWidth(0.7f)
                                    .padding(vertical = 8.dp)
                                    .height(40.dp)
                                    .testTag("warmupCard")) {
                              Row(
                                  verticalAlignment = Alignment.CenterVertically,
                                  horizontalArrangement = Arrangement.SpaceBetween,
                                  modifier =
                                      Modifier.padding(horizontal = 16.dp)
                                          .fillMaxWidth()
                                          .fillMaxHeight()) {
                                    Text(
                                        text = "Warmup",
                                        fontSize = SubtitleFontSize,
                                        color = White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Start)
                                    if (selectedWorkout?.warmup == true) {
                                      Icon(
                                          Icons.Default.CheckCircle,
                                          contentDescription = "Checkmark",
                                          tint = DarkGreen, // Etat activé
                                          modifier = Modifier.testTag("warmupGreenIcon"))
                                    } else {
                                      Icon(
                                          Icons.Default.Close,
                                          contentDescription = "Close",
                                          tint = Orange, // Etat désactivé
                                          modifier = Modifier.testTag("warmupRedIcon"))
                                    }
                                  }
                            }
                      }

                      // Liste des exercices
                      exerciseListItems(selectedWorkout?.exercises ?: emptyList(), {}, {})
                    }

                // Bouton Start
                NextButton(
                    text = "Start",
                    onClick = {
                      when (workoutTye) {
                        WorkoutType.BODY_WEIGHT ->
                            navigationActions.navigateTo(Screen.BODY_WEIGHT_WORKOUT)
                        WorkoutType.YOGA -> navigationActions.navigateTo(Screen.YOGA_WORKOUT)
                        WorkoutType.WARMUP -> TODO()
                        WorkoutType.RUNNING -> TODO()
                      }
                    },
                    modifier =
                        Modifier.width(Dimensions.ButtonWidth)
                            .height(Dimensions.ButtonHeight)
                            .align(Alignment.CenterHorizontally)
                            .background(brush = BlueGradient, shape = LeafShape)
                            .testTag("startButton"))
              }
        }
      },
      containerColor = LightBackground)
}
