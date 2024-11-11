package com.android.sample.ui.friends

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.ui.composables.CustomSearchBar
import com.android.sample.viewmodel.UserAccountViewModel

/** Composable part of the Add Friend screen */
@Composable
fun NewConnectionsContent(searchQuery: MutableState<String>, modifier: Modifier = Modifier, userAccountViewModel: UserAccountViewModel) {
  val newConnections =
      listOf(
          UserAccount(userId = "1", firstName = "Alice"),
          UserAccount(userId = "2", firstName = "Bob"),
          UserAccount(userId = "3", firstName = "Charlie"))

  val filteredConnections =
      newConnections.filter { profile ->
        profile.firstName.contains(searchQuery.value, ignoreCase = true)
      }

  Box(modifier) {
    Column(modifier = Modifier.fillMaxWidth()) {
      CustomSearchBar(
          query = searchQuery.value,
          onQueryChange = { searchQuery.value = it },
          modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp))

      Spacer(modifier = Modifier.height(16.dp))

      LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(filteredConnections) { profile ->
          ProfileItemWithRequest(
              profile = profile, onSendRequestClick = { userAccountViewModel.sendFriendRequest(profile.userId) })
          Spacer(modifier = Modifier.height(8.dp))
        }
      }
    }
  }
}
