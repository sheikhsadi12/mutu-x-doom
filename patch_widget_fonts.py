import re

with open('app/src/main/java/com/example/widget/RoutineWidget.kt', 'r') as f:
    content = f.read()

import_str = 'import com.example.R\nimport androidx.glance.text.FontFamily'
content = content.replace('import androidx.glance.text.FontFamily', import_str)

font_logic_target = '''    val fontFamily = when(fontPref) {
        "Serif" -> FontFamily.Serif
        "SansSerif" -> FontFamily.SansSerif
        // Glance does not natively support Compose GoogleFonts yet.
        // We fallback to standard system families for the widget.
        "Orbitron" -> FontFamily.Monospace
        "Hind Siliguri" -> FontFamily.SansSerif
        else -> FontFamily.Monospace
    }'''

font_logic_replacement = '''    val fontFamily = when(fontPref) {
        "Serif" -> FontFamily.Serif
        "SansSerif" -> FontFamily.SansSerif
        "Orbitron" -> FontFamily(R.font.orbitron)
        "Hind Siliguri" -> FontFamily(R.font.hind_siliguri)
        else -> FontFamily.Monospace
    }'''

content = content.replace(font_logic_target, font_logic_replacement)

with open('app/src/main/java/com/example/widget/RoutineWidget.kt', 'w') as f:
    f.write(content)
