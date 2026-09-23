import sys

with open("app/src/main/java/com/example/ui/components/AddMedicationDialog.kt", "r") as f:
    content = f.read()

# Add imports
imports = """import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TextButton
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.DateRange
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
"""

if "import androidx.compose.material3.DatePickerDialog" not in content:
    content = content.replace("import androidx.compose.material3.Text", "import androidx.compose.material3.Text\n" + imports)

# Add states
state_injection = """    var newTimeInput by remember { mutableStateOf("20:00") }
    
    var showStartDatePicker by remember { mutableStateOf(false) }
    var startDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    val displayDateFormatter = remember { SimpleDateFormat("dd/MM/yy", Locale.getDefault()) }
    val fullDateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }"""
content = content.replace('    var newTimeInput by remember { mutableStateOf("20:00") }', state_injection)

# Add UI for picking start date
# We will insert it under "if (scheduleMode != "On demand") {"
target_ui = """                if (scheduleMode != "On demand") {
                    Spacer(modifier = Modifier.height(14.dp))

                    // Schedule Dose Times"""
replacement_ui = """                if (scheduleMode != "On demand") {
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Text("Start date", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = displayDateFormatter.format(Date(startDateMillis)),
                        onValueChange = { },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().clickable { showStartDatePicker = true },
                        trailingIcon = {
                            IconButton(onClick = { showStartDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Select Start Date")
                            }
                        }
                    )
                    
                    if (showStartDatePicker) {
                        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = startDateMillis)
                        DatePickerDialog(
                            onDismissRequest = { showStartDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let {
                                        startDateMillis = it
                                    }
                                    showStartDatePicker = false
                                }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showStartDatePicker = false }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Schedule Dose Times"""
content = content.replace(target_ui, replacement_ui)

# Update startDate to use the selected date
target_start_date = 'startDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())'
replacement_start_date = 'startDate = fullDateFormatter.format(Date(startDateMillis))'
content = content.replace(target_start_date, replacement_start_date)

with open("app/src/main/java/com/example/ui/components/AddMedicationDialog.kt", "w") as f:
    f.write(content)

print("Success")
