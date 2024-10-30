package com.android.sample.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.drawText

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
@Composable
@Preview
fun CountDownTimer(
  currentTime_str: String = "01:23",
  maxTime_str: String = "02:36",
  fullColor: Color = Color.Green,
  criticalColor: Color = Color.Red,
  criticalRate: Float = 0.1f,
  stroke: Int = 25,
  arcSize: Float = 500f
) {
  Canvas(modifier = Modifier.fillMaxSize()) {
    val currentTime = convertTimeToSeconds(currentTime_str)
    val maxTime = convertTimeToSeconds(maxTime_str)

    val width = size.width
    val height = size.height

    val angle = (currentTime.toFloat() / maxTime.toFloat()) * 360f

    drawContext.canvas.nativeCanvas.apply {
      // Drawing the arc
      drawArc(
        color = if (currentTime.toFloat() / maxTime.toFloat() < criticalRate) criticalColor else fullColor,
        topLeft = Offset((width - arcSize) / 2, (height - arcSize) / 2),
        startAngle = -90f,
        sweepAngle = angle,
        useCenter = false,
        style = Stroke(stroke.toFloat()),
        size = Size(arcSize, arcSize)
      )

      // Centering the text within the arc
      drawText(
        currentTime_str,
        width / 2,
        height / 2 + 32f, // Centering text along the circle’s vertical axis
        android.graphics.Paint().apply {
          color = android.graphics.Color.BLACK
          textAlign = android.graphics.Paint.Align.CENTER
          textSize = 64f
        }
      )
    }

    // Drawing the small black circle at the center
    drawCircle(
      color = Color.Black,
      radius = 15f, // Radius of the small circle; adjust this as needed
      center = Offset(width / 2, height / 2)
    )
  }
}
