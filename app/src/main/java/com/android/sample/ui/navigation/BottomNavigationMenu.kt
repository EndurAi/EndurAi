package com.android.sample.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp


@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: String
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .testTag("bottomNavigationMenu"),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        tabList.forEach { tab ->
            NavigationBarItem(
                icon = { Icon(tab.icon, contentDescription = null) },
                label = { Text(tab.textId) },
                selected = tab.route == selectedItem,
                onClick = { onTabSelect(tab) },
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .testTag(tab.textId)
            )
        }
    }
}