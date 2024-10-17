package com.android.sample.ui.workout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionSelectionScreen(navigationActions: NavigationActions) {
  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("New session") },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("sessionSelectionScreenBackButton")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                  }
            })
      },
      content = { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize().padding(16.dp).testTag("sessionSelectionScreen")) {
              items(sessionItems) { session ->
                SessionCard(session) { selectedSession ->
                  when (selectedSession.title) {
                    "Body weight" -> navigationActions.navigateTo(Screen.IMPORTORCREATE)
                    "Running" -> {}
                    "Yoga" -> navigationActions.navigateTo(Screen.IMPORTORCREATE)
                  }
                }
              }
            }
      })
}

@Composable
fun SessionCard(session: Session, onSessionClick: (Session) -> Unit) {
  Card(
      modifier =
          Modifier.clickable { onSessionClick(session) }
              .fillMaxWidth()
              .height(180.dp)
              .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
              .testTag("sessionCard_${session.title}"),
      shape = RoundedCornerShape(8.dp),
      elevation = CardDefaults.cardElevation(8.dp),
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      Image(
          painter = painterResource(id = session.imageRes),
          contentDescription = session.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize().testTag("image_${session.title}"))
      Text(
          text = session.title,
          style = MaterialTheme.typography.bodyLarge,
          color = Color.White,
          modifier =
              Modifier.align(Alignment.BottomStart)
                  .padding(8.dp)
                  .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp)))
    }
  }
}

data class Session(val title: String, val imageRes: Int)

val sessionItems =
    listOf(
        Session("Body weight", R.drawable.body_weight_image),
        Session("Running", R.drawable.running_image),
        Session("Yoga", R.drawable.yoga_image))
