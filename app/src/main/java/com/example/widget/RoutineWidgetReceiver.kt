package com.example.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.appwidget.action.actionRunCallback
import com.example.widget.RoutineWidget
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class RoutineWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = RoutineWidget()
}

class NextDayActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            val currentMillis = prefs[RoutineWidget.dateKey] ?: LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val nextDay = LocalDate.ofEpochDay(currentMillis / (24 * 60 * 60 * 1000)).plusDays(1)
            prefs.toMutablePreferences().apply {
                this[RoutineWidget.dateKey] = nextDay.toEpochDay() * 24 * 60 * 60 * 1000
            }
        }
        RoutineWidget().update(context, glanceId)
    }
}

class PreviousDayActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            val currentMillis = prefs[RoutineWidget.dateKey] ?: LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val prevDay = LocalDate.ofEpochDay(currentMillis / (24 * 60 * 60 * 1000)).minusDays(1)
            prefs.toMutablePreferences().apply {
                this[RoutineWidget.dateKey] = prevDay.toEpochDay() * 24 * 60 * 60 * 1000
            }
        }
        RoutineWidget().update(context, glanceId)
    }
}
