package com.android.sample.ui.composables

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.android.sample.R

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
/**
 * Display a countdown timer that handles counting down before a process
 *
 * @param currentTime_int The current time in seconds.
 * @param maxTime_int The maximum time in seconds.
 * @param fullColor The color of the arc when the timer is not in the critical state.
 * @param criticalColor The color of the arc when the timer is in the critical state.
 * @param criticalRate The rate at which the timer is considered critical (e.g., 0.15 means 15% of
 *   max time).
 * @param stroke The stroke width of the arc.
 * @param arcSize The size of the arc.
 * @param onFinishedText The text to display when the timer finishes.
 * @param onFinishedIcon The icon to display when the timer finishes.
 * @param modifier The modifier to be applied to the Canvas.
 * @param isPaused Whether the timer is paused.
 * @param isCountDownTime Whether the timer is in countdown mode (Before decreasing the
 *   maxTime_Int).
 * @param countDownCurrentValue The current amount of time before the timer decreases the
 *   currentTime_Int.
 */
@Preview
fun CountDownTimer(
    currentTime_int: Int = 50,
    maxTime_int: Int = 60,
    fullColor: Color = Color.Green,
    criticalColor: Color = Color.Red,
    criticalRate: Float = 0.15f,
    stroke: Int = 28,
    arcSize: Float = 50f,
    onFinishedText: String = "Done",
    onFinishedIcon: ImageVector = Icons.Filled.Done,
    modifier: Modifier = Modifier,
    isPaused: Boolean = false,
    isFinished: Boolean = false,
    isCountDownTime: Boolean = true,
    countDownCurrentValue: Int = 3,
    onPauseClicked: () -> Unit = {},
    onFinish: () -> Unit = {}
) {

  val currentTime_str = convertSecondsToTime(currentTime_int)
  val maxTime_str = convertSecondsToTime(maxTime_int)

  Box(modifier.fillMaxSize().testTag("CountDownTimer")) {
    val animationState = remember {
      // Animate offset based on isFinished
      Animatable(IntOffset(0, if (isFinished) 0 else 50).y.toFloat())
    }
    val animationStateContentPadding = remember {
      // Animate offset based on isFinished
      Animatable(15f)
    }

    // Button to pause or resume the counter
    OutlinedButton(
        contentPadding = PaddingValues(animationStateContentPadding.value.toInt().dp),
        modifier =
            Modifier.align(Alignment.Center)
                .offset(y = animationState.value.toInt().dp) // Use animationState for offset
                .background(Color.Transparent)
                .testTag("CounterPauseResumeButton"),
        shape = CircleShape,
        border = BorderStroke(width = 2.dp, color = Color(0xFF6750A4)),
        onClick = onPauseClicked,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, contentColor = Color.White)) {
          Image(
              painter =
                  painterResource(
                      if (isFinished) R.drawable.baseline_done_24
                      else if (isPaused) R.drawable.baseline_play_arrow_24
                      else R.drawable.baseline_pause_24),
              contentDescription = "Play/Pause state",
              colorFilter = ColorFilter.tint(Color(0xFF6750A4)))
        }

    // Lift up animation for the button play/pause
    LaunchedEffect(isFinished) {
      animationState.animateTo(
          targetValue = if (isFinished) 0f else 65f, animationSpec = tween(durationMillis = 300))
      //
      animationStateContentPadding.animateTo(
          targetValue = if (isFinished) 35f else 15f, animationSpec = tween(durationMillis = 200))
    }

    Canvas(modifier = modifier) {
      val currentTime = convertTimeToSeconds(currentTime_str)
      val maxTime = convertTimeToSeconds(maxTime_str)

      val width = size.width
      val height = size.height

      val angle = (currentTime.toFloat() / maxTime.toFloat()) * 360f

      // Drawing the small  circle at the center
      drawArc(
          color =
              if (currentTime > 0) Color(red = 223, green = 162, blue = 240, alpha = 100)
              else Color.Green,
          startAngle = 0F,
          sweepAngle = 360F,
          useCenter = false,
          style = Stroke(stroke.toFloat(), cap = StrokeCap.Round),
          size = size)

      // Drawing the arc
      drawArc(
          color =
              if (currentTime.toFloat() / maxTime.toFloat() < criticalRate) criticalColor
              else fullColor,
          startAngle = -90f,
          sweepAngle = angle,
          useCenter = false,
          style = Stroke(stroke.toFloat(), cap = StrokeCap.Round),
          size = size)

      // Centering the text within the arc
      drawIntoCanvas { canvas ->
        val paint =
            android.graphics.Paint().apply {
              color = android.graphics.Color.BLACK
              textAlign = android.graphics.Paint.Align.CENTER
              textSize = 70f
              isAntiAlias = true // Enable anti-aliasing
            }
        if (isFinished.not()) {
          canvas.nativeCanvas.drawText(
              if (isCountDownTime.not()) currentTime_str else countDownCurrentValue.toString(),
              width / 2,
              height / 2 + 32f, // Centering text along the circle’s vertical axis
              paint)
        } else {
          onFinish()
        }
      }
    }
  }
}

/**
 * Converts a time string in the format "MM:SS" to the total number of seconds.
 *
 * @param currentTimeStr The time string in the format "MM:SS".
 * @return The total number of seconds.
 */
fun convertTimeToSeconds(currentTimeStr: String): Int {
  val parts = currentTimeStr.split(":")
  val minutes = parts[0].toInt()
  val seconds = parts[1].toInt()
  return minutes * 60 + seconds
}
/**
 * Converts a total number of seconds to a time string in the format "MM:SS".
 *
 * @param seconds The total number of seconds.
 * @return The time string in the format "MM:SS".
 */
fun convertSecondsToTime(seconds: Int): String {
  val minutes = seconds / 60
  val remainingSeconds = seconds % 60
  return String.format("%02d:%02d", minutes, remainingSeconds)
}
