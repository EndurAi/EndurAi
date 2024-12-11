package com.android.sample.ui.mlFeedback

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.sample.R
import com.android.sample.model.camera.CameraViewModel
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions

@Composable
fun CoachFeedbackScreen(navigationActions: NavigationActions, cameraViewModel: CameraViewModel) {
    Scaffold(
        topBar = {
            TopBar(
                title = R.string.coach_feedback_title,
                navigationActions = navigationActions
            )
        },
        content = {
            pd ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(pd)
                ) {

                }


        }
    )
}