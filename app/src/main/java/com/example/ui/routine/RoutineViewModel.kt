package com.example.ui.routine

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.RoutineEntity
import com.example.data.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class RoutineViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RoutineRepository
    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)

    private val _currentDate = MutableStateFlow(LocalDate.of(2024, 8, 22)) // Default to Aug 22 to show data
    val currentDate: StateFlow<LocalDate> = _currentDate.asStateFlow()

    private val _currentRoutine = MutableStateFlow<RoutineEntity?>(null)
    val currentRoutine: StateFlow<RoutineEntity?> = _currentRoutine.asStateFlow()

    init {
        val dao = AppDatabase.getDatabase(application).routineDao()
        repository = RoutineRepository(dao)

        viewModelScope.launch {
            repository.initializeDataIfNeeded()
            // Set current date to today if it exists in the range, otherwise default to Aug 22
            val today = LocalDate.now()
            val todayStr = today.format(dateFormatter)
            val todayRoutine = repository.getRoutineByDate(todayStr).firstOrNull()
            
            if (todayRoutine != null) {
                _currentDate.value = today
            } else {
                _currentDate.value = LocalDate.of(2024, 8, 22)
            }
            
            fetchRoutine()
        }
    }

    private fun fetchRoutine() {
        viewModelScope.launch {
            val dateStr = _currentDate.value.format(dateFormatter)
            repository.getRoutineByDate(dateStr).collect { routine ->
                _currentRoutine.value = routine
            }
        }
    }

    fun nextDay() {
        _currentDate.value = _currentDate.value.plusDays(1)
        fetchRoutine()
    }

    fun previousDay() {
        _currentDate.value = _currentDate.value.minusDays(1)
        fetchRoutine()
    }

    fun updateRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            repository.updateRoutine(routine)
        }
    }
}
