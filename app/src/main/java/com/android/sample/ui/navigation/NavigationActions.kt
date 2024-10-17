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
  const val VIDEO = "Video"
  const val ACHIEVEMENTS = "Achievements"
  const val PREFERENCES = "Preferences"
  const val SETTINGS = "Settings"
  const val SESSIONSELECTION = "Session Selection"
  const val IMPORTORCREATE = "Import Or Create"
  const val TEST = "Test"
}

object Screen {
  const val MAIN = "Main Screen"
  const val AUTH = "Auth Screen"
  const val VIDEO = "Video Screen"
  const val ACHIEVEMENTS = "Achievements Screen"
  const val PREFERENCES = "Preferences Screen"
  const val SETTINGS = "Settings Screen"
  const val SESSIONSELECTION = "Session Selection Screen"
  const val IMPORTORCREATE = "Import Or Create Screen"
  const val TEST = "Test Screen"
}

data class TopLevelDestination(val route: String, val icon: ImageVector, val textId: String)

object TopLevelDestinations {
  val MAIN = TopLevelDestination(Route.MAIN, Icons.Outlined.Home, textId = "Main")
  val VIDEO = TopLevelDestination(Route.VIDEO, Icons.Filled.PlayArrow, textId = "Video")
  val ACHIEVEMENTS =
      TopLevelDestination(Route.ACHIEVEMENTS, Icons.Outlined.DateRange, textId = "Achievements")
}

val LIST_OF_TOP_LEVEL_DESTINATIONS =
    listOf(TopLevelDestinations.MAIN, TopLevelDestinations.VIDEO, TopLevelDestinations.ACHIEVEMENTS)

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
