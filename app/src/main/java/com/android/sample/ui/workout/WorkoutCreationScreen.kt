package com.android.sample.ui.workout

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.workout.BodyWeightExercise
import com.android.sample.model.workout.BodyWeightExerciseType
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.ExerciseDetail
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaExercise
import com.android.sample.model.workout.YogaExerciseType
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.composables.ExerciseCard
import com.android.sample.ui.composables.SaveButton
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Blue
import com.android.sample.ui.theme.Grey
import com.android.sample.ui.theme.Purple60

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
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center) {
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
                          Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Next")
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next")
                          }
                        }
                  }
            }
          } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                  item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = Grey), // Gray color for consistency
                        modifier =
                            Modifier.fillMaxWidth(0.9f)
                                .padding(horizontal = 24.dp, vertical = 8.dp)) {
                          Row(
                              verticalAlignment = Alignment.CenterVertically,
                              modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                Text(
                                    text = "Warmup",
                                    fontSize = 18.sp,
                                    color = Color.DarkGray,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Start)
                                Switch(
                                    checked = warmup,
                                    onCheckedChange = { warmup = it },
                                    colors = SwitchDefaults.colors(checkedTrackColor = Blue),
                                    modifier =
                                        Modifier.padding(start = 8.dp).testTag("warmupSwitch"))
                              }
                        }
                  }

                  items(exerciseList) { exercise ->
                    when (exercise) {
                      is YogaExercise -> ExerciseCard(exercise)
                      is BodyWeightExercise -> ExerciseCard(exercise)
                    }
                  }

                  item {
                    // Vertical line connecting the cards
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                          Box(
                              modifier =
                                  Modifier.size(8.dp).background(Purple60, shape = CircleShape))
                          Spacer(modifier = Modifier.height(16.dp).width(2.dp).background(Purple60))
                        }
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = Grey), // Couleur grise pour la carte
                        modifier =
                            Modifier.fillMaxWidth(0.9f)
                                .padding(horizontal = 24.dp, vertical = 8.dp)) {
                          // Box cliquable avec texte "+"
                          Box(
                              contentAlignment = Alignment.Center,
                              modifier =
                                  Modifier.fillMaxWidth()
                                      .height(56.dp)
                                      .clickable { showExerciseDialog = true }
                                      .testTag("addExerciseButton")) {
                                Text(
                                    text = stringResource(id = R.string.addButton),
                                    fontSize = 24.sp,
                                    color = Color.DarkGray)
                              }
                        }
                  }

                  item {
                    SaveButton(
                        onSaveClick = {
                          when (workoutType) {
                            WorkoutType.YOGA -> {
                              workoutViewModel.addWorkout(
                                  YogaWorkout(
                                      workoutId = workoutViewModel.getNewUid(),
                                      name = name,
                                      description = description,
                                      warmup = warmup,
                                      exercises =
                                          exerciseList.toMutableList()
                                              as MutableList<YogaExercise>))
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
                        "saveButton")
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
