package com.android.sample.ui.workout

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.android.sample.R
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Exercise
import com.android.sample.model.workout.ExerciseDetail
import com.android.sample.model.workout.WarmUpViewModel
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.composables.ArrowBack
import com.android.sample.ui.composables.CountDownTimer
import com.android.sample.ui.composables.SkipButton
import com.android.sample.ui.composables.convertSecondsToTime
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import kotlinx.coroutines.delay

// Data class to hold the state of an exercise
data class ExerciseState(val exercise: Exercise, var isDone: Boolean)

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarmUpScreenBody(
    exerciseStateList: List<ExerciseState>?,
    workoutName: String,
    navigationActions: NavigationActions
) {
  // State variables for managing the UI and workout flow
  var exerciseIndex by remember { mutableIntStateOf(0) }
  val context = LocalContext.current
  val exerciseState =
    exerciseStateList?.get(exerciseIndex)
      ?: run {
        Toast.makeText(context, "Error while fetching the list of exercise", Toast.LENGTH_SHORT).show()
        error("Exercise state list is null or index out of bounds")
      }
  // Video box for the video model
  var videoBoxIsDisplayed by remember { mutableStateOf(true) }

  var finishButtonBoxIsDisplayed by remember { mutableStateOf(false) }
  // presentation button are the skip and the start button showed first
  var presentationButtonBoxIsDisplayed by remember { mutableStateOf(true) }
  // the goalCenterBox contains either the image of the exercise or the timer
  var goalCounterBoxIsDisplayed by remember { mutableStateOf(false) }
  var countDownTimerIsPaused by remember { mutableStateOf(false) }
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

  /** Moves to the next exercise or finishes the workout if all exercises are completed. */
  fun nextExercise() {
    if (exerciseIndex < exerciseStateList.size - 1) {
      exerciseIndex++
      paramToPresentation()
    } else {
      navigationActions.navigateTo(Screen.MAIN)
    }
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
  var timeLimit = 0

  // Initialize the timer for time-based exercises
  if (!exerciseIsRepetitionBased) {
    val rawDetail = exerciseState.exercise.detail as ExerciseDetail.TimeBased
    isCountdownTime = true
    timeLimit = rawDetail.durationInSeconds * rawDetail.sets
    timer = timeLimit

    // Coroutine to decrement the timer every second
    LaunchedEffect(Unit) {
      while (true) {
        delay(1000L)
        if (!countDownTimerIsPaused && finishButtonBoxIsDisplayed) {
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
            }
          }
        }
      }
    }
  }

  // Scaffold for basic material design layout
  Scaffold(
      topBar = {
        CenterAlignedTopAppBar(
            title = {

              // Display the workout name
              Text(
                  workoutName,
                  modifier =
                      Modifier.background(Color(0xFFD9D9D9), shape = RoundedCornerShape(20.dp))
                          .padding(horizontal = 80.dp)
                          .padding(1.dp)
                          .testTag("WorkoutName"),
                  fontWeight = FontWeight(500),
                  color = MaterialTheme.colorScheme.onSurface)
            },
            navigationIcon = { ArrowBack(navigationActions) })
      }) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally) {

              // Column for displaying exercise information
              Column(
                  modifier = Modifier.size(350.dp, 150.dp),
                  horizontalAlignment = Alignment.CenterHorizontally) {
                    // display the exercise name
                    Text(
                        text = exerciseState.exercise.type.toString(),
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 35.sp),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.height(50.dp).testTag("ExerciseName"))
                    Spacer(modifier = Modifier.height(16.dp))
                    // display the instruction
                    Text(
                        text = exerciseState.exercise.type.getInstruction(),
                        style =
                            MaterialTheme.typography.displaySmall.copy(
                                fontSize = 20.sp, lineHeight = 25.sp),
                        textAlign = TextAlign.Center,
                        modifier =
                            Modifier.width(317.dp).height(79.dp).testTag("ExerciseDescription"))
                  }

              // URL for the video demonstration (this should be dynamic)
              val URL =
                  "https://firebasestorage.googleapis.com/v0/b/endurai-92811.appspot.com/o/template_videos%2FPush%20Up.mp4?alt=media&token=2677215b-59a4-47c8-854b-a3326532e8af"

              // Box for the video player
              if (videoBoxIsDisplayed) {
                Box(
                    modifier =
                        Modifier.size(width = 350.dp, height = 200.dp).testTag("VideoPlayer")) {
                      VideoPlayer(context = LocalContext.current, url = URL)
                      Spacer(Modifier.height(5.dp))
                    }
              }

              // Column for displaying exercise goals (repetitions or timer)
              Column(
                  modifier =
                      if (goalCounterBoxIsDisplayed) Modifier.size(300.dp, 300.dp)
                      else Modifier.size(150.dp, 100.dp),
                  horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Image(
                          painter =
                              painterResource(
                                  if (exerciseIsRepetitionBased) R.drawable.baseline_timeline_24
                                  else R.drawable.baseline_access_time_24),
                          contentDescription = "repetition",
                          modifier = Modifier.padding(horizontal = (5).dp).testTag("GoalIcon"))
                      Text(
                          text =
                              (if (exerciseIsRepetitionBased) "$repetitions Rep."
                              else convertSecondsToTime(timeLimit)),
                          fontSize = 20.sp,
                          modifier = Modifier.testTag("GoalValue"))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Box for the goal counter: Display an image of the exercise type if rep based
                    // or a timer
                    if (goalCounterBoxIsDisplayed) {
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
                            modifier = Modifier.size(350.dp, 200.dp).testTag("ExerciseTypeIcon"))
                      } else {
                        CountDownTimer(
                            timer,
                            timeLimit,
                            modifier =
                                Modifier.size(220.dp).testTag("CountDownTimer").clickable {
                                  countDownTimerIsPaused = !countDownTimerIsPaused
                                },
                            isPaused = countDownTimerIsPaused,
                            countDownCurrentValue = countDownValue,
                            isCountDownTime = isCountdownTime)
                        Spacer(modifier = Modifier.height(5.dp))
                      }
                    }
                  }

              // Presentation button box
              if (presentationButtonBoxIsDisplayed) {
                Column(
                    modifier = Modifier.size(height = 120.dp, width = 180.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom) {
                      SkipButton(onClick = { nextExercise() })
                      Spacer(modifier = Modifier.height(25.dp))
                      Button(
                          onClick = {
                            presentationButtonBoxIsDisplayed = false
                            goalCounterBoxIsDisplayed = true
                            finishButtonBoxIsDisplayed = true
                            videoBoxIsDisplayed = false
                          },
                          modifier = Modifier.width(200.dp).height(50.dp).testTag("StartButton"),
                          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA9B0FF)),
                          shape = RoundedCornerShape(size = 11.dp)) {
                            Text("Start", color = Color.Black, fontSize = 20.sp)
                          }
                    }
              } else if (finishButtonBoxIsDisplayed) {
                // Finish button box to be displayed during a exercise to be executing
                Column(
                    modifier = Modifier.size(height = 250.dp, width = 180.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top) {
                      SkipButton(onClick = { nextExercise() })
                      Spacer(Modifier.size(25.dp))
                      Button(
                          onClick = { nextExercise() },
                          modifier =
                              Modifier.width(200.dp)
                                  .height(50.dp)
                                  .padding()
                                  .testTag("FinishButton"),
                          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA9B0FF)),
                          shape = RoundedCornerShape(size = 11.dp)) {
                            Text("Finish", color = Color.Black, fontSize = 20.sp)
                          }
                      Spacer(Modifier.size(25.dp))
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
  val exoPlayer = remember {
    ExoPlayer.Builder(context).build().apply {
      val mediaItem = MediaItem.fromUri(url)
      setMediaItem(mediaItem)
      prepare()
      playWhenReady = true
    }
  }

  AndroidView(
      modifier = Modifier.fillMaxSize(),
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
    workoutType: WorkoutType
) {
  // Get the selected workout based on the workout type
  val selectedWorkout =
      when (workoutType) {
        WorkoutType.YOGA -> yogaViewModel.selectedWorkout.value
        WorkoutType.WARMUP -> warmUpViewModel.selectedWorkout.value
        WorkoutType.BODY_WEIGHT -> bodyweightViewModel.selectedWorkout.value
        WorkoutType.RUNNING -> TODO()
      }

  // Create a list of ExerciseState objects from the selected workout, add the workout to it on
  // condition
  val exerciseStateList =
      selectedWorkout?.let {
        (if (selectedWorkout.warmup) warmUpViewModel.selectedWorkout.value?.exercises else listOf())
            ?.plus(it.exercises)
            ?.map { warmUpExercise -> ExerciseState(warmUpExercise, true) }
      }

  // Display the WarmUpScreenBody with the exercise list and workout name
  selectedWorkout?.name?.let {
    WarmUpScreenBody(exerciseStateList, workoutName = it, navigationActions = navigationActions)
  }
}
