package com.android.sample.ui.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.workout.WorkoutType
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportOrCreateScreen(navigationActions: NavigationActions, workoutType: WorkoutType) {
  Scaffold(
      topBar = { TopBar(navigationActions, R.string.new_session) },
      modifier = Modifier.testTag("ImportOrCreateScreen")) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              Text(
                  text =
                      "Do you want to create a new program from scratch or from an existing one?",
                  style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                  modifier = Modifier.padding(bottom = 24.dp).align(Alignment.CenterHorizontally))

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
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9CBEC8)),
                  modifier = Modifier.fillMaxWidth().padding(8.dp).height(60.dp)) {
                    Text(text = "Import", color = Color.Black, fontSize = 16.sp)
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
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9CBEC8)),
                  modifier = Modifier.fillMaxWidth().padding(8.dp).height(60.dp)) {
                    Text(text = "Create from scratch", color = Color.Black, fontSize = 16.sp)
                  }
            }
      }
}
