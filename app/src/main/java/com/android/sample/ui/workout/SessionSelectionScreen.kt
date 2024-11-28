package com.android.sample.ui.workout

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Dimensions
import com.android.sample.ui.theme.FontSizes
import com.android.sample.ui.theme.LightBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionSelectionScreen(navigationActions: NavigationActions) {
  Scaffold(
      topBar = { TopBar(navigationActions, R.string.NewWorkout) },
      content = { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize().padding(16.dp).testTag("sessionSelectionScreen")) {
              items(sessionItems) { session ->
                SessionCard(session) { selectedSession ->
                  when (selectedSession.title) {
                    "Body weight" -> navigationActions.navigateTo(Screen.IMPORTORCREATE_BODY_WEIGHT)
                    "Running" -> navigationActions.navigateTo(Screen.IMPORTORCREATE_RUNNING)
                    "Yoga" -> navigationActions.navigateTo(Screen.IMPORTORCREATE_YOGA)
                  }
                }
              }
            }
      },
      containerColor = LightBackground)
}

@Composable
fun SessionCard(session: Session, onSessionClick: (Session) -> Unit) {
  Card(
      modifier =
          Modifier.clickable { onSessionClick(session) }
              .fillMaxWidth()
              .height(180.dp)
              .padding(horizontal = Dimensions.SmallPadding)
              .shadow(8.dp, RoundedCornerShape(12.dp)) // Add shadow here
              .testTag("sessionCard_${session.title}"),
      shape = RoundedCornerShape(12.dp), // Updated the corner radius for consistency with Figma
      colors =
          CardDefaults.cardColors(
              containerColor = Color.Transparent), // Make the card background transparent
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      Image(
          painter = painterResource(id = session.imageRes),
          contentDescription = session.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize().testTag("image_${session.title}"))
      Text(
          text = session.title,
          style =
              MaterialTheme.typography.bodyLarge.copy(
                  fontSize = FontSizes.SubtitleFontSize,
                  fontWeight = FontWeight.ExtraBold,
                  shadow =
                      Shadow(
                          color = Color.Black,
                          offset = Offset(4f, 4f),
                          blurRadius = 6f
                          )),
          color = Color.White,
          modifier = Modifier.align(Alignment.BottomStart).padding(Dimensions.SmallPadding))
    }
  }
}

data class Session(val title: String, val imageRes: Int)

val sessionItems =
    listOf(
        Session("Body weight", R.drawable.body_weight_image),
        Session("Running", R.drawable.running_image),
        Session("Yoga", R.drawable.yoga_image))
