package com.android.sample.ui.mlFeedback

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.mlUtils.MlCoach
import com.android.sample.model.camera.CameraViewModel
import com.android.sample.model.workout.ExerciseType
import com.android.sample.ui.composables.CameraFeedBack
import com.android.sample.ui.composables.SaveButton
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.FontSizes.SubtitleFontSize

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun CoachFeedbackScreen(navigationActions: NavigationActions, cameraViewModel: CameraViewModel) {
  var isExerciseSelected by remember { mutableStateOf(false) }
  var isDropdownExpanded by remember { mutableStateOf(false) }
  var isRecordingInCamera by remember { mutableStateOf(false) }
  var userHasRecorded by remember { mutableStateOf(false) }
  var selectedExercise by remember { mutableStateOf(ExerciseType.PLANK) }
  var feedback by remember { mutableStateOf("") }
  val context = LocalContext.current
  Scaffold(
      topBar = {
        TopBar(
            navigationActions = navigationActions,
            title = R.string.coach_feedback_title,
        )
      },
      content = { pd ->
        if (!isExerciseSelected) {
          Column(
              modifier = Modifier.fillMaxWidth().padding(pd),
              horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Button(
                    onClick = { isDropdownExpanded = true },
                    modifier = Modifier.testTag("selectExerciseButton")) {
                      Text("Select Exercise")
                    }
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    modifier = Modifier.testTag("exerciseDropdownMenu")) {
                      ExerciseType.entries
                          .filter { it.hasMlFeedback }
                          .forEach { exerciseType ->
                            Box(
                                modifier =
                                    Modifier.fillMaxWidth().clickable {
                                      selectedExercise = exerciseType
                                      isDropdownExpanded = false
                                    }) {
                                  Text(text = exerciseType.toString(), fontSize = SubtitleFontSize)
                                }
                          }
                    }
                SaveButton(onSaveClick = { isExerciseSelected = true }, testTag = "saveButton")
              }
        } else {
          Box(modifier = Modifier.fillMaxWidth().padding(pd), contentAlignment = Alignment.Center) {
            Column {
              CameraFeedBack.CameraScreen(
                  cameraViewModel = cameraViewModel,
                  modifier = Modifier.size(220.dp, 350.dp).testTag("cameraFeedback"),
                poseDetectionRequired = true
              )
              Button(
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor =
                              if (isRecordingInCamera) Color.Red
                              else if (userHasRecorded) Color.Green else Color.Cyan),
                  onClick = {
                    if (userHasRecorded) {
                      isRecordingInCamera = false
                    } else {
                      userHasRecorded = true
                      cameraViewModel.enablePoseRecognition()
                      isRecordingInCamera = true
                    }
                  }) {
                    Text(
                        if (isRecordingInCamera) "Recording..."
                        else if (userHasRecorded) "Record again" else "Tap to record")
                  }
              if (userHasRecorded && !isRecordingInCamera) {
                Button(
                    onClick = {
                      val mlCoach = MlCoach(cameraViewModel, selectedExercise)
                      val feedBackList = mlCoach.getFeedback()
                      val stringBuilder = StringBuilder()
                      feedBackList.forEach { stringBuilder.append(it.toString()) }
                      val feedBack_str = stringBuilder.toString()

                      feedback = feedBack_str
                      cameraViewModel.finishPoseRecognition()
                    }) {
                      Text("Generate feedback")
                    }
                Text(feedback)
              }
            }
          }
        }
      })
}
