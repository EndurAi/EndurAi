package com.android.sample.ui.friends

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.ui.composables.CustomSearchBar
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.DarkBlue
import com.android.sample.viewmodel.UserAccountViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Screen to view the list of Friends */
@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    navigationActions: NavigationActions, userAccountViewModel: UserAccountViewModel,
) {
  val searchQuery = remember { mutableStateOf("") }

    val userAccount by userAccountViewModel.userAccount.collectAsState()


  val selectedFriends = remember { mutableStateListOf<String>() }

    val filteredFriendsList = remember(userAccount, searchQuery.value) {
        userAccountViewModel.getFriends().filter { friend ->
            friend.firstName.contains(searchQuery.value, ignoreCase = true)
        }
    }

  Column(modifier = Modifier.padding(16.dp).testTag("friendsScreen")) {
    TopBar(navigationActions = navigationActions, title = R.string.friends_title)

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).testTag("searchBarRow"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
          CustomSearchBar(
              query = searchQuery.value,
              onQueryChange = { searchQuery.value = it },
              modifier = Modifier.weight(1f).testTag("searchBar"))

          Spacer(modifier = Modifier.width(8.dp))

          Button(
              onClick = { navigationActions.navigateTo(Screen.ADD_FRIEND) },
              shape = CircleShape,
              colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
              modifier = Modifier.testTag("addFriendButton")) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Friend")
                Text("Add", color = Color.Black)
              }
        }

    Spacer(modifier = Modifier.height(16.dp))

    // Friends List
    LazyColumn(modifier = Modifier.fillMaxWidth().testTag("friendsList")) {
      items(filteredFriendsList) { friend ->
        FriendItem(
            friend = friend,
            isSelected = selectedFriends.contains(friend.userId),
            onSelectFriend = {
              if (selectedFriends.contains(friend.userId)) {
                selectedFriends.remove(friend.userId)
              } else {
                selectedFriends.add(friend.userId)
              }
            },
            onRemoveClick = { "not yet implemented" },
        )
        Spacer(modifier = Modifier.height(8.dp))
      }
    }

    // "Invite to a workout" button at the bottom if pressed on a card
    if (selectedFriends.isNotEmpty()) {
      Spacer(modifier = Modifier.height(16.dp))
      Button(
          onClick = { /* Trigger invite to workout action */},
          modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).testTag("inviteButton"),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)) {
            Text(
                text = stringResource(R.string.invite_to_workout),
                color = Color.White,
                fontWeight = FontWeight.Bold)
          }
    }
  }
}
