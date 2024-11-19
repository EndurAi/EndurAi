package com.android.sample.ui.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.R
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Blue
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigationActions: NavigationActions,
    userAccountViewModel: UserAccountViewModel = viewModel(factory = UserAccountViewModel.provideFactory(LocalContext.current))
) {
  val context = LocalContext.current

  Scaffold(
      modifier = Modifier.testTag("settingsScreen"),
      topBar = { TopBar(navigationActions, R.string.setting_title) },
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              // User data button
              BlueButton(
                  onClick = { navigationActions.navigateTo(Screen.EDIT_ACCOUNT) },
                  modifier = Modifier.testTag("userDataButton"),
                  title = R.string.UserData)

              BlueButton(
                  onClick = { navigationActions.navigateTo(Screen.PREFERENCES) },
                  modifier = Modifier.testTag("preferencesButton"),
                  title = R.string.Preferences)

              Column(
                  modifier = Modifier.fillMaxSize().padding(16.dp),
                  verticalArrangement = Arrangement.Bottom,
                  horizontalAlignment = Alignment.CenterHorizontally) {
                    RedButton(
                        onClick = {
                          userAccountViewModel.deleteAccount(
                              context,
                              onSuccess = {
                                Toast.makeText(
                                        context,
                                        R.string.SuccesfulDeleteMessage,
                                        Toast.LENGTH_SHORT)
                                    .show()
                                navigationActions.navigateTo("Auth Screen")
                              },
                              onFailure = { error ->
                                Toast.makeText(
                                        context,
                                        "Failed to delete account: ${error.message}",
                                        Toast.LENGTH_LONG)
                                    .show()
                              })
                        },
                        image = Icons.Outlined.Delete,
                        title = R.string.Delete_Account,
                        modifier = Modifier.fillMaxWidth().testTag("deleteAccountButton"))

                    Spacer(modifier = Modifier.height(16.dp)) // Space between buttons

                    RedButton(
                        onClick = {
                            userAccountViewModel.clearCacheOnLogout() // Clear local cache on logout
                            signOut(context)
                          navigationActions.navigateTo("Auth Screen")
                          Toast.makeText(context, R.string.LogoutMessage, Toast.LENGTH_SHORT).show()
                        },
                        image = Icons.Outlined.ExitToApp,
                        title = R.string.Logout,
                        modifier = Modifier.fillMaxWidth().testTag("logoutButton"))
                  }
            }
      })
}

@Composable
fun BlueButton(onClick: () -> Unit, modifier: Modifier = Modifier, title: Int) {
  Button(
      onClick = onClick,
      modifier = modifier.fillMaxWidth().height(60.dp).background(Blue, RoundedCornerShape(10.dp)),
      colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
        Text(text = stringResource(id = title), color = Color.Black)
      }
}

@Composable
fun RedButton(onClick: () -> Unit, image: ImageVector, title: Int, modifier: Modifier = Modifier) {
  Button(
      onClick = onClick,
      modifier =
          modifier
              .fillMaxWidth()
              .height(48.dp)
              .border(BorderStroke(2.dp, Color.Red), RoundedCornerShape(8.dp))
              .background(Color.Red.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)),
      colors =
          ButtonDefaults.buttonColors(
              contentColor = Color.Red, containerColor = Color.Transparent)) {
        Icon(imageVector = image, contentDescription = stringResource(title), tint = Color.Red)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(title), color = Color.Red)
      }
}

// Function to handle sign-out
fun signOut(context: Context) {
  FirebaseAuth.getInstance().signOut() // Sign out from Firebase Auth
  val gso =
      GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          .requestIdToken(context.getString(R.string.default_web_client_id))
          .requestEmail()
          .build()
  val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)
  googleSignInClient.signOut() // Sign out from Google
}
