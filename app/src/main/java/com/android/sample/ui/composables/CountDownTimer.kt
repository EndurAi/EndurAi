package com.android.sample.ui.composables

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.drawText

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.sample.R
import kotlin.math.cos
import kotlin.math.sin
@Composable
@Preview
fun CountDownTimer(
  currentTime_str: String = "00:02",
  maxTime_str: String ="00:03",
  fullColor: Color = Color.Green,
  criticalColor: Color = Color.Red,
  criticalRate: Float = 0.1f,
  stroke: Int = 28,
  arcSize: Float = 500f,
  onFinishedText : String ="Done",
  onFinishedIcon : ImageVector= Icons.Filled.Done
) {
  Canvas(modifier = Modifier.fillMaxSize()) {
    val currentTime = convertTimeToSeconds(currentTime_str)
    val maxTime = convertTimeToSeconds(maxTime_str)

    val width = size.width
    val height = size.height

    val angle = (currentTime.toFloat() / maxTime.toFloat()) * 360f

    // Drawing the small black circle at the center
    drawCircle(
      color = if(currentTime >0) Color.Black else Color.Green,
      radius = (arcSize / 2) * 0.94F, // Radius of the small circle; adjust this as needed
      center = Offset(width / 2, height / 2),
      style = Stroke(width = 5F)
    )

    // Drawing the arc
    drawArc(
      color = if (currentTime.toFloat() / maxTime.toFloat() < criticalRate) criticalColor else fullColor,
      topLeft = Offset((width - arcSize) / 2, (height - arcSize) / 2),
      startAngle = -90f,
      sweepAngle = angle,
      useCenter = false,
      style = Stroke(stroke.toFloat(), cap = StrokeCap.Round),
      size = Size(arcSize, arcSize)
    )

    // Centering the text within the arc
    drawIntoCanvas { canvas ->
      val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        textAlign = android.graphics.Paint.Align.CENTER
        textSize = 120f
        isAntiAlias = true // Enable anti-aliasing
      }
      if(currentTime > 0) {
        canvas.nativeCanvas.drawText(
          currentTime_str,
          width / 2,
          height / 2 + 32f, // Centering text along the circle’s vertical axis
          paint
        )
      }
      else{

        val checkStartX = width / 2 - 60f
        val checkStartY = height / 2 - 80f // Moved UPWARDS
        val checkLineLength = 80f

        val checkPaint = android.graphics.Paint().apply {
          color = android.graphics.Color.GREEN
          strokeWidth = 23f
          strokeCap = android.graphics.Paint.Cap.ROUND
          isAntiAlias = true
        }

        canvas.nativeCanvas.drawLine(
          checkStartX ,
          checkStartY + 30f,
          checkStartX + checkLineLength * 0.6f,
          checkStartY + checkLineLength+45,
          checkPaint
        )
        canvas.nativeCanvas.drawLine(
          checkStartX + checkLineLength * 0.6f,
          checkStartY + checkLineLength+50,
          checkStartX + checkLineLength + 60f,
          checkStartY - checkLineLength * 0.7f,
          checkPaint
        )

        // Draw the "Done!" text BELOW the check mark
        canvas.nativeCanvas.drawText(
          onFinishedText,
          width / 2,
          height / 2 + 150f, // Moved DOWNWARDS
          paint
        )




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