package com.android.sample.ui.friends

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.android.sample.model.achievements.StatisticsViewModel
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.ui.navigation.NavigationActions

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FriendStatisticsScreen(
    navigationActions: NavigationActions,
    userAccountViewModel: UserAccountViewModel,
    statisticsViewModel: StatisticsViewModel,
) {

//    val friendName = userAccountViewModel.selectedFriend.value?.firstName ?: "Unknown"
    Text(text = "Friend's Name: ")
//    val friendName = userAccountViewModel.getFriendName(friendId)
//    val workoutStatistics by userAccountViewModel.getWorkoutStats(friendId).collectAsState(initial = emptyList())
//
//    val gradientColors = listOf(Color.LightGray, Color.White)
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(brush = Brush.verticalGradient(colors = gradientColors))
//    ) {
//        Column(modifier = Modifier.fillMaxSize()) {
//            TopBarWithBack(
//                title = "$friendName's Workout Stats",
//                onBackClick = onBackClick
//            )
//
//            if (workoutStatistics.isEmpty()) {
//                EmptyStateMessage()
//            } else {
//                LazyColumn(
//                    modifier = Modifier.fillMaxSize(),
//                    verticalArrangement = Arrangement.spacedBy(16.dp),
//                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
//                ) {
//                    items(workoutStatistics) { stat ->
//                        WorkoutStatCard(stat)
//                    }
//                }
//            }
//        }
//    }
}
