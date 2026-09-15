package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.Medication

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddMedicationDialog(
    onDismiss: () -> Unit,
    onSave: (Medication) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var selectedForm by remember { mutableStateOf("Tablet") }
    var selectedInstruction by remember { mutableStateOf("After meal") }
    var scheduleMode by remember { mutableStateOf("Once daily") }
    val selectedSpecificDays = remember { mutableStateListOf<String>() }
    var intervalDays by remember { mutableStateOf("2") }
    val doseTimes = remember { mutableStateListOf("08:00") }
    var newTimeInput by remember { mutableStateOf("20:00") }

    var stock by remember { mutableStateOf("30") }
    var totalPack by remember { mutableStateOf("30") }
    var lowThreshold by remember { mutableStateOf("7") }
    var unitType by remember { mutableStateOf("pills") }
    var expiryDate by remember { mutableStateOf("") }
    var doctorOrRx by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#006D77") }

    val forms = listOf("Tablet", "Capsule", "Liquid", "Inhaler", "Drops", "Injection")
    val instructions = listOf("After meal", "Before meal", "With meal", "Bedtime", "Empty stomach")
    val colorPalette = listOf("#006D77", "#2A9D8F", "#E76F51", "#EF6C00", "#1565C0", "#6A1B9A", "#2E7D32")

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(vertical = 24.dp)
            .testTag("add_medication_dialog"),
        title = {
            Text(
                text = "Add New Medication",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Name & Dosage
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medication Name *") },
                    placeholder = { Text("e.g. Amoxicillin, Lisinopril") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("med_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        label = { Text("Dosage *") },
                        placeholder = { Text("500 mg, 10 ml") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("med_dosage_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = unitType,
                        onValueChange = { unitType = it },
                        label = { Text("Unit") },
                        placeholder = { Text("pills, ml") },
                        singleLine = true,
                        modifier = Modifier.weight(0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Medication Form
                Text("Dosage Form", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    forms.forEach { form ->
                        FilterChip(
                            selected = selectedForm == form,
                            onClick = {
                                selectedForm = form
                                unitType = when (form) {
                                    "Tablet" -> "pills"
                                    "Capsule" -> "capsules"
                                    "Liquid" -> "ml"
                                    "Inhaler" -> "sprays"
                                    "Drops" -> "drops"
                                    else -> "units"
                                }
                            },
                            label = { Text(form) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Food Instructions
                Text("Intake Instructions", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    instructions.forEach { inst ->
                        FilterChip(
                            selected = selectedInstruction == inst,
                            onClick = { selectedInstruction = inst },
                            label = { Text(inst) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Schedule Mode
                Text("Schedule", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                val scheduleModesList = listOf("Once daily", "Twice daily", "On demand", "Specific days", "Interval")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    scheduleModesList.forEach { mode ->
                        FilterChip(
                            selected = scheduleMode == mode,
                            onClick = { 
                                scheduleMode = mode 
                                if (mode == "Once daily" && doseTimes.size > 1) {
                                    while(doseTimes.size > 1) doseTimes.removeLast()
                                } else if (mode == "Twice daily") {
                                    if (doseTimes.isEmpty()) doseTimes.add("08:00")
                                    if (doseTimes.size == 1) doseTimes.add("20:00")
                                }
                            },
                            label = { Text(mode) }
                        )
                    }
                }
                
                if (scheduleMode == "Specific days") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Select Days", style = MaterialTheme.typography.labelSmall)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                        daysOfWeek.forEach { day ->
                            FilterChip(
                                selected = selectedSpecificDays.contains(day),
                                onClick = { 
                                    if (selectedSpecificDays.contains(day)) selectedSpecificDays.remove(day) 
                                    else selectedSpecificDays.add(day) 
                                },
                                label = { Text(day) }
                            )
                        }
                    }
                } else if (scheduleMode == "Interval") {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = intervalDays,
                        onValueChange = { if (it.all { c -> c.isDigit() }) intervalDays = it },
                        label = { Text("Days between doses") },
                        placeholder = { Text("e.g. 2 for every other day") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                if (scheduleMode != "On demand") {
                    Spacer(modifier = Modifier.height(14.dp))

                    // Schedule Dose Times
                    Text("Dose Times (HH:mm)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newTimeInput,
                            onValueChange = { newTimeInput = it },
                            placeholder = { Text("e.g. 14:00") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newTimeInput.isNotBlank() && !doseTimes.contains(newTimeInput.trim())) {
                                    doseTimes.add(newTimeInput.trim())
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add time")
                            Text("Add")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        doseTimes.forEach { time ->
                            FilterChip(
                                selected = true,
                                onClick = { if (doseTimes.size > 1) doseTimes.remove(time) },
                                label = { Text(time) },
                                trailingIcon = {
                                    if (doseTimes.size > 1) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(14.dp))
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Inventory Settings
                Text("Inventory & Refill Alerts", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { if (it.all { c -> c.isDigit() }) stock = it },
                        label = { Text("Initial Stock") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("initial_stock_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = totalPack,
                        onValueChange = { if (it.all { c -> c.isDigit() }) totalPack = it },
                        label = { Text("Total Pack Size") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = lowThreshold,
                        onValueChange = { if (it.all { c -> c.isDigit() }) lowThreshold = it },
                        label = { Text("Alert Below") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Expiry Date (Optional)") },
                    placeholder = { Text("YYYY-MM-DD") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = doctorOrRx,
                    onValueChange = { doctorOrRx = it },
                    label = { Text("Doctor / Rx Number (Optional)") },
                    placeholder = { Text("Dr. Vance / Rx #12345") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Color Theme Tag
                Text("Color Tag", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    colorPalette.forEach { hex ->
                        val color = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (selectedColor == hex) 3.dp else 0.dp,
                                    color = if (selectedColor == hex) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = hex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && dosage.isNotBlank()) {
                        val currentStockInt = stock.toIntOrNull() ?: 30
                        val totalPackInt = totalPack.toIntOrNull() ?: currentStockInt
                        val thresholdInt = lowThreshold.toIntOrNull() ?: 5
                        val med = Medication(
                            name = name.trim(),
                            dosage = dosage.trim(),
                            form = selectedForm,
                            instruction = selectedInstruction,
                            frequency = when (scheduleMode) {
                                "Once daily" -> "Daily"
                                "Twice daily" -> "Twice Daily"
                                "On demand" -> "On Demand"
                                "Specific days" -> "Specific: " + selectedSpecificDays.joinToString(",")
                                "Interval" -> "Every $intervalDays days"
                                else -> "Daily"
                            },
                            frequencyType = when (scheduleMode) {
                                "On demand" -> "OnDemand"
                                "Specific days" -> "SpecificDays"
                                "Interval" -> "Interval"
                                else -> "Daily"
                            },
                            frequencyData = when (scheduleMode) {
                                "Specific days" -> selectedSpecificDays.joinToString(",")
                                "Interval" -> intervalDays
                                else -> ""
                            },
                            startDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
                            doseTimes = if (scheduleMode == "On demand") "" else doseTimes.sorted().joinToString(","),
                            currentStock = currentStockInt,
                            totalPackSize = totalPackInt,
                            lowStockThreshold = thresholdInt,
                            unitType = unitType.trim().ifBlank { "pills" },
                            colorHex = selectedColor,
                            expiryDate = expiryDate.trim(),
                            doctorOrRx = doctorOrRx.trim(),
                            notes = notes.trim(),
                            isActive = true
                        )
                        onSave(med)
                    }
                },
                modifier = Modifier.testTag("save_medication_button")
            ) {
                Text("Save Medication")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
