package com.android.sample

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.android.sample.model.achievements.StatisticsRepositoryFirestore
import com.android.sample.model.achievements.StatisticsViewModel
import com.android.sample.model.calendar.CalendarViewModel
import com.android.sample.model.camera.CameraViewModel
import com.android.sample.model.preferences.PreferencesRepositoryFirestore
import com.android.sample.model.preferences.PreferencesViewModel
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.model.video.VideoViewModel
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.RunningWorkout
import com.android.sample.model.workout.WarmUp
import com.android.sample.model.workout.WarmUpViewModel
import com.android.sample.model.workout.WorkoutLocalCache
import com.android.sample.model.workout.WorkoutRepositoryFirestore
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.resources.C
import com.android.sample.ui.achievements.AchievementsScreen
import com.android.sample.ui.authentication.AddAccount
import com.android.sample.ui.authentication.SignInScreen
import com.android.sample.ui.calendar.CalendarScreen
import com.android.sample.ui.calendar.DayCalendarScreen
import com.android.sample.ui.friends.AddFriendScreen
import com.android.sample.ui.friends.FriendsScreen
import com.android.sample.ui.googlemap.RunningScreen
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
import com.android.sample.ui.workout.RunningSelectionScreen
import com.android.sample.ui.workout.SessionSelectionScreen
import com.android.sample.ui.workout.WorkoutCreationScreen
import com.android.sample.ui.workout.WorkoutOverviewScreen
import com.android.sample.ui.workout.WorkoutScreen
import com.android.sample.ui.workout.WorkoutSelectionScreen
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ActivityCompat.requestPermissions(
        this,
        arrayOf(
            // Permission for not precise location
            Manifest.permission.ACCESS_COARSE_LOCATION,
            // Permission for precise location
            Manifest.permission.ACCESS_FINE_LOCATION,
            // Permission to post location
            Manifest.permission.POST_NOTIFICATIONS),
        0)

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

  val context = LocalContext.current

  val workoutLocalCache = WorkoutLocalCache(context)

  val userAccountViewModel: UserAccountViewModel =
      viewModel(factory = UserAccountViewModel.provideFactory(context))
  val preferenceRepository = PreferencesRepositoryFirestore(Firebase.firestore)
  val preferencesViewModel = PreferencesViewModel(preferenceRepository)

  val videoViewModel: VideoViewModel = viewModel(factory = VideoViewModel.Factory)
  val bodyweightWorkoutRepository =
      WorkoutRepositoryFirestore(
          Firebase.firestore, workoutLocalCache, clazz = BodyWeightWorkout::class.java)
  val bodyweightWorkoutViewModel = WorkoutViewModel(bodyweightWorkoutRepository, workoutLocalCache)
  val yogaWorkoutRepository =
      WorkoutRepositoryFirestore(
          Firebase.firestore, workoutLocalCache, clazz = YogaWorkout::class.java)
  val yogaWorkoutViewModel = WorkoutViewModel(yogaWorkoutRepository, workoutLocalCache)

  val warmUpRepository =
      WorkoutRepositoryFirestore(Firebase.firestore, workoutLocalCache, clazz = WarmUp::class.java)
  val warmUpViewModel = WarmUpViewModel(warmUpRepository, workoutLocalCache)
  val calendarViewModel = CalendarViewModel()

  val cameraViewModel = CameraViewModel(context = context)
  val runningWorkoutRepository =
      WorkoutRepositoryFirestore(
          Firebase.firestore, workoutLocalCache, clazz = RunningWorkout::class.java)
  val runningWorkoutViewModel = WorkoutViewModel(runningWorkoutRepository, workoutLocalCache)
  val statisticsRepository = StatisticsRepositoryFirestore(Firebase.firestore)
  val statisticsViewModel = StatisticsViewModel(statisticsRepository)

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
        MainScreen(
            navigationActions,
            bodyweightWorkoutViewModel,
            yogaWorkoutViewModel,
            userAccountViewModel)
      }
      composable(Screen.VIEW_ALL) {
        ViewAllScreen(navigationActions, bodyweightWorkoutViewModel, yogaWorkoutViewModel)
      }
    }

    // Friends Screen

    navigation(startDestination = Screen.FRIENDS, route = Route.FRIENDS) {
      composable(Screen.FRIENDS) { FriendsScreen(navigationActions, userAccountViewModel) }
      composable(Screen.ADD_FRIEND) { AddFriendScreen(navigationActions, userAccountViewModel) }
    }

    // Video Screen
    navigation(startDestination = Screen.VIDEO_LIBRARY, route = Route.VIDEO_LIBRARY) {
      composable(Screen.VIDEO_LIBRARY) { VideoLibraryScreen(navigationActions, videoViewModel) }
      composable(Screen.VIDEO) { VideoScreen(navigationActions, videoViewModel) }
    }

    // Achievements Screen
    navigation(startDestination = Screen.ACHIEVEMENTS, route = Route.ACHIEVEMENTS) {
      composable(Screen.ACHIEVEMENTS) { AchievementsScreen(navigationActions, statisticsViewModel) }
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
      composable(Screen.SETTINGS) {
        SettingsScreen(
            navigationActions,
            bodyweightWorkoutViewModel,
            yogaWorkoutViewModel,
            userAccountViewModel)
      }
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

    // Import or Create Screen for running workout
    navigation(
        startDestination = Screen.IMPORTORCREATE_RUNNING, route = Route.IMPORTORCREATE_RUNNING) {
          composable(Screen.IMPORTORCREATE_RUNNING) { RunningSelectionScreen(navigationActions) }
        }

    // Body Weight Creation Screen
    navigation(startDestination = Screen.BODY_WEIGHT_CREATION, route = Route.BODY_WEIGHT_CREATION) {
      composable(Screen.BODY_WEIGHT_CREATION) {
        WorkoutCreationScreen(
            navigationActions, WorkoutType.BODY_WEIGHT, bodyweightWorkoutViewModel, false)
      }
    }

    // Body Weight Selection Screen
    navigation(startDestination = Screen.CHOOSE_BODYWEIGHT, route = Route.CHOOSE_BODYWEIGHT) {
      composable(Screen.CHOOSE_BODYWEIGHT) {
        WorkoutSelectionScreen(bodyweightWorkoutViewModel, navigationActions)
      }
    }

    // Body Weight Import Screen
    navigation(startDestination = Screen.BODY_WEIGHT_IMPORT, route = Route.BODY_WEIGHT_IMPORT) {
      composable(Screen.BODY_WEIGHT_IMPORT) {
        WorkoutCreationScreen(
            navigationActions, WorkoutType.BODY_WEIGHT, bodyweightWorkoutViewModel, true)
      }
    }

    // Yoga Creation Screen
    navigation(startDestination = Screen.YOGA_CREATION, route = Route.YOGA_CREATION) {
      composable(Screen.YOGA_CREATION) {
        WorkoutCreationScreen(navigationActions, WorkoutType.YOGA, yogaWorkoutViewModel, false)
      }
    }

    // Yoga Selection Screen
    navigation(startDestination = Screen.CHOOSE_YOGA, route = Route.CHOOSE_YOGA) {
      composable(Screen.CHOOSE_YOGA) {
        WorkoutSelectionScreen(yogaWorkoutViewModel, navigationActions)
      }
    }

    // Yoga Import Screen
    navigation(startDestination = Screen.YOGA_IMPORT, route = Route.YOGA_IMPORT) {
      composable(Screen.YOGA_IMPORT) {
        WorkoutCreationScreen(navigationActions, WorkoutType.YOGA, yogaWorkoutViewModel, true)
      }
    }

    // Yoga Creation Screen
    navigation(startDestination = Screen.YOGA_CREATION, route = Route.YOGA_CREATION) {
      composable(Screen.YOGA_CREATION) {
        WorkoutCreationScreen(navigationActions, WorkoutType.YOGA, yogaWorkoutViewModel, false)
      }
    }

    // Running Screen
    navigation(startDestination = Screen.RUNNING_SCREEN, route = Route.RUNNING_SCREEN) {
      composable(Screen.RUNNING_SCREEN) {
        RunningScreen(navigationActions, runningWorkoutViewModel)
      }
    }

    // Body Weight Overview Screen
    navigation(startDestination = Screen.BODY_WEIGHT_OVERVIEW, route = Route.BODY_WEIGHT_OVERVIEW) {
      composable(Screen.BODY_WEIGHT_OVERVIEW) {
        WorkoutOverviewScreen(
            navigationActions = navigationActions,
            bodyweightViewModel = bodyweightWorkoutViewModel,
            yogaViewModel = yogaWorkoutViewModel,
            workoutTye = WorkoutType.BODY_WEIGHT)
      }
    }

    // Body Weight Edit Screen
    navigation(startDestination = Screen.BODY_WEIGHT_EDIT, route = Route.BODY_WEIGHT_EDIT) {
      composable(Screen.BODY_WEIGHT_EDIT) {
        WorkoutCreationScreen(
            navigationActions,
            WorkoutType.BODY_WEIGHT,
            bodyweightWorkoutViewModel,
            true,
            editing = true)
      }
    }

    // Yoga Edit Screen
    navigation(startDestination = Screen.YOGA_EDIT, route = Route.YOGA_EDIT) {
      composable(Screen.YOGA_EDIT) {
        WorkoutCreationScreen(
            navigationActions, WorkoutType.YOGA, yogaWorkoutViewModel, true, editing = true)
      }
    }

    // Yoga Overview Screen
    navigation(startDestination = Screen.YOGA_OVERVIEW, route = Route.YOGA_OVERVIEW) {
      composable(Screen.YOGA_OVERVIEW) {
        WorkoutOverviewScreen(
            navigationActions = navigationActions,
            bodyweightViewModel = bodyweightWorkoutViewModel,
            yogaViewModel = yogaWorkoutViewModel,
            workoutTye = WorkoutType.YOGA)
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
            workoutType = WorkoutType.BODY_WEIGHT,
            cameraViewModel = cameraViewModel,
            videoViewModel = videoViewModel,
            userAccountViewModel = userAccountViewModel,
            statisticsViewModel = statisticsViewModel)
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
            workoutType = WorkoutType.YOGA,
            cameraViewModel = cameraViewModel,
            videoViewModel = videoViewModel,
            userAccountViewModel = userAccountViewModel,
            statisticsViewModel = statisticsViewModel)
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
            workoutType = WorkoutType.WARMUP,
            cameraViewModel = cameraViewModel,
            videoViewModel = videoViewModel,
            userAccountViewModel = userAccountViewModel,
            statisticsViewModel = statisticsViewModel)
      }
    }

    // Calendar Screen
    navigation(startDestination = Screen.CALENDAR, route = Route.CALENDAR) {
      composable(Screen.CALENDAR) {
        CalendarScreen(
            navigationActions, bodyweightWorkoutViewModel, yogaWorkoutViewModel, calendarViewModel)
      }
      composable(Screen.DAY_CALENDAR) {
        DayCalendarScreen(
            navigationActions, bodyweightWorkoutViewModel, yogaWorkoutViewModel, calendarViewModel)
      }
    }
  }
}
