package com.android.sample.ui.composables

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDateTime

@Composable
fun DateTimePicker(
    selectedDateTime: LocalDateTime?,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    title : String
) {
    val context = LocalContext.current

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

    OutlinedTextField(
        value =
        selectedDateTime?.let {
            "${it.dayOfMonth} ${it.month.name.lowercase().capitalize()} ${it.year} at ${it.hour}:${it.minute.toString().padStart(2, '0')}"
        } ?: "Select Date and Time",
        onValueChange = { /* No-op since we control the value */},
        readOnly = true,
        label = { Text(title) },
        trailingIcon = {
            IconButton(onClick = { showDateTimePickers() }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Date")
            }
        })
}