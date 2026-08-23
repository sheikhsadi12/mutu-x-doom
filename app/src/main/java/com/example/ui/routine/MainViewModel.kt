package com.example.ui.routine

import android.app.Application
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.RoutineEntity
import com.example.data.RoutineRepository
import com.example.widget.DoomsdayWidget
import com.example.widget.RoutineWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class MainViewModel(application: Application) : AndroidViewModel(application) {

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
                repository.ensureDataInitialized()
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
                DoomsdayWidget().updateAll(getApplication())
                RoutineWidget().updateAll(getApplication())
            }
        }
    }

    fun addNext30Days() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val latestDate = _currentDate.value
                val dao = AppDatabase.getDatabase(getApplication()).routineDao()
                val list = mutableListOf<RoutineEntity>()
                for (i in 1..30) {
                    val newDate = latestDate.plusDays(i.toLong())
                    list.add(
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
                dao.insertAll(list)
            }
            withContext(Dispatchers.Main) {
                DoomsdayWidget().updateAll(getApplication())
                RoutineWidget().updateAll(getApplication())
            }
        }
    }
}
