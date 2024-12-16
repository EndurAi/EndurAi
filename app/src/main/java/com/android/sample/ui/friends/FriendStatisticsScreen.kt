package com.android.sample.ui.friends

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.model.achievements.StatisticsViewModel
import com.android.sample.model.achievements.WorkoutStatistics
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.model.workout.WorkoutType
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.R


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FriendStatisticsScreen(
    navigationActions: NavigationActions,
    userAccountViewModel: UserAccountViewModel,
    statisticsViewModel: StatisticsViewModel,
) {


    val statisticsList by statisticsViewModel.friendWorkoutStatistics.collectAsState(initial = emptyList())


    LaunchedEffect(Unit) {
        val friendId = userAccountViewModel.selectedFriend.value?.userId ?: "unknown_friend"
        Log.d("FriendStats", "Fetching stats for friendId: $friendId")
        statisticsViewModel.getFriendWorkoutStatistics(friendId)
    }

    val friendName = userAccountViewModel.selectedFriend.value?.firstName ?: "Unknown"


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.LightGray, Color.White)
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TopBar(
            navigationActions = navigationActions,
            title = R.string.FriendsStatsTitle,
            displayArrow = true
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(statisticsList) { workout ->
                WorkoutCard(workout)
            }
        }
    }
}

@Composable
fun WorkoutCard(workout: WorkoutStatistics) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFFBBDEFB), Color(0xFFE3F2FD))
                    )
                )
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Workout Type: ${workout.type}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(
                        id = when (workout.type) {
                            WorkoutType.BODY_WEIGHT -> R.drawable.dumbell_inner_shadow
                            WorkoutType.YOGA -> R.drawable.yoga_innershadow
                            else -> R.drawable.running_innershadow
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "Date: ${workout.date.toLocalDate()}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "Duration: ${workout.duration} min",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "Calories Burnt: ${workout.caloriesBurnt} kcal",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
