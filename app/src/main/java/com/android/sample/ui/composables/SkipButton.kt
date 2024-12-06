package com.android.sample.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.sample.ui.theme.FontSizes.SubtitleFontSize
import com.android.sample.ui.theme.Orange
import com.android.sample.ui.theme.Transparent

@Composable
fun SkipButton(onClick: () -> Unit) {
  Button(
      onClick = onClick,
      colors = ButtonDefaults.buttonColors(containerColor = Transparent),
      border = BorderStroke(2.dp, Orange),
      modifier = Modifier.testTag("SkipButton").size(width = 120.dp, height = 50.dp)) {
        Text("Skip", color = Orange, fontWeight = FontWeight.SemiBold, fontSize = SubtitleFontSize)
      }
}
