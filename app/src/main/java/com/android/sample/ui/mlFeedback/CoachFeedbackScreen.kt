package com.android.sample.ui.mlFeedback

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.mlUtils.CoachFeedback
import com.android.sample.mlUtils.FeedbackRank
import com.android.sample.mlUtils.exercisesCriterions.JumpingJacksOpenCriterions
import com.android.sample.mlUtils.exercisesCriterions.PushUpsUpCrierions
import com.android.sample.mlUtils.rateToRank
import com.android.sample.model.camera.CameraViewModel
import com.android.sample.ui.composables.SaveButton
import com.android.sample.ui.composables.TalkingCoach
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Black
import com.android.sample.ui.theme.BlueWorkoutCard
import com.android.sample.ui.theme.FontSizes.BigTitleFontSize
import com.android.sample.ui.theme.Green
import com.android.sample.ui.theme.MediumGrey
import com.android.sample.ui.theme.OpenSans
import com.android.sample.ui.theme.Red
import com.android.sample.ui.theme.RunningTag
import com.android.sample.ui.theme.Yellow
import com.android.sample.ui.theme.YogaTag
import com.android.sample.ui.workout.LeafShape
import kotlinx.coroutines.launch

/**
 * Composable function that displays the coach feedback screen.
 *
 * @param navigationActions The navigation actions to be performed.
 * @param cameraViewModel The view model for the camera.
 */
@Composable
fun CoachFeedbackScreen(navigationActions: NavigationActions, cameraViewModel: CameraViewModel) {
  var showInfoDialogue by remember { mutableStateOf(false) }
  Scaffold(
      modifier = Modifier.testTag("coachFeedBackScreen"),
      topBar = {
        TopBar(title = R.string.coach_feedback_title, navigationActions = navigationActions)
      },
      content = { pd ->
        if (showInfoDialogue) {
          NoteInfoDialogue(
              onDismiss = { showInfoDialogue = false }, modifier = Modifier.testTag("infoDialogue"))
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(pd).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
              val rawFeedback = cameraViewModel.feedback
              val rank = getNote(rawFeedback!!)
              val exerciseString = exerciseName(rawFeedback)
              val durationOrRepetitionString = durationString(rawFeedback)
              val startingFeedback = genericFeedbackFromRank(rank)

              // Card with exercise name and duration/repetition
              Card(
                  colors = CardDefaults.cardColors(containerColor = BlueWorkoutCard),
                  shape = LeafShape,
                  modifier =
                      Modifier.padding(18.dp)
                          .shadow(8.dp, shape = LeafShape)
                          .testTag("exerciseCard")) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                          Text(
                              text = exerciseString,
                              fontFamily = OpenSans,
                              fontSize = 20.sp,
                              fontWeight = FontWeight.Bold,
                              modifier = Modifier.testTag("exerciseName"))
                          Spacer(modifier = Modifier.height(8.dp))
                          Text(
                              text = durationOrRepetitionString,
                              fontFamily = OpenSans,
                              fontSize = 16.sp,
                              fontWeight = FontWeight.Normal,
                              modifier = Modifier.testTag("exerciseDuration"))
                        }
                  }
              // Animated feedback rank circle
              RankCircle(rank, onClick = { showInfoDialogue = true })

              TalkingCoach(
                  text = startingFeedback + "\n" + rawFeedback.joinToString("\n") { it.toString() },
              )

              Spacer(modifier = Modifier.height(16.dp))
              // Done button
              SaveButton(
                  onSaveClick = { navigationActions.navigateTo(Screen.MAIN) },
                  testTag = "doneButton",
                  text = "Done")
            }
      })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteInfoDialogue(onDismiss: () -> Unit, modifier: Modifier) {
  BasicAlertDialog(
      onDismissRequest = onDismiss,
      modifier = modifier,
  ) {
    Card {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Note Information",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp))
        Text(
            text = stringResource(R.string.note_info),
        )
      }
    }
  }
}

private fun getNote(feedbacks: List<CoachFeedback>): FeedbackRank {
  val averageRate = feedbacks.map { it.successRate }.average()
  val rank = rateToRank(averageRate.toFloat())
  return rank
}

private fun genericFeedbackFromRank(rank: FeedbackRank): String {
  return when (rank) {
    FeedbackRank.S -> "Amazing! Keep it up! Can't say anything wrong about your performance!"
    FeedbackRank.A -> "Great job! Here are some tips to improve even more :"
    FeedbackRank.B -> "Good job! But you can surely do better! Here are some tips to improve :"
    FeedbackRank.C -> "Ok, there is room for improvement! Here are some tips to improve :"
    FeedbackRank.D ->
        "You need to improve in order to do the exercise correctly! Here are some tips to improve :"
    FeedbackRank.X -> "I couldn't see you. Make sure your whole body is in the camera frame."
  }
}

