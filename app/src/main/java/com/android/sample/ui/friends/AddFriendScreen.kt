package com.android.sample.ui.friends

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.DarkBlue

/** Screen for the option to add a friend to the user's friend list. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriendScreen(
    navigationActions: NavigationActions,
    userAccountViewModel: UserAccountViewModel
) {
  var selectedTab by remember { mutableStateOf("New Connections") }
  val searchQuery = remember { mutableStateOf("") }

  Column(modifier = Modifier.padding(16.dp).testTag("addFriendScreen")) {
    TopBar(navigationActions = navigationActions, title = R.string.add_friends_title)

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth().testTag("tabButtons"),
        horizontalArrangement = Arrangement.SpaceEvenly) {
          TabButton(
              text = "New Connections",
              isSelected = selectedTab == "New Connections",
              onClick = { selectedTab = "New Connections" },
              modifier = Modifier.testTag("newConnectionsTabButton"))

          Spacer(modifier = Modifier.width(8.dp))

          TabButton(
              text = "Invitations",
              isSelected = selectedTab == "Invitations",
              onClick = { selectedTab = "Invitations" },
              modifier = Modifier.testTag("invitationsTabButton"))
        }

    Spacer(modifier = Modifier.height(16.dp))

    when (selectedTab) {
      "New Connections" -> {
        NewConnectionsContent(searchQuery, modifier = Modifier.testTag("newConnectionsContent"), userAccountViewModel)
      }
      "Invitations" -> InvitationsContent(modifier = Modifier.testTag("invitationsContent"), userAccountViewModel)
    }
  }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
  Button(
      onClick = onClick,
      colors =
          ButtonDefaults.buttonColors(
              containerColor = if (isSelected) DarkBlue else Color.LightGray),
      shape = RoundedCornerShape(12.dp),
      modifier = modifier) {
        Text(
            text,
            color = if (isSelected) Color.White else Color.Black,
            fontWeight = FontWeight.Bold)
      }
}
