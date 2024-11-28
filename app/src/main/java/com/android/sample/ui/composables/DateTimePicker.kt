package com.android.sample.ui.composables

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.sample.ui.theme.Dimensions
import com.android.sample.ui.theme.FontSizes
import com.android.sample.ui.theme.LightBackground
import com.android.sample.ui.theme.TitleBlue
import com.android.sample.ui.workout.LeafShape
import java.time.LocalDateTime

@Composable
fun DateTimePicker(
    selectedDateTime: LocalDateTime?,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    title: String
) {
  val context = LocalContext.current
  val displayText =
      remember(selectedDateTime) {
        selectedDateTime?.let {
          "${it.dayOfMonth} ${it.month.name.lowercase().capitalize()} ${it.year} at ${it.hour}:${it.minute.toString().padStart(2, '0')}"
        } ?: "Select Date and Time"
      }

  // Function to launch the Date and Time pickers
  fun showDateTimePickers() {
    val now = LocalDateTime.now()
    val datePicker =
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
              val timePicker =
                  TimePickerDialog(
                      context,
                      { _, hourOfDay, minute ->
                        val selectedDate =
                            LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute)
                        onDateTimeSelected(selectedDate)
                      },
                      now.hour,
                      now.minute,
                      true)
              timePicker.show()
            },
            now.year,
            now.monthValue - 1,
            now.dayOfMonth)
    datePicker.show()
  }

  Column(modifier = Modifier.padding(vertical = 8.dp)) {
    Text(
        text = title,
        style =
            MaterialTheme.typography.bodySmall.copy(
                color = TitleBlue,
                fontWeight = FontWeight.Bold,
                fontSize = FontSizes.SubtitleFontSize),
        modifier = Modifier.padding(bottom = 4.dp))

    Card(
        shape = LeafShape,
        colors = CardDefaults.cardColors(containerColor = LightBackground),
        modifier =
            Modifier.fillMaxWidth().height(Dimensions.ButtonHeight).clickable {
              showDateTimePickers()
            },
        elevation = CardDefaults.cardElevation(4.dp)) {
          Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxSize().padding(horizontal = Dimensions.LargePadding)) {
                Text(
                    text = displayText,
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = if (selectedDateTime == null) Color.Gray else Color.Black),
                    modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select Date",
                    tint = Color.Gray,
                    modifier = Modifier.size(Dimensions.iconSize))
              }
        }
  }
}
