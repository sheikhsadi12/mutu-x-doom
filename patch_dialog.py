import re

with open('app/src/main/java/com/example/ui/routine/DashboardScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
'''fun EditRoutineDialog(
    routine: RoutineEntity,
    onDismiss: () -> Unit,
    onSave: (RoutineEntity) -> Unit
) {''',
'''fun EditRoutineDialog(
    routine: RoutineEntity,
    onDismiss: () -> Unit,
    onSave: (RoutineEntity) -> Unit,
    scale: Float = 1.0f
) {''')

content = content.replace(
'''    if (showEditDialog && currentRoutine != null) {
        EditRoutineDialog(
            routine = currentRoutine!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedRoutine ->
                viewModel.updateRoutine(updatedRoutine)
                showEditDialog = false
            }
        )
    }''',
'''    if (showEditDialog && currentRoutine != null) {
        EditRoutineDialog(
            routine = currentRoutine!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedRoutine ->
                viewModel.updateRoutine(updatedRoutine)
                showEditDialog = false
            },
            scale = scale
        )
    }''')

with open('app/src/main/java/com/example/ui/routine/DashboardScreen.kt', 'w') as f:
    f.write(content)
