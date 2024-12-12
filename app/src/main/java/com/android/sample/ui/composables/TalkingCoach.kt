package com.android.sample.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.ui.theme.Black
import com.android.sample.ui.theme.BlueGradient
import com.android.sample.ui.theme.White

@Composable
fun TalkingCoach(text: String, size: Dp = 150.dp) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.width(size)
                .background(Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            AnimatedText(
                modifier = Modifier.testTag("animatedText"),
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(color = White)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = R.drawable.endurai_coach),
            contentDescription = "Coach",
            modifier = Modifier.size(size)
                .clip(CircleShape)
                .shadow(8.dp, CircleShape)
                .background(BlueGradient, CircleShape)
                .testTag("coachImage")
        )
    }
}
