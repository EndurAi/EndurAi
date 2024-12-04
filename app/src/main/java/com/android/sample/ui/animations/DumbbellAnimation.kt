package com.android.sample.ui.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DumbbellAnimation(modifier: Modifier = Modifier) {
  // Animation states: sliding, pause, fading
  val transitionState = rememberInfiniteTransition()

  // Sliding animation for left weights
  val leftOffset by
      transitionState.animateFloat(
          initialValue = -150f,
          targetValue = -20f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(durationMillis = 1000, easing = LinearEasing),
                  repeatMode = RepeatMode.Restart),
          label = "")

  // Sliding animation for right weights
  val rightOffset by
      transitionState.animateFloat(
          initialValue = 150f,
          targetValue = 20f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(durationMillis = 1000, easing = LinearEasing),
                  repeatMode = RepeatMode.Restart),
          label = "")

  // Fade out effect (sequential after sliding stops)
  val alpha by
      transitionState.animateFloat(
          initialValue = 1f,
          targetValue = 0f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(durationMillis = 600, delayMillis = 400, easing = LinearEasing),
                  repeatMode = RepeatMode.Restart),
          label = "")

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Canvas(modifier = Modifier.size(300.dp)) {
      // Draw the dumbbell bar
      drawLine(
          color = Color.DarkGray.copy(alpha = alpha),
          start = center.copy(x = center.x - 100.dp.toPx(), y = center.y),
          end = center.copy(x = center.x + 100.dp.toPx(), y = center.y),
          strokeWidth = 8.dp.toPx())

      // Increased heights of the weights (shortest near extremities, tallest near center)
      val weightHeights = listOf(80f, 120f, 200f) // Increased the tallest weight
      val weightSizes = listOf(20f, 30f, 40f) // Widths of the weights

      // Draw left weights
      for (i in weightSizes.indices) {
        drawRoundRect(
            color = Color.Black.copy(alpha = alpha),
            size =
                androidx.compose.ui.geometry.Size(
                    width = weightSizes[i], height = weightHeights[i]),
            topLeft =
                center.copy(
                    x =
                        center.x - 100.dp.toPx() +
                            (i * 20.dp.toPx()) +
                            leftOffset, // Adjusted spacing
                    y = center.y - (weightHeights[i] / 2)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()))
      }

      // Draw right weights (mirroring the left side)
      for (i in weightSizes.indices) {
        drawRoundRect(
            color = Color.Black.copy(alpha = alpha),
            size =
                androidx.compose.ui.geometry.Size(
                    width = weightSizes[i], height = weightHeights[i]),
            topLeft =
                center.copy(
                    x =
                        center.x + 100.dp.toPx() - (i * 20.dp.toPx()) +
                            rightOffset, // Adjusted spacing
                    y = center.y - (weightHeights[i] / 2)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()))
      }
    }
  }
}
