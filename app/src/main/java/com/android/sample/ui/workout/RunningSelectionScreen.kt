package com.android.sample.ui.workout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.Black
import com.android.sample.ui.theme.Blue
import com.android.sample.ui.theme.NeutralGrey

@Composable
fun RunningSelectionScreen(navigationActions: NavigationActions) {
  val buttonColor = ButtonDefaults.buttonColors(Blue)
  Scaffold(
      modifier = Modifier.testTag("RunningSelectionScreen"),
      topBar = { TopBar(navigationActions, R.string.RunningWorkoutTitle) },
      content = { pd ->
        Column(
            modifier = Modifier.fillMaxSize().padding(pd),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              // Bouton "Without Path"
              Button(
                  onClick = { /* Action pour Without Path */},
                  modifier =
                      Modifier.fillMaxWidth(0.7f)
                          .padding(vertical = 8.dp)
                          .height(70.dp)
                          .testTag("withoutPathButton")
                          .shadow(8.dp, RoundedCornerShape(40.dp)),
                  colors = buttonColor,
                  shape = RoundedCornerShape(40.dp)) {
                    Text(text = "Without Path", fontSize = 16.sp, color = Black)
                  }

              InterButtonSpacer()

              // Bouton "Create New Path"
              Button(
                  onClick = { /* Action pour Create New Path */},
                  modifier =
                      Modifier.fillMaxWidth(0.7f)
                          .padding(vertical = 8.dp)
                          .height(70.dp)
                          .testTag("createNewPathButton")
                          .shadow(8.dp, RoundedCornerShape(40.dp)),
                  colors = buttonColor,
                  shape = RoundedCornerShape(40.dp)) {
                    Text(text = "Create New Path", fontSize = 16.sp, color = Black)
                  }

              InterButtonSpacer()

              // Bouton "Load Path"
              Button(
                  onClick = { /* Action pour Load Path */},
                  modifier =
                      Modifier.fillMaxWidth(0.7f)
                          .padding(vertical = 8.dp)
                          .height(70.dp)
                          .testTag("loadPathButton")
                          .shadow(8.dp, RoundedCornerShape(40.dp)),
                  colors = buttonColor,
                  shape = RoundedCornerShape(40.dp)) {
                    Text(text = "Load Path", fontSize = 16.sp, color = Black)
                  }

              // Image de silhouette d'un coureur
              Box(
                  modifier = Modifier.fillMaxSize(0.75f).padding(bottom = 16.dp),
                  contentAlignment = Alignment.BottomCenter) {
                    Image(
                        painter = painterResource(id = R.drawable.running_man),
                        contentDescription = "Running Silhouette",
                        modifier = Modifier.fillMaxSize(0.8f).testTag("runningSilhouette"),
                        colorFilter =
                            ColorFilter.tint(
                                NeutralGrey.copy(alpha = 0.5f)) // Applique une teinte gris clair
                        )
                  }
            }
      })
}

@Composable
fun InterButtonSpacer() {
  Spacer(modifier = Modifier.height(35.dp).testTag("interButtonSpacer"))
}
