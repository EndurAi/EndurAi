package com.android.sample.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Blue
import com.android.sample.ui.theme.DarkBlue
import com.google.firebase.auth.FirebaseAuth

/**
 * Composable function representing the settings screen of the application.
 *
 * @param navigationActions NavigationActions object to handle navigation events within the app.
 */
@Composable
fun SettingsScreen(navigationActions: NavigationActions) {
  val context = LocalContext.current

  Scaffold(
      modifier = Modifier.testTag("settingsScreen"),
      topBar = { TopBar(navigationActions) },
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              // User data button
              Button(
                  onClick = { /* TODO: Handle User Data */},
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(60.dp)
                          .testTag("userDataButton"), // Test tag for User Data button
                  colors = ButtonDefaults.buttonColors(containerColor = Blue)) {
                    Text("User data", color = Color.Black)
                  }

              // Preferences button
              Button(
                  onClick = { navigationActions.navigateTo(Screen.PREFERENCES) },
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(60.dp)
                          .testTag("preferencesButton"), // Test tag for Preferences button
                  colors = ButtonDefaults.buttonColors(containerColor = Blue)) {
                    Text("Preferences", color = Color.Black)
                  }

              Spacer(modifier = Modifier.height(200.dp)) // Adjust this height as needed

              // Delete account button
              Button(
                  onClick = { /* TODO: Handle Account Deletion */},
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(48.dp)
                          .testTag("deleteAccountButton")
                          .border(
                              2.dp,
                              Color.Red,
                              RoundedCornerShape(10.dp)), // Test tag for Delete Account button
                  colors =
                      ButtonDefaults.outlinedButtonColors(
                          containerColor = Color.Transparent,
                          contentColor = Color.Red,
                      )) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete Account",
                        tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete account", color = Color.Red)
                  }

              // Logout button
              Button(
                  onClick = {
                    FirebaseAuth.getInstance().signOut() // Sign out from Firebase
                    navigationActions.navigateTo("Auth Screen") // Navigate back to Auth screen
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                  },
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(48.dp)
                          .testTag("logoutButton")
                          .border(
                              2.dp,
                              Color.Red,
                              RoundedCornerShape(10.dp)), // Test tag for Logout button
                  colors =
                      ButtonDefaults.outlinedButtonColors(
                          containerColor = Color.Transparent,
                          contentColor = Color.Red,
                      )) {
                    Icon(
                        imageVector = Icons.Outlined.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", color = Color.Red)
                  }
            }
      })
}

/**
 * Composable function that creates a top bar for navigation.
 *
 * @param navigationActions NavigationActions object to handle navigation events when the back
 *   button is clicked.
 */
@Composable
fun TopBar(navigationActions: NavigationActions) {
  Row(
      modifier = Modifier.fillMaxWidth().background(DarkBlue),
      verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = { navigationActions.goBack() },
            modifier = Modifier.testTag("ArrowBackButton")) {
              Icon(
                  imageVector = Icons.Outlined.ArrowBack,
                  contentDescription = "Back",
                  tint = Color.White)
            }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = stringResource(id = R.string.setting_title),
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.weight(1f))
      }
}
