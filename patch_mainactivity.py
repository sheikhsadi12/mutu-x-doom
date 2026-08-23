import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

import_str = '''import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import java.util.concurrent.TimeUnit
import java.util.Calendar
import com.example.widget.WidgetUpdateWorker'''

content = content.replace('import com.example.ui.theme.MyApplicationTheme', import_str + '\nimport com.example.ui.theme.MyApplicationTheme')

work_logic = '''        super.onCreate(savedInstanceState)
        
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
'''

content = content.replace('        super.onCreate(savedInstanceState)', work_logic)

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
