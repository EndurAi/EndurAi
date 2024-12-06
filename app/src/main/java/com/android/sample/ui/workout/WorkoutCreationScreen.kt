package com.android.sample.ui.workout

import android.widget.Toast
import androidx.annotation.StringRes
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Exercise
import com.android.sample.model.workout.ExerciseDetail
import com.android.sample.model.workout.ExerciseType
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.composables.DateTimePicker
import com.android.sample.ui.composables.ExerciseCard
import com.android.sample.ui.composables.NextButton
import com.android.sample.ui.composables.SaveButton
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Black
import com.android.sample.ui.theme.BlueGradient
import com.android.sample.ui.theme.Dimensions
import com.android.sample.ui.theme.Dimensions.iconSize
import com.android.sample.ui.theme.FontSizes
import com.android.sample.ui.theme.FontSizes.SubtitleFontSize
import com.android.sample.ui.theme.LightBackground
import com.android.sample.ui.theme.LightBlue2
import com.android.sample.ui.theme.LightGrey
import com.android.sample.ui.theme.NeutralGrey
import com.android.sample.ui.theme.Purple60
import com.android.sample.ui.theme.TitleBlue
import com.android.sample.ui.theme.Transparent
import com.android.sample.ui.theme.White
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutCreationScreen(
    navigationActions: NavigationActions,
    workoutType: WorkoutType,
    workoutViewModel: WorkoutViewModel<Workout>,
    isImported: Boolean,
    editing: Boolean = false
) {
  val context = LocalContext.current
  val selectedWorkout =
      if (isImported) workoutViewModel.selectedWorkout.collectAsState().value else null

  var name by remember { mutableStateOf(selectedWorkout?.name ?: "") }
  var description by remember { mutableStateOf(selectedWorkout?.description ?: "") }
  var warmup by remember { mutableStateOf(selectedWorkout?.warmup ?: false) }
  var selectedDateTime by remember { mutableStateOf(selectedWorkout?.date ?: LocalDateTime.now()) }
  var exerciseList by remember {
    mutableStateOf(
        (selectedWorkout as? YogaWorkout)?.exercises
            ?: (selectedWorkout as? BodyWeightWorkout)?.exercises
            ?: mutableListOf())
  }
  var showNameDescriptionScreen by remember {
    mutableStateOf(!editing)
  } // If you are editing, you don't need to show the name and description screen
  var showExerciseDialog by remember { mutableStateOf(false) }
  var selectedExercise by remember { mutableStateOf<Exercise?>(null) }
  var selectedExerciseType by remember { mutableStateOf<ExerciseType?>(null) }
  var exerciseDetail by remember { mutableStateOf<ExerciseDetail?>(null) }
  var isDropdownExpanded by remember { mutableStateOf(false) }
  // Time-based exercise details to be shown in the input fields
  var durationInSeconds_input by remember { mutableStateOf("") }
  var sets_input by remember { mutableStateOf("") }
  var repetitions_input by remember { mutableStateOf("") }

  Scaffold(
      topBar = { TopBar(navigationActions, getWorkoutTypeStringRes(workoutType)) },
      containerColor = LightBackground,
      content = { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
          if (showNameDescriptionScreen) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
              Column(
                  modifier = Modifier.fillMaxSize().padding(paddingValues),
                  horizontalAlignment = Alignment.Start,
                  verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CustomTextField(
                        value = name,
                        onValueChange = { name = it },
                        title = "Exercise Plan Name",
                        placeholder = "Enter a name for your exercise plan",
                        modifier = Modifier.testTag("nameTextField"))

                    CustomTextField(
                        value = description,
                        onValueChange = { description = it },
                        title = "Description",
                        placeholder = "Enter the description of your exercise plan",
                        modifier = Modifier.testTag("descriptionTextField"))

                    DateTimePicker(
                        selectedDateTime = selectedDateTime,
                        onDateTimeSelected = { newDateTime ->
                          selectedDateTime = newDateTime // Mise à jour avec la date sélectionnée
                        },
                        title = "Workout Date")

                    Spacer(modifier = Modifier.height(Dimensions.LargePadding))

                    NextButton(
                        onClick = {
                          if (selectedDateTime != null) { // User needs to select a date
                            showNameDescriptionScreen = false
                          } else {
                            Toast.makeText(context, "Please select a date", Toast.LENGTH_SHORT)
                                .show()
                          }
                        },
                        text = "Next",
                        modifier =
                            Modifier.width(Dimensions.ButtonWidth)
                                .height(Dimensions.ButtonHeight)
                                .align(Alignment.CenterHorizontally)
                                .background(brush = BlueGradient, shape = LeafShape)
                                .testTag("nextButton"))
                  }
            }
          } else {
            LazyColumn(
                modifier =
                    Modifier.fillMaxSize()
                        .padding(paddingValues)
                        .padding(top = Dimensions.ExtraLargePadding),
                horizontalAlignment = Alignment.CenterHorizontally) {
                  item {
                    Card(
                        shape = RoundedCornerShape(50.dp), // More rounded corners
                        colors =
                            CardDefaults.cardColors(containerColor = LightBlue2.copy(alpha = 0.7f)),
                        modifier =
                            Modifier.fillMaxWidth(0.7f) // Reduce the width
                                .padding(vertical = 8.dp)) {
                          Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.SpaceBetween,
                              modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                                Text(
                                    text = "Warmup",
                                    fontSize = SubtitleFontSize,
                                    color = White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Start)
                                Switch(
                                    checked = warmup,
                                    onCheckedChange = { warmup = it },
                                    colors = SwitchDefaults.colors(checkedTrackColor = LightBlue2),
                                    modifier = Modifier.testTag("warmupSwitch"))
                              }
                        }
                  }
                  exerciseListItems(
                      exerciseList,
                      onCardClick = { exercise ->
                        showExerciseDialog = true
                        selectedExercise = exercise
                        selectedExerciseType = exercise.type
                        exerciseDetail = exercise.detail
                      },
                      onDetailClick = {})

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
                                containerColor = LightGrey), // Couleur grise pour la carte
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
                    Spacer(modifier = Modifier.height(Dimensions.ExtraLargePadding))
                    SaveButton(
                        onSaveClick = {
                          when (workoutType) {
                            WorkoutType.YOGA -> {
                              val yogaWorkout =
                                  YogaWorkout(
                                      workoutId = workoutViewModel.getNewUid(),
                                      name = name,
                                      description = description,
                                      warmup = warmup,
                                      date = selectedDateTime!!,
                                      exercises =
                                          exerciseList.toMutableList() as MutableList<Exercise>)
                              if (editing) {
                                workoutViewModel.updateWorkout(yogaWorkout)
                                workoutViewModel.selectWorkout(yogaWorkout)
                              } else {
                                workoutViewModel.addWorkout(yogaWorkout)
                              }
                            }
                            WorkoutType.BODY_WEIGHT -> {
                              val bodyWeightWorkout =
                                  BodyWeightWorkout(
                                      workoutId = workoutViewModel.getNewUid(),
                                      name = name,
                                      description = description,
                                      warmup = warmup,
                                      date = selectedDateTime!!,
                                      exercises =
                                          exerciseList.toMutableList() as MutableList<Exercise>)
                              if (editing) {
                                workoutViewModel.updateWorkout(bodyWeightWorkout)
                                workoutViewModel.selectWorkout(bodyWeightWorkout)
                              } else {
                                workoutViewModel.addWorkout(bodyWeightWorkout)
                              }
                            }
                            else -> {}
                          }
                          Toast.makeText(context, "Workout successfully saved", Toast.LENGTH_SHORT)
                              .show()
                          if (editing) {
                            navigationActions.goBack()
                          } else {
                            navigationActions.navigateTo(Screen.MAIN)
                          }
                        },
                        "saveButton")
                  }
                }
          }
        }
      })

  if (showExerciseDialog) {
    AlertDialog(
        onDismissRequest = {
          showExerciseDialog = false
          selectedExercise = null
          selectedExerciseType = null
          exerciseDetail = null
        },
        title = { Text("Add Exercise", fontWeight = FontWeight.Bold) },
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
                      ExerciseType.entries
                          .filter { it.workoutType == WorkoutType.YOGA }
                          .forEach { type ->
                            Box(
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .clickable {
                                          selectedExerciseType = type
                                          exerciseDetail = type.detail
                                          isDropdownExpanded = false
                                        }
                                        .padding(8.dp)
                                        .testTag("exerciseType${type.name}")) {
                                  Row(
                                      verticalAlignment = Alignment.CenterVertically,
                                      modifier = Modifier.padding(horizontal = 8.dp)) {
                                        Icon(
                                            painter =
                                                painterResource(id = getExerciseIcon(type.name)),
                                            contentDescription = "${type.name} Icon",
                                            modifier = Modifier.size(iconSize))
                                        Spacer(
                                            modifier =
                                                Modifier.width(8.dp)) // Space between icon and text
                                        Text(text = type.toString(), fontSize = SubtitleFontSize)
                                      }
                                }
                          }
                    }
                    WorkoutType.BODY_WEIGHT -> {
                      ExerciseType.entries
                          .filter { it.workoutType == WorkoutType.BODY_WEIGHT }
                          .forEach { type ->
                            Box(
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .clickable {
                                          selectedExerciseType = type
                                          exerciseDetail = type.detail
                                          isDropdownExpanded = false
                                        }
                                        .padding(8.dp)
                                        .testTag("exerciseType${type.name}")) {
                                  Row(
                                      verticalAlignment = Alignment.CenterVertically,
                                      modifier = Modifier.padding(horizontal = 8.dp)) {
                                        Icon(
                                            painter =
                                                painterResource(
                                                    id =
                                                        getExerciseIcon(
                                                            type.name)), // Replace with your actual
                                            // icon resource
                                            contentDescription = "${type.name} Icon",
                                            modifier = Modifier.size(iconSize))
                                        Spacer(
                                            modifier =
                                                Modifier.width(8.dp)) // Space between icon and text
                                        Text(text = type.toString(), fontSize = SubtitleFontSize)
                                      }
                                }
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

              val seletedExerciseTypeDetail = selectedExerciseType!!.detail

              when (seletedExerciseTypeDetail) {
                is ExerciseDetail.TimeBased -> {
                  // Duration
                  OutlinedTextField(
                      value =
                          if ((exerciseDetail as ExerciseDetail.TimeBased)
                              .durationInSeconds
                              .toString() == "0")
                              ""
                          else
                              (exerciseDetail as ExerciseDetail.TimeBased)
                                  .durationInSeconds
                                  .toString(),
                      onValueChange = {
                        val newValue = it.toIntOrNull()
                        exerciseDetail =
                            (exerciseDetail as ExerciseDetail.TimeBased).copy(
                                durationInSeconds = newValue ?: 0)
                      },
                      label = { Text("Duration (seconds)") },
                      modifier = Modifier.testTag("durationTextField"))
                  // nb of sets
                  OutlinedTextField(
                      value =
                          if ((exerciseDetail as ExerciseDetail.TimeBased).sets.toString() == "0")
                              ""
                          else (exerciseDetail as ExerciseDetail.TimeBased).sets.toString(),
                      onValueChange = {
                        val newValue = it.toIntOrNull()
                        exerciseDetail =
                            (exerciseDetail as ExerciseDetail.TimeBased).copy(sets = newValue ?: 0)
                      },
                      label = { Text("Sets") },
                      modifier = Modifier.testTag("setsTextField"))
                }
                is ExerciseDetail.RepetitionBased -> {
                  OutlinedTextField(
                      value =
                          if ((exerciseDetail as ExerciseDetail.RepetitionBased)
                              .repetitions
                              .toString() == "0")
                              ""
                          else
                              (exerciseDetail as ExerciseDetail.RepetitionBased)
                                  .repetitions
                                  .toString(),
                      onValueChange = {
                        val newValue = it.toIntOrNull()
                        exerciseDetail = ExerciseDetail.RepetitionBased(repetitions = newValue ?: 0)
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
                  if (selectedExercise != null) {
                    val index = exerciseList.indexOf(selectedExercise!!)
                    val newEx =
                        Exercise(
                            id = workoutViewModel.getNewUid(),
                            type = selectedExerciseType!!,
                            detail = exerciseDetail!!)
                    exerciseList[index] = newEx
                  } else {
                    exerciseList =
                        (exerciseList +
                                Exercise(
                                    id = workoutViewModel.getNewUid(),
                                    type = selectedExerciseType!!,
                                    detail = exerciseDetail!!))
                            .toMutableList()
                  }
                  showExerciseDialog = false
                  selectedExercise = null
                  selectedExerciseType = null
                  exerciseDetail = null
                }
              },
              modifier = Modifier.testTag("addExerciseConfirmButton")) {
                if (selectedExercise != null) {
                  Text("Update")
                } else {
                  Text("Add")
                }
              }
        },
        dismissButton = {
          if (selectedExercise != null) {
            Button(
                onClick = {
                  exerciseList.remove(selectedExercise!!)
                  showExerciseDialog = false
                  selectedExercise = null
                  selectedExerciseType = null
                  exerciseDetail = null
                },
                modifier = Modifier.testTag("deleteExerciseButton")) {
                  Text("Delete")
                }
          }
          Button(
              onClick = { showExerciseDialog = false },
              modifier = Modifier.testTag("addExerciseCancelButton")) {
                Text("Cancel")
              }
        })
  }
}
/**
 * Adds a list of exercise items to a LazyColumn. The list of exercises is displayed as cards.
 *
 * You need to use this inside a [LazyColumn], it would replace an [items] call.
 *
 * @param exerciseList The list of exercises to display.
 * @param onCardClick Callback function to be invoked when an exercise card is clicked.
 * @param onDetailClick Callback function to be invoked when the detail button of an exercise card
 *   is clicked.
 */
