import re

with open('app/src/main/java/com/example/ui/routine/WidgetPreviewEngine.kt', 'r') as f:
    content = f.read()

content = content.replace('import com.example.ui.theme.ThorCyan', 'import com.example.ui.theme.ThorCyan\nimport com.example.ui.theme.OrbitronFamily\nimport com.example.ui.theme.HindSiliguriFamily')

font_logic_target = '''    val fontFamily = when(fontPref) {
        "Serif" -> FontFamily.Serif
        "SansSerif" -> FontFamily.SansSerif
        else -> FontFamily.Monospace
    }'''

font_logic_replacement = '''    val fontFamily = when(fontPref) {
        "Serif" -> FontFamily.Serif
        "SansSerif" -> FontFamily.SansSerif
        "Orbitron" -> OrbitronFamily
        "Hind Siliguri" -> HindSiliguriFamily
        else -> FontFamily.Monospace
    }'''
content = content.replace(font_logic_target, font_logic_replacement)

with open('app/src/main/java/com/example/ui/routine/WidgetPreviewEngine.kt', 'w') as f:
    f.write(content)


with open('app/src/main/java/com/example/ui/routine/WidgetEditorScreen.kt', 'r') as f:
    content2 = f.read()

content2 = content2.replace('listOf("Monospace", "Serif", "SansSerif")', 'listOf("Monospace", "Orbitron", "Hind Siliguri")')

with open('app/src/main/java/com/example/ui/routine/WidgetEditorScreen.kt', 'w') as f:
    f.write(content2)

with open('app/src/main/java/com/example/widget/RoutineWidget.kt', 'r') as f:
    content3 = f.read()

content3 = content3.replace('import androidx.glance.appwidget.cornerRadius', 'import androidx.glance.appwidget.cornerRadius\nimport com.example.ui.theme.OrbitronFamily\nimport com.example.ui.theme.HindSiliguriFamily')
font_logic_target3 = '''    val fontFamily = when(fontPref) {
        "Serif" -> FontFamily.Serif
        "SansSerif" -> FontFamily.SansSerif
        else -> FontFamily.Monospace
    }'''

font_logic_replacement3 = '''    // Note: Glance may fallback to system fonts if custom fonts are not natively bundled.
    val fontFamily = when(fontPref) {
        "Serif" -> FontFamily.Serif
        "SansSerif" -> FontFamily.SansSerif
        "Orbitron" -> OrbitronFamily
        "Hind Siliguri" -> HindSiliguriFamily
        else -> FontFamily.Monospace
    }'''

content3 = content3.replace(font_logic_target3, font_logic_replacement3)

with open('app/src/main/java/com/example/widget/RoutineWidget.kt', 'w') as f:
    f.write(content3)
