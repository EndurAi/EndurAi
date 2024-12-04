package com.android.sample.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.workout.Exercise
import com.android.sample.model.workout.ExerciseDetail
import com.android.sample.ui.theme.DarkerBlue
import com.android.sample.ui.theme.LightBlue2
import com.android.sample.ui.theme.Purple20
import com.android.sample.ui.theme.White

/**
 * Displays a card representing an exercise with its name and details.
 *
 * @param exercise the [Exercise] data to display in the card.
 */
@Composable
fun ExerciseCard(
    exercise: Exercise,
    onCardClick: () -> Unit,
    onDetailClick: () -> Unit,
    innerColor: Color = DarkerBlue,
    textToDisplay: String = "",
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
    // Vertical line connecting the cards
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)) {
          Box(modifier = Modifier.size(8.dp).background(Purple20, shape = CircleShape))
          Spacer(modifier = Modifier.height(16.dp).width(2.dp).background(Purple20))
        }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightBlue2), // Blue color
        modifier =
            Modifier.fillMaxWidth(0.9f)
                .testTag("exerciseCard")
                .padding(horizontal = 16.dp)
                .clickable { onCardClick() }) {
          Row(
              modifier = Modifier.padding(16.dp).fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically) {
                // Exercise name (on the left)
                Text(
                    text = exercise.type.toString(),
                    fontSize = 16.sp,
                    color = White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start)

                // Exercise details (icon and information)
                ExerciseDetailCard(
                    exercise.detail,
                    onClick = onDetailClick,
                    textToDisplay = textToDisplay,
                    cardColor = innerColor,
                    modifier = innerModifier)
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
fun ExerciseDetailCard(
    detail: ExerciseDetail,
    onClick: () -> Unit,
    cardColor: Color = DarkerBlue,
    textToDisplay: String = "",
    modifier: Modifier = Modifier
) {
  Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = cardColor), // Darker blue color
      modifier =
          Modifier.padding(start = 8.dp).testTag("detailCard").wrapContentSize().clickable {
            onClick()
          }) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically) {
              when (detail) {
                is ExerciseDetail.TimeBased -> {
                  Icon(
                      painter = painterResource(id = R.drawable.pace),
                      contentDescription = "Time Based",
                      modifier = Modifier.size(20.dp),
                      tint = White // White icon
                      )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                      text =
                          if (textToDisplay.isBlank())
                              "${formatTime(detail.durationInSeconds)} X ${detail.sets}"
                          else textToDisplay,
                      fontSize = 14.sp,
                      color = White, // Black text
                      fontWeight = FontWeight.Bold,
                      modifier = modifier)
                }
                is ExerciseDetail.RepetitionBased -> {
                  Icon(
                      painter = painterResource(id = R.drawable.timeline),
                      contentDescription = "Repetition Based",
                      modifier = Modifier.size(20.dp),
                      tint = White // White icon
                      )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                      text =
                          if (textToDisplay.isBlank()) "X ${detail.repetitions}" else textToDisplay,
                      fontSize = 14.sp,
                      color = White, // White text
                      fontWeight = FontWeight.Bold,
                      modifier = modifier)
                }
              }
            }
      }
}

fun formatTime(time: Int): String {
  val minutes = time / 60
  val seconds = time % 60
  val secondSimbol = "s"
  val minuteSimbol = "m"
  return if (minutes == 0 && seconds > 0) {
    "${seconds}${secondSimbol}"
  } else if (seconds == 0 && minutes > 0) {
    "${minutes}${minuteSimbol}"
  } else if (minutes == 0 && seconds == 0) {
    "0${minuteSimbol}0${secondSimbol}"
  } else {
    "${minutes}${minuteSimbol}${seconds}${secondSimbol}"
  }
}
