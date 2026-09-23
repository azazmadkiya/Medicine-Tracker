package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.Medication
import com.example.ui.viewmodel.MedicineViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPlannerScreen(
    viewModel: MedicineViewModel,
    onBack: () -> Unit
) {
    val allMedications by viewModel.allMedications.collectAsState()
    
    // Default to a 2-day trip
    var startDateMillis by remember { mutableStateOf(Calendar.getInstance().timeInMillis) }
    var endDateMillis by remember { 
        mutableStateOf(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 2) }.timeInMillis) 
    }
    
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    
    val tripDurationDays = remember(startDateMillis, endDateMillis) {
        val diff = endDateMillis - startDateMillis
        val days = TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(1)
        days
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip Planner", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                
                // Hero Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Luggage,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Plan your medication for travel",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Enter your trip details below and we'll calculate exactly the right amount of medication you need to take with you.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            item {
                Text(
                    text = "Your trip details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column {
                        // Start Date
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showStartPicker = true }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Start date", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = formatDateOrRelative(startDateMillis),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                        
                        // End Date
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showEndPicker = true }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("End date", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = formatDateOrRelative(endDateMillis),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                        
                        // Duration
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Trip duration", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = "$tripDurationDays days",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            item {
                Text(
                    text = "Your medication needs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column {
                        if (allMedications.isEmpty()) {
                            Text(
                                text = "No medications scheduled.",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            allMedications.forEachIndexed { index, med ->
                                val pillsNeeded = calculatePillsForTrip(med, tripDurationDays.toInt(), startDateMillis)
                                MedicationNeedItem(medication = med, pillsNeeded = pillsNeeded)
                                if (index < allMedications.lastIndex) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
        
        // Date Pickers
        if (showStartPicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = startDateMillis)
            DatePickerDialog(
                onDismissRequest = { showStartPicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { startDateMillis = it }
                        showStartPicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showStartPicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        
        if (showEndPicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = endDateMillis)
            DatePickerDialog(
                onDismissRequest = { showEndPicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { endDateMillis = it }
                        showEndPicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showEndPicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
fun MedicationNeedItem(medication: Medication, pillsNeeded: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = medication.name.firstOrNull()?.uppercase() ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${medication.name} (${medication.dosage})",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$pillsNeeded ${medication.form.lowercase()}(s)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun calculatePillsForTrip(medication: Medication, days: Int, startDateMillis: Long): Int {
    val doseCountPerDay = medication.doseTimes.split(",").filter { it.isNotBlank() }.size.coerceAtLeast(1)
    
    when (medication.frequencyType) {
        "Daily" -> {
            return doseCountPerDay * days
        }
        "SpecificDays" -> {
            val daysOfWeek = medication.frequencyData.split(",").map { it.trim() }
            var matchCount = 0
            val cal = Calendar.getInstance()
            cal.timeInMillis = startDateMillis
            val sdf = SimpleDateFormat("EEE", Locale.ENGLISH)
            for (i in 0 until days) {
                val dow = sdf.format(cal.time)
                if (daysOfWeek.contains(dow)) {
                    matchCount++
                }
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
            return matchCount * doseCountPerDay
        }
        "Interval" -> {
            val interval = medication.frequencyData.toIntOrNull() ?: 2
            val occurrences = Math.ceil(days.toDouble() / interval.coerceAtLeast(1).toDouble()).toInt()
            return occurrences * doseCountPerDay
        }
        "OnDemand" -> {
            return 0
        }
        else -> return doseCountPerDay * days
    }
}

fun formatDateOrRelative(millis: Long): String {
    val cal = Calendar.getInstance()
    
    val target = Calendar.getInstance().apply { timeInMillis = millis }
    
    if (cal.get(Calendar.YEAR) == target.get(Calendar.YEAR) && 
        cal.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)) {
        return "Today"
    }
    
    cal.add(Calendar.DAY_OF_YEAR, 1)
    if (cal.get(Calendar.YEAR) == target.get(Calendar.YEAR) && 
        cal.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)) {
        return "Tomorrow"
    }
    
    val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return format.format(Date(millis))
}
