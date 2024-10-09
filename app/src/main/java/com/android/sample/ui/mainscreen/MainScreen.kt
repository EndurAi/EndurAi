package com.android.sample.ui.mainscreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_OF_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions

@Composable
fun MainScreen(navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("mainScreen"),
      content = { padding -> Text("Main Screen", modifier = Modifier.padding(padding)) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_OF_TOP_LEVEL_DESTINATIONS,
            selectedItem = navigationActions.currentRoute())
      })
}
