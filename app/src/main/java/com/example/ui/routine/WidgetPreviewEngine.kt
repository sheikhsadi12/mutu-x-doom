package com.example.ui.routine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ThorCyan
import com.example.ui.theme.OrbitronFamily
import com.example.ui.theme.HindSiliguriFamily
import com.example.ui.theme.KalpurushFamily
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WidgetPreviewEngine(scale: Float, opacity: Float, themeHex: String, fontPref: String) {
    val baseThemeColor = try { Color(android.graphics.Color.parseColor("#$themeHex")) } catch(e: Exception) { Color(0xFF00E676) }
    
    val fontFamily = when(fontPref) {
        "Serif" -> FontFamily.Serif
        "SansSerif" -> FontFamily.SansSerif
        "Orbitron" -> OrbitronFamily
        "Hind Siliguri" -> HindSiliguriFamily
        "Kalpurush" -> KalpurushFamily
        else -> FontFamily.Monospace
    }
    
    val dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH))
    val bgDark = Color(0x050914).copy(alpha = opacity)
    
    Column {
        Text("WIDGET PREVIEW (3:2 RATIO)", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f) // 3:2 Ratio
                .background(bgDark, RoundedCornerShape(24.dp))
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Left
                    Column(modifier = Modifier.weight(1f)) {
                        Text("SYSTEM SECURE", color = baseThemeColor, fontSize = (12 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                        Text("PHASE X", color = Color.White, fontSize = (16 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily, modifier = Modifier.padding(top = 2.dp))
                    }
                    
                    // Center
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("<", color = baseThemeColor, fontSize = (20 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily, modifier = Modifier.padding(end = 6.dp))
                        Box(contentAlignment = Alignment.Center) {
                            Text(dateStr.uppercase(), color = DangerRed, fontSize = (28 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily, modifier = Modifier.padding(end = 3.dp))
                            Text(dateStr.uppercase(), color = baseThemeColor, fontSize = (28 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily, modifier = Modifier.padding(start = 3.dp))
                            Text(dateStr.uppercase(), color = Color.White, fontSize = (28 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                        }
                        Text(">", color = baseThemeColor, fontSize = (20 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily, modifier = Modifier.padding(start = 6.dp))
                    }
                    
                    // Right
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("DAILY OVERRIDE", color = baseThemeColor, fontSize = (12 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                        Text("TARGET ASSIGNED", color = DangerRed, fontSize = (14 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily, textAlign = TextAlign.End, maxLines = 2, modifier = Modifier.padding(top = 2.dp))
                    }
                }
                
                // Bottom Row
                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    PreviewMechaCard("MORNING", "Sample morning data...", baseThemeColor, scale, opacity, fontFamily, Modifier.weight(1f).padding(end = 4.dp))
                    PreviewMechaCard("NOON", "Sample noon data...", ThorCyan, scale, opacity, fontFamily, Modifier.weight(1f).padding(horizontal = 4.dp))
                    PreviewMechaCard("NIGHT", "Sample night data...", baseThemeColor, scale, opacity, fontFamily, Modifier.weight(1f).padding(start = 4.dp))
                }
            }
        }
    }
}

@Composable
fun PreviewMechaCard(title: String, content: String, color: Color, scale: Float, opacity: Float, fontFamily: FontFamily, modifier: Modifier = Modifier) {
    val borderColor = color.copy(alpha = opacity * 0.8f)
    val glassColor = Color(0x050914).copy(alpha = opacity * 0.5f)
    
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(borderColor, RoundedCornerShape(12.dp))
            .padding(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(glassColor, RoundedCornerShape(11.dp))
                .padding(12.dp)
        ) {
            Text("/// $title", color = color, fontSize = (14 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily, modifier = Modifier.padding(bottom = 6.dp))
            Text(content, color = Color.White, fontSize = (18 * scale).sp, fontFamily = fontFamily, maxLines = 4)
        }
    }
}
