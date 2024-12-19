package com.android.sample.ui.friends

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.ui.composables.TextDialog

/** Composable part of the Add Friend screen */
@SuppressLint("SuspiciousIndentation", "StateFlowValueCalledInComposition")
@Composable
fun InvitationsContent(modifier: Modifier, userAccountViewModel: UserAccountViewModel) {

  LaunchedEffect(Unit) { userAccountViewModel.fetchReceivedRequests() }

  val invitations by userAccountViewModel.receivedRequests.collectAsState()

  Box(modifier) {
    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
    Column(modifier = Modifier.fillMaxWidth()) {
      Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        if (invitations.isNotEmpty()) {
          TextDialog(
              userAccountViewModel.userAccount.value?.let {
                stringResource(id = R.string.welcome_message_invitations, it.firstName)
              } ?: "")
        } else {
          TextDialog(stringResource(id = R.string.NoInvitations))
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(invitations) { profile ->
          ProfileItemWithAcceptReject(
              profile = profile,
              onAcceptClick = { userAccountViewModel.acceptFriendRequest(profile.userId) },
              onRejectClick = { userAccountViewModel.rejectFriendRequest(profile.userId) })
          Spacer(modifier = Modifier.height(8.dp))
        }
      }
    }
  }
}
