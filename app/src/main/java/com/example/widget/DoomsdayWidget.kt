package com.example.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
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
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.example.data.AppDatabase
import com.example.data.RoutineEntity
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class DoomsdayWidget : GlanceAppWidget() {
    companion object {
        val dateKey = longPreferencesKey("current_date_epoch")
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
            val currentMillis = prefs[dateKey] ?: LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            val currentDate = LocalDate.ofEpochDay(currentMillis / (24 * 60 * 60 * 1000))
            val dateKeyStr = currentDate.toString() // "yyyy-MM-dd"

            // Synchronous DB fetch on worker thread
            val routine = runBlocking {
                val db = AppDatabase.getDatabase(context)
                var result = db.routineDao().getRoutineByDateKey(dateKeyStr).firstOrNull()
                if (result == null && db.routineDao().getCount() == 0) {
                    AppDatabase.prepopulateDatabase(db.routineDao())
                    result = db.routineDao().getRoutineByDateKey(dateKeyStr).firstOrNull()
                }
                result
            }

            DoomsdayWidgetContent(currentDate = currentDate, dateKeyStr = dateKeyStr, routine = routine)
        }
    }
}

@Composable
fun DoomsdayWidgetContent(currentDate: LocalDate, dateKeyStr: String, routine: RoutineEntity?) {
    val context = LocalContext.current
    val size = LocalSize.current
    val isCompactHeight = size.height < 100.dp

    val BgDarkColor = Color(0xFF050914)
    val DoomGreenColor = Color(0xFF00E676)
    val ThorCyanColor = Color(0xFF00E5FF)
    val DangerRedColor = Color(0xFFFF2A2A)

    val BgDarkProvider = ColorProvider(BgDarkColor, BgDarkColor)
    val DoomGreenProvider = ColorProvider(DoomGreenColor, DoomGreenColor)
    val ThorCyanProvider = ColorProvider(ThorCyanColor, ThorCyanColor)
    val DangerRedProvider = ColorProvider(DangerRedColor, DangerRedColor)
    val WhiteProvider = ColorProvider(Color.White, Color.White)

    val orbitronFamily = FontFamily("orbitron")
    val hindFamily = FontFamily("hind_siliguri")

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(BgDarkProvider)
            .cornerRadius(20.dp)
            .padding(14.dp),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
    ) {
        // TOP HUD CONTROL BAR
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(bottom = if (isCompactHeight) 0.dp else 8.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            // LEFT: Status & Phase
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = "SYSTEM SECURE",
                    style = TextStyle(
                        color = DoomGreenProvider,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = orbitronFamily
                    )
                )
                Text(
                    text = (routine?.phase ?: "PHASE 1").uppercase(),
                    style = TextStyle(
                        color = WhiteProvider,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = orbitronFamily
                    )
                )
            }

            // CENTER: Date Navigator (< Date >)
            Row(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                Text(
                    text = "<",
                    style = TextStyle(
                        color = DoomGreenProvider,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = orbitronFamily
                    ),
                    modifier = GlanceModifier
                        .padding(end = 6.dp)
                        .clickable(actionRunCallback<WidgetPreviousDayActionCallback>())
                )

                val displayDate = routine?.displayDate ?: currentDate.format(
                    DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)
                )

                Text(
                    text = displayDate,
                    style = TextStyle(
                        color = WhiteProvider,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = hindFamily,
                        textAlign = TextAlign.Center
                    )
                )

                Text(
                    text = ">",
                    style = TextStyle(
                        color = DoomGreenProvider,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = orbitronFamily
                    ),
                    modifier = GlanceModifier
                        .padding(start = 6.dp)
                        .clickable(actionRunCallback<WidgetNextDayActionCallback>())
                )
            }

            // RIGHT: Ratio Cycler & Target
            Column(
                modifier = GlanceModifier.defaultWeight(),
                horizontalAlignment = Alignment.Horizontal.End
            ) {
                Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
                    if (routine != null) {
                        Text(
                            text = "🔄 ${routine.ratio}",
                            style = TextStyle(
                                color = ThorCyanProvider,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = orbitronFamily
                            ),
                            modifier = GlanceModifier
                                .padding(end = 4.dp)
                                .clickable(
                                    actionRunCallback<CycleRatioActionCallback>(
                                        actionParametersOf(ActionParameters.Key<String>("dateKey") to routine.dateKey)
                                    )
                                )
                        )
                    }
                    Text(
                        text = "RATIO",
                        style = TextStyle(
                            color = DoomGreenProvider,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = orbitronFamily
                        )
                    )
                }

                Text(
                    text = (routine?.target ?: "STANDBY").uppercase(),
                    style = TextStyle(
                        color = DangerRedProvider,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = hindFamily,
                        textAlign = TextAlign.End
                    ),
                    maxLines = 1
                )
            }
        }

        // BOTTOM 3 CARDS OR FALLBACK
        if (!isCompactHeight) {
            if (routine != null) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    WidgetBlockCard(
                        slot = "MORNING",
                        content = routine.morning,
                        color = DoomGreenColor,
                        fontFamily = hindFamily,
                        modifier = GlanceModifier.defaultWeight().padding(end = 4.dp)
                    )
                    WidgetBlockCard(
                        slot = "NOON",
                        content = routine.noon,
                        color = ThorCyanColor,
                        fontFamily = hindFamily,
                        modifier = GlanceModifier.defaultWeight().padding(horizontal = 4.dp)
                    )
                    WidgetBlockCard(
                        slot = "NIGHT",
                        content = routine.night,
                        color = if (routine.night.contains("🎯") || routine.night.contains("এক্সাম")) DangerRedColor else DoomGreenColor,
                        fontFamily = hindFamily,
                        modifier = GlanceModifier.defaultWeight().padding(start = 4.dp)
                    )
                }
            } else {
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LOADING DB... OPEN APP",
                        style = TextStyle(
                            color = ThorCyanProvider,
                            fontFamily = orbitronFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun WidgetBlockCard(
    slot: String,
    content: String,
    color: Color,
    fontFamily: FontFamily,
    modifier: GlanceModifier = GlanceModifier
) {
    val borderColor = ColorProvider(color.copy(alpha = 0.7f), color.copy(alpha = 0.7f))
    val glassColor = ColorProvider(Color(0xFF090D1A), Color(0xFF090D1A))

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(borderColor)
            .cornerRadius(10.dp)
            .padding(1.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(glassColor)
                .cornerRadius(9.dp)
                .padding(8.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = "/// $slot",
                style = TextStyle(
                    color = ColorProvider(color, color),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily("orbitron")
                ),
                modifier = GlanceModifier.padding(bottom = 4.dp)
            )
            Text(
                text = content,
                style = TextStyle(
                    color = ColorProvider(Color.White, Color.White),
                    fontSize = 12.sp,
                    fontFamily = fontFamily
                ),
                maxLines = 3
            )
        }
    }
}

class CycleRatioActionCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val dateKeyParam = ActionParameters.Key<String>("dateKey")
        val dateKey = parameters[dateKeyParam] ?: return

        val db = AppDatabase.getDatabase(context)
        val routine = db.routineDao().getRoutineByDateKeySync(dateKey) ?: return

        val ratios = listOf("3:1", "3:2", "1:2", "2:1", "4:1")
        val currentIndex = ratios.indexOf(routine.ratio)
        val nextIndex = if (currentIndex == -1) 0 else (currentIndex + 1) % ratios.size

        db.routineDao().updateRoutine(routine.copy(ratio = ratios[nextIndex]))
        DoomsdayWidget().update(context, glanceId)
        RoutineWidget().update(context, glanceId)
    }
}

class WidgetNextDayActionCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, glanceId) { prefs ->
            val currentMillis = prefs[DoomsdayWidget.dateKey] ?: LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            val nextDay = LocalDate.ofEpochDay(currentMillis / (24 * 60 * 60 * 1000)).plusDays(1)
            prefs.toMutablePreferences().apply {
                this[DoomsdayWidget.dateKey] = nextDay.toEpochDay() * 24 * 60 * 60 * 1000
                this[RoutineWidget.dateKey] = nextDay.toEpochDay() * 24 * 60 * 60 * 1000
            }
        }
        DoomsdayWidget().update(context, glanceId)
        RoutineWidget().update(context, glanceId)
    }
}

class WidgetPreviousDayActionCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, glanceId) { prefs ->
            val currentMillis = prefs[DoomsdayWidget.dateKey] ?: LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            val prevDay = LocalDate.ofEpochDay(currentMillis / (24 * 60 * 60 * 1000)).minusDays(1)
            prefs.toMutablePreferences().apply {
                this[DoomsdayWidget.dateKey] = prevDay.toEpochDay() * 24 * 60 * 60 * 1000
                this[RoutineWidget.dateKey] = prevDay.toEpochDay() * 24 * 60 * 60 * 1000
            }
        }
        DoomsdayWidget().update(context, glanceId)
        RoutineWidget().update(context, glanceId)
    }
}

typealias RoutineWidget = DoomsdayWidget
