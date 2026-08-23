import re

with open('app/src/main/java/com/example/widget/RoutineWidget.kt', 'r') as f:
    content = f.read()

import_str = '''import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.TextAlign
import androidx.glance.text.FontFamily
import androidx.glance.appwidget.SizeMode
import androidx.glance.LocalSize
import androidx.compose.ui.unit.DpSize'''

content = content.replace('import androidx.glance.text.FontFamily', import_str)

size_mode_target = '''    companion object {
        val dateKey = longPreferencesKey("current_date")
    }'''

size_mode_replacement = '''    companion object {
        val dateKey = longPreferencesKey("current_date")
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(110.dp, 40.dp),  // 2x1
            DpSize(110.dp, 110.dp), // 2x2
            DpSize(180.dp, 40.dp),  // 3x1
            DpSize(180.dp, 110.dp), // 3x2
            DpSize(250.dp, 110.dp)  // 4x2
        )
    )'''

content = content.replace(size_mode_target, size_mode_replacement)

# Update WidgetContent
# We need to make the layout adapt based on height
# But even simple vertical centering is requested. Let's make sure it's vertically centered.
# The user says "The widget MUST utilize the entire available space. Apply GlanceModifier.fillMaxSize() to the root container. Vertically and horizontally center the content within the widget so there is no awkward empty space at the bottom."

column_start = '''    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(BackgroundDark)
            .cornerRadius(24.dp)
            .padding(16.dp)
    ) {'''

column_replacement = '''    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(BackgroundDark)
            .cornerRadius(24.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
    ) {
        val size = LocalSize.current
        val isSmallHeight = size.height < 100.dp
'''
content = content.replace(column_start, column_replacement)


# Now fix the row spacing
row_top_start = '''        // Top Row (3 Columns)
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalAlignment = Alignment.Top
        ) {'''

row_top_replacement = '''        // Top Row (3 Columns)
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(bottom = if (isSmallHeight) 0.dp else 8.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {'''
content = content.replace(row_top_start, row_top_replacement)

# Update Center element
center_el_target = '''            // Center: Navigator < + Massive Date + >
            Row(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {'''
center_el_replacement = '''            // Center: Navigator < + Massive Date + >
            Row(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {'''
content = content.replace(center_el_target, center_el_replacement)

# Only show the bottom row if it is not small height
bottom_row_target = '''        // Bottom Row (3 Horizontal Cards)
        if (routine != null) {
            Row(
                modifier = GlanceModifier.fillMaxWidth().defaultWeight()
            ) {
                WidgetMechaCard("MORNING", routine.morning, baseThemeColor, scale, opacity, fontFamily, modifier = GlanceModifier.defaultWeight().padding(end = 4.dp))
                WidgetMechaCard("NOON", routine.noon, ThorCyanColor, scale, opacity, fontFamily, modifier = GlanceModifier.defaultWeight().padding(horizontal = 4.dp))
                WidgetMechaCard("NIGHT", routine.night, baseThemeColor, scale, opacity, fontFamily, modifier = GlanceModifier.defaultWeight().padding(start = 4.dp))
            }
        } else {
            Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("NO DATA PROTOCOL FOR THIS DATE", style = TextStyle(color = ThemePrimary, fontFamily = fontFamily, fontSize = (14 * scale).sp))
            }
        }'''

bottom_row_replacement = '''        // Bottom Row (3 Horizontal Cards)
        if (!isSmallHeight) {
            if (routine != null) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    WidgetMechaCard("MORNING", routine.morning, baseThemeColor, scale, opacity, fontFamily, modifier = GlanceModifier.defaultWeight().padding(end = 4.dp))
                    WidgetMechaCard("NOON", routine.noon, ThorCyanColor, scale, opacity, fontFamily, modifier = GlanceModifier.defaultWeight().padding(horizontal = 4.dp))
                    WidgetMechaCard("NIGHT", routine.night, baseThemeColor, scale, opacity, fontFamily, modifier = GlanceModifier.defaultWeight().padding(start = 4.dp))
                }
            } else {
                Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("NO DATA PROTOCOL FOR THIS DATE", style = TextStyle(color = ThemePrimary, fontFamily = fontFamily, fontSize = (14 * scale).sp))
                }
            }
        }'''
content = content.replace(bottom_row_target, bottom_row_replacement)

# Update Mecha Card alignments
mecha_card_target = '''    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(borderColor)
            .cornerRadius(12.dp)
            .padding(1.dp) // Simulated Border thickness
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(glassColor)
                .cornerRadius(11.dp)
                .padding(12.dp)
        ) {'''

mecha_card_replacement = '''    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(borderColor)
            .cornerRadius(12.dp)
            .padding(1.dp), // Simulated Border thickness
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(glassColor)
                .cornerRadius(11.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {'''
content = content.replace(mecha_card_target, mecha_card_replacement)

with open('app/src/main/java/com/example/widget/RoutineWidget.kt', 'w') as f:
    f.write(content)
