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
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.ui.theme.DarkBlue
import com.android.sample.ui.theme.Green
import com.android.sample.ui.theme.ProfileBlue
import com.android.sample.ui.theme.profileFontSize

/** Composable for the Profile cards */
@Composable
fun ProfileItem(
    profile: UserAccount,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
  Row(
      modifier =
          modifier
              .fillMaxWidth()
              .padding(12.dp)
              .background(color = ProfileBlue, shape = RoundedCornerShape(16.dp))
              .padding(16.dp), // Inner padding for better spacing
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
              modifier =
                  Modifier.size(56.dp)
                      .background(Color.White, shape = CircleShape), // Light blue circle
              contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(32.dp),
                    tint = DarkBlue)
              }
          Spacer(modifier = Modifier.width(16.dp))
          Text(
              text = profile.firstName,
              style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
              color = Color.Black, // Black text for contrast
              fontWeight = FontWeight.Bold)
        }
        content()
      }
}

/** Profile item with Accept and Reject buttons */
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
          colors = ButtonDefaults.buttonColors(containerColor = Green), // Green button
          shape = RoundedCornerShape(16.dp), // Rounded buttons for consistency
          modifier = Modifier.height(36.dp)) {
            Text(
                text = "Accept",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = profileFontSize)
          }
      Button(
          onClick = onRejectClick,
          colors = ButtonDefaults.buttonColors(containerColor = Red), // Red button
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.height(36.dp)) {
            Text(
                text = stringResource(id = R.string.Reject),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = profileFontSize)
          }
    }
  }
}

/** Profile item with Request Button */
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
        colors =
            ButtonDefaults.buttonColors(
                containerColor =
                    if (requestSent) Color.Gray
                    else Color.Blue // Gray for sent, dark blue for active
                ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.height(36.dp)) {
          Text(
              text =
                  if (requestSent) stringResource(R.string.RequestSent)
                  else stringResource(R.string.SendRequest),
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp)
        }
  }
}

/** Friend Item using ProfileItem */
@Composable
fun FriendItem(
    friend: UserAccount,
    isSelected: Boolean,
    onSelectFriend: () -> Unit,
    onRemoveClick: () -> Unit
) {
  ProfileItem(
      profile = friend, modifier = Modifier.clickable(onClick = onSelectFriend).padding(8.dp)) {
        Button(
            onClick = onRemoveClick,
            colors = ButtonDefaults.buttonColors(containerColor = Red), // Red button
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.height(36.dp)) {
              Text(
                  text = stringResource(id = R.string.Remove),
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp)
            }
      }
}
