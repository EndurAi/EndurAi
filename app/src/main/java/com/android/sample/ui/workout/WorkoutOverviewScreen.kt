package com.android.sample.ui.workout

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.sample.R
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.Green
import com.android.sample.ui.theme.Red

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun WorkoutOverviewScreen(navigationActions: NavigationActions, bodyweightViewModel: WorkoutViewModel<BodyWeightWorkout>, yogaViewModel: WorkoutViewModel<BodyWeightWorkout>,workoutTye: WorkoutType) {
    val selectedWorkout = when (workoutTye) {
        WorkoutType.BODY_WEIGHT -> bodyweightViewModel.selectedWorkout.value
        WorkoutType.YOGA -> yogaViewModel.selectedWorkout.value
        WorkoutType.WARMUP -> TODO()
        WorkoutType.RUNNING -> TODO()
    }

    Scaffold(
        topBar = {
            when (workoutTye) {
                WorkoutType.BODY_WEIGHT -> TopBar(navigationActions, R.string.BodyWeightWorkoutTitle)
                WorkoutType.YOGA -> TopBar(navigationActions, R.string.YogaWorkoutTitle)
                WorkoutType.WARMUP -> TODO()
                WorkoutType.RUNNING -> TODO()
            }
             },
        content = { pd ->
            LazyColumn {
                item { //Top item with workout name and edit button
                    Row(modifier = Modifier.padding(pd)) {
                        Card (modifier = Modifier.padding(pd)) {
                            Text(text = selectedWorkout?.name ?: "BodyWeight Plan")
                        }
                        IconButton(
                            onClick = {
                                //TODO: Navigate to the workout creation screen with correct parameters
                            },
                            modifier = Modifier.padding(pd),
                            content = {Icon(Icons.Default.Edit, contentDescription = "Edit")}
                        )
                    }
                }
                item {
                    Card(modifier = Modifier.testTag("warmupYesNo")) {
                        //Row with the a "Warmup" text and an icon, either a green checkmark or a red X
                        Row(modifier = Modifier.padding(pd)) {
                            Text(text = "Warmup")
                            if (selectedWorkout?.warmup == true) {
                                Icon(Icons.Default.Check, contentDescription = "Checkmark", tint = Green )
                            } else {
                                Icon(Icons.Default.Close, contentDescription = "X", tint = Red )
                            }
                            }
                    }
                }
                exerciseListItems(selectedWorkout?.exercises ?: emptyList(),{},{})
            }


            //Exercises list
        }
    )
}