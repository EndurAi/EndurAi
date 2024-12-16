package com.android.sample.ui.workout

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.android.sample.R
import com.android.sample.model.achievements.StatisticsViewModel
import com.android.sample.model.camera.CameraViewModel
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.model.video.VideoViewModel
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Exercise
import com.android.sample.model.workout.ExerciseDetail
import com.android.sample.model.workout.WarmUpViewModel
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.composables.ArrowBack
import com.android.sample.ui.composables.CameraFeedBack
import com.android.sample.ui.composables.CountDownTimer
import com.android.sample.ui.composables.DualVideoPlayer
import com.android.sample.ui.composables.NextButton
import com.android.sample.ui.composables.SkipButton
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.composables.WorkoutSummaryScreen
import com.android.sample.ui.composables.convertSecondsToTime
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Black
import com.android.sample.ui.theme.BlueGradient
import com.android.sample.ui.theme.Dimensions
import com.android.sample.ui.theme.FontSizes.SubtitleFontSize
import com.android.sample.ui.theme.FontSizes.TitleFontSize
import com.android.sample.ui.theme.Green2
import com.android.sample.ui.theme.LightBackground
import com.android.sample.ui.theme.LightBlue2
import com.android.sample.ui.theme.Line
import com.android.sample.ui.theme.OpenSans
import com.android.sample.ui.theme.Red2
import com.android.sample.ui.theme.TitleBlue
import com.android.sample.ui.theme.White
import kotlinx.coroutines.delay

