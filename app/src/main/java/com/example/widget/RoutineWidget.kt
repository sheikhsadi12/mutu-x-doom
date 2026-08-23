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
import androidx.glance.text.FontFamily
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
    val BackgroundDark = ColorProvider(Color(0xFF050914), Color(0xFF050914))
    val DoomGreenColor = Color(0xFF00E676)
    val ThorCyanColor = Color(0xFF00E5FF)
    val DangerRedColor = Color(0xFFFF2A2A)
    val DoomGreen = ColorProvider(DoomGreenColor, DoomGreenColor)
    val ThorCyan = ColorProvider(ThorCyanColor, ThorCyanColor)
    val DangerRed = ColorProvider(DangerRedColor, DangerRedColor)
    val White = ColorProvider(Color.White, Color.White)
    
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(12.dp)
    ) {
        // Top Row (3 Columns)
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left: SYSTEM SECURE + PHASE
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text("SYSTEM SECURE", style = TextStyle(color = DoomGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
                if (routine != null) {
                    Text(routine.phase.uppercase(), style = TextStyle(color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace), modifier = GlanceModifier.padding(top = 2.dp))
                }
            }

            // Center: Navigator < + Massive Date + >
            Row(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "<",
                    style = TextStyle(color = ThorCyan, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                    modifier = GlanceModifier.padding(end = 6.dp).clickable(actionRunCallback<PreviousDayActionCallback>())
                )
                
                Box(contentAlignment = Alignment.Center) {
                    Text(dateStr.uppercase(), style = TextStyle(color = DangerRed, fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace), modifier = GlanceModifier.padding(end = 3.dp))
                    Text(dateStr.uppercase(), style = TextStyle(color = ThorCyan, fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace), modifier = GlanceModifier.padding(start = 3.dp))
                    Text(dateStr.uppercase(), style = TextStyle(color = White, fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
                }

                Text(
                    text = ">",
                    style = TextStyle(color = ThorCyan, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                    modifier = GlanceModifier.padding(start = 6.dp).clickable(actionRunCallback<NextDayActionCallback>())
                )
            }

            // Right: DAILY OVERRIDE + Target text
            Column(modifier = GlanceModifier.defaultWeight(), horizontalAlignment = Alignment.End) {
                Text("DAILY OVERRIDE", style = TextStyle(color = ThorCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
                if (routine != null) {
                    Text(
                        text = routine.target.uppercase(),
                        style = TextStyle(color = DangerRed, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, textAlign = TextAlign.End),
                        maxLines = 2,
                        modifier = GlanceModifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // Bottom Row (3 Horizontal Cards)
        if (routine != null) {
            Row(
                modifier = GlanceModifier.fillMaxWidth().defaultWeight()
            ) {
                WidgetMechaCard("MORNING", routine.morning, DoomGreenColor, modifier = GlanceModifier.defaultWeight().padding(end = 4.dp))
                WidgetMechaCard("NOON", routine.noon, ThorCyanColor, modifier = GlanceModifier.defaultWeight().padding(horizontal = 4.dp))
                WidgetMechaCard("NIGHT", routine.night, DoomGreenColor, modifier = GlanceModifier.defaultWeight().padding(start = 4.dp))
            }
        } else {
            Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("NO DATA PROTOCOL FOR THIS DATE", style = TextStyle(color = ThorCyan, fontFamily = FontFamily.Monospace))
            }
        }
    }
}

@Composable
fun WidgetMechaCard(title: String, content: String, color: Color, modifier: GlanceModifier = GlanceModifier) {
    val borderColor = ColorProvider(color.copy(alpha = 0.5f), color.copy(alpha = 0.5f))
    val glassColor = ColorProvider(Color(0xD9050914), Color(0xD9050914))
    
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(borderColor)
            .padding(1.dp) // Simulated Border thickness
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(glassColor)
                .padding(6.dp)
        ) {
            Text(
                text = title,
                style = TextStyle(color = ColorProvider(color, color), fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                modifier = GlanceModifier.padding(bottom = 2.dp)
            )
            
            Text(
                text = content,
                style = TextStyle(color = ColorProvider(Color.White, Color.White), fontSize = 18.sp, fontFamily = FontFamily.Monospace),
                maxLines = 4
            )
        }
    }
}
