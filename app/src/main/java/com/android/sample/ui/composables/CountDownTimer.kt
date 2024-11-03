package com.android.sample.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.drawText

@Composable
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
    modifier: Modifier,
    isPaused: Boolean = false
) {

  val currentTime_str = convertSecondsToTime(currentTime_int)
  val maxTime_str = convertSecondsToTime(maxTime_int)

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
            textSize = 90f
            isAntiAlias = true // Enable anti-aliasing
          }
      if (currentTime > 0) {
        canvas.nativeCanvas.drawText(
            currentTime_str,
            width / 2,
            height / 2 + 32f, // Centering text along the circle’s vertical axis
            paint)

        if (isPaused) {
          val path =
              android.graphics.Path().apply {
                moveTo(width / 2, height / 2 - (50f / 3)) // 3x smaller Y
                lineTo(width / 2 - (50f / 3), height / 2 + (50f / 3)) // 3x smaller X and Y
                lineTo(width / 2 + (50f / 3), height / 2 + (50f / 3)) // 3x smaller X and Y
                close()
              }

          // Rotate the canvas before drawing the path
          canvas.save()
          canvas.translate(0f, 80f)
          canvas.rotate(90f, width / 2, height / 2) // Rotate by 90 degrees around the center

          // Translate the canvas downwards
          // Adjust the value (50f) to move it further down

          canvas.nativeCanvas.drawPath(path, paint.apply { color = android.graphics.Color.BLACK })
          canvas.restore() // Restore the canvas to its original state
        }
      } else {

        val checkStartX = width / 2 - 60f
        val checkStartY = height / 2 - 80f // Moved UPWARDS
        val checkLineLength = 80f

        val checkPaint =
            android.graphics.Paint().apply {
              color = android.graphics.Color.GREEN
              strokeWidth = 23f
              strokeCap = android.graphics.Paint.Cap.ROUND
              isAntiAlias = true
            }

        canvas.nativeCanvas.drawLine(
            checkStartX,
            checkStartY + 30f,
            checkStartX + checkLineLength * 0.6f - 2F,
            checkStartY + checkLineLength + 45,
            checkPaint)
        canvas.nativeCanvas.drawLine(
            checkStartX + checkLineLength * 0.6f,
            checkStartY + checkLineLength + 50,
            checkStartX + checkLineLength + 60f,
            checkStartY - checkLineLength * 0.7f,
            checkPaint)

        // Draw the "Done!" text BELOW the check mark
        canvas.nativeCanvas.drawText(
            onFinishedText,
            width / 2,
            height / 2 + 150f, // Moved DOWNWARDS
            paint)
      }
    }
  }
}

fun convertTimeToSeconds(currentTimeStr: String): Int {
  val parts = currentTimeStr.split(":")
  val minutes = parts[0].toInt()
  val seconds = parts[1].toInt()
  return minutes * 60 + seconds
}

fun convertSecondsToTime(seconds: Int): String {
  val minutes = seconds / 60
  val remainingSeconds = seconds % 60
  return String.format("%02d:%02d", minutes, remainingSeconds)
}
