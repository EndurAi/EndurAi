package com.android.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.android.sample.model.preferences.PreferencesRepositoryFirestore
import com.android.sample.model.preferences.PreferencesViewModel
import com.android.sample.resources.C
import com.android.sample.ui.achievements.AchievementsScreen
import com.android.sample.ui.authentication.SignInScreen
import com.android.sample.ui.mainscreen.OverviewScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.preferences.PreferencesScreen
import com.android.sample.ui.settings.SettingsScreen
import com.android.sample.ui.theme.SampleAppTheme
import com.android.sample.ui.video.VideoScreen
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      SampleAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
              MainApp()
            }
      }
    }
  }
}

@Composable
fun MainApp() {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)
  val preferenceRepository = PreferencesRepositoryFirestore(Firebase.firestore)
  val preferencesViewModel = PreferencesViewModel(preferenceRepository)
  NavHost(navController = navController, startDestination = Route.AUTH) {

    // Auth Screen
    navigation(startDestination = Screen.AUTH, route = Route.AUTH) {
      composable(Screen.AUTH) { SignInScreen(navigationActions) }
    }

    // Main Screen
    navigation(startDestination = Screen.MAIN, route = Route.MAIN) {
      composable(Screen.MAIN) { OverviewScreen(navigationActions) }
    }

    // Video Screen
    navigation(startDestination = Screen.VIDEO, route = Route.VIDEO) {
      composable(Screen.VIDEO) { VideoScreen(navigationActions) }
    }

    // Achievements Screen
    navigation(startDestination = Screen.ACHIEVEMENTS, route = Route.ACHIEVEMENTS) {
      composable(Screen.ACHIEVEMENTS) { AchievementsScreen(navigationActions) }
    }

    // Preferences Screen
    navigation(startDestination = Screen.PREFERENCES, route = Route.PREFERENCES) {
      composable(Screen.PREFERENCES) { PreferencesScreen(navigationActions, preferencesViewModel) }
    }

    // Settings Screen
    navigation(startDestination = Screen.SETTINGS, route = Route.SETTINGS) {
      composable(Screen.SETTINGS) { SettingsScreen(navigationActions) }
    }
  }
}
