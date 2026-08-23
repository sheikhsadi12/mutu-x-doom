import re

with open('app/src/main/java/com/example/ui/routine/WidgetPreviewEngine.kt', 'r') as f:
    content = f.read()

import_str = 'import com.example.ui.theme.HindSiliguriFamily\nimport com.example.ui.theme.KalpurushFamily'
content = content.replace('import com.example.ui.theme.HindSiliguriFamily', import_str)

font_logic_target = '''        "Hind Siliguri" -> HindSiliguriFamily
        else -> FontFamily.Monospace'''

font_logic_replacement = '''        "Hind Siliguri" -> HindSiliguriFamily
        "Kalpurush" -> KalpurushFamily
        else -> FontFamily.Monospace'''

content = content.replace(font_logic_target, font_logic_replacement)

with open('app/src/main/java/com/example/ui/routine/WidgetPreviewEngine.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/routine/WidgetEditorScreen.kt', 'r') as f:
    content2 = f.read()

content2 = content2.replace('listOf("Monospace", "Orbitron", "Hind Siliguri")', 'listOf("Monospace", "Orbitron", "Hind Siliguri", "Kalpurush")')

with open('app/src/main/java/com/example/ui/routine/WidgetEditorScreen.kt', 'w') as f:
    f.write(content2)

