import re

with open("app/src/main/java/com/example/ui/screens/AppointmentsScreen.kt", "r") as f:
    content = f.read()

# Add import
content = content.replace("import com.example.ui.components.AddAppointmentDialog",
"""import com.example.ui.components.AddAppointmentDialog
import com.example.ui.components.AddContactDialog""")

# Add dialog states
states = """    var showScheduleDialog by remember { mutableStateOf(false) }
    var showAddPharmacyDialog by remember { mutableStateOf(false) }
    var showAddHospitalDialog by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }"""
content = content.replace("    var showScheduleDialog by remember { mutableStateOf(false) }\n    var selectedTabIndex by remember { mutableStateOf(0) }", states)

# Update FAB
old_fab = """        floatingActionButton = {
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
new_fab = """        floatingActionButton = {
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
        }"""
content = content.replace(old_fab, new_fab)

# Add dialog renderings at the bottom
old_dialog = """        if (showScheduleDialog) {
            AddAppointmentDialog(
                onDismiss = { showScheduleDialog = false },
                onSave = { newAppt ->
                    viewModel.scheduleAppointment(newAppt)
                    showScheduleDialog = false
                }
            )
        }
    }
}"""
new_dialogs = """        if (showScheduleDialog) {
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
}"""
content = content.replace(old_dialog, new_dialogs)

with open("app/src/main/java/com/example/ui/screens/AppointmentsScreen.kt", "w") as f:
    f.write(content)

