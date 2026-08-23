package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RoutineEntity
import com.example.ui.routine.MainViewModel
import com.example.ui.theme.HindSiliguriFamily
import com.example.ui.theme.KalpurushFamily
import com.example.ui.theme.OrbitronFamily
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

val BackgroundDark = Color(0xFF050914)
val DoomGreen = Color(0xFF00E676)
val ThorCyan = Color(0xFF00E5FF)
val DangerRed = Color(0xFFFF2A2A)
val CardSurfaceDark = Color(0x33000000)

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = BackgroundDark,
                    surface = BackgroundDark,
                    primary = DoomGreen,
                    secondary = ThorCyan,
                    error = DangerRed
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundDark
                ) {
                    DoomsdayDashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoomsdayDashboardScreen(viewModel: MainViewModel) {
    val currentDate by viewModel.currentDate.collectAsState()
    val currentRoutine by viewModel.currentRoutine.collectAsState()
    var showEditSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "DOOMSDAY HUD",
                            fontFamily = OrbitronFamily,
                            fontWeight = FontWeight.Bold,
                            color = DoomGreen,
                            fontSize = 18.sp,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "SYSTEM ACTIVE // MASTER ROUTINE",
                            fontFamily = OrbitronFamily,
                            color = ThorCyan.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.addNext30Days() }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add 30 Days",
                            tint = ThorCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showEditSheet = true },
                containerColor = DoomGreen,
                contentColor = Color.Black,
                shape = CutCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit Routine")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "EDIT PROTOCOL",
                    fontFamily = OrbitronFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundDark)
                .drawBehind {
                    val gridSpacing = 40.dp.toPx()
                    for (x in 0..size.width.toInt() step gridSpacing.toInt()) {
                        drawLine(
                            Color.White.copy(alpha = 0.02f),
                            Offset(x.toFloat(), 0f),
                            Offset(x.toFloat(), size.height),
                            strokeWidth = 1f
                        )
                    }
                    for (y in 0..size.height.toInt() step gridSpacing.toInt()) {
                        drawLine(
                            Color.White.copy(alpha = 0.02f),
                            Offset(0f, y.toFloat()),
                            Offset(size.width, y.toFloat()),
                            strokeWidth = 1f
                        )
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // DATE & RATIO HUD BANNER
                HudHeaderCard(
                    currentDate = currentDate,
                    displayDate = currentRoutine?.displayDate ?: currentDate.format(
                        DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)
                    ),
                    phase = currentRoutine?.phase ?: "PHASE 1",
                    target = currentRoutine?.target ?: "STANDBY",
                    ratio = currentRoutine?.ratio ?: "3:1",
                    onPrevDay = { viewModel.previousDay() },
                    onNextDay = { viewModel.nextDay() }
                )

                // 3 TIME BLOCK CARDS
                if (currentRoutine != null) {
                    val routine = currentRoutine!!
                    MechaTimeBlock(
                        title = "MORNING // গতিবিদ্যা",
                        content = routine.morning,
                        accentColor = DoomGreen,
                        fontFamily = HindSiliguriFamily
                    )
                    MechaTimeBlock(
                        title = "NOON // রাসায়নিক ও গণিত",
                        content = routine.noon,
                        accentColor = ThorCyan,
                        fontFamily = HindSiliguriFamily
                    )
                    MechaTimeBlock(
                        title = "NIGHT // রিভিশন ও এক্সাম",
                        content = routine.night,
                        accentColor = if (routine.night.contains("এক্সাম") || routine.night.contains("🎯")) DangerRed else DoomGreen,
                        fontFamily = KalpurushFamily
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .border(1.dp, DoomGreen.copy(alpha = 0.3f), CutCornerShape(12.dp))
                            .background(CardSurfaceDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NO PROTOCOL FOR THIS DATE\nTAP '+' TO SEED 30 DAYS",
                            color = ThorCyan,
                            fontFamily = OrbitronFamily,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        if (showEditSheet && currentRoutine != null) {
            EditRoutineBottomSheet(
                routine = currentRoutine!!,
                onDismiss = { showEditSheet = false },
                onSave = { updated ->
                    viewModel.updateRoutine(updated)
                    showEditSheet = false
                }
            )
        }
    }
}

@Composable
fun HudHeaderCard(
    currentDate: LocalDate,
    displayDate: String,
    phase: String,
    target: String,
    ratio: String,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DoomGreen.copy(alpha = 0.5f), CutCornerShape(14.dp)),
        shape = CutCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0F1D))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // TOP STATUS ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STATUS: SECURE // $phase".uppercase(),
                    color = DoomGreen,
                    fontFamily = OrbitronFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "RATIO: $ratio",
                    color = ThorCyan,
                    fontFamily = OrbitronFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            // DATE NAVIGATOR ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevDay) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Day",
                        tint = DoomGreen
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = displayDate,
                        color = Color.White,
                        fontFamily = HindSiliguriFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Text(
                        text = currentDate.toString(),
                        color = ThorCyan.copy(alpha = 0.6f),
                        fontFamily = OrbitronFamily,
                        fontSize = 11.sp
                    )
                }

                IconButton(onClick = onNextDay) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Day",
                        tint = DoomGreen
                    )
                }
            }

            HorizontalDivider(color = DoomGreen.copy(alpha = 0.2f), thickness = 1.dp)

            // DAILY TARGET ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DAILY TARGET:",
                    color = DoomGreen,
                    fontFamily = OrbitronFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = target,
                    color = DangerRed,
                    fontFamily = HindSiliguriFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f, fill = false).padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun MechaTimeBlock(
    title: String,
    content: String,
    accentColor: Color,
    fontFamily: FontFamily
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, accentColor.copy(alpha = 0.6f), CutCornerShape(12.dp)),
        shape = CutCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF090D1A))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "/// $title",
                color = accentColor,
                fontFamily = OrbitronFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )

            // Highlight exam terms in red
            val formattedAnnotated = remember(content) {
                buildAnnotatedString {
                    val examKeywords = listOf("🎯", "অফলাইন এক্সাম", "এক্সাম", "Exam", "Weekly Exam", "Monthly Exam", "কাল এক্সাম")
                    var index = 0
                    while (index < content.length) {
                        var matchedKeyword: String? = null
                        for (keyword in examKeywords) {
                            if (content.startsWith(keyword, index)) {
                                matchedKeyword = keyword
                                break
                            }
                        }
                        if (matchedKeyword != null) {
                            withStyle(SpanStyle(color = DangerRed, fontWeight = FontWeight.Bold)) {
                                append(matchedKeyword)
                            }
                            index += matchedKeyword.length
                        } else {
                            append(content[index])
                            index++
                        }
                    }
                }
            }

            Text(
                text = formattedAnnotated,
                color = Color.White,
                fontFamily = fontFamily,
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRoutineBottomSheet(
    routine: RoutineEntity,
    onDismiss: () -> Unit,
    onSave: (RoutineEntity) -> Unit
) {
    var morning by remember { mutableStateOf(routine.morning) }
    var noon by remember { mutableStateOf(routine.noon) }
    var night by remember { mutableStateOf(routine.night) }
    var target by remember { mutableStateOf(routine.target) }
    var phase by remember { mutableStateOf(routine.phase) }
    var ratio by remember { mutableStateOf(routine.ratio) }
    var ratioDropdownExpanded by remember { mutableStateOf(false) }

    val ratioOptions = listOf("3:1", "3:2", "3:3", "2:1", "2:2", "4:1", "1:2", "2:4")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundDark,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "EDIT PROTOCOL: ${routine.displayDate}",
                color = DoomGreen,
                fontFamily = OrbitronFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            OutlinedTextField(
                value = phase,
                onValueChange = { phase = it },
                label = { Text("Phase Status", color = DoomGreen) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DoomGreen,
                    unfocusedBorderColor = DoomGreen.copy(alpha = 0.4f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // RATIO EXPOSED DROPDOWN
            ExposedDropdownMenuBox(
                expanded = ratioDropdownExpanded,
                onExpandedChange = { ratioDropdownExpanded = !ratioDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = ratio,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Widget Ratio Layout", color = ThorCyan) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = ratioDropdownExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ThorCyan,
                        unfocusedBorderColor = ThorCyan.copy(alpha = 0.4f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                ExposedDropdownMenu(
                    expanded = ratioDropdownExpanded,
                    onDismissRequest = { ratioDropdownExpanded = false }
                ) {
                    ratioOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = "Ratio $option", color = Color.White) },
                            onClick = {
                                ratio = option
                                ratioDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = target,
                onValueChange = { target = it },
                label = { Text("Daily Target", color = DangerRed) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DangerRed,
                    unfocusedBorderColor = DangerRed.copy(alpha = 0.4f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            OutlinedTextField(
                value = morning,
                onValueChange = { morning = it },
                label = { Text("Morning Slot", color = DoomGreen) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DoomGreen,
                    unfocusedBorderColor = DoomGreen.copy(alpha = 0.4f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            OutlinedTextField(
                value = noon,
                onValueChange = { noon = it },
                label = { Text("Noon Slot", color = ThorCyan) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ThorCyan,
                    unfocusedBorderColor = ThorCyan.copy(alpha = 0.4f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            OutlinedTextField(
                value = night,
                onValueChange = { night = it },
                label = { Text("Night Slot", color = DangerRed) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DangerRed,
                    unfocusedBorderColor = DangerRed.copy(alpha = 0.4f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("CANCEL", color = Color.White, fontFamily = OrbitronFamily)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = {
                        onSave(
                            routine.copy(
                                morning = morning,
                                noon = noon,
                                night = night,
                                target = target,
                                phase = phase,
                                ratio = ratio
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DoomGreen),
                    shape = CutCornerShape(8.dp)
                ) {
                    Text("SAVE TO DATABASE", color = Color.Black, fontFamily = OrbitronFamily, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
