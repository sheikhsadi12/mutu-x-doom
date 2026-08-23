package com.example.ui.routine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.RoutineEntity
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DoomGreen
import com.example.ui.theme.ThorCyan
import com.example.ui.theme.OrbitronFamily
import com.example.ui.theme.HindSiliguriFamily
import androidx.compose.ui.platform.LocalContext
import com.example.widget.WidgetPreferences
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: RoutineViewModel, onOpenWidgetEditor: () -> Unit = {}) {
    val currentDate by viewModel.currentDate.collectAsState()
    val currentRoutine by viewModel.currentRoutine.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)
    val scale = WidgetPreferences.getScale(LocalContext.current)

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("DOOMSDAY PROTOCOL", fontFamily = OrbitronFamily, fontWeight = FontWeight.Bold, color = Color(0xFF00E676)) },
                actions = {
                    IconButton(onClick = onOpenWidgetEditor) {
                        Icon(Icons.Filled.Settings, contentDescription = "Widget Settings", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.addNext30Days() },
                containerColor = Color(0xFF00E676),
                contentColor = Color.Black
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add 30 Days")
                Spacer(Modifier.width(8.dp))
                Text("ADD 30 DAYS", fontFamily = OrbitronFamily, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundDark)
            .drawBehind {
                // Deep Space Scanlines & Grid
                val gridSpacing = 40.dp.toPx()
                val scanlineSpacing = 4.dp.toPx()
                
                // Vertical grid lines
                for (x in 0..size.width.toInt() step gridSpacing.toInt()) {
                    drawLine(Color.White.copy(alpha = 0.03f), Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height), strokeWidth = 1f)
                }
                // Horizontal grid lines
                for (y in 0..size.height.toInt() step gridSpacing.toInt()) {
                    drawLine(Color.White.copy(alpha = 0.03f), Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), strokeWidth = 1f)
                }
                // Scanlines
                for (y in 0..size.height.toInt() step scanlineSpacing.toInt()) {
                    drawLine(Color.Black.copy(alpha = 0.2f), Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), strokeWidth = 2f)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // 2:1 Horizontal Landscape HUD Container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .aspectRatio(1.5f) // Forces the 3:2 constraint
                .shadow(elevation = 30.dp, shape = CutCornerShape(16.dp), spotColor = DoomGreen, ambientColor = DoomGreen)
                .background(Color(0xCC050914), CutCornerShape(16.dp))
                .border(2.dp, Color.White.copy(alpha = 0.05f), CutCornerShape(16.dp))
                .padding(16.dp)
        ) {
            // Top Row (3 Columns)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.35f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Left: Static Status Dot + "SYSTEM SECURE" + PHASE (Glitch Text)
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).background(DoomGreen, CutCornerShape(2.dp)))
                        Text("SYSTEM SECURE", color = DoomGreen, fontSize = (14 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = OrbitronFamily, letterSpacing = 0.1.em)
                    }
                    if (currentRoutine != null) {
                        StaticGlitchText(
                            text = currentRoutine!!.phase.uppercase(),
                            fontSize = (24 * scale).sp,
                            color = Color.White
                        )
                    }
                }

                // Center: Navigator < + Massive Date + >
                Row(
                    modifier = Modifier.weight(1.5f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.previousDay() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", tint = ThorCyan)
                    }
                    StaticGlitchText(
                        text = currentDate.format(dateFormatter).uppercase(),
                        fontSize = (48 * scale).sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    IconButton(onClick = { viewModel.nextDay() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = ThorCyan)
                    }
                }

                // Right: "DAILY OVERRIDE" + Target text
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("DAILY OVERRIDE", color = ThorCyan, fontSize = (14 * scale).sp, fontWeight = FontWeight.Bold, fontFamily = OrbitronFamily, letterSpacing = 0.1.em)
                    if (currentRoutine != null) {
                        Text(
                            text = currentRoutine!!.target.uppercase(),
                            color = DangerRed,
                            fontSize = (16 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = OrbitronFamily,
                            textAlign = TextAlign.End,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Row (3 Horizontal Cards)
            if (currentRoutine != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.65f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MechaCard(modifier = Modifier.weight(1f), title = "Morning", content = currentRoutine!!.morning, color = DoomGreen)
                    MechaCard(modifier = Modifier.weight(1f), title = "Noon", content = currentRoutine!!.noon, color = ThorCyan)
                    MechaCard(modifier = Modifier.weight(1f), title = "Night", content = currentRoutine!!.night, color = DoomGreen)
                }
            } else {
                Box(modifier = Modifier.weight(0.65f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("NO DATA PROTOCOL FOR THIS DATE", color = ThorCyan, fontFamily = OrbitronFamily)
                }
            }
        }

        // Edit FAB
        if (currentRoutine != null) {
            FloatingActionButton(
                onClick = { showEditDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .shadow(20.dp, CutCornerShape(8.dp), spotColor = DoomGreen),
                shape = CutCornerShape(8.dp),
                containerColor = DoomGreen,
                contentColor = BackgroundDark
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit Routine")
            }
        }
    }

    if (showEditDialog && currentRoutine != null) {
        EditRoutineDialog(
            routine = currentRoutine!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedRoutine ->
                viewModel.updateRoutine(updatedRoutine)
                showEditDialog = false
            },
            scale = scale
        )
    }
    } // Scaffold close
}

