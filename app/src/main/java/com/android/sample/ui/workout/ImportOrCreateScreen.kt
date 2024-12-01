package com.android.sample.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.workout.WorkoutType
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.BlueGradient
import com.android.sample.ui.theme.Dimensions
import com.android.sample.ui.theme.FontSizes
import com.android.sample.ui.theme.LightBackground
import com.android.sample.ui.theme.Transparent
import com.android.sample.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportOrCreateScreen(navigationActions: NavigationActions, workoutType: WorkoutType) {
  Scaffold(
      topBar = { TopBar(navigationActions, R.string.NewWorkout) },
      containerColor = LightBackground,
      modifier = Modifier.testTag("ImportOrCreateScreen")) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Dimensions.LargePadding, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Center the content vertically
            ) {
              Spacer(modifier = Modifier.weight(1f)) // Center the text vertically

              Text(
                  text =
                      "Do you want to create a new program from scratch or from an existing one?",
                  style =
                      TextStyle(fontSize = FontSizes.TitleFontSize, fontWeight = FontWeight.Bold),
                  modifier = Modifier.padding(bottom = 32.dp).align(Alignment.CenterHorizontally),
                  textAlign = TextAlign.Center)

              Spacer(modifier = Modifier.weight(1f)) // Center the buttons

              // Import Button
              Button(
                  onClick = {
                    when (workoutType) {
                      WorkoutType.BODY_WEIGHT ->
                          navigationActions.navigateTo(Screen.CHOOSE_BODYWEIGHT)
                      WorkoutType.YOGA -> navigationActions.navigateTo(Screen.CHOOSE_YOGA)
                      WorkoutType.RUNNING -> TODO()
                      WorkoutType.WARMUP -> TODO()
                    }
                  },
                  shape = LeafShape,
                  colors = ButtonDefaults.buttonColors(containerColor = Transparent),
                  contentPadding = PaddingValues(),
                  modifier =
                      Modifier.width(Dimensions.ButtonWidth)
                          .padding(vertical = Dimensions.SmallPadding)
                          .height(Dimensions.ButtonHeight)
                          .background(brush = BlueGradient, shape = LeafShape)) {
                    Box(
                        modifier =
                            Modifier.fillMaxSize()
                                .background(
                                    brush = BlueGradient, shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center) {
                          Text(
                              text = "Import",
                              color = White,
                              fontSize = FontSizes.SubtitleFontSize,
                              fontWeight = FontWeight.Bold)
                        }
                  }

              // Create from Scratch Button
              Button(
                  onClick = {
                    when (workoutType) {
                      WorkoutType.BODY_WEIGHT ->
                          navigationActions.navigateTo(Screen.BODY_WEIGHT_CREATION)
                      WorkoutType.YOGA -> navigationActions.navigateTo(Screen.YOGA_CREATION)
                      WorkoutType.RUNNING -> TODO()
                      WorkoutType.WARMUP -> TODO()
                    }
                  },
                  shape = LeafShape,
                  colors = ButtonDefaults.buttonColors(containerColor = Transparent),
                  contentPadding = PaddingValues(),
                  modifier =
                      Modifier.width(Dimensions.ButtonWidth)
                          .padding(vertical = Dimensions.SmallPadding)
                          .height(Dimensions.ButtonHeight)
                          .background(brush = BlueGradient, shape = LeafShape)) {
                    Box(
                        modifier =
                            Modifier.fillMaxSize()
                                .background(
                                    brush = BlueGradient, shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center) {
                          Text(
                              text = "Create from scratch",
                              color = White,
                              fontSize = FontSizes.SubtitleFontSize,
                              fontWeight = FontWeight.Bold)
                        }
                  }

              Spacer(modifier = Modifier.weight(0.5f)) // Center the buttons
        }
      }
}

val LeafShape: CornerBasedShape =
    RoundedCornerShape(
        topStart = CornerSize(20.dp),
        topEnd = CornerSize(10.dp),
        bottomStart = CornerSize(10.dp),
        bottomEnd = CornerSize(20.dp))
