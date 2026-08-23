package com.example.ui.routine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.updateAll
import com.example.widget.RoutineWidget
import com.example.widget.WidgetPreferences
import kotlinx.coroutines.launch
import com.example.ui.routine.WidgetPreviewEngine
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetEditorScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var scale by remember { mutableFloatStateOf(WidgetPreferences.getScale(context)) }
    var opacity by remember { mutableFloatStateOf(WidgetPreferences.getOpacity(context)) }
    var themeColor by remember { mutableStateOf(WidgetPreferences.getThemeColor(context)) }
    var fontFamily by remember { mutableStateOf(WidgetPreferences.getFontFamily(context)) }

    fun saveAndSync() {
        WidgetPreferences.setScale(context, scale)
        WidgetPreferences.setOpacity(context, opacity)
        WidgetPreferences.setThemeColor(context, themeColor)
        WidgetPreferences.setFontFamily(context, fontFamily)
        
        scope.launch {
            RoutineWidget().updateAll(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WIDGET EDITOR", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color(0xFF00E676)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF050914))
            )
        },
        containerColor = Color(0xFF050914)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // WIDGET PREVIEW ENGINE
            WidgetPreviewEngine(scale, opacity, themeColor, fontFamily)

            // SCALE
            Column {
                Text("TEXT SCALE: ${(scale * 100).toInt()}%", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Slider(
                    value = scale,
                    onValueChange = { scale = it; saveAndSync() },
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF00E676), activeTrackColor = Color(0xFF00E676))
                )
            }
            
            // OPACITY
            Column {
                Text("BACKGROUND OPACITY: ${(opacity * 100).toInt()}%", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Slider(
                    value = opacity,
                    onValueChange = { opacity = it; saveAndSync() },
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF00E676), activeTrackColor = Color(0xFF00E676))
                )
            }
            
            // COLOR
            Column {
                Text("PRIMARY THEME COLOR", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    val colors = listOf("FF00E676", "FF00E5FF", "FFFF2A2A", "FFFFC107", "FFD500F9")
                    colors.forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(android.graphics.Color.parseColor("#$hex")), RoundedCornerShape(8.dp))
                                .border(
                                    width = if (themeColor == hex) 3.dp else 0.dp,
                                    color = if (themeColor == hex) Color.White else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { themeColor = hex; saveAndSync() }
                        )
                    }
                }
            }

            // FONT
            Column {
                Text("FONT FAMILY", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Monospace", "Orbitron", "Hind Siliguri").forEach { font ->
                        Button(
                            onClick = { fontFamily = font; saveAndSync() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (fontFamily == font) Color(0xFF00E676) else Color(0x3300E676),
                                contentColor = if (fontFamily == font) Color.Black else Color.White
                            )
                        ) {
                            Text(font.uppercase(), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

        }
    }
}
