package com.android.sample.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.ui.theme.LegendBodyweight
import com.android.sample.ui.theme.LegendRunning
import com.android.sample.ui.theme.LegendYoga
import com.android.sample.ui.theme.OpenSans

/**
 * Displays an individual legend item with a colored background and text.
 *
 * @param color The color for the legend item.
 * @param text The text description for the workout type.
 */
@Composable
fun LegendItem(color: Color, text: String) {
  val shape =
      RoundedCornerShape(topStart = 15.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 15.dp)
  Box(
      modifier =
          Modifier.testTag("legendItem")
              .shadow(4.dp, shape = shape)
              .background(color, shape = shape)
              .padding(horizontal = 12.dp, vertical = 4.dp)) {
        Text(text = text, color = Color.Black, fontSize = 20.sp, fontFamily = OpenSans)
      }
}

/** Displays a row of workout type legends (e.g., Bodyweight, Yoga, Running). */
@Composable
fun Legend() {
  Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("Categories"),
      horizontalArrangement = Arrangement.SpaceEvenly) {
        LegendItem(LegendBodyweight, stringResource(R.string.TitleTabBody))
        LegendItem(LegendYoga, stringResource(R.string.TitleTabYoga))
        LegendItem(LegendRunning, stringResource(R.string.TitleTabRunning))
      }
}
