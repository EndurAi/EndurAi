package com.android.sample.ui.friends

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.ui.composables.Bubbles
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.BlueTabLight
import com.android.sample.ui.theme.BlueTabStrong

/** Screen for the option to add a friend to the user's friend list. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriendScreen(
    navigationActions: NavigationActions,
    userAccountViewModel: UserAccountViewModel
) {
  var selectedTab by remember { mutableStateOf(Tab.NEW_CONNECTIONS) }

  Box(modifier = Modifier.fillMaxSize()) {
    // Background content

    Bubbles()
    // Foreground content
    Column(modifier = Modifier.fillMaxSize().testTag("addFriendScreen")) {
      TopBar(navigationActions = navigationActions, title = R.string.add_friends_title)

      Spacer(modifier = Modifier.height(16.dp))

      Row(
          modifier = Modifier.fillMaxWidth().testTag("tabButtons"),
          horizontalArrangement = Arrangement.Center) {
            TabButton(
                tab = Tab.NEW_CONNECTIONS,
                isSelected = selectedTab == Tab.NEW_CONNECTIONS,
                onClick = { selectedTab = Tab.NEW_CONNECTIONS },
                modifier = Modifier.weight(1f).testTag("newConnectionsTabButton"))

            Spacer(modifier = Modifier.width(8.dp))

            TabButton(
                tab = Tab.INVITATIONS, // Pass enum value
                isSelected = selectedTab == Tab.INVITATIONS,
                onClick = { selectedTab = Tab.INVITATIONS },
                modifier = Modifier.weight(1f).testTag("invitationsTabButton"))
          }

      Spacer(modifier = Modifier.height(16.dp))

      when (selectedTab) {
        Tab.NEW_CONNECTIONS -> {
          NewConnectionsContent(
              searchQuery = remember { mutableStateOf("") },
              modifier = Modifier.testTag("newConnectionsContent"),
              userAccountViewModel = userAccountViewModel)
        }
        Tab.INVITATIONS -> {
          InvitationsContent(
              modifier = Modifier.testTag("invitationsContent"),
              userAccountViewModel = userAccountViewModel)
        }
      }
    }
  }
}

@Composable
fun TabButton(tab: Tab, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Button(
      onClick = onClick,
      colors =
          ButtonDefaults.buttonColors(
              containerColor =
                  if (isSelected) BlueTabStrong
                  else BlueTabLight // Dark blue for selected, light blue for unselected
              ),
      shape =
          RoundedCornerShape(
              topStart = 50.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 50.dp),
      elevation =
          ButtonDefaults.elevatedButtonElevation(
              defaultElevation = 8.dp, // Shadow visible only for the button
              pressedElevation = 4.dp, // Reduce shadow when button is pressed
              disabledElevation = 0.dp // No shadow when button is disabled
              ),
      modifier =
          modifier
              .padding(4.dp) // Add spacing between buttons
              .height(40.dp) // Adjust height for better alignment
      ) {
        Text(
            text =
                stringResource(
                    id =
                        when (tab) {
                          Tab.NEW_CONNECTIONS -> R.string.SocialNewConnections
                          Tab.INVITATIONS -> R.string.SocialInvitations
                        }),
            color =
                if (isSelected) Color.White
                else Color.Black, // White text for selected, black for unselected
            fontWeight = FontWeight.Bold)
      }
}

enum class Tab {
  NEW_CONNECTIONS,
  INVITATIONS
}
