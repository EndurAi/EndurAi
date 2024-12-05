package com.android.sample.ui.friends

import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.ui.composables.CustomSearchBar
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.DarkBlue

/** Screen to view the list of Friends */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    navigationActions: NavigationActions,
    userAccountViewModel: UserAccountViewModel
) {
    val searchQuery = remember { mutableStateOf("") }

    LaunchedEffect(Unit) { userAccountViewModel.fetchFriends() }

    val selectedFriends = remember { mutableStateListOf<String>() }

//    val friendsList by userAccountViewModel.friends.collectAsState()

    val friendsList = emptyList<UserAccount>()


    val filteredFriendsList =
        friendsList.filter { friend ->
            friend.firstName.contains(searchQuery.value, ignoreCase = true)
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFEF)) // Light gray background
            .padding(16.dp)
            .testTag("friendsScreen")
    ) {
        TopBar(navigationActions = navigationActions, title = R.string.friends_title)

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar with Add Button (Styled like the Figma)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.White, shape = RoundedCornerShape(topStart = 25.dp, topEnd = 10.dp, bottomStart = 10.dp, bottomEnd = 25.dp)) // Rounded search bar background
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Search Field
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    placeholder = { Text("Search Bar", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedPlaceholderColor = Color.White,
                        unfocusedPlaceholderColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = Color.White),
                    maxLines = 1,
                    singleLine = true
                )
            }

            // Add Button
            Button(
                onClick = { navigationActions.navigateTo(Screen.ADD_FRIEND) },
                modifier = Modifier.size(40.dp), // Match the Figma size
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp), // Remove default padding
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)) // Purple color
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Friend",
                    tint = Color.Black // White icon for the cross
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredFriendsList.isEmpty()) {
            // Empty State Design
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
//                Icon(
//                    imageVector = Icons.Filled.SentimentDissatisfied,
//                    contentDescription = null,
//                    modifier = Modifier.size(150.dp),
//                    tint = Color.Gray
//                )
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFF6C63FF), // Purple background for card
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Oh, you currently don’t have any friends.\nClick on Add to expand your network!",
                        modifier = Modifier.padding(16.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            // Friends List
            LazyColumn(modifier = Modifier.fillMaxWidth().testTag("friendsList")) {
                Log.d("FriendsScreen", "Filtered Friends List: $filteredFriendsList")
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
                        onRemoveClick = {
                            userAccountViewModel.removeFriend(friend.userId)
                            userAccountViewModel.fetchFriends()
                        },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // "Invite to a workout" button at the bottom if pressed on a card
        if (selectedFriends.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Trigger invite to workout action */ },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).testTag("inviteButton"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)) // Purple background
            ) {
                Text(
                    text = stringResource(R.string.invite_to_workout),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}



