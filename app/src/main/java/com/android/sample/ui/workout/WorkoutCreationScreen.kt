package com.android.sample.ui.workout

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.android.sample.model.workout.BodyWeightExercise
import com.android.sample.model.workout.BodyWeightExerciseType
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.ExerciseDetail
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutRepositoryFirestore
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaExercise
import com.android.sample.model.workout.YogaExerciseType
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutCreationScreen(
    navigationActions: NavigationActions,
    workoutType: WorkoutType,
    workoutViewModel: WorkoutViewModel<Workout>,
    isImported: Boolean
) {
  val context = LocalContext.current
  val selectedWorkout =
      if (isImported) workoutViewModel.selectedWorkout.collectAsState().value else null

  var name by remember { mutableStateOf(selectedWorkout?.name ?: "") }
  var description by remember { mutableStateOf(selectedWorkout?.description ?: "") }
  var warmup by remember { mutableStateOf(selectedWorkout?.warmup ?: false) }
  var exerciseList by remember {
    mutableStateOf(
        (selectedWorkout as? YogaWorkout)?.exercises
            ?: (selectedWorkout as? BodyWeightWorkout)?.exercises
            ?: emptyList())
  }
  var showNameDescriptionScreen by remember { mutableStateOf(true) }
  var showExerciseDialog by remember { mutableStateOf(false) }
  var selectedExerciseType by remember { mutableStateOf<Any?>(null) }
  var exerciseDetail by remember { mutableStateOf<ExerciseDetail?>(null) }
  var isDropdownExpanded by remember { mutableStateOf(false) }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text(workoutType.toString(), modifier = Modifier.testTag("workoutTopBar")) },
            navigationIcon = {
              IconButton(onClick = { navigationActions.goBack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
              }
            })
      },
      content = { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
          if (showNameDescriptionScreen) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
              Column(
                  modifier = Modifier.fillMaxSize().padding(paddingValues),
                  horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                  verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Exercise Plan Name") },
                        placeholder = { Text("Enter name of your exercise plan") },
                        modifier = Modifier.testTag("nameTextField"))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("Describe your exercise plan") },
                        modifier = Modifier.testTag("descriptionTextField"))
                    Button(
                        onClick = { showNameDescriptionScreen = false },
                        modifier = Modifier.testTag("nextButton")) {
                          Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Text("Next")
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next")
                          }
                        }
                  }
            }
          } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center) {
                  Row(
                      verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                      modifier = Modifier.padding(16.dp)) {
                        Text("Warmup")
                        Switch(
                            checked = warmup,
                            onCheckedChange = { warmup = it },
                            modifier = Modifier.padding(start = 8.dp).testTag("warmupSwitch"))
                      }
                  exerciseList.forEach { exercise ->
                    when (exercise) {
                      is YogaExercise ->
                          Text(
                              "${exercise.type}: ${exercise.detail}",
                              modifier = Modifier.testTag("exerciseItem"))
                      is BodyWeightExercise ->
                          Text(
                              "${exercise.type}: ${exercise.detail}",
                              modifier = Modifier.testTag("exerciseItem"))
                    }
                  }
                  Button(
                      onClick = { showExerciseDialog = true },
                      modifier = Modifier.padding(16.dp).testTag("addExerciseButton")) {
                        Text("+ Add Exercise")
                      }
                  Button(
                      onClick = {
                        when (workoutType) {
                          WorkoutType.YOGA -> {
                            workoutViewModel.addWorkout(
                                YogaWorkout(
                                    workoutId = workoutViewModel.getNewUid(),
                                    name = name,
                                    description = description,
                                    warmup = warmup,
                                    exercises =
                                        exerciseList.toMutableList() as MutableList<YogaExercise>))
                          }
                          WorkoutType.BODY_WEIGHT -> {
                            workoutViewModel.addWorkout(
                                BodyWeightWorkout(
                                    workoutId = workoutViewModel.getNewUid(),
                                    name = name,
                                    description = description,
                                    warmup = warmup,
                                    exercises =
                                        exerciseList.toMutableList()
                                            as MutableList<BodyWeightExercise>))
                          }
                          else -> {}
                        }
                        Toast.makeText(context, "Workout successfully added", Toast.LENGTH_SHORT)
                            .show()
                        navigationActions.navigateTo(Screen.MAIN)
                      },
                      modifier = Modifier.padding(16.dp).testTag("saveButton")) {
                        Text("Save")
                      }
                }
          }
        }
      })

  if (showExerciseDialog) {
    AlertDialog(
        onDismissRequest = { showExerciseDialog = false },
        title = { Text("Add Exercise") },
        text = {
          Column {
            Button(
                onClick = { isDropdownExpanded = true },
                modifier = Modifier.testTag("selectExerciseTypeButton")) {
                  Text("Select Exercise Type")
                }
            DropdownMenu(
                expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }) {
                  when (workoutType) {
                    WorkoutType.YOGA -> {
                      YogaExerciseType.entries.forEach { type ->
                        DropdownMenuItem(
                            onClick = {
                              selectedExerciseType = type
                              isDropdownExpanded = false
                            },
                            modifier = Modifier.testTag("exerciseType${type.name}"),
                            text = { Text(type.name) })
                      }
                    }
                    WorkoutType.BODY_WEIGHT -> {
                      BodyWeightExerciseType.entries.forEach { type ->
                        DropdownMenuItem(
                            onClick = {
                              selectedExerciseType = type
                              isDropdownExpanded = false
                            },
                            modifier = Modifier.testTag("exerciseType${type.name}"),
                            text = { Text(type.name) })
                      }
                    }
                    else -> {}
                  }
                }
            if (selectedExerciseType != null) {
              Text(
                  "Selected Exercise: ${selectedExerciseType.toString()}",
                  modifier = Modifier.testTag("selectedExerciseType"))
              Text("Goal")
              Row {
                Button(
                    onClick = { exerciseDetail = ExerciseDetail.TimeBased(0, 0) },
                    modifier = Modifier.testTag("timeBasedButton")) {
                      Text("TimeBased")
                    }
                Button(
                    onClick = { exerciseDetail = ExerciseDetail.RepetitionBased(0) },
                    modifier = Modifier.testTag("repetitionBasedButton")) {
                      Text("RepetitionBased")
                    }
              }
              when (exerciseDetail) {
                is ExerciseDetail.TimeBased -> {
                  OutlinedTextField(
                      value =
                          (exerciseDetail as ExerciseDetail.TimeBased).durationInSeconds.toString(),
                      onValueChange = {
                        val newValue = it.toIntOrNull()
                        if (newValue != null) {
                          exerciseDetail =
                              (exerciseDetail as ExerciseDetail.TimeBased).copy(
                                  durationInSeconds = newValue)
                        }
                      },
                      label = { Text("Duration (seconds)") },
                      modifier = Modifier.testTag("durationTextField"))
                  OutlinedTextField(
                      value = (exerciseDetail as ExerciseDetail.TimeBased).sets.toString(),
                      onValueChange = {
                        val newValue = it.toIntOrNull()
                        if (newValue != null) {
                          exerciseDetail =
                              (exerciseDetail as ExerciseDetail.TimeBased).copy(sets = newValue)
                        }
                      },
                      label = { Text("Sets") },
                      modifier = Modifier.testTag("setsTextField"))
                }
                is ExerciseDetail.RepetitionBased -> {
                  OutlinedTextField(
                      value =
                          (exerciseDetail as ExerciseDetail.RepetitionBased).repetitions.toString(),
                      onValueChange = {
                        val newValue = it.toIntOrNull()
                        if (newValue != null) {
                          exerciseDetail =
                              (exerciseDetail as ExerciseDetail.RepetitionBased).copy(
                                  repetitions = newValue)
                        }
                      },
                      label = { Text("Repetitions") },
                      modifier = Modifier.testTag("repetitionsTextField"))
                }
                else -> {}
              }
            }
          }
        },
        confirmButton = {
          Button(
              onClick = {
                if (selectedExerciseType != null && exerciseDetail != null) {
                  exerciseList =
                      exerciseList +
                          when (workoutType) {
                            WorkoutType.YOGA ->
                                YogaExercise(
                                    exerciseId = workoutViewModel.getNewUid(),
                                    type = selectedExerciseType as YogaExerciseType,
                                    detail = exerciseDetail!!)
                            WorkoutType.BODY_WEIGHT ->
                                BodyWeightExercise(
                                    exerciseId = workoutViewModel.getNewUid(),
                                    type = selectedExerciseType as BodyWeightExerciseType,
                                    detail = exerciseDetail!!)
                            else -> throw IllegalArgumentException("Unsupported workout type")
                          }
                  showExerciseDialog = false
                }
              },
              modifier = Modifier.testTag("addExerciseConfirmButton")) {
                Text("Add")
              }
        },
        dismissButton = {
          Button(
              onClick = { showExerciseDialog = false },
              modifier = Modifier.testTag("addExerciseCancelButton")) {
                Text("Cancel")
              }
        })
  }
}