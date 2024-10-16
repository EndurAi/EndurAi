package com.android.sample.ui.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq

class NavigationActionsTest {

  private lateinit var navigationDestination: NavDestination
  private lateinit var navHostController: NavHostController
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationDestination = mock(NavDestination::class.java)
    val navGraph = mock(NavGraph::class.java)
    `when`(navGraph.startDestinationId).thenReturn(0) // Mock the startDestinationId
    navHostController = mock(NavHostController::class.java)
    `when`(navHostController.graph)
        .thenReturn(navGraph) // Set the mocked NavGraph to the NavHostController
    navigationActions = NavigationActions(navHostController)
  }

  @Test
  fun navigateToCallsController() {
    navigationActions.navigateTo(TopLevelDestinations.MAIN)
    verify(navHostController).navigate(eq(Route.MAIN), any<NavOptionsBuilder.() -> Unit>())

    navigationActions.navigateTo(Screen.VIDEO)
    verify(navHostController).navigate(Screen.VIDEO)
  }

  @Test
  fun goBackCallsController() {
    navigationActions.goBack()
    verify(navHostController).popBackStack()
  }

  @Test
  fun currentRouteWorksWithDestination() {
    `when`(navHostController.currentDestination).thenReturn(navigationDestination)
    `when`(navigationDestination.route).thenReturn(Route.MAIN)

    assertThat(navigationActions.currentRoute(), `is`(Route.MAIN))
  }

  @Test
  fun navigateToTopLevelDestinationClearsBackStack() {
    val destination = TopLevelDestinations.MAIN
    navigationActions.navigateTo(destination)

    val captor = argumentCaptor<NavOptionsBuilder.() -> Unit>()
    verify(navHostController).navigate(eq(destination.route), captor.capture())

    val navOptionsBuilder = NavOptionsBuilder()
    captor.firstValue.invoke(navOptionsBuilder)
    assertThat(navOptionsBuilder.popUpTo, `is`(0))
    assertThat(navOptionsBuilder.launchSingleTop, `is`(true))
    assertThat(navOptionsBuilder.restoreState, `is`(true))
  }

  @Test
  fun navigateToTopLevelDestinationClearsBackStackWithAUTH_PATH() {
    val destinationMocked = mock(TopLevelDestination::class.java)
    `when`(destinationMocked.route).thenReturn(Route.AUTH)
    navigationActions.navigateTo(destinationMocked)

    val captor = argumentCaptor<NavOptionsBuilder.() -> Unit>()
    verify(navHostController).navigate(eq(destinationMocked.route), captor.capture())

    val navOptionsBuilder = NavOptionsBuilder()
    captor.firstValue.invoke(navOptionsBuilder)
    assertThat(navOptionsBuilder.popUpTo, `is`(0))
    assertThat(navOptionsBuilder.launchSingleTop, `is`(true))
    assertThat(navOptionsBuilder.restoreState, `is`(false))
  }
}