// Data class to hold the state of an exercise
data class ExerciseState(val exercise: Exercise, var isDone: Boolean)

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreenBody(
    exerciseStateList: List<ExerciseState>?,
    workoutName: String,
    navigationActions: NavigationActions,
    cameraViewModel: CameraViewModel,
    videoViewModel: VideoViewModel,
    hasWarmUp: Boolean,
    userAccountViewModel: UserAccountViewModel,
    workoutViewModel: WorkoutViewModel<Workout>,
    workoutID: String,
    statisticsViewModel: StatisticsViewModel,
    workout: Workout
) {
  // State variables for managing the UI and workout flow
  var exerciseIndex by remember { mutableIntStateOf(0) }
  val context = LocalContext.current
  val exerciseState =
      exerciseStateList?.get(exerciseIndex)
          ?: run {
            Toast.makeText(context, "Error while fetching the list of exercise", Toast.LENGTH_SHORT)
                .show()
            error("Exercise state list is null or index out of bounds")
          }
  // Video box for the video model
  var videoBoxIsDisplayed by remember { mutableStateOf(true) }

  var finishButtonBoxIsDisplayed by remember { mutableStateOf(false) }
  // presentation button are the skip and the start button showed first
  var presentationButtonBoxIsDisplayed by remember { mutableStateOf(true) }
  // The camera feedback is displaying or not
  var cameraFeedbackIsDisplayed by remember { mutableStateOf(false) }
  // if the camera is currently recording
  var isRecordingInCamera by remember { mutableStateOf(cameraViewModel.recording.value != null) }
  var cameraRecordAsked by remember { mutableStateOf(false) }
  var userHasRecorded by remember { mutableStateOf(false) }
  var URL by remember { mutableStateOf("") }
  val videoList by videoViewModel.videos.collectAsState(initial = emptyList())
  LaunchedEffect(videoList) { videoViewModel.loadVideos() }

  // current angle of the camera logo
  val angle by
      animateFloatAsState(
          targetValue = if (cameraRecordAsked) 180f else 0f,
          animationSpec = tween(durationMillis = 500),
          label = "" // Adjust duration as needed
          )
  // the goalCenterBox contains either the image of the exercise or the timer
  var goalCounterBoxIsDisplayed by remember { mutableStateOf(false) }
  var countDownTimerIsPaused by remember { mutableStateOf(true) }
  var comparisonVideoIsDisplayed by remember { mutableStateOf(false) }
  var summaryScreenIsDisplayed by remember { mutableStateOf(false) }
  // Setting a countdown or not before the time based exercises begins
  var isCountdownTime by remember {
    mutableStateOf(true)
  } // for the countdown before the exercise starts
  val maxCountDownTIme = 3
  var countDownValue by remember { mutableIntStateOf(maxCountDownTIme) }
  // Tone to be played when te countDown decreases
  val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

  // Function to set UI state for exercise presentation
  fun paramToPresentation() {
    videoBoxIsDisplayed = true
    finishButtonBoxIsDisplayed = false
    presentationButtonBoxIsDisplayed = true
    goalCounterBoxIsDisplayed = false
    countDownTimerIsPaused = false
  }

  /** Determine if the exercise is repetition-based or time-based. */
  val exerciseIsRepetitionBased =
      when (exerciseState.exercise.detail) {
        is ExerciseDetail.TimeBased -> false
        is ExerciseDetail.RepetitionBased -> true
      }

  /** Get the number of repetitions for repetition-based exercises */
  val repetitions =
      when (exerciseState.exercise.detail) {
        is ExerciseDetail.RepetitionBased ->
            (exerciseState.exercise.detail as ExerciseDetail.RepetitionBased).repetitions
        else -> 0
      }

  /** State variables for managing the timer */
  var timer by remember { mutableIntStateOf(0) }
  var currentSet by remember { mutableIntStateOf(0) }
  var timeLimit = 0
  var numberOfSets by remember { mutableIntStateOf(0) }

  // Initialize the timer for time-based exercises
  if (!exerciseIsRepetitionBased) {
    val rawDetail = exerciseState.exercise.detail as ExerciseDetail.TimeBased
    isCountdownTime = true
    countDownTimerIsPaused = true
    timeLimit = rawDetail.durationInSeconds
    numberOfSets = rawDetail.sets
    timer = timeLimit
    // Coroutine to decrement the timer every second
    LaunchedEffect(Unit) {
      while (true) {
        delay(1000L)
        if (!countDownTimerIsPaused &&
            finishButtonBoxIsDisplayed &&
            !cameraRecordAsked &&
            !comparisonVideoIsDisplayed &&
            currentSet < numberOfSets) {
          if (isCountdownTime) {
            if (countDownValue > 0) {
              countDownValue--
              toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
            } else if (countDownValue == 0) {
              isCountdownTime = false
              toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 150)
              countDownValue = maxCountDownTIme
            }
          } else {
            if (timer > 0) {
              timer--
            } else if (timer == 0) { // Reload the timer and add one done set
              if (currentSet != numberOfSets.minus(1)) {
                timer = timeLimit
                isCountdownTime = true
              }

              currentSet++
              // reload the countdown time
              isCountdownTime = true
              countDownValue = maxCountDownTIme
              countDownTimerIsPaused = true // pause automaticaly after a set
            }
          }
        }
      }
    }
  }

  /** Moves to the next exercise or finishes the workout if all exercises are completed. */
  fun nextExercise() {

    userHasRecorded = false
    cameraRecordAsked = false
    cameraFeedbackIsDisplayed = false
    comparisonVideoIsDisplayed = false
    if (exerciseIndex < exerciseStateList.size - 1) {
      exerciseIndex++
      paramToPresentation()
      currentSet = 0 // reset the set counter
      timer = 0 // reset the counter
      countDownValue = maxCountDownTIme
    } else if (!summaryScreenIsDisplayed) {
      summaryScreenIsDisplayed = true
    } else {
      // delete the workout
      val stats =
          statisticsViewModel.computeWorkoutStatistics(
              workout = workout,
              exerciseList = exerciseStateList ?: emptyList(),
              userAccountViewModel = userAccountViewModel)
      statisticsViewModel.addWorkoutStatistics(stats)
        workoutViewModel.transferWorkoutToDone(workoutID)
      navigationActions.navigateTo(Screen.MAIN)
    }
  }

  // Scaffold for basic material design layout
  Scaffold(
      topBar = {
        if (!summaryScreenIsDisplayed) {
          CenterAlignedTopAppBar(
              title = {
                // Display the workout name
                Text(
                    text = workoutName,
                    modifier = Modifier.padding(end = 8.dp).testTag("WorkoutName"),
                    fontSize = 20.sp,
                    color = TitleBlue,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold // Makes the text bold
                    )
              },
              navigationIcon = { ArrowBack(navigationActions) },
              colors =
                  TopAppBarColors(
                      containerColor = LightBackground,
                      scrolledContainerColor = LightBackground,
                      navigationIconContentColor = Black,
                      titleContentColor = TitleBlue,
                      actionIconContentColor = TitleBlue))
        } else TopBar(navigationActions, R.string.Summary)
      },
      containerColor = LightBackground) { innerPadding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .testTag("WorkoutScreenBodyColumn"),
            horizontalAlignment = Alignment.CenterHorizontally) {
              if (summaryScreenIsDisplayed) {
                WorkoutSummaryScreen(
                    workoutName = workoutName,
                    hasWarmUp = hasWarmUp,
                    exerciseStateList.filter { it.exercise.type.workoutType != WorkoutType.WARMUP },
                    onfinishButtonClicked = { nextExercise() },
                    userAccountViewModel = userAccountViewModel)
              } else {
                // Column for displaying exercise information
                Column(
                    modifier = Modifier.size(350.dp, 150.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                      // display the exercise name
                      Text(
                          text = exerciseState.exercise.type.toString(),
                          style = MaterialTheme.typography.labelLarge.copy(fontSize = 35.sp),
                          fontWeight = FontWeight.Bold,
                          color = TitleBlue,
                          modifier = Modifier.height(50.dp).testTag("ExerciseName"))
                      Divider(
                          color = Line,
                          thickness = 0.5.dp,
                          modifier =
                              Modifier.padding(horizontal = 25.dp, vertical = 1.dp)
                                  .padding(bottom = 10.dp)
                                  .shadow(1.dp))
                      Spacer(modifier = Modifier.height(16.dp))
                      // display the instruction
                      Text(
                          text = exerciseState.exercise.type.getInstruction(),
                          style =
                              MaterialTheme.typography.displaySmall.copy(
                                  fontSize = 20.sp, lineHeight = 25.sp),
                          fontFamily = OpenSans,
                          textAlign = TextAlign.Center,
                          modifier =
                              Modifier.width(317.dp).height(79.dp).testTag("ExerciseDescription"))
                    }

                // Box for the video player
                if (videoBoxIsDisplayed) {

                  URL =
                      if (videoList.isNotEmpty())
                          videoList.first { it.title == exerciseState.exercise.type.toString() }.url
                      else ""

                  if (URL.isNotEmpty()) {
                    Box(
                        modifier =
                            Modifier.size(width = 350.dp, height = 200.dp)
                                .testTag("VideoPlayer")
                                .composed { key(URL) { this } }) {
                          VideoPlayer(context = LocalContext.current, url = URL)
                          Spacer(Modifier.height(5.dp))
                        }
                  } else {
                    // Show a progress circle if the video list is not yet fetched
                    Box(
                        modifier =
                            Modifier.size(width = 350.dp, height = 200.dp)
                                .testTag("LoadingIndicator"),
                        contentAlignment = Alignment.Center) {
                          androidx.compose.material3.CircularProgressIndicator(
                              modifier = Modifier.size(50.dp).padding(16.dp),
                              color = MaterialTheme.colorScheme.primary)
                        }
                  }
                }

                // Column for displaying exercise goals (repetitions or timer)

                Column(
                    modifier = Modifier.fillMaxHeight().padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter =
                                painterResource(
                                    if (exerciseIsRepetitionBased) R.drawable.baseline_timeline_24
                                    else R.drawable.baseline_access_time_24),
                            contentDescription = "repetition",
                            modifier = Modifier.padding(horizontal = (5).dp).testTag("GoalIcon"),
                            colorFilter = ColorFilter.tint(TitleBlue))
                        Text(
                            text =
                                (if (exerciseIsRepetitionBased) "$repetitions Rep."
                                else
                                    "${convertSecondsToTime(timeLimit)}${if (numberOfSets>1) " x $numberOfSets" else "" }"),
                            fontSize = TitleFontSize,
                            fontWeight = FontWeight.SemiBold,
                            color = TitleBlue,
                            modifier = Modifier.testTag("GoalValue"))
                      }

                      Spacer(modifier = Modifier.height(10.dp))
                      if (goalCounterBoxIsDisplayed &&
                          exerciseIsRepetitionBased.not() &&
                          numberOfSets > 1) {
                        Box(
                            modifier =
                                Modifier.width(75.dp)
                                    .height(38.dp)
                                    .border(
                                        width = 2.dp, // Adjust border thickness as needed
                                        color = Color(0xFF6750A4),
                                        shape = RoundedCornerShape(25.dp)),
                            contentAlignment = Alignment.Center) {
                              Text(
                                  text = "$currentSet/$numberOfSets",
                                  color = Color(0xFF6750A4) // Same color as the border
                                  )
                            }
                      }
                      Spacer(modifier = Modifier.height(10.dp))

                      // Box for the goal counter: Display an image of the exercise type if rep
                      // based
                      // or a timer
                      if (goalCounterBoxIsDisplayed) {

                        if (comparisonVideoIsDisplayed) {
                          //
                          Column(Modifier.height(500.dp).fillMaxWidth()) {
                            DualVideoPlayer(cameraViewModel.videoFile.value, url = URL, context)
                          }
                        } else if (!cameraRecordAsked) {

                          if (exerciseIsRepetitionBased) {

                            Image(
                                painter =
                                    painterResource(
                                        id =
                                            when (exerciseState.exercise.type.workoutType) {
                                              WorkoutType.WARMUP -> R.drawable.warmup_logo
                                              WorkoutType.YOGA -> R.drawable.yoga
                                              WorkoutType.BODY_WEIGHT -> R.drawable.dumbbell
                                              WorkoutType.RUNNING -> TODO()
                                            }),
                                contentDescription = "Goal Counter",
                                modifier =
                                    Modifier.size(350.dp, 200.dp).testTag("ExerciseTypeIcon"))
                          } else {
                            CountDownTimer(
                                timer,
                                timeLimit,
                                modifier = Modifier.size(220.dp),
                                isPaused = countDownTimerIsPaused,
                                isFinished = (timer == 0 && currentSet == numberOfSets),
                                countDownCurrentValue = countDownValue,
                                isCountDownTime = isCountdownTime,
                                onPauseClicked = {
                                  countDownTimerIsPaused = countDownTimerIsPaused.not()
                                })
                            Spacer(modifier = Modifier.height(5.dp))
                          }
                        } else {
                          CameraFeedBack.CameraScreen(
                              cameraViewModel, modifier = Modifier.size(220.dp, 350.dp))
                        }
                      }
                    }

                // Presentation button box
                if (presentationButtonBoxIsDisplayed) {

                  SkipButton(
                      onClick = {
                        exerciseStateList[exerciseIndex].isDone = false
                        nextExercise()
                      })
                  Spacer(modifier = Modifier.height(30.dp))
                  // Switch to ask if the user wants to record itself
                  Card(
                      modifier =
                          Modifier.padding(
                                  horizontal = 16.dp, vertical = 8.dp) // Adjust padding as needed
                              .shadow(
                                  elevation = 3.dp, shape = RoundedCornerShape(25.dp)) // Add shadow
                              .clip(RoundedCornerShape(25.dp)), // Ensure rounded corners
                      colors =
                          CardDefaults.cardColors(
                              containerColor = White), // Set card background color
                      elevation = CardDefaults.elevatedCardElevation(8.dp), // Add elevation
                  ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                            Modifier.clip(RoundedCornerShape(25.dp)) // Add rounded corners
                                .background(White) // Add background color
                                .padding(8.dp)) {
                          Image(
                              painter = painterResource(id = R.drawable.baseline_camera_24),
                              contentDescription = "Record Video",
                              modifier = Modifier.padding(end = 8.dp).rotate(angle))

                          Text(
                              "Record yourself",
                              fontSize = SubtitleFontSize,
                              fontWeight = FontWeight.Bold,
                              fontFamily = OpenSans,
                              color = TitleBlue) // Add text "Record"
                          Spacer(modifier = Modifier.width(5.dp))
                          Switch(
                              checked = cameraRecordAsked,
                              onCheckedChange = { cameraRecordAsked = it },
                              colors = SwitchDefaults.colors(checkedTrackColor = LightBlue2),
                              modifier = Modifier.testTag("recordSwitch"))
                        }
                  }
                  Spacer(Modifier.height(30.dp))
                  NextButton(
                      text = "Start",
                      onClick = {
                        presentationButtonBoxIsDisplayed = false
                        goalCounterBoxIsDisplayed = true
                        finishButtonBoxIsDisplayed = true
                        videoBoxIsDisplayed = false
                      },
                      modifier =
                          Modifier.width(Dimensions.ButtonWidth)
                              .height(Dimensions.ButtonHeight)
                              .align(Alignment.CenterHorizontally)
                              .background(brush = BlueGradient, shape = LeafShape)
                              .testTag("StartButton"))
                } else if (finishButtonBoxIsDisplayed) {
                  // Finish button box to be displayed during a exercise to be executing
                  Column(
                      modifier =
                          Modifier.size(height = 250.dp, width = 180.dp).padding(top = 20.dp),
                      horizontalAlignment = Alignment.CenterHorizontally,
                      verticalArrangement = Arrangement.Top) {
                        if (cameraRecordAsked) {
                          Button(
                              colors =
                                  ButtonDefaults.buttonColors(
                                      containerColor =
                                          if (isRecordingInCamera) Red2
                                          else if (userHasRecorded) Green2
                                          else LightBlue2.copy(alpha = 0.7F)),
                              onClick = {
                                userHasRecorded = true
                                cameraViewModel.recordVideo(
                                    onSuccess = {
                                      isRecordingInCamera = false
                                      Toast.makeText(context, "Record saved", Toast.LENGTH_SHORT)
                                          .show()
                                    },
                                    onFailure = { isRecordingInCamera = false },
                                    onFinishRecording = { isRecordingInCamera = false },
                                    onStarting = { isRecordingInCamera = true })
                              }) {
                                Text(
                                    if (isRecordingInCamera) "Recording..."
                                    else if (userHasRecorded) "Record again" else "Tap to record",
                                    color = White,
                                    fontFamily = OpenSans,
                                    fontWeight = FontWeight.Bold)
                              }
                        }
                        Spacer(Modifier.size(25.dp))
                        SkipButton(
                            onClick = {
                              exerciseStateList[exerciseIndex].isDone = false
                              nextExercise()
                            })
                        Spacer(Modifier.size(25.dp))

                        NextButton(
                            text = "Finish",
                            onClick = {
                              if (cameraRecordAsked && userHasRecorded) {
                                comparisonVideoIsDisplayed = true
                                cameraRecordAsked = false // acknowledge the record demand
                              } else {
                                nextExercise()
                              }
                            },
                            modifier =
                                Modifier.width(Dimensions.ButtonWidth)
                                    .height(Dimensions.ButtonHeight)
                                    .align(Alignment.CenterHorizontally)
                                    .background(brush = BlueGradient, shape = LeafShape)
                                    .testTag("FinishButton"),
                            arrow = false)
                        Spacer(Modifier.size(25.dp))
                      }
                }
              }
            }
      }
}

