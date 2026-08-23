import re

with open('app/src/main/java/com/example/widget/RoutineWidget.kt', 'r') as f:
    content = f.read()

# Replace getRoutineByDate with getRoutineByDateKey
content = content.replace('AppDatabase.getDatabase(context).routineDao().getRoutineByDate(dateStr)', 'AppDatabase.getDatabase(context).routineDao().getRoutineByDateKey(currentDate.toString())')

# Now add CycleRatioActionCallback at the bottom
callback_code = '''
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.action.ActionParameters

class CycleRatioActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val dateKeyParam = ActionParameters.Key<String>("dateKey")
        val dateKey = parameters[dateKeyParam] ?: return
        
        val db = AppDatabase.getDatabase(context)
        val routine = db.routineDao().getRoutineByDateKeySync(dateKey) ?: return
        
        val ratios = listOf("3:1", "3:2", "3:3", "2:1", "2:2", "4:1", "1:2", "2:4")
        val currentIndex = ratios.indexOf(routine.ratio)
        val nextIndex = (currentIndex + 1) % ratios.size
        
        db.routineDao().updateRoutine(routine.copy(ratio = ratios[nextIndex]))
        RoutineWidget().update(context, glanceId)
    }
}
'''
if 'CycleRatioActionCallback' not in content:
    content += callback_code

with open('app/src/main/java/com/example/widget/RoutineWidget.kt', 'w') as f:
    f.write(content)

