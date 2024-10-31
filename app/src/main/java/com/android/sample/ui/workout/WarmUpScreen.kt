package com.android.sample.ui.workout

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.android.sample.model.workout.Exercise
import com.android.sample.model.workout.ExerciseDetail
import com.android.sample.model.workout.WarmUpViewModel
import com.android.sample.ui.composables.CountDownTimer
import com.android.sample.ui.composables.convertSecondsToTime
import com.android.sample.ui.navigation.NavigationActions
import kotlinx.coroutines.delay
import kotlin.jvm.Throws
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.FlexibleTypeDeserializer.ThrowException

data class ExerciseState(val exercise: Exercise, var isDone : Boolean)


@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarmUpScreenBody(exerciseStateList: List<ExerciseState>?) {
  // Variable for the screen
  var exerciseIndex by remember { mutableIntStateOf(0) }
  val exerciseState  = exerciseStateList?.get(exerciseIndex) ?: error("Exercise state list is null or index out of bounds")
  var videoBoxIsDisplayed by remember { mutableStateOf(true) }
  var finishButtonBoxIsDisplayed by remember { mutableStateOf(false) }
  var presentationButtonBoxIsDisplayed by remember { mutableStateOf(true) }
  var goalCounterBoxIsDisplayed by remember { mutableStateOf(false) }
  var countDownTimerIsPaused by remember { mutableStateOf(false) }

  fun paramToPresentation(){
    videoBoxIsDisplayed =true
    finishButtonBoxIsDisplayed =false
    presentationButtonBoxIsDisplayed = true
    goalCounterBoxIsDisplayed = false

  }

  fun paramToExercise(){
    videoBoxIsDisplayed =false
    finishButtonBoxIsDisplayed =true
    presentationButtonBoxIsDisplayed = false
    goalCounterBoxIsDisplayed = true

  }


  val exerciseIsRepetitionBased = when(exerciseState.exercise.detail){
    is ExerciseDetail.TimeBased -> false;
    is ExerciseDetail.RepetitionBased -> true
  }


  val repetitions = when (exerciseState.exercise.detail) {
    is ExerciseDetail.RepetitionBased -> (exerciseState.exercise.detail as ExerciseDetail.RepetitionBased).repetitions
    else -> 0
  }
  var timer by remember { mutableIntStateOf( 0) }
  var timeLimit = 0
  if(!exerciseIsRepetitionBased){
    val rawDetail = exerciseState.exercise.detail as ExerciseDetail.TimeBased
    //TODO Handle the Sets factor
   timeLimit = rawDetail.durationInSeconds * rawDetail.sets
    timer = timeLimit


  LaunchedEffect(Unit) {
    while (timer >= 0) {
      delay(1000L)
      if (goalCounterBoxIsDisplayed && !countDownTimerIsPaused) {
        timer--
      }
    }
  }
  }

  Scaffold(
      topBar = {
        CenterAlignedTopAppBar(
            title = {
              Text(
                  "WarmUp",
                  modifier =
                  Modifier
                    .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(20.dp))
                    .padding(horizontal = 80.dp)
                    .padding(1.dp),
                  fontWeight = FontWeight(500),
                  color = MaterialTheme.colorScheme.onSurface)
            })
      }) { innerPadding ->
        Column(
            modifier =
            Modifier
              .fillMaxSize()
              .padding(innerPadding) // Use innerPadding to avoid overlapping with the app bar
              .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                //TODO THe name here
                  text = exerciseState.exercise.type.toString(),
                  style = MaterialTheme.typography.labelLarge.copy(fontSize = 35.sp),
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.height(79.dp))

              Spacer(modifier = Modifier.height(16.dp))
          //description text
              Text(
                //TODO add some descpriton
                  text = exerciseState.exercise.type.name,
                  style =
                      MaterialTheme.typography.displaySmall.copy(
                          fontSize = 20.sp, lineHeight = 25.sp),
                  textAlign = TextAlign.Center,
                  modifier = Modifier
                    .width(317.dp)
                    .height(79.dp))
              Spacer(modifier = Modifier.height(16.dp))
          //TODO add video specific
              val URL =
                  "https://firebasestorage.googleapis.com/v0/b/endurai-92811.appspot.com/o/template_videos%2FPush%20Up.mp4?alt=media&token=2677215b-59a4-47c8-854b-a3326532e8af"

              // Box for the video player
              if (videoBoxIsDisplayed) {
                val videoBox =
                    Box(modifier = Modifier.size(width = 350.dp, height = 200.dp)) {
                      VideoPlayer(context = LocalContext.current, url = URL)
                    }
              }

              Spacer(modifier = Modifier.height(16.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                if (exerciseIsRepetitionBased) {
                  Image(
                      painter = painterResource(R.drawable.baseline_timeline_24),
                      contentDescription = "repetition",
                      modifier = Modifier.padding(horizontal = (5).dp))

                  Text(text = "$repetitions Rep.", fontSize = 20.sp)
                } else {
                  Image(
                      painter = painterResource(R.drawable.baseline_access_time_24),
                      contentDescription = "timerLogo",
                      modifier = Modifier.padding(horizontal = (5).dp))
                  Text(convertSecondsToTime(timeLimit), fontSize = 20.sp)
                }
              }
              Spacer(modifier = Modifier.height(35.dp))

              // Box for the goal counter
              if (goalCounterBoxIsDisplayed) {

                if (exerciseIsRepetitionBased) {
                  Box(modifier = Modifier.size(200.dp)) {
                    val workoutImage =
                        Image(
                            painter = painterResource(id = R.drawable.warmup_logo),
                            contentDescription = "Goal Counter",
                            modifier = Modifier.size(350.dp, 200.dp))
                  }
                } else {
                  Box(modifier = Modifier.size(200.dp)) {
                    CountDownTimer(timer,timeLimit)
                  }
                }
              }

              // Presentation button box
              if (presentationButtonBoxIsDisplayed) {
                val presentationButtonBox =
                    Column(
                        modifier = Modifier.size(height = 120.dp, width = 180.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom) {
                          // Skip button
                          Button(
                              onClick = {exerciseIndex++
                                paramToPresentation()
                                        },
                              colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                                Text("Skip", color = Color.Black)
                              }
                          Spacer(modifier = Modifier.height(25.dp))
                          // Start button
                          Button(
                              onClick = {
                                presentationButtonBoxIsDisplayed = false
                                goalCounterBoxIsDisplayed = true
                                finishButtonBoxIsDisplayed = true
                                videoBoxIsDisplayed = false
                              },
                              modifier = Modifier
                                .width(200.dp)
                                .height(50.dp),
                              colors =
                                  ButtonDefaults.buttonColors(containerColor = Color(0xFFA9B0FF)),
                              shape = RoundedCornerShape(size = 11.dp)) {
                                Text("Start", color = Color.Black, fontSize = 20.sp)
                              }
                        }
              } else if (finishButtonBoxIsDisplayed) {
                val finishButtonBox =
                    Column(
                        modifier = Modifier.size(height = 250.dp, width = 180.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top) {
                          if (!exerciseIsRepetitionBased && timer != 0) {
                            Button(
                                onClick = { countDownTimerIsPaused = !countDownTimerIsPaused },
                                modifier = Modifier.size(65.dp)) {
                                  Image(
                                      painterResource(
                                          if (countDownTimerIsPaused)
                                              R.drawable.baseline_play_arrow_24
                                          else R.drawable.baseline_pause_24),
                                      contentDescription = "play/pausebtn",
                                      modifier = Modifier.fillMaxSize())
                                }
                            Spacer(Modifier.size(25.dp))
                          } else {
                            Spacer(modifier = Modifier.size(90.dp))
                          }

                          Spacer(Modifier.size(25.dp))
                          val finishButton =
                              Button(
                                  onClick = {
                                    exerciseIndex++
                                    paramToPresentation()},
                                  modifier = Modifier
                                    .width(200.dp)
                                    .height(50.dp)
                                    .padding(),
                                  colors =
                                      ButtonDefaults.buttonColors(
                                          containerColor = Color(0xFFA9B0FF)),
                                  shape = RoundedCornerShape(size = 11.dp)) {
                                    Text("Finish", color = Color.Black, fontSize = 20.sp)
                                  }

                          Spacer(Modifier.size(25.dp))

                          val skipButton =
                              Button(
                                  onClick = {exerciseIndex++
                                            paramToPresentation()},
                                  colors =
                                      ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                                    Text("Skip", color = Color.Black)
                                  }

                        }
              }
            }
      }



}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(url: String, context: Context) {
  // Créer et gérer l'instance ExoPlayer en utilisant remember
  val exoPlayer = remember {
    ExoPlayer.Builder(context).build().apply {
      // Charger l'élément multimédia en utilisant l'URL fournie
      val mediaItem = MediaItem.fromUri(url)
      setMediaItem(mediaItem)
      prepare()
      playWhenReady = true
    }
  }

  // Utiliser DisposableEffect pour libérer le lecteur lorsque le composable est supprimé de
  // l'interface utilisateur
  AndroidView(
      modifier = Modifier.fillMaxSize(),
      factory = {
        PlayerView(context).apply {
          player = exoPlayer
          useController = true // Masquer les contrôles par défaut
          resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT // Remplir tout l'écran
          layoutParams =
              ViewGroup.LayoutParams(
                  ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
      })
  DisposableEffect(Unit) { onDispose { exoPlayer.release() } }
}



@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun WarmUpScreen(navigationActions: NavigationActions, warmUpViewModel: WarmUpViewModel){

  val selectedWorkout = warmUpViewModel.selectedWorkout.value
  val exerciseStateList =
    selectedWorkout?.exercises?.map { warmUpExercise -> ExerciseState(warmUpExercise, true) }


  WarmUpScreenBody(exerciseStateList)








}


