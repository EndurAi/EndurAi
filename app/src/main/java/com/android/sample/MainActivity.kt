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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.android.sample.model.preferences.PreferencesRepositoryFirestore
import com.android.sample.model.preferences.PreferencesViewModel
import com.android.sample.model.video.VideoViewModel
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.WarmUp
import com.android.sample.model.workout.WarmUpViewModel
import com.android.sample.model.workout.WorkoutRepositoryFirestore
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.resources.C
import com.android.sample.ui.achievements.AchievementsScreen
import com.android.sample.ui.authentication.AddAccount
import com.android.sample.ui.authentication.SignInScreen
import com.android.sample.ui.calendar.CalendarScreen
import com.android.sample.ui.mainscreen.MainScreen
import com.android.sample.ui.mainscreen.ViewAllScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.preferences.PreferencesScreen
import com.android.sample.ui.settings.SettingsScreen
import com.android.sample.ui.theme.SampleAppTheme
import com.android.sample.ui.video.VideoLibraryScreen
import com.android.sample.ui.video.VideoScreen
import com.android.sample.ui.workout.ImportOrCreateScreen
import com.android.sample.ui.workout.SessionSelectionScreen
import com.android.sample.ui.workout.WorkoutCreationScreen
import com.android.sample.ui.workout.WorkoutScreen
import com.android.sample.viewmodel.UserAccountViewModel
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
              val startDestination = intent.getStringExtra("START_DESTINATION") ?: Route.AUTH
              MainApp(startDestination)
            }
      }
    }
  }
}

