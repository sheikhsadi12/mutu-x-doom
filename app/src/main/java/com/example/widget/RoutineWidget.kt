package com.example.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.color.ColorProvider
import androidx.glance.ButtonDefaults
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.TextAlign
import androidx.glance.layout.Alignment
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
import com.example.R
import androidx.glance.text.FontFamily
import androidx.glance.appwidget.SizeMode
import androidx.glance.LocalSize
import androidx.compose.ui.unit.DpSize
import androidx.glance.appwidget.cornerRadius
import com.example.data.AppDatabase
import com.example.data.RoutineEntity
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class RoutineWidget : GlanceAppWidget() {

    companion object {
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
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val currentMillis = prefs[dateKey] ?: LocalDate.of(2024, 8, 22).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val currentDate = LocalDate.ofEpochDay(currentMillis / (24 * 60 * 60 * 1000))
            val dateStr = currentDate.format(DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH))

            val routine = runBlocking {
                AppDatabase.getDatabase(context).routineDao().getRoutineByDate(dateStr).firstOrNull()
            }

            WidgetContent(dateStr, routine)
        }
    }
}

@Composable
fun WidgetContent(dateStr: String, routine: RoutineEntity?) {
    val context = LocalContext.current
    val scale = WidgetPreferences.getScale(context)
    val opacity = WidgetPreferences.getOpacity(context)
    val themeHex = WidgetPreferences.getThemeColor(context)
    val fontPref = WidgetPreferences.getFontFamily(context)
    
    val baseThemeColor = try { Color(android.graphics.Color.parseColor("#$themeHex")) } catch(e: Exception) { Color(0xFF00E676) }
    
    val fontFamily = when(fontPref) {
        "Serif" -> FontFamily.Serif
        "SansSerif" -> FontFamily.SansSerif
        "Orbitron" -> FontFamily("orbitron")
        "Hind Siliguri" -> FontFamily("hind_siliguri")
        else -> FontFamily.Monospace
    }
    
    val alphaInt = (opacity * 255).toInt()
    val BackgroundDark = ColorProvider(Color(0x050914).copy(alpha = opacity), Color(0x050914).copy(alpha = opacity))
    val ThemePrimary = ColorProvider(baseThemeColor, baseThemeColor)
    val ThorCyanColor = Color(0xFF00E5FF)
    val DangerRedColor = Color(0xFFFF2A2A)
    val ThorCyan = ColorProvider(ThorCyanColor, ThorCyanColor)
    val DangerRed = ColorProvider(DangerRedColor, DangerRedColor)
    val White = ColorProvider(Color.White, Color.White)
    
    Column(
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

        // Top Row (3 Columns)
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(bottom = if (isSmallHeight) 0.dp else 8.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            // Left: SYSTEM SECURE + PHASE
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text("SYSTEM SECURE", style = TextStyle(color = ThemePrimary, fontSize = (12 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily))
                if (routine != null) {
                    Text(routine.phase.uppercase(), style = TextStyle(color = White, fontSize = (16 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily), modifier = GlanceModifier.padding(top = 2.dp))
                }
            }

            // Center: Navigator < + Massive Date + >
            Row(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                Text(
                    text = "<",
                    style = TextStyle(color = ThemePrimary, fontSize = (20 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily),
                    modifier = GlanceModifier.padding(end = 6.dp).clickable(actionRunCallback<PreviousDayActionCallback>())
                )
                
                Box(contentAlignment = Alignment.Center) {
                    Text(dateStr.uppercase(), style = TextStyle(color = DangerRed, fontSize = (28 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily), modifier = GlanceModifier.padding(end = 3.dp))
                    Text(dateStr.uppercase(), style = TextStyle(color = ThemePrimary, fontSize = (28 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily), modifier = GlanceModifier.padding(start = 3.dp))
                    Text(dateStr.uppercase(), style = TextStyle(color = White, fontSize = (28 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily))
                }

                Text(
                    text = ">",
                    style = TextStyle(color = ThemePrimary, fontSize = (20 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily),
                    modifier = GlanceModifier.padding(start = 6.dp).clickable(actionRunCallback<NextDayActionCallback>())
                )
            }

            // Right: DAILY OVERRIDE + Target text
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
            }
        }

        // Bottom Row (3 Horizontal Cards)
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
        }
    }
}

@Composable
fun WidgetMechaCard(title: String, content: String, color: Color, scale: Float, opacity: Float, fontFamily: androidx.glance.text.FontFamily, modifier: GlanceModifier = GlanceModifier) {
    val borderColor = ColorProvider(color.copy(alpha = opacity * 0.8f), color.copy(alpha = opacity * 0.8f))
    val glassColor = ColorProvider(Color(0x050914).copy(alpha = opacity * 0.5f), Color(0x050914).copy(alpha = opacity * 0.5f))
    
    Box(
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
        ) {
            Text(
                text = "/// $title",
                style = TextStyle(color = ColorProvider(color, color), fontSize = (14 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily),
                modifier = GlanceModifier.padding(bottom = 6.dp)
            )
            
            Text(
                text = content,
                style = TextStyle(color = ColorProvider(Color.White, Color.White), fontSize = (18 * scale).sp, fontFamily = fontFamily),
                maxLines = 4
            )
        }
    }
}
