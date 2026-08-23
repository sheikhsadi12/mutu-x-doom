import re

with open('app/src/main/java/com/example/ui/routine/RoutineViewModel.kt', 'w') as f:
    f.write('''package com.example.ui.routine

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.glance.appwidget.updateAll
import com.example.widget.RoutineWidget
import com.example.data.AppDatabase
import com.example.data.RoutineEntity
import com.example.data.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class RoutineViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RoutineRepository
    private val _currentDate = MutableStateFlow(LocalDate.now())
    val currentDate: StateFlow<LocalDate> = _currentDate.asStateFlow()

    private val _currentRoutine = MutableStateFlow<RoutineEntity?>(null)
    val currentRoutine: StateFlow<RoutineEntity?> = _currentRoutine.asStateFlow()

    init {
        val dao = AppDatabase.getDatabase(application).routineDao()
        repository = RoutineRepository(dao)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.initializeDataIfNeeded()
            }
            _currentDate.value = LocalDate.now()
            fetchRoutine()
        }
    }

    private fun fetchRoutine() {
        viewModelScope.launch {
            val dateKey = _currentDate.value.toString()
            repository.getRoutineByDateKey(dateKey).collect { routine ->
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
            withContext(Dispatchers.IO) {
                repository.updateRoutine(routine)
            }
            withContext(Dispatchers.Main) {
                RoutineWidget().updateAll(getApplication())
            }
        }
    }
}
''')

