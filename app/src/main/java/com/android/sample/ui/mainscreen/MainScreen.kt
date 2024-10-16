package com.android.sample.ui.mainscreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_OF_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

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
              Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center) {
                    Text(
                        text = "New Workout Plan",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp))
                    Button(
                        onClick = { navigationActions.navigateTo(Screen.SESSIONSELECTION) },
                        modifier = Modifier.size(width = 200.dp, height = 100.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary)) {
                          Icon(
                              imageVector = Icons.Default.Add,
                              contentDescription = "New Workout",
                              modifier = Modifier.size(48.dp),
                              tint = Color.White)
                        }
                  }
            }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_OF_TOP_LEVEL_DESTINATIONS,
            selectedItem = navigationActions.currentRoute())
      })
}
