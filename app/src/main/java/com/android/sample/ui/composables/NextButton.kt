package com.android.sample.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.sample.ui.theme.BlueGradient
import com.android.sample.ui.theme.FontSizes.SubtitleFontSize
import com.android.sample.ui.theme.Transparent
import com.android.sample.ui.theme.White
import com.android.sample.ui.workout.LeafShape

@Composable
fun NextButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    arrow: Boolean = true
) {
  Button(
      onClick = onClick,
      modifier = modifier,
      colors = ButtonDefaults.buttonColors(containerColor = Transparent),
      contentPadding = PaddingValues(),
      shape = LeafShape) {
        Box(
            modifier = Modifier.fillMaxSize().background(brush = BlueGradient, shape = LeafShape),
            contentAlignment = Alignment.Center) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = text,
                    color = White,
                    fontSize = SubtitleFontSize,
                    fontWeight = FontWeight.Bold)
                if (arrow) {
                  Icon(
                      imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                      contentDescription = "Next",
                      tint = White,
                      modifier = Modifier.padding(start = 4.dp))
                }
              }
            }
      }
}
