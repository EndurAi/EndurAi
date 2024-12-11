package com.android.sample.ui.mlFeedback

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.mlUtils.ExerciseFeedBack
import com.android.sample.mlUtils.MlCoach
import com.android.sample.model.camera.CameraViewModel
import com.android.sample.model.workout.ExerciseType
import com.android.sample.ui.composables.CameraFeedBack
import com.android.sample.ui.composables.RunningDesignButton
import com.android.sample.ui.composables.SaveButton
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.DarkGrey
import com.android.sample.ui.theme.FontSizes.SubtitleFontSize
import com.android.sample.ui.theme.White

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun CoachCaptureScreen(navigationActions: NavigationActions, cameraViewModel: CameraViewModel) {
  var isExerciseSelected by remember { mutableStateOf(false) }
  var isDropdownExpanded by remember { mutableStateOf(false) }
  var isRecordingInCamera by remember { mutableStateOf(false) }
  var userHasRecorded by remember { mutableStateOf(false) }
  var selectedExercise by remember { mutableStateOf(ExerciseType.PLANK) }
  var jointPositionRequested by remember { mutableStateOf(false) }
    var showInfoDialogue by remember { mutableStateOf(true) }
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
              modifier = Modifier
                  .fillMaxWidth()
                  .padding(pd),
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
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
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
            if (showInfoDialogue){
                CoachInfoDialogue { showInfoDialogue = false }
            }

          Box(modifier = Modifier
              .fillMaxWidth()
              .padding(pd), contentAlignment = Alignment.Center) {
              CameraFeedBack.CameraScreen(
                  cameraViewModel = cameraViewModel,
                  modifier = Modifier
                      .fillMaxSize()
                      .testTag("cameraFeedback"),
                  poseDetectionRequired = jointPositionRequested,
                  exerciseCriterions = ExerciseFeedBack.getCriterions(selectedExercise)

              )
              Column(
                  modifier = Modifier.fillMaxSize().padding(32.dp),
                  verticalArrangement = Arrangement.Bottom,
                  horizontalAlignment = Alignment.CenterHorizontally
              ) {
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceAround
                  ) {
                      val smallButtonModifier = Modifier.size(40.dp).background(DarkGrey,
                          CircleShape).clip(
                          CircleShape)
                          .shadow(elevation = 10.dp, shape = CircleShape, clip = false)
                    // Info button
                      IconButton(
                            onClick = { showInfoDialogue = true },
                            modifier = smallButtonModifier.testTag("infoButton")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "Info",
                                tint = White
                            )
                        }

                      // Record and feedback button
                      RunningDesignButton(
                          onClick = {
                              if (isRecordingInCamera) {
                                  isRecordingInCamera = false
                                  jointPositionRequested = false
                                  userHasRecorded = true
                              } else if (userHasRecorded) {
                                  val feedback = MlCoach(cameraViewModel, selectedExercise).getFeedback()
                                  cameraViewModel.feedback = feedback
                              }
                              else {
                                  cameraViewModel.enablePoseRecognition()
                                  isRecordingInCamera = true
                              }
                          },
                          title = if (isRecordingInCamera) "Stop" else if (userHasRecorded) "Feedback" else "Record",
                          testTag = "recordButton",
                          size = 85.dp
                      )

                      // See joints button
                      IconButton(
                          onClick = {jointPositionRequested = jointPositionRequested.not()},
                          modifier = smallButtonModifier.testTag("jointsButton")
                      ) {
                          Icon(
                              imageVector = Icons.Default.Share,
                                contentDescription = "Joints toggle",
                                tint = White
                          )
                      }
                  }
          }
//              if (userHasRecorded && !isRecordingInCamera) {
//                Button(
//                    onClick = {
//                      val mlCoach = MlCoach(cameraViewModel, selectedExercise)
//                      val feedBackList = mlCoach.getFeedback()
//                      val stringBuilder = StringBuilder()
//                      feedBackList.forEach { stringBuilder.append(it.toString()) }
//                      val feedBack_str = stringBuilder.toString()
//
//                      feedback = feedBack_str
//                      cameraViewModel.finishPoseRecognition()
//                    }) {
//                      Text("Generate feedback")
//                    }
//                Text(feedback)
//              }

          }
        }
      })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachInfoDialogue(onDismissRequest: () -> Unit) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest
    ) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "How to use the ML Coach",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("Press record and start your exercise")
                Text("Your whole body has to be visible in the camera frame.")
                Text("To ensure proper feedback, please be perpendicular to the camera. For instance if you are doing a plank, you should no be facing the camera.")
                Text("Coach will highlight mistakes in real time.")

            }
        }
    }
}
