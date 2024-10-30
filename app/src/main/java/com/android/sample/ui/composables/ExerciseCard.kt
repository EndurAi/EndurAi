package com.android.sample.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.workout.Exercise
import com.android.sample.model.workout.ExerciseDetail
import com.android.sample.ui.theme.Blue

/**
 * Displays a card representing an exercise with its name and details.
 *
 * @param exercise the [Exercise] data to display in the card.
 */
@Composable
fun ExerciseCard(exercise: Exercise) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth().testTag("exerciseCard")) {
        // Vertical line connecting the cards
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
              Box(modifier = Modifier.size(8.dp).background(Color(0xFF9C7EEA), shape = CircleShape))
              Spacer(modifier = Modifier.height(16.dp).width(2.dp).background(Color(0xFF9C7EEA)))
            }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFD3D3D3)), // Gray color
            modifier = Modifier.fillMaxWidth(0.9f).padding(horizontal = 16.dp)) {
              Row(
                  modifier = Modifier.padding(16.dp).fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically) {
                    // Exercise name (on the left)
                    Text(
                        text = exercise.exType.toString(),
                        fontSize = 18.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start)

                    // Exercise details (icon and information)
                    ExerciseDetailCard(exercise.exDetail)
                  }
            }
      }
}

/**
 * Displays a card with the details of an exercise, either time-based or repetition-based.
 *
 * @param detail the [ExerciseDetail] data to display in the card.
 */
@Composable
fun ExerciseDetailCard(detail: ExerciseDetail) {
  Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Blue), // Darker blue color
      modifier = Modifier.padding(start = 8.dp).wrapContentSize()) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically) {
              when (detail) {
                is ExerciseDetail.TimeBased -> {
                  Icon(
                      painter = painterResource(id = R.drawable.pace),
                      contentDescription = "Time Based",
                      modifier = Modifier.size(20.dp),
                      tint = Color.Black // Black icon
                      )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                      text = "${detail.durationInSeconds / 60}′ X ${detail.sets}",
                      fontSize = 14.sp,
                      color = Color.Black // Black text
                      )
                }
                is ExerciseDetail.RepetitionBased -> {
                  Icon(
                      painter = painterResource(id = R.drawable.timeline),
                      contentDescription = "Repetition Based",
                      modifier = Modifier.size(20.dp),
                      tint = Color.Black // Black icon
                      )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                      text = "X ${detail.repetitions}",
                      fontSize = 14.sp,
                      color = Color.Black // Black text
                      )
                }
              }
            }
      }
}
