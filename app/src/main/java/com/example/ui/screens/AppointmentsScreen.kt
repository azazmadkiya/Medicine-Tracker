package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.AddAppointmentDialog
import com.example.ui.components.AddContactDialog
import com.example.ui.components.AppointmentCard
import com.example.ui.viewmodel.AppointmentViewModel
import com.example.ui.viewmodel.MedicineViewModel
import com.example.ui.viewmodel.ContactViewModel
import com.example.ui.components.ContactCard
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.example.utils.ReportExporter

@Composable
fun AppointmentsScreen(
    viewModel: AppointmentViewModel,
    medicineViewModel: MedicineViewModel,
    contactViewModel: ContactViewModel,
    modifier: Modifier = Modifier
) {
    val upcomingAppointments by viewModel.upcomingAppointments.collectAsState()
    val pastAppointments by viewModel.pastAppointments.collectAsState()
    val allAppointments = (upcomingAppointments + pastAppointments).sortedByDescending { it.dateTimeMillis }
    val historyList by medicineViewModel.medicationHistoryList.collectAsState()
    val context = LocalContext.current

    val pharmacies by contactViewModel.pharmacies.collectAsState()
    val hospitals by contactViewModel.hospitals.collectAsState()

    var showScheduleDialog by remember { mutableStateOf(false) }
    var showAddPharmacyDialog by remember { mutableStateOf(false) }
    var showAddHospitalDialog by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("APPOINTMENTS", "PHARMACIES", "HOSPITALS")

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    when (selectedTabIndex) {
                        0 -> showScheduleDialog = true
                        1 -> showAddPharmacyDialog = true
                        2 -> showAddHospitalDialog = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 64.dp)
                    .testTag("add_item_fab"),
                icon = { Icon(Icons.Outlined.AddCircleOutline, contentDescription = "Add") },
                text = { Text("Add", fontWeight = FontWeight.SemiBold) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Header Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Support",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                IconButton(
                    onClick = {
                        ReportExporter.exportReport(context, historyList, allAppointments)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Export Report",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                divider = {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
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
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> { // Appointments
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SupportAgent,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                        
                                    Spacer(modifier = Modifier.width(16.dp))
                                        
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Chat with Mighty",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 18.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Your personal MyTherapy Assistant",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                                
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        items(allAppointments, key = { it.id }) { appt ->
                            AppointmentCard(
                                appointment = appt,
                                onMarkCompleted = { viewModel.markCompleted(appt.id) },
                                onDelete = { viewModel.deleteAppointment(appt) },
                                onTestNotification = { viewModel.triggerTestNotification(appt) }
                            )
                        }
                    }
                    1 -> { // Pharmacies
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                        items(pharmacies, key = { it.id }) { pharmacy ->
                            ContactCard(contact = pharmacy)
                        }
                    }
                    2 -> { // Hospitals
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                        items(hospitals, key = { it.id }) { hospital ->
                            ContactCard(contact = hospital)
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        if (showScheduleDialog) {
            AddAppointmentDialog(
                onDismiss = { showScheduleDialog = false },
                onSave = { newAppt ->
                    viewModel.scheduleAppointment(newAppt)
                    showScheduleDialog = false
                }
            )
        }

        if (showAddPharmacyDialog) {
            AddContactDialog(
                type = "PHARMACY",
                onDismiss = { showAddPharmacyDialog = false },
                onSave = { contact ->
                    contactViewModel.insertContact(contact)
                    showAddPharmacyDialog = false
                }
            )
        }

        if (showAddHospitalDialog) {
            AddContactDialog(
                type = "HOSPITAL",
                onDismiss = { showAddHospitalDialog = false },
                onSave = { contact ->
                    contactViewModel.insertContact(contact)
                    showAddHospitalDialog = false
                }
            )
        }
    }
}
