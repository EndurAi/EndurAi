package com.android.sample.ui.friends

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.sample.model.userAccount.UserAccount
import com.firebase.ui.auth.data.model.User
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(friendsList: List<UserAccount>, onAddClick: () -> Unit, onRemoveClick: (UserAccount) -> Unit, onFriendClick: (UserAccount) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = "",
                onValueChange = { /* Handle search logic */ },
                placeholder = { Text("Search bar") },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onAddClick) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Friend")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        friendsList.forEach { friend ->
            FriendItem(friend, onRemoveClick = { onRemoveClick(friend) }, onClick = { onFriendClick(friend) })
        }
    }
}

@Composable
fun FriendItem(friend: UserAccount, onRemoveClick: () -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(friend.firstName, style = MaterialTheme.typography.bodyLarge)

        Button(onClick = onRemoveClick, colors = ButtonDefaults.buttonColors(containerColor = Color.Red))  {
            Text("Remove", color = Color.White)
        }
    }
}