package com.android.sample.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.ui.theme.BlueGradient
import com.android.sample.ui.theme.ContrailOne
import com.android.sample.ui.theme.RunningGrey
import com.android.sample.ui.theme.White

@Composable
fun RunningDesignButton(
    onClick: () -> Unit,
    title: String,
    showIcon: Boolean = false,
    testTag: String
) {

  Box(
      modifier =
          Modifier.size(80.dp)
              .shadow(elevation = 10.dp, shape = CircleShape, clip = false)
              .background(brush = BlueGradient, shape = CircleShape)
              .clickable(onClick = onClick)
              .testTag(testTag),
      contentAlignment = Alignment.Center) {
        Text(
            text = title,
            color = White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = ContrailOne)
      }
}

@Composable
fun RunningDesignButtonGrey(
    onClick: () -> Unit,
    title: String,
    showIcon: Boolean = false,
    testTag: String
) {

  Box(
      modifier =
          Modifier.size(80.dp)
              .shadow(elevation = 10.dp, shape = CircleShape, clip = false)
              .background(color = RunningGrey, shape = CircleShape)
              .clickable(onClick = onClick)
              .testTag(testTag),
      contentAlignment = Alignment.Center) {
        Text(
            text = title,
            color = White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = ContrailOne)
      }
}
