package com.android.sample.ui.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.BlueGradient
import com.android.sample.ui.theme.OpenSans
import com.android.sample.ui.theme.White

/**
 * A composable function that displays a top app bar with a title and a back navigation icon.
 *
 * @param navigationActions The actions to handle navigation events, typically used to manage the
 *   back navigation.
 * @param title The string resource ID for the title text to be displayed in the app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navigationActions: NavigationActions,
    @StringRes title: Int,
    displayArrow: Boolean = true
) {

  androidx.compose.foundation.layout.Box(
      modifier =
          Modifier.fillMaxWidth()
              .wrapContentHeight()
              .background(brush = BlueGradient)
              .height(90.dp)) {
        TopAppBar(
            title = {
              Row() {
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = stringResource(id = title),
                    fontSize = 30.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.SemiBold,
                    color = White,
                    modifier = Modifier.testTag("ScreenTitle"))
              }
            },
            navigationIcon = {
              if (displayArrow) {
                ArrowBack(navigationActions, Color.White)
              }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent),
            modifier = Modifier.testTag("TopBar").align(Alignment.Center))
      }
}
