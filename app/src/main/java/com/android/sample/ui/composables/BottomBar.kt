package com.android.sample.ui.composables

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.ui.navigation.LIST_OF_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.navigation.TopLevelDestinations
import com.android.sample.ui.theme.BlueGradient
import com.android.sample.ui.theme.NeutralGrey
import com.android.sample.ui.theme.Transparent
import com.android.sample.ui.theme.White

/**
 * A composable function that represents a customizable and animated bottom navigation bar.
 *
 * The `BottomBar` provides navigation to the top-level destinations defined in
 * `LIST_OF_TOP_LEVEL_DESTINATIONS`. It highlights the currently selected destination with
 * animations for size and position while maintaining an intuitive navigation experience.
 *
 * @param navigationActions The `NavigationActions` instance used to manage navigation between
 *   destinations.
 */
@Composable
fun BottomBar(
    navigationActions: NavigationActions,
) {

  Box(
      modifier =
          Modifier.fillMaxWidth()
              .height(60.dp)
              .padding(horizontal = 16.dp)
              .shadow(elevation = 5.dp, shape = RoundedCornerShape(30.dp), clip = false)
              .background(color = White, shape = RoundedCornerShape(30.dp))
              .testTag("BottomBar")) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically) {
              LIST_OF_TOP_LEVEL_DESTINATIONS.forEach { destination ->
                val isSelected =
                    navigationActions.currentRoute() ==
                        (destination.route +
                            " Screen") // navigationActions.currentRoute() returns the Screen....
                val isAdd = destination.route == TopLevelDestinations.ADD.route

                  var isAddSelected = false

                // Animation for vertical offset
                val offsetY by animateDpAsState(targetValue = if (isSelected) (-15).dp else 0.dp)

                  val offsetYAdd by animateDpAsState(targetValue = if (isAddSelected) (-15).dp else 0.dp)

                val offsetXBodyweight by
                    animateDpAsState(targetValue = if (isAddSelected) (-15).dp else 0.dp)

                val offsetXYoga by animateDpAsState(targetValue = if (isAddSelected) (15).dp else 0.dp)

                val offsetYWorkout by
                    animateDpAsState(targetValue = if (isAddSelected) (-25).dp else 0.dp)

                // Animation for circle size
                val circleSize by animateDpAsState(targetValue = if (isSelected) 55.dp else 30.dp)

                  val circleSizeAdd by animateDpAsState(targetValue = if (isAddSelected) 55.dp else 30.dp)

                if (isAdd) {

                    Box(
                        modifier = Modifier.fillMaxWidth().weight(11f),
                        contentAlignment = Alignment.Center) {

                        Row(
                            horizontalArrangement = Arrangement.Center
                        )
                        {

                        Column(
                            Modifier.offset(y = offsetYWorkout),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier =
                                if (isAddSelected)
                                    Modifier.background(BlueGradient, CircleShape)
                                        .size(circleSizeAdd)
                                        .clickable(
                                            onClick = {
                                                    navigationActions.navigateTo(Screen.IMPORTORCREATE_BODY_WEIGHT)
                                            })
                                else
                                    Modifier.background(Transparent, CircleShape)
                                        .size(circleSizeAdd)
                                        .clickable(
                                            onClick = {

                                            }),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.outline_fitness_center_24),
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp),
                                    tint = if (isAddSelected) White else Transparent
                                )
                            }
                        }

                            Column(
                                Modifier.offset(y = offsetYAdd),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier =
                                    if (isAddSelected)
                                        Modifier.background(BlueGradient, CircleShape)
                                            .size(circleSizeAdd)
                                            .clickable(
                                                onClick = {
                                                    navigationActions.navigateTo(Screen.RUNNING_SCREEN)
                                                })
                                    else
                                        Modifier.background(Transparent, CircleShape)
                                            .size(circleSizeAdd)
                                            .clickable(
                                                onClick = {
                                                    isAddSelected = true
                                                }),
                                    contentAlignment = Alignment.Center,
                                ) {

                                    if (isAddSelected) {

                                        Icon(
                                            painter = painterResource(R.drawable.outline_directions_run_24),
                                            contentDescription = null,
                                            modifier = Modifier.size(30.dp),
                                            tint = if (isAddSelected) White else Transparent
                                        )


                                    } else {

                                    Icon(
                                        imageVector = destination.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(30.dp),
                                        tint = if (isAddSelected) White else NeutralGrey
                                    )
                                }
                                }
                            }

                            Column(
                                Modifier.offset(y = offsetYWorkout),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier =
                                    if (isSelected)
                                        Modifier.background(BlueGradient, CircleShape)
                                            .size(circleSizeAdd)
                                            .clickable(
                                                onClick = {
                                                    navigationActions.navigateTo(Screen.IMPORTORCREATE_YOGA)
                                                })
                                            .testTag(destination.textId)
                                    else
                                        Modifier.background(Transparent, CircleShape)
                                            .size(circleSizeAdd)
                                            .clickable(
                                                onClick = {

                                                })
                                            .testTag(destination.textId),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_self_improvement_24),
                                        contentDescription = null,
                                        modifier = Modifier.size(30.dp),
                                        tint = if (isAddSelected) White else Transparent
                                    )
                                }
                            }

                    }

                    }




                } else {



                  Box(
                      modifier = Modifier.fillMaxWidth().weight(11f),
                      contentAlignment = Alignment.Center) {
                        Column(
                            Modifier.offset(y = offsetY),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                              Box(
                                  modifier =
                                      if (isSelected)
                                          Modifier.background(BlueGradient, CircleShape)
                                              .size(circleSize)
                                              .clickable(
                                                  onClick = {
                                                    if (isAdd) {
                                                      isAddSelected = true
                                                    } else {
                                                      navigationActions.navigateTo(
                                                          destination.route)
                                                    }
                                                  })
                                              .testTag(destination.textId)
                                      else
                                          Modifier.background(Transparent, CircleShape)
                                              .size(circleSize)
                                              .clickable(
                                                  onClick = {
                                                    if (isAdd) {
                                                      isAddSelected = false
                                                    } else {
                                                      navigationActions.navigateTo(
                                                          destination.route)
                                                    }
                                                  })
                                              .testTag(destination.textId),
                                  contentAlignment = Alignment.Center,
                              ) {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp),
                                    tint = if (isSelected) White else NeutralGrey)
                              }
                            }
                      }
                }
              }
            }
      }
  Spacer(modifier = Modifier.height(70.dp))
}
