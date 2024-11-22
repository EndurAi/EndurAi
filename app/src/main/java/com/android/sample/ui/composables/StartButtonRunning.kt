package com.android.sample.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StartButton(onClick: () -> Unit, title: String, showIcon: Boolean) {
  Button(
      onClick = onClick,
      modifier =
          Modifier.padding(8.dp).shadow(4.dp, shape = RoundedCornerShape(50.dp)).height(50.dp).testTag(title + "Button"),
      colors =
          ButtonDefaults.buttonColors(
              containerColor = Color(0xFF7D83AE)
              ),
      shape = RoundedCornerShape(50.dp)
      ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.width(100.dp)) {
              Text(
                  text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
              if (showIcon) {

                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Arrow",
                    tint = Color.White)
              }
            }
      }
}
