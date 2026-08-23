import re

# Fix ViewModel
with open('app/src/main/java/com/example/ui/routine/RoutineViewModel.kt', 'r') as f:
    vm_content = f.read()

add30days_code = '''
    fun addNext30Days() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val latestDate = _currentDate.value
                for (i in 1..30) {
                    val newDate = latestDate.plusDays(i.toLong())
                    repository.updateRoutine(
                        RoutineEntity(
                            dateKey = newDate.toString(),
                            displayDate = "${newDate.dayOfMonth} Month",
                            phase = "Phase X",
                            target = "NEW TARGET ASSIGNED",
                            morning = "Pending data upload...",
                            noon = "Pending data upload...",
                            night = "Pending data upload...",
                            ratio = "3:1"
                        )
                    )
                }
            }
            withContext(Dispatchers.Main) {
                RoutineWidget().updateAll(getApplication())
            }
        }
    }
}
'''
vm_content = vm_content.replace('}\n', '}\n' + add30days_code).replace('}\n\n    fun addNext30Days()', '\n    fun addNext30Days()')

# Correct the replacement logic so we don't duplicate `}`
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

    fun addNext30Days() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val latestDate = _currentDate.value
                for (i in 1..30) {
                    val newDate = latestDate.plusDays(i.toLong())
                    repository.updateRoutine(
                        RoutineEntity(
                            dateKey = newDate.toString(),
                            displayDate = "${newDate.dayOfMonth} Month",
                            phase = "Phase X",
                            target = "NEW TARGET ASSIGNED",
                            morning = "Pending data upload...",
                            noon = "Pending data upload...",
                            night = "Pending data upload...",
                            ratio = "3:1"
                        )
                    )
                }
            }
            withContext(Dispatchers.Main) {
                RoutineWidget().updateAll(getApplication())
            }
        }
    }
}
''')

# Fix RoutineWidget
with open('app/src/main/java/com/example/widget/RoutineWidget.kt', 'r') as f:
    widget_content = f.read()

# Remove the late imports from string
widget_content = widget_content.replace('import androidx.glance.appwidget.action.ActionCallback', '')
widget_content = widget_content.replace('import androidx.glance.action.ActionParameters\n', '')

import_str = 'import androidx.glance.appwidget.action.ActionCallback\nimport androidx.glance.action.ActionParameters\nimport androidx.glance.action.actionParametersOf\nimport androidx.glance.appwidget.action.actionRunCallback'
if 'ActionCallback' not in widget_content[:1500]: # Ensure it's imported at the top
    widget_content = widget_content.replace('import androidx.glance.text.FontFamily', import_str + '\nimport androidx.glance.text.FontFamily')

with open('app/src/main/java/com/example/widget/RoutineWidget.kt', 'w') as f:
    f.write(widget_content)

