package com.android.sample.model.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

class CalendarViewModel : ViewModel() {
    private val _selectedDate = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    fun updateSelectedDate(date: LocalDate) {
        viewModelScope.launch {
            _selectedDate.emit(date)
        }
    }
}