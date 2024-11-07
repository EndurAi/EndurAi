package com.android.sample.ui.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.ui.navigation.NavigationActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.android.sample.R
import com.android.sample.ui.composables.CustomSearchBar
import com.android.sample.ui.composables.TopBar

@Composable
fun ProfileItem(
    profile: UserAccount,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(Color(0xFFEFEFEF), shape = MaterialTheme.shapes.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Gray, shape = CircleShape)
                    .padding(8.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(profile.firstName, style = MaterialTheme.typography.bodyLarge, fontSize = 18.sp)
        }
        content()
    }
}

@Composable
fun ProfileItemWithRequest(
    profile: UserAccount,
    onSendRequestClick: () -> Unit
) {
    var requestSent by remember { mutableStateOf(false) }

    ProfileItem(profile = profile) {
        Button(
            onClick = {
                onSendRequestClick()
                requestSent = !requestSent
            },
            colors = ButtonDefaults.buttonColors(containerColor = if (requestSent) Color.Gray else Color(0xFF3A4DA1)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(if (requestSent) "Request Sent" else "Send Request", color = Color.White)
        }
    }
}

@Composable
fun ProfileItemWithAcceptReject(
    profile: UserAccount,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    ProfileItem(profile = profile) {
        Row {
            Button(
                onClick = onAcceptClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                shape = CircleShape,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Text("Accept", color = Color.White)
            }
            Button(
                onClick = onRejectClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = CircleShape
            ) {
                Text("Reject", color = Color.White)
            }
        }
    }
}

@Composable
fun FriendItem(
    friend: UserAccount,
    isSelected: Boolean,
    onSelectFriend: () -> Unit,
    onRemoveClick: () -> Unit
) {
    ProfileItem(
        profile = friend,
        modifier = Modifier
            .clickable(onClick = onSelectFriend)
            .background(if (isSelected) Color.LightGray else Color.Transparent)
    ) {
        Button(
            onClick = onRemoveClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            shape = CircleShape
        ) {
            Text("Remove", color = Color.White)
        }
    }
}