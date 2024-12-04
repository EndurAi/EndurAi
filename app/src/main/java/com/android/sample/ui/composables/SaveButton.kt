package com.android.sample.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.sample.ui.theme.Dimensions
import com.android.sample.ui.theme.FontSizes
import com.android.sample.ui.theme.greenGradient
import com.android.sample.ui.workout.LeafShape

@Composable
fun SaveButton(onSaveClick: () -> Unit, testTag: String) {

  Button(
      onClick = onSaveClick,
      modifier =
          Modifier.width(Dimensions.ButtonWidth)
              .height(Dimensions.ButtonHeight)
              .padding(16.dp)
              .background(brush = greenGradient, shape = LeafShape)
              .testTag(testTag),
      colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
      shape = LeafShape,
      contentPadding = PaddingValues()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Save", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Save",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = FontSizes.SubtitleFontSize,
            )
          }
        }
      }
}
