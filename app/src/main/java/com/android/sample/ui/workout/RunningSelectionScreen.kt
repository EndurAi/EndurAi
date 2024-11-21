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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions

@Composable
fun RunningSelectionScreen(navigationActions: NavigationActions) {
    val buttonColor = ButtonDefaults.buttonColors(Color.Blue)
    Scaffold(
        modifier = Modifier.testTag("RunningSelectionScreen"),
        topBar = {
            TopBar(navigationActions, R.string.RunningWorkoutTitle)
        },
        content = { pd ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pd),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Bouton "Without Path"
                Button(
                    onClick = { /* Action pour Without Path */ },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(vertical = 8.dp)
                        .height(50.dp)
                        .testTag("withoutPathButton"),
                    colors = buttonColor,
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(text = "Without Path", fontSize = 16.sp)
                }

                // Bouton "Create New Path"
                Button(
                    onClick = { /* Action pour Create New Path */ },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(vertical = 8.dp)
                        .height(50.dp)
                        .testTag("createNewPathButton"),
                    colors = buttonColor,
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(text = "Create New Path", fontSize = 16.sp)
                }

                // Bouton "Load Path"
                Button(
                    onClick = { /* Action pour Load Path */ },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(vertical = 8.dp)
                        .height(50.dp)
                        .testTag("loadPathButton"),
                    colors = buttonColor,
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(text = "Load Path", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Image de silhouette d'un coureur
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.running_man),
                        contentDescription = "Running Silhouette",
                        modifier = Modifier.fillMaxSize(0.5f).testTag("runningSilhouette"),
                        colorFilter = ColorFilter.tint(Color.Gray.copy(alpha = 0.5f)) // Applique une teinte gris clair
                    )
                }
            }
        }

    )
}