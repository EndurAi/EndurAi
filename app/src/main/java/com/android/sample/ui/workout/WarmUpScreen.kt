package com.android.sample.ui.workout

import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.android.sample.R
import com.android.sample.ui.composables.CountDownTimer
import com.android.sample.ui.composables.convertTimeToSeconds
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun ExerciseScreen(
    exerciseName: String = "Jumping Jacks",
    instruction: String = "Stand, jump while spreading arms and legs.",
    repetitions: Int = 10,
    onSkip: () -> Unit = {},
    onStart: () -> Unit = {}
) {
  var videoBoxIsDisplayed by remember { mutableStateOf(false) }
  var finishButtonBoxIsDisplayed by remember { mutableStateOf(true) }
  var presentationButtonBoxIsDisplayed by remember { mutableStateOf(false) }
  var goalCounterBoxIsDisplayed by remember { mutableStateOf(true) }
  val timeLimit = "02:45"
  val timer = remember { mutableStateOf(timeLimit) }
  val exerciseIsRepetitionBased = false
  LaunchedEffect(Unit) {

    var totalSeconds = convertTimeToSeconds(timer.value)
    while (totalSeconds >= 0) {
      val minutes = totalSeconds / 60
      val seconds = totalSeconds % 60
      timer.value = String.format("%02d:%02d", minutes, seconds)
      delay(1000L)
      if (goalCounterBoxIsDisplayed ) {
        totalSeconds--
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
                      Modifier.background(Color(0xFFD9D9D9), shape = RoundedCornerShape(20.dp))
                          .padding(horizontal = 80.dp)
                          .padding(1.dp),
                  fontWeight = FontWeight(500),
                  color = MaterialTheme.colorScheme.onSurface)
            })
      }) { innerPadding ->
    Column(
      modifier =
      Modifier.fillMaxSize()
        .padding(innerPadding) // Use innerPadding to avoid overlapping with the app bar
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = exerciseName,
        style = MaterialTheme.typography.labelLarge.copy(fontSize = 35.sp),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.height(79.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = instruction,
        style =
        MaterialTheme.typography.displaySmall.copy(
          fontSize = 20.sp, lineHeight = 25.sp
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.width(317.dp).height(79.dp)
      )
      Spacer(modifier = Modifier.height(16.dp))
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
        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Repetitions")
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$repetitions Rep.")
      }
      Spacer(modifier = Modifier.height(35.dp))

      // Box for the goal counter
      if (goalCounterBoxIsDisplayed) {

        if (exerciseIsRepetitionBased) {
          Box(modifier = Modifier.size(width = 350.dp, height = 200.dp)) {
            val workoutImage =
              Image(
                painter = painterResource(id = R.drawable.warmup_logo),
                contentDescription = "Goal Counter",
                modifier = Modifier.size(350.dp, 200.dp)
              )
          }
        } else {
          CountDownTimer(currentTime_str = timer.value, maxTime_str = timeLimit)

        }
      }


      // Presentation button box
      if (presentationButtonBoxIsDisplayed) {
        val presentationButtonBox =
          Column(
            modifier = Modifier.size(height = 120.dp, width = 180.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
          ) {
            // Skip button
            Button(
              onClick = { videoBoxIsDisplayed = !videoBoxIsDisplayed },
              colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
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
              modifier = Modifier.width(200.dp).height(50.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA9B0FF)),
              shape = RoundedCornerShape(size = 11.dp)
            ) {
              Text("Start", color = Color.Black, fontSize = 20.sp)
            }
          }
      } else if (finishButtonBoxIsDisplayed) {
        val finishButtonBox =
          Column(
            modifier = Modifier.size(height = 120.dp, width = 180.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
          ) {
            Button(
              onClick = {},
              modifier = Modifier.width(200.dp).height(50.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA9B0FF)),
              shape = RoundedCornerShape(size = 11.dp)
            ) {
              Text("Finish", color = Color.Black, fontSize = 20.sp)
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
