import re

with open('app/src/main/java/com/example/ui/routine/DashboardScreen.kt', 'r') as f:
    content = f.read()

# Replace dateStr with displayDate
content = content.replace('routine.dateStr', 'routine.displayDate')
content = content.replace('currentRoutine!!.dateStr', 'currentRoutine!!.displayDate')

# Modify EditRoutineDialog
old_dialog = '''fun EditRoutineDialog(
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
                    label = { Text("Daily Target", color = DangerRed) },
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
                        Text("CANCEL", color = Color.White)
                    }
                    Button(
                        onClick = {
                            onSave(routine.copy(morning = morning, noon = noon, night = night, target = target))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ThorCyan)
                    ) {
                        Text("SAVE", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}'''

new_dialog = '''@OptIn(ExperimentalMaterial3Api::class)
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
    var ratio by remember { mutableStateOf(routine.ratio) }
    var expanded by remember { mutableStateOf(false) }
    val ratioOptions = listOf("3:1", "3:2", "3:3", "2:1", "2:2", "4:1", "1:2", "2:4")

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
                    label = { Text("Daily Target", color = DangerRed) },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DangerRed,
                        unfocusedBorderColor = DangerRed.copy(alpha = 0.5f)
                    )
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = ratio,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Widget Ratio", color = Color.White) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        ratioOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    ratio = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL", color = Color.White)
                    }
                    Button(
                        onClick = {
                            onSave(routine.copy(morning = morning, noon = noon, night = night, target = target, ratio = ratio))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ThorCyan)
                    ) {
                        Text("SAVE", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}'''

content = content.replace(old_dialog, new_dialog)

with open('app/src/main/java/com/example/ui/routine/DashboardScreen.kt', 'w') as f:
    f.write(content)

