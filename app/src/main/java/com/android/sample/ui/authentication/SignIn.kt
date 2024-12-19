package com.android.sample.ui.authentication

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.ui.animations.DumbbellAnimation
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.navigation.TopLevelDestinations
import com.android.sample.ui.theme.ContrailOne
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun SignInScreen(
    userAccountViewModel: UserAccountViewModel =
        viewModel(factory = UserAccountViewModel.provideFactory(LocalContext.current)),
    navigationActions: NavigationActions
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val isLoading by
      userAccountViewModel.isLoading.collectAsState() // Directly observe isLoading from ViewModel

  val user by remember { mutableStateOf(Firebase.auth.currentUser) }
  val userAccount by userAccountViewModel.userAccount.collectAsState(initial = null)

  val launcher =
      rememberFirebaseAuthLauncher(
          onAuthComplete = { result ->
            Log.d("SignInScreen", "User signed in: ${result.user?.displayName}")
            result.user?.uid?.let { userId ->
              checkUserAccountAndNavigate(userId, userAccountViewModel, navigationActions, scope)
            }
            Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
          },
          onAuthError = {
            Log.e("SignInScreen", "Failed to sign in: ${it.statusCode}")
            Toast.makeText(context, "Login Failed!", Toast.LENGTH_LONG).show()
          })
  val token = stringResource(com.android.sample.R.string.default_web_client_id)
  // The main container for the screen
  // A surface container using the 'background' color from the theme

  if (user == null) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { padding ->
          Column(
              modifier = Modifier.fillMaxSize().padding(padding),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center,
          ) {
            // App Logo Image
            Image(
                painter = painterResource(id = com.android.sample.R.drawable.endurailogo),
                contentDescription = "App Logo",
                modifier = Modifier.size(250.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // Welcome Text
            Text(
                modifier = Modifier.testTag("loginTitle"),
                text = "Welcome",
                style =
                    MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 50.sp, lineHeight = 64.sp, fontFamily = ContrailOne),
                // center the text

                textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(48.dp))

            // Authenticate With Google Button
            GoogleSignInButton(
                onSignInClick = {
                  val gso =
                      GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                          .requestIdToken(token)
                          .requestEmail()
                          .build()
                  val googleSignInClient = GoogleSignIn.getClient(context, gso)
                  launcher.launch(googleSignInClient.signInIntent)
                })
          }
        })
  } else {
    // When user is signed in, check if they have an account
    LaunchedEffect(userAccount) {
      Firebase.auth.currentUser?.uid?.let { userId ->
        checkUserAccountAndNavigate(userId, userAccountViewModel, navigationActions, scope)
      }
    }
  }

  // Show Loading Dialog if `isLoading` is true
  if (isLoading) {
    //    LoadingDialog()
    DumbbellAnimation(modifier = Modifier.testTag("loadingDialog"))
  }
}

fun checkUserAccountAndNavigate(
    userId: String,
    userAccountViewModel: UserAccountViewModel,
    navigationActions: NavigationActions,
    scope: CoroutineScope
) {
  userAccountViewModel.getUserAccount(userId)

  scope.launch {
    userAccountViewModel.isLoading.collect { isLoading ->
      if (!isLoading) {
        // Observe changes in userAccount to know if profile exists
        userAccountViewModel.userAccount.collect { account ->
          if (account != null) {
            // If account exists, navigate to main screen
            navigationActions.navigateTo(TopLevelDestinations.MAIN)
          } else {
            // If no account exists, navigate to AddAccount screen
            navigationActions.navigateTo(Screen.ADD_ACCOUNT)
          }
          return@collect
        }
      }
    }
  }
}

@Composable
fun GoogleSignInButton(onSignInClick: () -> Unit) {
  Button(
      onClick = onSignInClick,
      colors = ButtonDefaults.buttonColors(containerColor = Color.White), // Button color
      shape = RoundedCornerShape(50), // Circular edges for the button
      border = BorderStroke(1.dp, Color.LightGray),
      modifier =
          Modifier.padding(8.dp)
              .height(48.dp) // Adjust height as needed
              .fillMaxWidth(0.6f)
              .testTag("loginButton")) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()) {
              // Load the Google logo from resources
              Image(
                  painter =
                      painterResource(
                          id =
                              com.android.sample.R.drawable
                                  .google_logo), // Ensure this drawable exists
                  contentDescription = "Google Logo",
                  modifier =
                      Modifier.size(30.dp) // Size of the Google logo
                          .padding(end = 8.dp))

              // Text for the button
              Text(
                  text = "Sign in with Google",
                  color = Color.Gray, // Text color
                  fontSize = 16.sp, // Font size
                  fontWeight = FontWeight.Medium)
            }
      }
}

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
  val scope = rememberCoroutineScope()
  return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      result ->
    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
    try {
      val account = task.getResult(ApiException::class.java)!!
      val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
      scope.launch {
        val authResult = Firebase.auth.signInWithCredential(credential).await()
        onAuthComplete(authResult)
      }
    } catch (e: ApiException) {
      onAuthError(e)
    }
  }
}
