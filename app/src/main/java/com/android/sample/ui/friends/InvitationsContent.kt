package com.android.sample.ui.friends

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.ui.theme.Purple20

/** Composable part of the Add Friend screen */
@SuppressLint("SuspiciousIndentation", "StateFlowValueCalledInComposition")
@Composable
fun InvitationsContent(modifier: Modifier, userAccountViewModel: UserAccountViewModel ) {

    LaunchedEffect(Unit) {
        userAccountViewModel.fetchReceivedRequests()
    }


    val invitations by userAccountViewModel.receivedRequests.collectAsState()

  Box(modifier) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Box(
          modifier =
              Modifier.fillMaxWidth()
                  .padding(8.dp)
                  .background(Purple20, shape = RoundedCornerShape(8.dp))
                  .padding(16.dp),
          contentAlignment = Alignment.Center) {
            Text(
                text = userAccountViewModel.userAccount.value?.let {
                    stringResource(id = R.string.welcome_message_invitations, it.firstName)
                } ?: "",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 16.sp)
          }

      Spacer(modifier = Modifier.height(16.dp))

      LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(invitations) { profile ->
          ProfileItemWithAcceptReject(
              profile = profile,
              onAcceptClick = {
                  userAccountViewModel.acceptFriendRequest(profile.userId)
                  userAccountViewModel.fetchReceivedRequests()},
              onRejectClick = {
                  userAccountViewModel.rejectFriendRequest(profile.userId)
                  userAccountViewModel.fetchReceivedRequests()})
          Spacer(modifier = Modifier.height(8.dp))
        }
      }
    }
  }
}
