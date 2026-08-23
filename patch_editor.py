import re

with open('app/src/main/java/com/example/ui/routine/WidgetEditorScreen.kt', 'r') as f:
    content = f.read()

# Add WidgetPreviewEngine to the top of the settings column
import_str = 'import com.example.ui.routine.WidgetPreviewEngine\nimport androidx.compose.foundation.rememberScrollState\nimport androidx.compose.foundation.verticalScroll'
content = content.replace('import kotlinx.coroutines.launch', 'import kotlinx.coroutines.launch\n' + import_str)

column_start = '''        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),'''

column_replacement = '''        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),'''

content = content.replace(column_start, column_replacement)

scale_section = '''            // SCALE
            Column {'''

scale_replacement = '''            // WIDGET PREVIEW ENGINE
            WidgetPreviewEngine(scale, opacity, themeColor, fontFamily)

            // SCALE
            Column {'''

content = content.replace(scale_section, scale_replacement)

with open('app/src/main/java/com/example/ui/routine/WidgetEditorScreen.kt', 'w') as f:
    f.write(content)
