import re

with open('app/src/main/java/com/example/ui/routine/DashboardScreen.kt', 'r') as f:
    content = f.read()

# Import the new fonts
content = content.replace('import com.example.ui.theme.ThorCyan', 'import com.example.ui.theme.ThorCyan\nimport com.example.ui.theme.OrbitronFamily\nimport com.example.ui.theme.HindSiliguriFamily\nimport androidx.compose.ui.platform.LocalContext\nimport com.example.widget.WidgetPreferences')

# Update typography inside MechaCard
mecha_card_target = '''        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Static Inner Indicator
                    Box(modifier = Modifier.size(6.dp).background(color, CutCornerShape(2.dp)))
                    Text(
                        text = title.uppercase(),
                        color = color,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            
            Text(
                text = highlightExamText(content),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 28.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }'''

mecha_card_replacement = '''        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Static Inner Indicator
                Box(modifier = Modifier.size(6.dp).background(color, CutCornerShape(2.dp)))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = title.uppercase(),
                    color = color,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = OrbitronFamily,
                    letterSpacing = 0.1.em
                )
            }
            
            Text(
                text = highlightExamText(content),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 32.sp,
                fontFamily = HindSiliguriFamily,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }'''

content = content.replace(mecha_card_target, mecha_card_replacement)

# Replace FontFamily.Monospace with OrbitronFamily inside headers
content = content.replace('fontFamily = FontFamily.Monospace', 'fontFamily = OrbitronFamily')

with open('app/src/main/java/com/example/ui/routine/DashboardScreen.kt', 'w') as f:
    f.write(content)