fun LazyListScope.exerciseListItems(
    exerciseList: List<Exercise>,
    onCardClick: (Exercise) -> Unit,
    onDetailClick: (Exercise) -> Unit
) {
  items(exerciseList) { exercise ->
    ExerciseCard(
        exercise,
        onCardClick = { onCardClick(exercise) },
        onDetailClick = { onDetailClick(exercise) })
  }
}

@StringRes
fun getWorkoutTypeStringRes(workoutType: WorkoutType): Int {
  return when (workoutType) {
    WorkoutType.BODY_WEIGHT -> R.string.TitleTabBody
    WorkoutType.YOGA -> R.string.TitleTabYoga
    WorkoutType.RUNNING -> R.string.TitleTabRunning
    WorkoutType.WARMUP -> R.string.TitleTabWarmup
  }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    title: String,
    placeholder: String,
    modifier: Modifier = Modifier
) {
  Column(modifier = modifier.padding(vertical = 8.dp)) {
    // Title outside the text field
    Text(
        text = title,
        style =
            MaterialTheme.typography.bodySmall.copy(
                color = TitleBlue, // Title color
                fontWeight = FontWeight.Bold,
                fontSize = FontSizes.SubtitleFontSize),
        modifier = Modifier.padding(bottom = 4.dp))

    // Text field container
    Card(
        shape = LeafShape,
        colors = CardDefaults.cardColors(containerColor = LightBackground),
        modifier = Modifier.fillMaxWidth().height(Dimensions.ButtonHeight),
        elevation = CardDefaults.cardElevation(4.dp)) {
          Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxSize().padding(horizontal = Dimensions.LargePadding)) {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = {
                      Text(
                          text = placeholder,
                          style = MaterialTheme.typography.bodyMedium.copy(color = NeutralGrey))
                    },
                    colors =
                        TextFieldDefaults.colors(
                            focusedContainerColor = Transparent,
                            unfocusedContainerColor = Transparent,
                            cursorColor = TitleBlue,
                            focusedTextColor = Black,
                            unfocusedTextColor = Black,
                            focusedIndicatorColor = Transparent,
                            unfocusedIndicatorColor = Transparent),
                    modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = NeutralGrey,
                    modifier = Modifier.size(Dimensions.iconSize))
              }
        }
  }
}

fun getExerciseIcon(type: String): Int {
  return when (type) {
    "PUSH_UPS" -> R.drawable.pushups
    "SQUATS" -> R.drawable.squats
    "PLANK" -> R.drawable.plank
    "CHAIR" -> R.drawable.chair
    "DOWNWARD_DOG" -> R.drawable.downwarddog
    "TREE_POSE" -> R.drawable.treepose
    "UPWARD_FACING_DOG" -> R.drawable.upwardfacingdog
    "WARRIOR_II" -> R.drawable.warrior2
    else -> R.drawable.dumbbell // Default icon if the type is not found
  }
}
