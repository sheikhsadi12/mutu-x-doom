import re

with open('app/src/main/java/com/example/ui/routine/RoutineViewModel.kt', 'r') as f:
    content = f.read()

# Add imports
content = content.replace('import java.util.Locale', 'import java.util.Locale\nimport androidx.glance.appwidget.updateAll\nimport com.example.widget.RoutineWidget')

# Update updateRoutine
update_target = '''    fun updateRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            repository.updateRoutine(routine)
        }
    }'''

update_replacement = '''    fun updateRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            repository.updateRoutine(routine)
            RoutineWidget().updateAll(getApplication())
        }
    }'''

content = content.replace(update_target, update_replacement)

# Update addNext30Days
add_target = '''            }
        }
    }
}'''

add_replacement = '''            }
            RoutineWidget().updateAll(getApplication())
        }
    }
}'''

content = content.replace(add_target, add_replacement)

with open('app/src/main/java/com/example/ui/routine/RoutineViewModel.kt', 'w') as f:
    f.write(content)
