package com.android.sample.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.ui.navigation.LIST_OF_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.BottomBarColor

@Composable
fun BottomBar(
    navigationActions: NavigationActions,
) {

  BottomAppBar(
      modifier = Modifier.testTag("BottomBar"),
      containerColor = BottomBarColor,
      contentColor = Color.White,
      tonalElevation = 8.dp) {
        LIST_OF_TOP_LEVEL_DESTINATIONS.forEach { destination ->
          Column(
              modifier = Modifier.weight(1f).padding(vertical = 8.dp),
              horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { navigationActions.navigateTo(destination.route) }) {
                  Icon(
                      imageVector = destination.icon,
                      contentDescription = destination.textId,
                      tint = Color.White,
                      modifier = Modifier.size(30.dp))
                }
                Text(
                    text = destination.textId,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp))
              }
        }
      }
}