private fun durationString(feedbacks: List<CoachFeedback>): String {
  val feedback = feedbacks.first()
  return "${feedback.feedbackUnit.valuePrefix}: ${feedback.feedbackValue} ${feedback.feedbackUnit.stringRepresentation}"
}

private fun exerciseName(feedbacks: List<CoachFeedback>): String {
  return when (feedbacks.first().exerciseCriterion) {
    PushUpsUpCrierions -> "Push Ups"
    JumpingJacksOpenCriterions -> "Jumping Jacks"
    else -> feedbacks.first().exerciseCriterion.name
  }
}

/**
 * Composable function that displays a circle with a rank inside, with a nice animation
 *
 * @param rank The rank to be displayed inside the circle.
 */
@Composable
fun RankCircle(rank: FeedbackRank, onClick: () -> Unit) {
  // Main color depending on the rank
  val rankColor = getColorForRank(rank)

  // Infinite animation for the breathing effect
  val infiniteTransition = rememberInfiniteTransition()
  val animatedRadius =
      infiniteTransition.animateFloat(
          initialValue = 0f,
          targetValue = 1f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(durationMillis = 2000, easing = LinearOutSlowInEasing),
                  repeatMode = RepeatMode.Reverse // Contraction effect after expansion
                  ))

  // Animation for the large initial waves
  val initialWaveCount = 5 // Number of initial waves
  val initialWaveRadius = remember { mutableStateListOf<Float>() }
  val initialWaveAlpha = remember { mutableStateListOf<Float>() }

  // Initialize wave states
  if (initialWaveRadius.isEmpty()) {
    repeat(initialWaveCount) { index ->
      initialWaveRadius.add(0f)
      initialWaveAlpha.add(1f)
    }
  }

  // Launch initial animations
  LaunchedEffect(Unit) {
    initialWaveRadius.forEachIndexed { index, _ ->
      launch {
        animate(
            initialValue = 0f,
            targetValue = 3.5f, // Waves extend beyond the screen
            animationSpec =
                tween(durationMillis = 1500 + index * 300, easing = LinearOutSlowInEasing)) {
                value,
                _ ->
              initialWaveRadius[index] = value
            }
      }
      launch {
        animate(
            initialValue = 1f,
            targetValue = 0f, // Waves gradually fade out
            animationSpec =
                tween(durationMillis = 1500 + index * 300, easing = LinearOutSlowInEasing)) {
                value,
                _ ->
              initialWaveAlpha[index] = value
            }
      }
    }
  }

  Box(
      contentAlignment = Alignment.Center,
      modifier =
          Modifier.size(180.dp) // Global size
              .testTag("rankCircle")) {
        // Large initial waves and breathing effect (background)
        Canvas(modifier = Modifier.fillMaxSize()) {
          val canvasRadius = size.minDimension / 2

          // Large initial waves
          initialWaveRadius.forEachIndexed { index, waveRadius ->
            if (initialWaveAlpha[index] > 0f) { // Display only if visible
              drawCircle(
                  color = rankColor.copy(alpha = 0.3f * initialWaveAlpha[index]),
                  radius = canvasRadius * waveRadius,
                  style = Stroke(width = 6.dp.toPx()) // Thin large waves
                  )
            }
          }

          // Breathing effect
          drawCircle(
              color = rankColor.copy(alpha = 0.3f), // Semi-transparent color
              radius = canvasRadius * animatedRadius.value, // Animated radius
              style = Stroke(width = 8.dp.toPx()) // Outline only
              )
        }

        // Main circle (above animations)
        Box(
            modifier =
                Modifier.size(140.dp) // Smaller size compared to animations
                    .shadow(
                        elevation = 12.dp, // Soft shadow
                        shape = CircleShape,
                        clip = true)
                    .background(MediumGrey, CircleShape) // Fully opaque background
                    .border(
                        width = 4.dp, // Outline of the main circle
                        color = rankColor,
                        shape = CircleShape)
                    .clickable { onClick() }
                    .testTag("rankButton")) {
              // Rank text in the center
              Text(
                  text = rank.name,
                  color = rankColor, // Text color based on the rank
                  fontFamily = OpenSans,
                  fontSize = BigTitleFontSize,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.align(Alignment.Center).testTag("rankText"))
            }
      }
}

private fun getColorForRank(rank: FeedbackRank): Color {
  return when (rank) {
    FeedbackRank.S -> YogaTag
    FeedbackRank.A -> Green
    FeedbackRank.B -> RunningTag
    FeedbackRank.C -> Yellow
    FeedbackRank.D -> Red
    FeedbackRank.X -> Black
  }
}
