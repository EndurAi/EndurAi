package com.android.sample.ui.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.ui.theme.DarkBlue
import com.android.sample.ui.theme.LightGrey

/** Composable for the Profile cards */
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
            .background(Color(0xFFB3E5FC), shape = RoundedCornerShape(16.dp)) // Light blue background with rounded corners
            .padding(16.dp), // Inner padding for better spacing
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFB3E5FC), shape = CircleShape), // Matching light blue circle
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(32.dp),
                    tint = Color(0xFF1E3A8A) // Dark blue icon color
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = profile.firstName,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                color = Color.Black, // Black text
                fontWeight = FontWeight.Bold
            )
        }
        content()
    }
}

@Composable
fun ProfileItemWithAcceptReject(
    profile: UserAccount,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    ProfileItem(profile = profile) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onAcceptClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Green button color
                shape = RoundedCornerShape(16.dp), // Rounded button
                modifier = Modifier.height(36.dp) // Adjust button height
            ) {
                Text(
                    text = "Accept",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Button(
                onClick = onRejectClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)), // Red button color
                shape = RoundedCornerShape(16.dp), // Rounded button
                modifier = Modifier.height(36.dp) // Adjust button height
            ) {
                Text(
                    text = "Reject",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ProfileItemWithRequest(
    profile: UserAccount,
    sentRequests: List<UserAccount>,
    onSendRequestClick: () -> Unit
) {
    val requestSent = remember { sentRequests.any { it.userId == profile.userId } }

    ProfileItem(profile = profile) {
        Button(
            onClick = {
                if (!requestSent) {
                    onSendRequestClick()
                }
            },
            enabled = !requestSent,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (requestSent) Color.Gray else Color(0xFF1E3A8A) // Dark blue for active, gray for sent
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.height(36.dp) // Adjust height for consistency
        ) {
            Text(
                text = if (requestSent) "Request Sent" else "Send Request",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
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
            .background(
                color = if (isSelected) Color.LightGray else Color(0xFFB3E5FC), // Background changes on selection
                shape = RoundedCornerShape(16.dp) // Rounded corners for the item
            )
    ) {
        Button(
            onClick = onRemoveClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)), // Red button for remove
            shape = RoundedCornerShape(16.dp), // Consistent rounded corners
            modifier = Modifier.height(36.dp) // Uniform button height
        ) {
            Text(
                text = "Remove",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}


