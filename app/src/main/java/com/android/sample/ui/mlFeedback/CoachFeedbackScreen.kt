package com.android.sample.ui.mlFeedback

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.sample.R
import com.android.sample.model.camera.CameraViewModel
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun CoachFeedbackScreen(navigationActions: NavigationActions, cameraViewModel: CameraViewModel) {
    Scaffold(
        topBar = {
            TopBar(
                navigationActions = navigationActions,
                title = R.string.coach_feedback_title,
            )
        },
        content = {
            
        }
    )
}