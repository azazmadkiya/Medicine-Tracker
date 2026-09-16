package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.Contact

@Composable
fun AddContactDialog(
    type: String, // "PHARMACY" or "HOSPITAL"
    contactToEdit: Contact? = null,
    onDismiss: () -> Unit,
    onSave: (Contact) -> Unit
) {
    var name by remember { mutableStateOf(contactToEdit?.name ?: "") }
    var phoneNumber by remember { mutableStateOf(contactToEdit?.phoneNumber ?: "") }
    var address by remember { mutableStateOf(contactToEdit?.address ?: "") }
    var notes by remember { mutableStateOf(contactToEdit?.notes ?: "") }

    val isFormValid = name.isNotBlank() && phoneNumber.isNotBlank()
    val typeName = if (type == "PHARMACY") "Pharmacy" else "Hospital"
    val title = if (contactToEdit != null) "Edit $typeName" else "Add $typeName"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isFormValid) {
                        onSave(
                            contactToEdit?.copy(
                                name = name,
                                phoneNumber = phoneNumber,
                                address = address,
                                notes = notes
                            ) ?: Contact(
                                name = name,
                                type = type,
                                phoneNumber = phoneNumber,
                                address = address,
                                notes = notes
                            )
                        )
                    }
                },
                enabled = isFormValid
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
