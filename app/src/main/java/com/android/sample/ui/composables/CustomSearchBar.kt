package com.android.sample.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomSearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
  Box(
      modifier =
          modifier.clip(RoundedCornerShape(24.dp)).background(Color(0xFFF0F0F0)).padding(8.dp)) {
        Row {
          Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search Icon",
              tint = Color.Gray)
          BasicTextField(
              value = query,
              onValueChange = onQueryChange,
              modifier = Modifier.fillMaxWidth(),
              decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                  androidx.compose.material3.Text(
                      "Search", color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
                }
                innerTextField()
              })
        }
      }
}
