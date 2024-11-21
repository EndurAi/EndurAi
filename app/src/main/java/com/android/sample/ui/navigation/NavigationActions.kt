package com.android.sample.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController

object Route {
  const val MAIN = "Main"
  const val AUTH = "Auth"
  const val VIDEO_LIBRARY = "VideoLibrary"
  const val VIDEO = "Video"
  const val ACHIEVEMENTS = "Achievements"
  const val PREFERENCES = "Preferences"
  const val SETTINGS = "Settings"
  const val BODY_WEIGHT_CREATION = "BodyWeightTraining"
  const val RUNNING_CREATION = "RunningTraining"
  const val YOGA_CREATION = "YogaTraining"
  const val SESSIONSELECTION = "Session Selection"
  const val IMPORTORCREATE_BODY_WEIGHT = "Import Or Create Body Weight"
  const val IMPORTORCREATE_YOGA = "Import Or Create Yoga"
  const val TEST = "Test"
  const val WARMUP_WORKOUT = "Warmup workout"
  const val BODY_WEIGHT_WORKOUT = "BodyWeight workout"
  const val YOGA_WORKOUT = "Yoga workout"
  const val FRIENDS = "Friends"
  const val CALENDAR = "Calendar"
  const val ADD_ACCOUNT = "Add Account"
  const val EDIT_ACCOUNT = "Edit Account"
  const val BODY_WEIGHT_IMPORT = "BodyWeightImport"
  const val CHOOSE_BODYWEIGHT = "Choose Bodyweight"
  const val YOGA_IMPORT = "YogaImport"
  const val CHOOSE_YOGA = "Choose Yoga"
  const val BODY_WEIGHT_OVERVIEW = "BodyWeightOverview"
  const val YOGA_OVERVIEW = "YogaOverview"
  const val BODY_WEIGHT_EDIT = "BodyWeightEdit"
  const val YOGA_EDIT = "YogaEdit"
  const val IMPORTORCREATE_RUNNING = "Import Or Create Running"
}

object Screen {
  const val MAIN = "Main Screen"
  const val AUTH = "Auth Screen"
  const val VIDEO_LIBRARY = "Video Library Screen"
  const val VIDEO = "Video Screen"
  const val ACHIEVEMENTS = "Achievements Screen"
  const val PREFERENCES = "Preferences Screen"
  const val SETTINGS = "Settings Screen"
  const val BODY_WEIGHT_CREATION = "BodyWeightTraining Screen"
  const val RUNNING_CREATION = "RunningTraining Screen"
  const val YOGA_CREATION = "YogaTraining Screen"
  const val SESSIONSELECTION = "Session Selection Screen"
  const val IMPORTORCREATE_BODY_WEIGHT = "Import Or Create Body Weight Screen"
  const val IMPORTORCREATE_YOGA = "Import Or Create Yoga Screen"
  const val CALENDAR = "Calendar Screen"
  const val VIEW_ALL = "View All Screen"
  const val TEST = "Test Screen"
  const val WARMUP_WORKOUT = "Warmup workout screen"
  const val BODY_WEIGHT_WORKOUT = "BodyWeight workout screen"
  const val YOGA_WORKOUT = "Yoga workout screen"
  const val DAY_CALENDAR = "Day Calendar Screen"
  const val ADD_ACCOUNT = "Add Account Screen"
  const val EDIT_ACCOUNT = "Edit Account Screen"
  const val FRIENDS = "Friends Screen"
  const val ADD_FRIEND = "Add Friend Screen"
  const val YOGA_IMPORT = "Yoga import screen"
  const val CHOOSE_BODYWEIGHT = "Choose Bodyweight Screen"
  const val CHOOSE_YOGA = "Choose Yoga Screen"
  const val BODY_WEIGHT_IMPORT = "BodyWeight import screen"
  const val BODY_WEIGHT_OVERVIEW = "BodyWeight overview screen"
  const val YOGA_OVERVIEW = "Yoga overview screen"
  const val BODY_WEIGHT_EDIT = "BodyWeight edit screen"
  const val YOGA_EDIT = "Yoga edit screen"
    const val IMPORTORCREATE_RUNNING = "Import Or Create Running Screen"
}

data class TopLevelDestination(val route: String, val icon: ImageVector, val textId: String)

object TopLevelDestinations {
  val MAIN = TopLevelDestination(Route.MAIN, Icons.Outlined.Home, textId = "Main")
  val VIDEO = TopLevelDestination(Route.VIDEO_LIBRARY, Icons.Filled.PlayArrow, textId = "Video")
  val CALENDAR = TopLevelDestination(Route.CALENDAR, Icons.Outlined.DateRange, textId = "Calendar")
}

val LIST_OF_TOP_LEVEL_DESTINATIONS =
    listOf(TopLevelDestinations.MAIN, TopLevelDestinations.VIDEO, TopLevelDestinations.CALENDAR)

open class NavigationActions(private val navController: NavHostController) {
  /**
   * Navigate to the specified [TopLevelDestination]
   *
   * @param destination The top level destination to navigate to Clear the back stack when
   *   navigating to a new destination This is useful when navigating to a new screen from the
   *   bottom navigation bar as we don't want to keep the previous screen in the back stack
   */
  open fun navigateTo(destination: TopLevelDestination) {
    navController.navigate(destination.route) {
      popUpTo(navController.graph.startDestinationId) {
        saveState = true
        inclusive = true
      }
      launchSingleTop = true

      if (destination.route != Route.AUTH) {
        restoreState = true
      }
    }
  }

  /**
   * Navigate to the specified screen
   *
   * @param screen the screen to navigate to. This should be a constant from [Screen]
   */
  open fun navigateTo(screen: String) {
    navController.navigate(screen)
  }

  /** Navigate back to the previous screen */
  open fun goBack() {
    navController.popBackStack()
  }

  /**
   * Get the current route of the navigation controller
   *
   * @return the current route
   */
  open fun currentRoute(): String {
    return navController.currentDestination?.route ?: ""
  }
}
