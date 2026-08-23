import re

with open('app/src/main/java/com/example/ui/routine/DashboardScreen.kt', 'r') as f:
    content = f.read()

# Make DashboardScreen use scale
content = content.replace('val dateFormatter = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)', 'val dateFormatter = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)\n    val scale = WidgetPreferences.getScale(LocalContext.current)')

# Replace hardcoded font sizes with scaled ones in DashboardScreen.kt
# fontSize = 18.sp -> fontSize = (18 * scale).sp
# We'll use regex for sp replacements inside DashboardScreen.kt (we need to be careful to only replace ones we intend to).

def replace_sp(match):
    size = int(match.group(1))
    # Don't scale small padding values if we accidentally catch them, but we only match fontSize and lineHeight
    return f'fontSize = ({size} * scale).sp'

content = re.sub(r'fontSize\s*=\s*(\d+)\.sp', replace_sp, content)

def replace_line_height(match):
    size = int(match.group(1))
    return f'lineHeight = ({size} * scale).sp'

content = re.sub(r'lineHeight\s*=\s*(\d+)\.sp', replace_line_height, content)

# MechaCard function needs 'scale: Float' parameter
content = content.replace('fun MechaCard(modifier: Modifier = Modifier, title: String, content: String, color: Color)', 'fun MechaCard(modifier: Modifier = Modifier, title: String, content: String, color: Color, scale: Float = 1.0f)')
content = content.replace('MechaCard("MORNING", currentRoutine!!.morning, DoomGreen)', 'MechaCard("MORNING", currentRoutine!!.morning, DoomGreen, scale)')
content = content.replace('MechaCard("NOON", currentRoutine!!.noon, ThorCyan)', 'MechaCard("NOON", currentRoutine!!.noon, ThorCyan, scale)')
content = content.replace('MechaCard("NIGHT", currentRoutine!!.night, DoomGreen)', 'MechaCard("NIGHT", currentRoutine!!.night, DoomGreen, scale)')

with open('app/src/main/java/com/example/ui/routine/DashboardScreen.kt', 'w') as f:
    f.write(content)
