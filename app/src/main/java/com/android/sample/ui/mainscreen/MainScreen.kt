package com.android.sample.ui.mainscreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_OF_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("mainScreen"),
      topBar = {
        TopAppBar(
            title = { Text("Main Screen") },
            actions = {
              IconButton(onClick = { navigationActions.navigateTo("Settings Screen") }) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = Color.Black)
              }
            })
      },
      content = { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center) {
              Text("No content yet")
            }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_OF_TOP_LEVEL_DESTINATIONS,
            selectedItem = navigationActions.currentRoute())
      })
}
