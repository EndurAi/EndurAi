package com.android.sample.ui.friends

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.achievements.StatisticsViewModel
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.ui.composables.TextDialog
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.AddPurple

/** Screen to view the list of Friends */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    navigationActions: NavigationActions,
    userAccountViewModel: UserAccountViewModel,
    statisticsViewModel: StatisticsViewModel
) {
  val searchQuery = remember { mutableStateOf("") }

  LaunchedEffect(Unit) { userAccountViewModel.fetchFriends() }

  val selectedFriends = remember { mutableStateListOf<String>() }
  val friendsList by userAccountViewModel.friends.collectAsState()
  val filteredFriendsList =
      friendsList.filter { friend ->
        friend.firstName.contains(searchQuery.value, ignoreCase = true)
      }

  // Gradient background
  val gradientColors = listOf(Color.LightGray, Color.White) // Light gray to soft white gradient
  Box(
      modifier =
          Modifier.fillMaxSize()
              .background(brush = Brush.verticalGradient(colors = gradientColors))) {
        Column(modifier = Modifier.fillMaxSize().testTag("friendsScreen")) {
          TopBar(navigationActions = navigationActions, title = R.string.friends_title)
          Spacer(modifier = Modifier.height(16.dp))

          SearchBarWithAddButton(searchQuery, navigationActions)

          Spacer(modifier = Modifier.height(16.dp))

          if (filteredFriendsList.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                  // Image with shadow
                  Image(
                      painter = painterResource(id = R.drawable.sadface), // Replace with your image
                      contentDescription = "Sad Image",
                      modifier =
                          Modifier.size(200.dp)
                              .align(Alignment.CenterHorizontally)
                              .shadow(12.dp, CircleShape) // Add subtle shadow
                      )
                  Spacer(modifier = Modifier.height(16.dp))
                  TextDialog(stringResource(R.string.NoFriends))
                }
          } else {
            // Friends List (Unchanged)
            LazyColumn(modifier = Modifier.fillMaxWidth().testTag("friendsList")) {
              items(filteredFriendsList) { friend ->
                FriendItem(
                    friend = friend,
                    isSelected = selectedFriends.contains(friend.userId),
                    onSelectFriend = {
                      userAccountViewModel.selectFriend(friend)
                      navigationActions.navigateTo(Screen.FRIEND_STATS)
                    },
                    onRemoveClick = { userAccountViewModel.removeFriend(friend.userId) },
                )
                Spacer(modifier = Modifier.height(8.dp))
              }
            }
          }

          if (selectedFriends.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navigationActions.navigateTo(Screen.FRIEND_STATS) },
                modifier =
                    Modifier.fillMaxWidth().padding(horizontal = 8.dp).testTag("inviteButton"),
                shape = RoundedCornerShape(8.dp),
                colors =
                    ButtonDefaults.buttonColors(containerColor = AddPurple) // Purple background
                ) {
                  Text(
                      text = stringResource(R.string.invite_to_workout),
                      color = Color.White,
                      fontWeight = FontWeight.Bold)
                }
          }
        }
      }
}

@Composable
fun SearchBarWithAddButton(
    searchQuery: MutableState<String>,
    navigationActions: NavigationActions
) {
  Row(
      modifier =
          Modifier.testTag("searchBarRow")
              .fillMaxWidth()
              .height(50.dp)
              .padding(horizontal = 16.dp)
              .background(
                  Color.White,
                  shape =
                      RoundedCornerShape(
                          topStart = 25.dp,
                          topEnd = 10.dp,
                          bottomStart = 10.dp,
                          bottomEnd = 25.dp)),
      verticalAlignment = Alignment.CenterVertically) {
        // Search Field
        Row(
            modifier =
                Modifier.testTag("searchBar").weight(1f).fillMaxHeight().padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically) {
              Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = "Search Icon",
                  tint = Color.Gray)
              Spacer(modifier = Modifier.width(8.dp))
              TextField(
                  value = searchQuery.value,
                  onValueChange = { searchQuery.value = it },
                  placeholder = { Text("Search Bar", color = Color.Gray) },
                  modifier = Modifier.fillMaxWidth(),
                  colors =
                      TextFieldDefaults.colors(
                          focusedTextColor = Color.Black,
                          unfocusedTextColor = Color.Black,
                          focusedPlaceholderColor = Color.Gray,
                          unfocusedPlaceholderColor = Color.Gray,
                          focusedContainerColor = Color.Transparent,
                          unfocusedContainerColor = Color.Transparent,
                          cursorColor = Color.Black),
                  maxLines = 1,
                  singleLine = true)
            }

        // Add Button
        Button(
            onClick = { navigationActions.navigateTo(Screen.ADD_FRIEND) },
            modifier =
                Modifier.testTag("addFriendButton")
                    .fillMaxHeight() // Ensure it stretches fully vertically
                    .width(90.dp), // Optional fixed width
            shape =
                RoundedCornerShape(
                    topStart = 0.dp, topEnd = 10.dp, bottomStart = 0.dp, bottomEnd = 25.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AddPurple) // Purple color
            ) {
              Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = "Add Friend",
                  tint = Color.White)
            }
      }
}