@Composable
fun MainApp(startDestination: String = Route.AUTH) {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)
  val userAccountViewModel: UserAccountViewModel = viewModel(factory = UserAccountViewModel.Factory)
  val preferenceRepository = PreferencesRepositoryFirestore(Firebase.firestore)
  val preferencesViewModel = PreferencesViewModel(preferenceRepository)

  val videoViewModel: VideoViewModel = viewModel(factory = VideoViewModel.Factory)
  val bodyweightWorkoutRepository =
      WorkoutRepositoryFirestore(Firebase.firestore, clazz = BodyWeightWorkout::class.java)
  val bodyweightWorkoutViewModel = WorkoutViewModel(bodyweightWorkoutRepository)
  val yogaWorkoutRepository =
      WorkoutRepositoryFirestore(Firebase.firestore, clazz = YogaWorkout::class.java)
  val yogaWorkoutViewModel = WorkoutViewModel(yogaWorkoutRepository)

  val warmUpRepository = WorkoutRepositoryFirestore(Firebase.firestore, clazz = WarmUp::class.java)
  val warmUpViewModel = WarmUpViewModel(warmUpRepository)

  NavHost(navController = navController, startDestination = startDestination) {

    // Auth Screen
    navigation(startDestination = Screen.AUTH, route = Route.AUTH) {
      composable(Screen.AUTH) { SignInScreen(userAccountViewModel, navigationActions) }
    }

    // Add Account Screen
    navigation(startDestination = Screen.ADD_ACCOUNT, route = Route.ADD_ACCOUNT) {
      composable(Screen.ADD_ACCOUNT) { AddAccount(userAccountViewModel, navigationActions, false) }
    }

    // Main Screen
    navigation(startDestination = Screen.MAIN, route = Route.MAIN) {
      composable(Screen.MAIN) {
        MainScreen(navigationActions, bodyweightWorkoutViewModel, yogaWorkoutViewModel)
      }
      composable(Screen.VIEW_ALL) {
        ViewAllScreen(navigationActions, bodyweightWorkoutViewModel, yogaWorkoutViewModel)
      }
    }

    // Video Screen
    navigation(startDestination = Screen.VIDEO_LIBRARY, route = Route.VIDEO_LIBRARY) {
      composable(Screen.VIDEO_LIBRARY) { VideoLibraryScreen(navigationActions, videoViewModel) }
      composable(Screen.VIDEO) { VideoScreen(navigationActions, videoViewModel) }
    }

    // Achievements Screen
    navigation(startDestination = Screen.ACHIEVEMENTS, route = Route.ACHIEVEMENTS) {
      composable(Screen.ACHIEVEMENTS) { AchievementsScreen(navigationActions) }
    }

    // Preferences Screen
    navigation(startDestination = Screen.PREFERENCES, route = Route.PREFERENCES) {
      composable(Screen.PREFERENCES) { PreferencesScreen(navigationActions, preferencesViewModel) }
    }

    // Edit Account Screen
    navigation(startDestination = Screen.EDIT_ACCOUNT, route = Route.EDIT_ACCOUNT) {
      composable(Screen.EDIT_ACCOUNT) { AddAccount(userAccountViewModel, navigationActions, true) }
    }

    // Settings Screen
    navigation(startDestination = Screen.SETTINGS, route = Route.SETTINGS) {
      composable(Screen.SETTINGS) { SettingsScreen(navigationActions) }
    }

    // Session Selection Screen
    navigation(startDestination = Screen.SESSIONSELECTION, route = Route.SESSIONSELECTION) {
      composable(Screen.SESSIONSELECTION) { SessionSelectionScreen(navigationActions) }
    }

    // Import or Create Screen for body weight workout
    navigation(
        startDestination = Screen.IMPORTORCREATE_BODY_WEIGHT,
        route = Route.IMPORTORCREATE_BODY_WEIGHT) {
          composable(Screen.IMPORTORCREATE_BODY_WEIGHT) {
            ImportOrCreateScreen(navigationActions, workoutType = WorkoutType.BODY_WEIGHT)
          }
        }

    // Import or Create Screen for yoga workout
    navigation(startDestination = Screen.IMPORTORCREATE_YOGA, route = Route.IMPORTORCREATE_YOGA) {
      composable(Screen.IMPORTORCREATE_YOGA) {
        ImportOrCreateScreen(navigationActions, workoutType = WorkoutType.YOGA)
      }
    }

    // Body Weight Creation Screen
    navigation(startDestination = Screen.BODY_WEIGHT_CREATION, route = Route.BODY_WEIGHT_CREATION) {
      composable(Screen.BODY_WEIGHT_CREATION) {
        WorkoutCreationScreen(
            navigationActions, WorkoutType.BODY_WEIGHT, bodyweightWorkoutViewModel, false)
      }
    }

    // Yoga Creation Screen
    navigation(startDestination = Screen.YOGA_CREATION, route = Route.YOGA_CREATION) {
      composable(Screen.YOGA_CREATION) {
        WorkoutCreationScreen(navigationActions, WorkoutType.YOGA, yogaWorkoutViewModel, false)
      }
    }

    // Yoga Creation Screen
    navigation(startDestination = Screen.YOGA_CREATION, route = Route.YOGA_CREATION) {
      composable(Screen.YOGA_CREATION) {
        WorkoutCreationScreen(navigationActions, WorkoutType.YOGA, yogaWorkoutViewModel, false)
      }
    }

    // Body Weight Workout
    navigation(startDestination = Screen.BODY_WEIGHT_WORKOUT, route = Route.BODY_WEIGHT_WORKOUT) {
      composable(Screen.BODY_WEIGHT_WORKOUT) {
        WorkoutScreen(
            navigationActions = navigationActions,
            bodyweightViewModel = bodyweightWorkoutViewModel,
            yogaViewModel = yogaWorkoutViewModel,
            warmUpViewModel = warmUpViewModel,
            workoutType = WorkoutType.BODY_WEIGHT)
      }
    }
    // Yoga Workout
    navigation(startDestination = Screen.YOGA_WORKOUT, route = Route.YOGA_WORKOUT) {
      composable(Screen.YOGA_WORKOUT) {
        WorkoutScreen(
            navigationActions = navigationActions,
            bodyweightViewModel = bodyweightWorkoutViewModel,
            yogaViewModel = yogaWorkoutViewModel,
            warmUpViewModel = warmUpViewModel,
            workoutType = WorkoutType.YOGA)
      }
    }

    // warmUp Workout
    navigation(startDestination = Screen.WARMUP_WORKOUT, route = Route.WARMUP_WORKOUT) {
      composable(Screen.WARMUP_WORKOUT) {
        WorkoutScreen(
            navigationActions = navigationActions,
            bodyweightViewModel = bodyweightWorkoutViewModel,
            yogaViewModel = yogaWorkoutViewModel,
            warmUpViewModel = warmUpViewModel,
            workoutType = WorkoutType.WARMUP)
      }
    }

    // Calendar Screen
    navigation(startDestination = Screen.CALENDAR, route = Route.CALENDAR) {
      composable(Screen.CALENDAR) {
        CalendarScreen(navigationActions, bodyweightWorkoutViewModel, yogaWorkoutViewModel)
      }
    }
  }
}
