package com.android.sample.ui.mlFeedback

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.mlUtils.ExerciseFeedBack
import com.android.sample.mlUtils.MlCoach
import com.android.sample.model.camera.CameraViewModel
import com.android.sample.model.workout.ExerciseType
import com.android.sample.ui.composables.CameraFeedBack
import com.android.sample.ui.composables.RunningDesignButton
import com.android.sample.ui.composables.SaveButton
import com.android.sample.ui.composables.TalkingCoach
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.BlueWorkoutCard
import com.android.sample.ui.theme.ContrailOne
import com.android.sample.ui.theme.DarkGrey
import com.android.sample.ui.theme.Dimensions
import com.android.sample.ui.theme.Dimensions.iconSize
import com.android.sample.ui.theme.FontSizes.MediumTitleFontSize
import com.android.sample.ui.theme.FontSizes.SubtitleFontSize
import com.android.sample.ui.theme.White
import com.android.sample.ui.workout.getExerciseIcon

/**
 * A composable function that displays the coach capture screen.
 *
 * @param navigationActions A class that contains the actions that can be performed on the
 *   navigation.
 * @param cameraViewModel A view model that contains the camera data.
 * @param isTesting A boolean value that is true if the composable is being tested. Used for testing
 *   purposes.
 */
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun CoachCaptureScreen(
    navigationActions: NavigationActions,
    cameraViewModel: CameraViewModel,
    isTesting: Boolean = false
) {
  var isExerciseSelected by remember { mutableStateOf(false) }
  var isDropdownExpanded by remember { mutableStateOf(false) }
  var isRecordingInCamera by remember { mutableStateOf(false) }
  var userHasRecorded by remember { mutableStateOf(false) }
  var selectedExercise by remember { mutableStateOf(ExerciseType.PLANK) }
  var jointPositionRequested by remember { mutableStateOf(false) }
  var showInfoDialogue by remember { mutableStateOf(true) }
  Scaffold(
      modifier = Modifier.testTag("coachCaptureScreen"),
      topBar = {
        TopBar(
            navigationActions = navigationActions,
            title = R.string.coach_feedback_title,
        )
      },
      content = { pd ->
        if (!isExerciseSelected) {
          Column(
              modifier = Modifier.fillMaxSize().padding(pd),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.SpaceEvenly) {
                // Talking Coach

                TalkingCoach(text = "Select an exercise you want to get feedback on")

                // Exercise dropdown menu
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                  Card(
                      modifier =
                          Modifier.clickable(onClick = { isDropdownExpanded = true })
                              .width(Dimensions.ButtonWidth)
                              .height(Dimensions.ButtonHeight)
                              .shadow(8.dp)
                              .testTag("exerciseDropdownCard"),
                      colors = CardDefaults.cardColors(containerColor = BlueWorkoutCard)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                          Text(
                              text = selectedExercise.toString(),
                              fontSize = MediumTitleFontSize,
                              style =
                                  MaterialTheme.typography.bodyMedium.copy(
                                      fontFamily = ContrailOne),
                              modifier = Modifier.padding(8.dp).testTag("selectedExerciseText"))

                          Image(
                              painter = painterResource(id = R.drawable.baseline_arrow_drop_up_24),
                              contentDescription = "Dropdown",
                              modifier = Modifier.size(48.dp).padding(8.dp).testTag("dropdownIcon"))
                        }

                        DropdownMenu(
                            modifier = Modifier.testTag("exerciseDropdownMenu"),
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }) {
                              ExerciseType.entries
                                  .filter { it.hasMlFeedback }
                                  .forEach { exerciseType ->
                                    Box(
                                        modifier =
                                            Modifier.fillMaxWidth()
                                                .clickable {
                                                  selectedExercise = exerciseType
                                                  isDropdownExpanded = false
                                                }
                                                .padding(8.dp)
                                                .testTag("exerciseType${exerciseType.name}")) {
                                          Row(
                                              verticalAlignment = Alignment.CenterVertically,
                                              modifier = Modifier.padding(horizontal = 8.dp)) {
                                                Icon(
                                                    painter =
                                                        painterResource(
                                                            id =
                                                                getExerciseIcon(exerciseType.name)),
                                                    contentDescription =
                                                        "${exerciseType.name} Icon",
                                                    modifier = Modifier.size(iconSize))
                                                Spacer(
                                                    modifier =
                                                        Modifier.width(
                                                            8.dp)) // Space between icon and text
                                                Text(
                                                    text = exerciseType.toString(),
                                                    fontSize = SubtitleFontSize)
                                              }
                                        }
                                  }
                            }
                      }
                }

                SaveButton(
                    onSaveClick = { isExerciseSelected = true },
                    text = "Train",
                    testTag = "saveButton")
              }
        } else {
          if (showInfoDialogue) {
            CoachInfoDialogue { showInfoDialogue = false }
          }

          Box(modifier = Modifier.fillMaxWidth().padding(pd), contentAlignment = Alignment.Center) {
            if (!isTesting) {
              CameraFeedBack.CameraScreen(
                  cameraViewModel = cameraViewModel,
                  modifier = Modifier.fillMaxSize().testTag("cameraFeedback"),
                  poseDetectionRequired = jointPositionRequested,
                  exerciseCriterions = ExerciseFeedBack.getCriterions(selectedExercise))
            }
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally) {
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceAround) {
                        val smallButtonModifier =
                            Modifier.size(40.dp)
                                .background(DarkGrey, CircleShape)
                                .clip(CircleShape)
                                .shadow(elevation = 10.dp, shape = CircleShape, clip = false)
                        // Info button
                        IconButton(
                            onClick = { showInfoDialogue = true },
                            modifier = smallButtonModifier.testTag("infoButton")) {
                              Icon(
                                  imageVector = Icons.Outlined.Info,
                                  contentDescription = "Info",
                                  tint = White)
                            }

                        // Record and feedback button
                        RunningDesignButton(
                            onClick = {
                              if (isRecordingInCamera) {
                                isRecordingInCamera = false
                                jointPositionRequested = false
                                userHasRecorded = true
                              } else if (userHasRecorded) {
                                val feedback =
                                    MlCoach(cameraViewModel, selectedExercise).getFeedback()
                                cameraViewModel.feedback = feedback
                                cameraViewModel.finishPoseRecognition()
                                navigationActions.navigateTo(Screen.COACH_FEEDBACK)
                              } else {
                                cameraViewModel.enablePoseRecognition()
                                isRecordingInCamera = true
                              }
                            },
                            title =
                                if (isRecordingInCamera) "Stop"
                                else if (userHasRecorded) "Feedback" else "Record",
                            testTag = "recordButton",
                            size = 85.dp)

                        // See joints button
                        IconButton(
                            onClick = { jointPositionRequested = jointPositionRequested.not() },
                            modifier = smallButtonModifier.testTag("jointsButton")) {
                              Icon(
                                  painter =
                                      painterResource(
                                          id =
                                              R.drawable
                                                  .data_analytics_interface_symbol_of_connected_circles),
                                  contentDescription = "Switch camera",
                                  tint = White)
                            }
                      }
                }
          }
        }
      })
}

/**
 * A composable function that displays a dialogue with information on how to use the coach.
 *
 * @param onDismissRequest A lambda function that is called when the dialogue is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachInfoDialogue(onDismissRequest: () -> Unit) {
  BasicAlertDialog(
      onDismissRequest = onDismissRequest,
      modifier = Modifier.testTag("infoDialogue"),
  ) {
    Card {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "How to use the Coach",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp))
        Text("Press record and start your exercise.\n")
        Text("Your whole body has to be visible in the camera frame.\n")
        Text(
            "To ensure proper feedback, please be perpendicular to the camera. For instance if you are doing a plank, you should no be facing the camera.\n")
        Text("Coach will highlight mistakes in real time.")
      }
    }
  }
}
