package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.ArrowBack
import com.example.data.local.entity.Medication
import com.example.ui.viewmodel.MedicineViewModel
import com.example.ui.viewmodel.MedicationHistory
import com.example.ui.theme.StatusGreen
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.data.local.entity.DoseLog

@Composable
fun ProgressScreen(
    viewModel: MedicineViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(1) } // Default to LIST
    val tabs = listOf("CHARTS", "LIST")
    var selectedMedicationForHistory by remember { mutableStateOf<Medication?>(null) }

    if (selectedMedicationForHistory != null) {
        MedicationDetailedHistoryScreen(
            medication = selectedMedicationForHistory!!,
            viewModel = viewModel,
            onBack = { selectedMedicationForHistory = null }
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            indicator = { tabPositions ->
                if (selectedTabIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> {
                // Charts tab - re-use inventory screen for now, or just show a placeholder
                InventoryScreen(viewModel = viewModel, modifier = Modifier.weight(1f))
            }
            1 -> {
                // List tab - History
                MedicationHistoryList(
                    viewModel = viewModel,
                    onMedicationClick = { selectedMedicationForHistory = it }
                )
            }
        }
    }
}

@Composable
fun MedicationHistoryList(
    viewModel: MedicineViewModel,
    onMedicationClick: (Medication) -> Unit
) {
    val historyList by viewModel.medicationHistoryList.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(historyList, key = { it.medication.id }) { history ->
            MedicationHistoryCard(
                history = history,
                onClick = { onMedicationClick(history.medication) }
            )
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationHistoryCard(
    history: MedicationHistory,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${history.medication.name} (${history.medication.dosage})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Last 7 days",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                history.historyDays.forEach { day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = day.dayOfWeek,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (day.isTaken) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(StatusGreen)
                                    .then(
                                        if (day.isToday) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                        else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Taken",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .then(
                                        if (day.isToday) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                        else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationDetailedHistoryScreen(
    medication: Medication,
    viewModel: MedicineViewModel,
    onBack: () -> Unit
) {
    val logs by viewModel.getLogsForMedication(medication.id).collectAsState(initial = emptyList())
    
    // Group logs by month
    val groupedLogs = remember(logs) {
        val formatMonth = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val parseDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        logs.groupBy { log ->
            try {
                val date = parseDate.parse(log.doseDate)
                if (date != null) formatMonth.format(date) else "Unknown"
            } catch (e: Exception) {
                "Unknown"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "${medication.name} History", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            groupedLogs.forEach { (month, monthLogs) ->
                item {
                    Text(
                        text = month,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column {
                            monthLogs.forEachIndexed { index, log ->
                                DoseHistoryItem(log = log, medication = medication)
                                if (index < monthLogs.size - 1) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            if (logs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No history recorded yet.",
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DoseHistoryItem(log: DoseLog, medication: Medication) {
    val parseDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formatDay = SimpleDateFormat("EEEE, dd/MM/yy", Locale.getDefault())
    
    val displayDate = try {
        val date = parseDate.parse(log.doseDate)
        if (date != null) formatDay.format(date) else log.doseDate
    } catch (e: Exception) {
        log.doseDate
    }
    
    // Parse time if possible (assume HH:mm) to convert to 12h format or just use as is
    val displayTime = try {
        val parseTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formatTime = SimpleDateFormat("h:mm a", Locale.getDefault())
        val time = parseTime.parse(log.doseTime)
        if (time != null) formatTime.format(time).lowercase() else log.doseTime
    } catch (e: Exception) {
        log.doseTime
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = displayDate,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = medication.dosage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayTime,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (log.status == "TAKEN") {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Taken",
                    tint = StatusGreen,
                    modifier = Modifier.size(24.dp)
                )
            } else if (log.status == "SKIPPED") {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Skipped",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                // Pending
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Pending",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
