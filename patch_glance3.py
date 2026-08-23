import re

with open('app/src/main/java/com/example/widget/RoutineWidget.kt', 'r') as f:
    content = f.read()

# Make sure actionParametersOf is imported
import_str = 'import androidx.glance.action.actionParametersOf\nimport androidx.glance.appwidget.action.actionRunCallback'
if 'actionParametersOf' not in content:
    content = content.replace('import androidx.glance.appwidget.action.actionRunCallback', import_str)

# Replace dateStr in the center with displayDate or dateStr
center_target = '''                Box(contentAlignment = Alignment.Center) {
                    Text(dateStr.uppercase(), style = TextStyle(color = DangerRed, fontSize = (28 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily), modifier = GlanceModifier.padding(end = 3.dp))
                    Text(dateStr.uppercase(), style = TextStyle(color = ThemePrimary, fontSize = (28 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily), modifier = GlanceModifier.padding(start = 3.dp))
                    Text(dateStr.uppercase(), style = TextStyle(color = White, fontSize = (28 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily))
                }'''
center_replacement = '''                Box(contentAlignment = Alignment.Center) {
                    val displayTxt = routine?.displayDate?.uppercase() ?: dateStr.uppercase()
                    Text(displayTxt, style = TextStyle(color = DangerRed, fontSize = (28 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily), modifier = GlanceModifier.padding(end = 3.dp))
                    Text(displayTxt, style = TextStyle(color = ThemePrimary, fontSize = (28 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily), modifier = GlanceModifier.padding(start = 3.dp))
                    Text(displayTxt, style = TextStyle(color = White, fontSize = (28 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily))
                }'''
content = content.replace(center_target, center_replacement)

# Replace the Right section to add ratio
right_target = '''            // Right: DAILY OVERRIDE + Target text
            Column(modifier = GlanceModifier.defaultWeight(), horizontalAlignment = Alignment.End) {
                Text("DAILY OVERRIDE", style = TextStyle(color = ThemePrimary, fontSize = (12 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily))
                if (routine != null) {
                    Text(
                        text = routine.target.uppercase(),
                        style = TextStyle(color = DangerRed, fontSize = (14 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily, textAlign = TextAlign.End),
                        maxLines = 2,
                        modifier = GlanceModifier.padding(top = 2.dp)
                    )
                }
            }'''
right_replacement = '''            // Right: DAILY OVERRIDE + Target text
            Column(modifier = GlanceModifier.defaultWeight(), horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
                    if (routine != null) {
                        Text(
                            text = "🔄 ${routine.ratio}",
                            style = TextStyle(color = ThemePrimary, fontSize = (12 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily),
                            modifier = GlanceModifier.padding(end = 6.dp).clickable(actionRunCallback<CycleRatioActionCallback>(actionParametersOf(ActionParameters.Key<String>("dateKey") to routine.dateKey)))
                        )
                    }
                    Text("DAILY OVERRIDE", style = TextStyle(color = ThemePrimary, fontSize = (12 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily))
                }
                if (routine != null) {
                    Text(
                        text = routine.target.uppercase(),
                        style = TextStyle(color = DangerRed, fontSize = (14 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily, textAlign = TextAlign.End),
                        maxLines = 2,
                        modifier = GlanceModifier.padding(top = 2.dp)
                    )
                }
            }'''
content = content.replace(right_target, right_replacement)

# Fix Kalpurush font mapping
font_logic_target = '''    val fontFamily = when(fontPref) {
        "Serif" -> FontFamily.Serif
        "SansSerif" -> FontFamily.SansSerif
        "Orbitron" -> FontFamily("orbitron")
        "Hind Siliguri" -> FontFamily("hind_siliguri")
        else -> FontFamily.Monospace
    }'''

font_logic_replacement = '''    val fontFamily = when(fontPref) {
        "Serif" -> FontFamily.Serif
        "SansSerif" -> FontFamily.SansSerif
        "Orbitron" -> FontFamily("orbitron")
        "Hind Siliguri" -> FontFamily("hind_siliguri")
        "Kalpurush" -> FontFamily("kalpurush")
        else -> FontFamily.Monospace
    }'''
content = content.replace(font_logic_target, font_logic_replacement)


with open('app/src/main/java/com/example/widget/RoutineWidget.kt', 'w') as f:
    f.write(content)
