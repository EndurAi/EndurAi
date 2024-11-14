package com.android.sample.ui.workout

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Black
import com.android.sample.ui.theme.Blue
import com.android.sample.ui.theme.DarkBlue
import com.android.sample.ui.theme.Green
import com.android.sample.ui.theme.LightGrey
import com.android.sample.ui.theme.Red

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun WorkoutOverviewScreen(
    navigationActions: NavigationActions,
    bodyweightViewModel: WorkoutViewModel<BodyWeightWorkout>,
    yogaViewModel: WorkoutViewModel<YogaWorkout>,
    workoutTye: WorkoutType
) {
    val selectedWorkout = when (workoutTye) {
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
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        item {
                            // Section avec le nom de l'entraînement et le bouton d'édition
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Card(
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .height(40.dp),
                                    colors = CardDefaults.cardColors(containerColor = LightGrey), // Couleur de fond gris clair
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = selectedWorkout?.name ?: "BodyWeight Plan",
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).testTag("workoutName"),
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        when (workoutTye) {
                                            WorkoutType.BODY_WEIGHT -> navigationActions.navigateTo(Screen.BODY_WEIGHT_EDIT)
                                            WorkoutType.YOGA -> navigationActions.navigateTo(Screen.YOGA_EDIT)
                                            WorkoutType.WARMUP -> TODO()
                                            WorkoutType.RUNNING -> TODO()
                                        }
                                    },
                                    modifier = Modifier.size(40.dp).testTag("editButton"),
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = Black
                                    )
                                }
                            }
                        }

                        // Section Warmup avec l'icône activé/désactivé
                        item {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = LightGrey),
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .padding(vertical = 8.dp)
                                    .testTag("warmupCard")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Warmup",
                                        modifier = Modifier.weight(1f),
                                        fontSize = 18.sp
                                    )
                                    if (selectedWorkout?.warmup == true) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Checkmark",
                                            tint = Green, // Couleur bleue pour l'état activé
                                            modifier = Modifier.testTag("warmupGreenIcon")
                                        )
                                    } else {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Close",
                                            tint = Red, // Couleur rouge pour l'état désactivé
                                            modifier = Modifier.testTag("warmupRedIcon")
                                        )
                                    }
                                }
                            }
                        }

                        // Liste des exercices
                        exerciseListItems(selectedWorkout?.exercises ?: emptyList(), {}, {})
                    }

                    // Bouton Start
                    Button(
                        onClick = {
                            when (workoutTye) {
                                WorkoutType.BODY_WEIGHT -> navigationActions.navigateTo(Screen.BODY_WEIGHT_WORKOUT)
                                WorkoutType.YOGA -> navigationActions.navigateTo(Screen.YOGA_WORKOUT)
                                WorkoutType.WARMUP -> TODO()
                                WorkoutType.RUNNING -> TODO()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(vertical = 24.dp)
                            .height(50.dp)
                            .testTag("startButton"),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Start",
                                fontSize = 20.sp
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next")
                        }
                    }
                }
            }
        }
    )
}