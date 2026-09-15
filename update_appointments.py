import re

with open("app/src/main/java/com/example/ui/screens/AppointmentsScreen.kt", "r") as f:
    content = f.read()

# Add ContactViewModel to imports
content = content.replace("import com.example.ui.viewmodel.MedicineViewModel", 
"""import com.example.ui.viewmodel.MedicineViewModel
import com.example.ui.viewmodel.ContactViewModel
import com.example.ui.components.ContactCard
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset""")

# Add ContactViewModel parameter
content = content.replace("medicineViewModel: MedicineViewModel,",
"medicineViewModel: MedicineViewModel,\n    contactViewModel: ContactViewModel,")

# Add state for tabs and contacts
state_vars = """    val historyList by medicineViewModel.medicationHistoryList.collectAsState()
    val context = LocalContext.current

    val pharmacies by contactViewModel.pharmacies.collectAsState()
    val hospitals by contactViewModel.hospitals.collectAsState()

    var showScheduleDialog by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("APPOINTMENTS", "PHARMACIES", "HOSPITALS")"""

content = content.replace("    val historyList by medicineViewModel.medicationHistoryList.collectAsState()\n    val context = LocalContext.current\n\n    var showScheduleDialog by remember { mutableStateOf(false) }", state_vars)

# Change FAB to only show on Appointments tab
fab_code = """        floatingActionButton = {
            if (selectedTabIndex == 0) {
                ExtendedFloatingActionButton(
                    onClick = { showScheduleDialog = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(bottom = 64.dp)
                        .testTag("schedule_appointment_fab"),
                    icon = { Icon(Icons.Outlined.AddCircleOutline, contentDescription = "Add") },
                    text = { Text("Add", fontWeight = FontWeight.SemiBold) }
                )
            }
        }"""
content = re.sub(r"        floatingActionButton = \{.*?\n        \}", fab_code, content, flags=re.DOTALL)

# Add TabRow under Header Title Row
tab_row = """            Spacer(modifier = Modifier.height(16.dp))
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
            }"""

content = content.replace("            Spacer(modifier = Modifier.height(24.dp))\n            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))", tab_row)

# Wrap LazyColumn contents in a when(selectedTabIndex)
new_lazy_column = """            LazyColumn(
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
            }"""

content = re.sub(r"            LazyColumn\(.*?\)\s*\{.*?item\s*\{\s*Spacer\(modifier = Modifier.height\(100.dp\)\)\s*\}\s*\}", new_lazy_column, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/AppointmentsScreen.kt", "w") as f:
    f.write(content)

