package com.android.sample.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.navigation.TopLevelDestinations
import com.android.sample.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navigationActions: NavigationActions) {
  val context = LocalContext.current

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Settings", fontSize = 20.sp) },
            navigationIcon = {
              IconButton(onClick = { navigationActions.navigateTo(TopLevelDestinations.MAIN) }) {
                Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "Back")
              }
            },
            modifier = Modifier.testTag("settingsScreen") // Add testTag for the screen itself
            )
      },
      content = { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              // User data button
              Button(
                  onClick = { navigationActions.navigateTo(Screen.EDIT_ACCOUNT) },
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(60.dp)
                          .background(Color.LightGray, RoundedCornerShape(10.dp))
                          .testTag("userDataButton"), // Test tag for User Data button
                  colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                    Text("User data", color = Color.Black)
                  }

              // Preferences button
              Button(
                  onClick = { navigationActions.navigateTo(Screen.PREFERENCES) },
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(60.dp)
                          .background(Color.LightGray, RoundedCornerShape(10.dp))
                          .testTag("preferencesButton"), // Test tag for Preferences button
                  colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                    Text("Preferences", color = Color.Black)
                  }

              Spacer(modifier = Modifier.height(200.dp)) // Adjust this height as needed

              // Delete account button
              Button(
                  onClick = { /* TODO: Handle Account Deletion */},
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(48.dp)
                          .testTag("deleteAccountButton"), // Test tag for Delete Account button
                  colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete Account",
                        tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete account", color = Color.White)
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
                          .testTag("logoutButton"), // Test tag for Logout button
                  colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Icon(
                        imageVector = Icons.Outlined.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", color = Color.White)
                  }
            }
      })
}
