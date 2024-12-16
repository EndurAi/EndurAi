package com.android.sample.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TextDialog(message: String) {
  Surface(
      modifier = Modifier.padding(16.dp),
      color = Color(0xFF6C63FF), // Purple card
      shape = RoundedCornerShape(12.dp)) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium)
      }
}
