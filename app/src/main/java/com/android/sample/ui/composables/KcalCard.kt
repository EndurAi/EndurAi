package com.android.sample.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.ui.theme.Black
import com.android.sample.ui.theme.NeutralGrey
import com.android.sample.ui.theme.OpenSans
import com.android.sample.ui.theme.White

@Composable
fun CaloriesDisplay(
    calories: Int,
) {
  Surface(
      modifier =
          Modifier.width(300.dp)
              .height(150.dp)
              .shadow(elevation = 10.dp, shape = RoundedCornerShape(8.dp)),
      color = White,
      shape = RoundedCornerShape(8.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                  text = calories.toString(),
                  fontSize = 80.sp,
                  fontFamily = OpenSans,
                  fontWeight = FontWeight.Bold,
                  color = Black)

              Text(
                  text = stringResource(R.string.Kcal),
                  fontSize = 18.sp,
                  fontFamily = OpenSans,
                  fontWeight = FontWeight.SemiBold,
                  color = NeutralGrey)
            }
      }
}
