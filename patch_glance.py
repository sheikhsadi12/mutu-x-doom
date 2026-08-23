import re

with open('app/src/main/java/com/example/widget/RoutineWidget.kt', 'r') as f:
    content = f.read()

# Remove compose font families from Glance widget
content = content.replace('import com.example.ui.theme.OrbitronFamily\n', '')
content = content.replace('import com.example.ui.theme.HindSiliguriFamily\n', '')

font_logic_target = '''    // Note: Glance may fallback to system fonts if custom fonts are not natively bundled.
    val fontFamily = when(fontPref) {
        "Serif" -> FontFamily.Serif
        "SansSerif" -> FontFamily.SansSerif
        "Orbitron" -> OrbitronFamily
        "Hind Siliguri" -> HindSiliguriFamily
        else -> FontFamily.Monospace
    }'''

font_logic_replacement = '''    val fontFamily = when(fontPref) {
        "Serif" -> FontFamily.Serif
        "SansSerif" -> FontFamily.SansSerif
        // Glance does not natively support Compose GoogleFonts yet.
        // We fallback to standard system families for the widget.
        "Orbitron" -> FontFamily.Monospace
        "Hind Siliguri" -> FontFamily.SansSerif
        else -> FontFamily.Monospace
    }'''

content = content.replace(font_logic_target, font_logic_replacement)

# Make sure we use Glance's FontFamily instead of Compose's in MechaCard function signature
content = content.replace('fontFamily: FontFamily,', 'fontFamily: androidx.glance.text.FontFamily,')

with open('app/src/main/java/com/example/widget/RoutineWidget.kt', 'w') as f:
    f.write(content)
