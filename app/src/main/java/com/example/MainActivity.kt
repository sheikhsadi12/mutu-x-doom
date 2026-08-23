package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.example.ui.routine.DashboardScreen
import com.example.ui.routine.RoutineViewModel
import com.example.ui.routine.WidgetEditorScreen
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import java.util.concurrent.TimeUnit
import java.util.Calendar
import com.example.widget.WidgetUpdateWorker
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: RoutineViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Schedule daily midnight widget update
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()
        dueDate.set(Calendar.HOUR_OF_DAY, 0)
        dueDate.set(Calendar.MINUTE, 1)
        dueDate.set(Calendar.SECOND, 0)
        
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }
        
        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
        val dailyWorkRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .build()
            
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "MidnightWidgetUpdate",
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyWorkRequest
        )

        enableEdgeToEdge()
        setContent {
            var currentScreen by remember { mutableStateOf("dashboard") }
            
            MyApplicationTheme {
                if (currentScreen == "dashboard") {
                    DashboardScreen(
                        viewModel = viewModel,
                        onOpenWidgetEditor = { currentScreen = "editor" }
                    )
                } else {
                    WidgetEditorScreen(
                        onBack = { currentScreen = "dashboard" }
                    )
                }
            }
        }
    }
}
