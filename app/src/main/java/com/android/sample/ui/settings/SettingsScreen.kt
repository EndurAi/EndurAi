package com.android.sample.ui.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.R
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.composables.BottomBar
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.BlueWorkoutCard
import com.android.sample.ui.theme.FontSizes.TitleFontSize
import com.android.sample.ui.theme.LightRed
import com.android.sample.ui.theme.OpenSans
import com.android.sample.ui.theme.RedStroke
import com.android.sample.ui.theme.Shape.buttonShape
import com.android.sample.ui.theme.Shape.roundButtonShape
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigationActions: NavigationActions,
    bodyweightViewModel: WorkoutViewModel<BodyWeightWorkout>,
    yogaViewModel: WorkoutViewModel<YogaWorkout>,
    userAccountViewModel: UserAccountViewModel =
        viewModel(factory = UserAccountViewModel.provideFactory(LocalContext.current)),
) {
  val context = LocalContext.current
  var showDeleteConfirmation by remember { mutableStateOf(false) }

  Scaffold(
      modifier = Modifier.testTag("settingsScreen"),
      bottomBar = { BottomBar(navigationActions = navigationActions) },
      // Disable arrow as the bottom bar is displayed
      topBar = { TopBar(navigationActions, R.string.setting_title, displayArrow = false) },
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(modifier = Modifier.fillMaxHeight(0.05f))
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
                        onClick = { showDeleteConfirmation = true }, // Show the confirmation dialog
                        image = Icons.Outlined.Delete,
                        title = R.string.Delete_Account,
                        modifier = Modifier.testTag("deleteAccountButton"))

                    Spacer(modifier = Modifier.height(16.dp)) // Space between buttons

                    RedButton(
                        onClick = {
                          userAccountViewModel.clearCacheOnLogout() // Clear local cache on logout
                          bodyweightViewModel.clearCache()
                          yogaViewModel.clearCache()
                          signOut(context)
                          navigationActions.navigateTo("Auth Screen")
                          Toast.makeText(context, R.string.LogoutMessage, Toast.LENGTH_SHORT).show()
                        },
                        image = Icons.Outlined.ExitToApp,
                        title = R.string.Logout,
                        modifier = Modifier.testTag("logoutButton"))
                  }
            }
      })

  // Separate composable for the confirmation dialog
  if (showDeleteConfirmation) {
    DeleteConfirmationDialog(
        onConfirm = {
          userAccountViewModel.deleteAccount(
              context,
              onSuccess = {
                Toast.makeText(context, R.string.SuccesfulDeleteMessage, Toast.LENGTH_SHORT).show()
                navigationActions.navigateTo("Auth Screen")
              },
              onFailure = { error ->
                Toast.makeText(
                        context, "Failed to delete account: ${error.message}", Toast.LENGTH_LONG)
                    .show()
              })
          showDeleteConfirmation = false
        },
        onDismiss = { showDeleteConfirmation = false })
  }
}

@Composable
fun BlueButton(onClick: () -> Unit, modifier: Modifier = Modifier, title: Int) {
  Button(
      shape = buttonShape,
      onClick = onClick,
      modifier = modifier.padding(10.dp).fillMaxWidth(0.7f).shadow(4.dp, shape = buttonShape),
      colors = ButtonDefaults.buttonColors(containerColor = BlueWorkoutCard)) {
        Text(
            text = stringResource(id = title),
            color = Color.Black,
            fontFamily = OpenSans,
            fontSize = TitleFontSize,
            fontWeight = FontWeight.SemiBold)
      }
}

@Composable
fun RedButton(onClick: () -> Unit, image: ImageVector, title: Int, modifier: Modifier = Modifier) {
  Button(
      shape = roundButtonShape,
      onClick = onClick,
      modifier =
          modifier
              .padding(10.dp)
              .fillMaxWidth(0.8f)
              .shadow(4.dp, shape = roundButtonShape)
              .border(BorderStroke(1.dp, RedStroke), roundButtonShape),
      colors = ButtonDefaults.buttonColors(LightRed)) {
        Icon(
            imageVector = image,
            modifier = Modifier.size(30.dp),
            contentDescription = stringResource(title),
            tint = RedStroke)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(title),
            color = RedStroke,
            fontFamily = OpenSans,
            fontSize = TitleFontSize,
            fontWeight = FontWeight.SemiBold)
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