// Composable function for the video player using ExoPlayer : SHOULD BE ADAPTED TO THE VIDEO VIEW
// MODEL
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(url: String, context: Context) {
  val exoPlayer = remember { ExoPlayer.Builder(context).build() }

  // This will be called whenever the 'url' changes
  LaunchedEffect(url) {
    val mediaItem = MediaItem.fromUri(url)
    exoPlayer.setMediaItem(mediaItem)
    exoPlayer.prepare()
    exoPlayer.playWhenReady = true
  }
  AndroidView(
      modifier =
          Modifier.fillMaxSize()
              .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
              .clip(RoundedCornerShape(16.dp)),
      factory = {
        PlayerView(context).apply {
          player = exoPlayer
          useController = true
          resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
          layoutParams =
              ViewGroup.LayoutParams(
                  ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
      })

  DisposableEffect(Unit) { onDispose { exoPlayer.release() } }
}

/** WorkoutScreen that display the workflow during a workout */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun WorkoutScreen(
    navigationActions: NavigationActions,
    warmUpViewModel: WarmUpViewModel,
    bodyweightViewModel: WorkoutViewModel<BodyWeightWorkout>,
    yogaViewModel: WorkoutViewModel<YogaWorkout>,
    workoutType: WorkoutType,
    cameraViewModel: CameraViewModel = CameraViewModel(LocalContext.current),
    videoViewModel: VideoViewModel,
    userAccountViewModel: UserAccountViewModel,
    statisticsViewModel: StatisticsViewModel
) {
  // Get the selected workout based on the workout type
  val viewModel =
      when (workoutType) {
        WorkoutType.YOGA -> yogaViewModel
        WorkoutType.WARMUP -> warmUpViewModel
        WorkoutType.BODY_WEIGHT -> bodyweightViewModel
        WorkoutType.RUNNING -> TODO()
      }

  val selectedWorkout = viewModel.selectedWorkout.value

  // Create a list of ExerciseState objects from the selected workout, add the workout to it on
  // condition
  val exerciseStateList =
      selectedWorkout?.let {
        (if (selectedWorkout.warmup) warmUpViewModel.selectedWorkout.value?.exercises else listOf())
            ?.plus(it.exercises)
            ?.map { warmUpExercise -> ExerciseState(warmUpExercise, true) }
      }

  val workoutID = selectedWorkout?.workoutId ?: ""

  // Display the WarmUpScreenBody with the exercise list and workout name
  selectedWorkout?.name?.let {
    WorkoutScreenBody(
        exerciseStateList,
        workoutName = it,
        navigationActions = navigationActions,
        cameraViewModel = cameraViewModel,
        videoViewModel = videoViewModel,
        hasWarmUp = selectedWorkout.warmup,
        userAccountViewModel = userAccountViewModel,
        workoutViewModel = viewModel,
        workoutID = workoutID,
        statisticsViewModel = statisticsViewModel,
        workout = selectedWorkout)
  }
}
