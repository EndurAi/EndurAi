package com.android.sample.ui.mainscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_OF_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.R
import com.android.sample.model.userAccount.Gender
import com.android.sample.model.userAccount.HeightUnit
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.WeightUnit
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.theme.Blue
import com.android.sample.ui.theme.DarkBlue
import com.android.sample.ui.theme.DarkBlue2
import com.android.sample.ui.theme.Grey
import com.android.sample.ui.theme.GreyLight
import java.util.Date

@Composable
fun OverviewScreen(navigationActions: NavigationActions) {
    //Temp until got real account
    val account = UserAccount("","Micheal", "Phelps", 1.8f, HeightUnit.METER, 70f, WeightUnit.KG, Gender.MALE, Date(), "")
    val profile = R.drawable.homme

    val workouts = listOf(
        BodyWeightWorkout(
            workoutId = "1",
            name = "Run in Lavaux",
            description = "Enjoying Lavaux with user2 and user3",
            warmup = true,
            userIdSet = mutableSetOf("user1", "user2", "user3")
        ),
        YogaWorkout(
            workoutId = "3",
            name = "After Comparch relax",
            description = "Chilling time",
            warmup = true,
            userIdSet = mutableSetOf("user1")
        ),

        BodyWeightWorkout(
            workoutId = "2",
            name = "Summer body",
            description = "Be ready for the summer",
            warmup = false,
            userIdSet = mutableSetOf("user1", "user4")
        ),
    )

    Scaffold(
        modifier = Modifier.testTag("mainScreen"),
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row with Profile Picture and Settings Icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkBlue)
                        .padding(vertical = 16.dp) // Ajouter un padding vertical uniquement
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        // Profile image (replace with your resource ID)
                        Image(
                            painter = painterResource(id = profile),
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "What's up Michael ?", style = MaterialTheme.typography.titleSmall.copy(fontSize = 30.sp), color = Color.White)
                    }

                    Spacer(Modifier.weight(1f))

                    // Settings Icon
                    IconButton(
                        onClick = { navigationActions.navigateTo("Settings Screen") },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                // My workout sessions section
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "My workout sessions",
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 25.sp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(GreyLight)
                            .padding(10.dp)
                    ){

                        for (workout in workouts.take(2)) {
                            WorkoutCard(workout, profile)
                        }

                        // Si plus de 2 workouts, affiche le bouton "View All"

                            Button(
                                onClick = { /* Navigate to screen displaying all workouts */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DarkBlue2,
                                    contentColor = Color.White
                                )

                            ) {
                                Text(
                                    text = "View all",
                                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 15.sp),
                                )
                            }

                    }


                }

                // Quick workout section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Quick workout",
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 25.sp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(GreyLight)
                            .padding(10.dp)
                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            QuickWorkoutIcon(R.drawable.running_man)
                            QuickWorkoutIcon(R.drawable.pushups)
                            QuickWorkoutIcon(R.drawable.yoga)
                            QuickWorkoutIcon(R.drawable.dumbbell)
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth()
                ){
                    // New Workout plan button
                    Text("New workout plan",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 25.sp),)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 16.dp)
                            .height(48.dp)
                            .background(Grey, RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "New Workout",
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

            }
        },
        bottomBar = {
            Column (
                modifier = Modifier.background(Blue),
            ){
                BottomNavigationMenu(
                    onTabSelect = { route -> navigationActions.navigateTo(route) },
                    tabList = LIST_OF_TOP_LEVEL_DESTINATIONS,
                    selectedItem = navigationActions.currentRoute(),
                )
            }

        }
    )
}

@Composable
fun WorkoutCard(workout: Workout, profile: Int) {
    val workoutImage = when (workout) {
        is BodyWeightWorkout -> R.drawable.pushups
        is YogaWorkout -> R.drawable.yoga
        else -> R.drawable.dumbbell
    }

    Card(
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Blue) // Couleur de fond bleu clair
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Padding interne dans la carte
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = workout.name, style = MaterialTheme.typography.titleSmall.copy(fontSize = 17.sp))
                Text(text = workout.description, style = MaterialTheme.typography.bodyMedium)
                Image(
                    painter = painterResource(id = profile),
                    contentDescription = "Participant",
                    modifier = Modifier.size(15.dp)
                )
            }
            Image(
                painter = painterResource(id = workoutImage),
                contentDescription = "Workout Icon",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}


@Composable
fun QuickWorkoutIcon(iconId: Int) {
    Box(
        modifier = Modifier
            .size(75.dp)
            .background(Blue, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = "Quick Workout Icon",
            modifier = Modifier.size(35.dp)
        )
    }
}



