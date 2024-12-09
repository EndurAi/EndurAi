package com.android.sample.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Dimensions {
  val ButtonHeight = 50.dp
  val ButtonWidth = 200.dp
  val SmallPadding = 8.dp
  val LargePadding = 16.dp
  val ExtraLargePadding = 32.dp
  val iconSize = 24.dp
}

object FontSizes {
  val BigTitleFontSize = 45.sp
  val TitleFontSize = 20.sp
  val MediumTitleFontSize = 18.sp
  val SubtitleFontSize = 16.sp
  val ButtonFontSize = 14.sp
}

object Shape {
    val buttonShape = RoundedCornerShape(topStart = 40.dp, topEnd = 15.dp, bottomStart = 15.dp, bottomEnd = 40.dp)
    val roundButtonShape = RoundedCornerShape(30.dp)
}
