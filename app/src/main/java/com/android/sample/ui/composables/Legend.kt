package com.android.sample.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.ui.theme.BodyWeightTag
import com.android.sample.ui.theme.RunningTag
import com.android.sample.ui.theme.YogaTag

/**
 * Displays an individual legend item with a colored background and text.
 *
 * @param color The color for the legend item.
 * @param text The text description for the workout type.
 */
@Composable
fun LegendItem(color: Color, text: String) {
  Box(
      modifier =
          Modifier.background(color, shape = MaterialTheme.shapes.medium)
              .padding(horizontal = 12.dp, vertical = 4.dp)) {
        Text(text = text, color = Color.Black, fontWeight = FontWeight.Bold)
      }
}

/** Displays a row of workout type legends (e.g., Bodyweight, Yoga, Running). */
@Composable
fun Legend() {
  Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("Categories"),
      horizontalArrangement = Arrangement.SpaceEvenly) {
        LegendItem(BodyWeightTag, stringResource(R.string.TitleTabBody))
        LegendItem(YogaTag, stringResource(R.string.TitleTabYoga))
        LegendItem(RunningTag, stringResource(R.string.TitleTabRunning))
      }
}
