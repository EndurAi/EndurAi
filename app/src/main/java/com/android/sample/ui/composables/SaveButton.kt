package com.android.sample.ui.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun SaveButton(onSaveClick: () -> Unit, testTag: String) {
  Button(
      onClick = onSaveClick,
      modifier = Modifier.fillMaxWidth().testTag(testTag).padding(16.dp),
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
      shape = RoundedCornerShape(8.dp)) {
        Icon(imageVector = Icons.Default.Check, contentDescription = "Save", tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Save", color = Color.White)
      }
}
