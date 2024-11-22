package com.android.sample.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun distanceDisplay(distance: Double) {
  Column(
      horizontalAlignment = Alignment.Start, // Align "Distance" to the start
      modifier = Modifier.padding(16.dp)
      ) {
        Text(
            text = "Distance",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 20.sp),
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
            )
        Box(
            modifier =
                Modifier.width(300.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEAF0FF))
                    .border(BorderStroke(1.dp, Color.LightGray), shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
              Text(
                  text = String.format("%.2f km", distance),
                  style =
                      MaterialTheme.typography.bodyLarge.copy(
                          fontSize = 36.sp
                          ),
                  color = Color.Black,
                  modifier = Modifier.align(Alignment.Center)
                  )
            }
      }
}

@Composable
fun chronoDisplay(elapsedTime: Long) {
  Column(
      horizontalAlignment = Alignment.Start, // Align "Time" to the start
      modifier = Modifier.padding(16.dp)
      ) {
        Text(
            text = "Time",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 20.sp),
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
            )
        Box(
            modifier =
                Modifier.width(300.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEAF0FF))
                    .border(BorderStroke(1.dp, Color.LightGray), shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
              Text(
                  text = formatElapsedTime(elapsedTime),
                  style =
                      MaterialTheme.typography.bodyLarge.copy(
                          fontSize = 36.sp
                          ),
                  color = Color.Black,
                  modifier = Modifier.align(Alignment.Center)
                  )
            }
      }
}

@Composable
fun paceDisplay(paceString: String) {
  Column(
      horizontalAlignment = Alignment.Start, // Align "Pace" to the start
      modifier = Modifier.padding(16.dp)
      ) {
        Text(
            text = "Pace",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 20.sp),
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
            )
        Box(
            modifier =
                Modifier.width(300.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEAF0FF))
                    .border(BorderStroke(1.dp, Color.LightGray), shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
              Text(
                  text = paceString,
                  style =
                      MaterialTheme.typography.bodyLarge.copy(
                          fontSize = 36.sp
                          ),
                  color = Color.Black,
                  modifier = Modifier.align(Alignment.Center)
                  )
            }
      }
}

fun formatElapsedTime(elapsedTime: Long): String {
  val hours = elapsedTime / 3600
  val minutes = (elapsedTime % 3600) / 60
  val seconds = elapsedTime % 60
  return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
