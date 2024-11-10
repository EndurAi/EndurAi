package com.android.sample.model.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class CalendarViewModel : ViewModel() {
    private val _selectedDate = MutableStateFlow(LocalDateTime.now())
    val selectedDate: StateFlow<LocalDateTime> = _selectedDate

    fun updateSelectedDate(date: LocalDateTime) {
        viewModelScope.launch {
            _selectedDate.emit(date)
        }
    }
}