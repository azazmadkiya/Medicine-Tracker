package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.DoseWithMedication
import com.example.ui.components.DoseCard

import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen

import com.example.ui.viewmodel.MedicineViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DayItem(
    val dateString: String, // YYYY-MM-DD
    val dayOfWeek: String,  // Mon, Tue
    val dayOfMonth: String, // 15
    val isToday: Boolean
)

@Composable
fun TodayScreen(
    viewModel: MedicineViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val doses by viewModel.currentDoses.collectAsState()
    val adherence by viewModel.adherence.collectAsState()

    val daysList = remember {
        val list = mutableListOf<DayItem>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -2)
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        for (i in 0..7) {
            val dStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
            val dow = SimpleDateFormat("EEE", Locale.getDefault()).format(cal.time)
            val dom = SimpleDateFormat("d", Locale.getDefault()).format(cal.time)
            list.add(DayItem(dStr, dow, dom, dStr == todayStr))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    val morningDoses = doses.filter {
        val hour = it.scheduledTime.split(":").firstOrNull()?.toIntOrNull() ?: 0
        hour in 5..11
    }
    val afternoonDoses = doses.filter {
        val hour = it.scheduledTime.split(":").firstOrNull()?.toIntOrNull() ?: 0
        hour in 12..16
    }
    val eveningDoses = doses.filter {
        val hour = it.scheduledTime.split(":").firstOrNull()?.toIntOrNull() ?: 0
        hour in 17..20
    }
    val nightDoses = doses.filter {
        val hour = it.scheduledTime.split(":").firstOrNull()?.toIntOrNull() ?: 0
        hour >= 21 || hour < 5
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Doses",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    val dateFormatted = try {
                        val d = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate)
                        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(d ?: Date())
                    } catch (e: Exception) {
                        "Today"
                    }
                    Text(
                        text = dateFormatted,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Streak Badge
                Surface(
                    color = StatusAmber.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("streak_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = StatusAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "7 Day Streak",
                            color = StatusAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Horizontal Date Selector
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("date_picker_row")
            ) {
                items(daysList) { day ->
                    val isSelected = day.dateString == selectedDate
                    Card(
                        modifier = Modifier
                            .width(54.dp)
                            .height(72.dp)
                            .clickable { viewModel.setSelectedDate(day.dateString) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = day.dayOfWeek,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = day.dayOfMonth,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            if (day.isToday) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color.White else MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Adherence Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("adherence_summary_card"),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { adherence.adherenceRate },
                                modifier = Modifier.size(68.dp),
                                color = Color.White,
                                trackColor = Color.White.copy(alpha = 0.25f),
                                strokeWidth = 7.dp
                            )
                            Text(
                                text = "${(adherence.adherenceRate * 100).toInt()}%",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(18.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when {
                                    adherence.totalScheduled == 0 -> "No Doses Today"
                                    adherence.takenCount == adherence.totalScheduled -> "All Done for Today!"
                                    else -> "Daily Adherence"
                                },
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${adherence.takenCount} of ${adherence.totalScheduled} doses completed (${adherence.pendingCount} remaining)",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Doses Grouped by Period
        if (doses.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No medications scheduled for this date",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Switch to Medications tab to add a new prescription schedule.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            if (morningDoses.isNotEmpty()) {
                item {
                    PeriodHeader(title = "Morning", subtitle = "6:00 AM – 11:59 AM", count = morningDoses.size)
                }
                items(morningDoses) { dose ->
                    DoseCard(
                        dose = dose,
                        onTake = { viewModel.markDoseTaken(dose) },
                        onSkip = { viewModel.markDoseSkipped(dose) },
                        onReset = { viewModel.resetDoseStatus(dose) },
                        onTestNotification = { viewModel.triggerTestNotification(dose.medication) }
                    )
                }
            }

            if (afternoonDoses.isNotEmpty()) {
                item {
                    PeriodHeader(title = "Afternoon", subtitle = "12:00 PM – 4:59 PM", count = afternoonDoses.size)
                }
                items(afternoonDoses) { dose ->
                    DoseCard(
                        dose = dose,
                        onTake = { viewModel.markDoseTaken(dose) },
                        onSkip = { viewModel.markDoseSkipped(dose) },
                        onReset = { viewModel.resetDoseStatus(dose) },
                        onTestNotification = { viewModel.triggerTestNotification(dose.medication) }
                    )
                }
            }

            if (eveningDoses.isNotEmpty()) {
                item {
                    PeriodHeader(title = "Evening", subtitle = "5:00 PM – 8:59 PM", count = eveningDoses.size)
                }
                items(eveningDoses) { dose ->
                    DoseCard(
                        dose = dose,
                        onTake = { viewModel.markDoseTaken(dose) },
                        onSkip = { viewModel.markDoseSkipped(dose) },
                        onReset = { viewModel.resetDoseStatus(dose) },
                        onTestNotification = { viewModel.triggerTestNotification(dose.medication) }
                    )
                }
            }

            if (nightDoses.isNotEmpty()) {
                item {
                    PeriodHeader(title = "Night", subtitle = "9:00 PM – 5:59 AM", count = nightDoses.size)
                }
                items(nightDoses) { dose ->
                    DoseCard(
                        dose = dose,
                        onTake = { viewModel.markDoseTaken(dose) },
                        onSkip = { viewModel.markDoseSkipped(dose) },
                        onReset = { viewModel.resetDoseStatus(dose) },
                        onTestNotification = { viewModel.triggerTestNotification(dose.medication) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
fun PeriodHeader(
    title: String,
    subtitle: String,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
        
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = CircleShape
        ) {
            Text(
                text = "$count ${if (count == 1) "dose" else "doses"}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}
