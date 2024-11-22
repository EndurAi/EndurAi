package com.android.sample.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.ui.theme.Black
import com.android.sample.ui.theme.Blue

@Composable
fun ToggleButton(onClick : (Boolean) -> Unit, isToggled : Boolean, title : String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            color = Black
        )

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = isToggled,
            onCheckedChange = onClick ,
            colors = SwitchDefaults.colors(checkedTrackColor = Blue),
            modifier =
            Modifier.padding(start = 8.dp).testTag("switchToggle"))
    }
}
