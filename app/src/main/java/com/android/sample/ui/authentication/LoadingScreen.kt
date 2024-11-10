package com.android.sample.ui.authentication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun LoadingScreen() {
  Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.fillMaxSize().testTag("loadingScreen")) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
      }
}
