package com.example.widget

import android.content.Context
import android.content.SharedPreferences

object WidgetPreferences {
    private const val PREFS_NAME = "widget_prefs"
    
    fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setScale(context: Context, scale: Float) {
        getPrefs(context).edit().putFloat("font_scale", scale).apply()
    }
    
    fun getScale(context: Context): Float {
        return getPrefs(context).getFloat("font_scale", 1.0f)
    }
    
    fun setOpacity(context: Context, opacity: Float) {
        getPrefs(context).edit().putFloat("bg_opacity", opacity).apply()
    }
    
    fun getOpacity(context: Context): Float {
        return getPrefs(context).getFloat("bg_opacity", 0.65f)
    }

    fun setThemeColor(context: Context, colorHex: String) {
        getPrefs(context).edit().putString("theme_color", colorHex).apply()
    }
    
    fun getThemeColor(context: Context): String {
        return getPrefs(context).getString("theme_color", "FF00E676") ?: "FF00E676" // Default DoomGreen
    }
    
    fun setFontFamily(context: Context, font: String) {
        getPrefs(context).edit().putString("font_family", font).apply()
    }
    
    fun getFontFamily(context: Context): String {
        return getPrefs(context).getString("font_family", "Monospace") ?: "Monospace"
    }
}
