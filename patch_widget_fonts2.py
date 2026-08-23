import re

with open('app/src/main/java/com/example/widget/RoutineWidget.kt', 'r') as f:
    content = f.read()

font_logic_target = '''    val fontFamily = when(fontPref) {
        "Serif" -> FontFamily.Serif
        "SansSerif" -> FontFamily.SansSerif
        "Orbitron" -> FontFamily(R.font.orbitron)
        "Hind Siliguri" -> FontFamily(R.font.hind_siliguri)
        else -> FontFamily.Monospace
    }'''

font_logic_replacement = '''    val fontFamily = when(fontPref) {
        "Serif" -> FontFamily.Serif
        "SansSerif" -> FontFamily.SansSerif
        "Orbitron" -> FontFamily("orbitron")
        "Hind Siliguri" -> FontFamily("hind_siliguri")
        else -> FontFamily.Monospace
    }'''

content = content.replace(font_logic_target, font_logic_replacement)

with open('app/src/main/java/com/example/widget/RoutineWidget.kt', 'w') as f:
    f.write(content)