@Composable
fun StaticGlitchText(text: String, fontSize: TextUnit, color: Color, modifier: Modifier = Modifier) {
    val offset = (fontSize.value / 12).dp
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = text,
            color = DangerRed.copy(alpha = 0.8f),
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            fontFamily = OrbitronFamily,
            modifier = Modifier.offset(x = -offset)
        )
        Text(
            text = text,
            color = ThorCyan.copy(alpha = 0.8f),
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            fontFamily = OrbitronFamily,
            modifier = Modifier.offset(x = offset)
        )
        Text(
            text = text,
            color = color,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            fontFamily = OrbitronFamily,
        )
    }
}

@Composable
fun MechaCard(modifier: Modifier = Modifier, title: String, content: String, color: Color, scale: Float = 1.0f) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(CutCornerShape(10.dp))
            .background(color.copy(alpha = 0.1f)) // Static Glow
            .border(2.dp, color.copy(alpha = 0.5f), CutCornerShape(10.dp))
            .drawBehind {
                // Micro-Tech Details
                val cornerSize = 12.dp.toPx()
                val stroke = 3f
                // Top-left crosshair
                drawLine(color, Offset(0f, cornerSize), Offset(cornerSize, cornerSize), strokeWidth = stroke)
                drawLine(color, Offset(cornerSize, 0f), Offset(cornerSize, cornerSize), strokeWidth = stroke)
                
                // Bottom-right crosshair
                drawLine(color, Offset(size.width - cornerSize, size.height - cornerSize), Offset(size.width, size.height - cornerSize), strokeWidth = stroke)
                drawLine(color, Offset(size.width - cornerSize, size.height - cornerSize), Offset(size.width - cornerSize, size.height), strokeWidth = stroke)
            }
            .background(Color(0xD9050914), CutCornerShape(10.dp)) // Glass dark with high opacity
            .padding(12.dp)
    ) {
        Column(
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
                    fontSize = (18 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = OrbitronFamily,
                    letterSpacing = 0.1.em
                )
            }
            
            Text(
                text = highlightExamText(content),
                color = Color.White,
                fontSize = (22 * scale).sp,
                fontWeight = FontWeight.Normal,
                lineHeight = (32 * scale).sp,
                fontFamily = HindSiliguriFamily,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

fun highlightExamText(text: String): androidx.compose.ui.text.AnnotatedString {
    val examKeywords = listOf("এক্সাম", "Exam", "exam", "EXAM")
    return buildAnnotatedString {
        var currentIndex = 0
        while (currentIndex < text.length) {
            var foundKeyword = false
            for (keyword in examKeywords) {
                if (text.startsWith(keyword, currentIndex)) {
                    withStyle(style = SpanStyle(color = DangerRed, fontWeight = FontWeight.Bold)) {
                        append(keyword)
                    }
                    currentIndex += keyword.length
                    foundKeyword = true
                    break
                }
            }
            if (!foundKeyword) {
                append(text[currentIndex])
                currentIndex++
            }
        }
    }
}

@Composable
fun EditRoutineDialog(
    routine: RoutineEntity,
    onDismiss: () -> Unit,
    onSave: (RoutineEntity) -> Unit,
    scale: Float = 1.0f
) {
    var morning by remember { mutableStateOf(routine.morning) }
    var noon by remember { mutableStateOf(routine.noon) }
    var night by remember { mutableStateOf(routine.night) }
    var target by remember { mutableStateOf(routine.target) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = BackgroundDark),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, ThorCyan, CutCornerShape(16.dp)),
            shape = CutCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Edit Routine: ${routine.displayDate}", color = DoomGreen, fontWeight = FontWeight.Bold, fontFamily = OrbitronFamily, fontSize = (18 * scale).sp)
                
                OutlinedTextField(
                    value = morning,
                    onValueChange = { morning = it },
                    label = { Text("Morning", color = DoomGreen) },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DoomGreen,
                        unfocusedBorderColor = DoomGreen.copy(alpha = 0.5f)
                    )
                )
                
                OutlinedTextField(
                    value = noon,
                    onValueChange = { noon = it },
                    label = { Text("Noon", color = ThorCyan) },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ThorCyan,
                        unfocusedBorderColor = ThorCyan.copy(alpha = 0.5f)
                    )
                )
                
                OutlinedTextField(
                    value = night,
                    onValueChange = { night = it },
                    label = { Text("Night", color = DoomGreen) },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DoomGreen,
                        unfocusedBorderColor = DoomGreen.copy(alpha = 0.5f)
                    )
                )
                
                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("Target", color = DangerRed) },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DangerRed,
                        unfocusedBorderColor = DangerRed.copy(alpha = 0.5f)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(routine.copy(morning = morning, noon = noon, night = night, target = target))
                        },
                        shape = CutCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ThorCyan)
                    ) {
                        Text("Save", color = BackgroundDark)
                    }
                }
            }
        }
    }
}
