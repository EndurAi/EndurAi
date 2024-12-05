package com.android.sample.ui.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.ui.navigation.NavigationActions

@Composable
fun ArrowBack(navigationActions: NavigationActions, color: Color = Color.Black) {
  IconButton(
      onClick = { navigationActions.goBack() }, modifier = Modifier.testTag("ArrowBackButton")) {
        Icon(
            imageVector = Icons.Outlined.ArrowBack,
            contentDescription = "Back",
            tint = color,
            modifier = Modifier.size(30.dp))
      }
}
