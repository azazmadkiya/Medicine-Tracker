package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.Contact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfessionalScreen(
    contactToEdit: Contact? = null,
    onDismiss: () -> Unit,
    onSave: (Contact) -> Unit
) {
    var name by remember { mutableStateOf(contactToEdit?.name ?: "") }
    var email by remember { mutableStateOf(contactToEdit?.email ?: "") }
    var phoneNumber by remember { mutableStateOf(contactToEdit?.phoneNumber ?: "") }
    var website by remember { mutableStateOf(contactToEdit?.website ?: "") }
    var speciality by remember { mutableStateOf(contactToEdit?.speciality ?: "") }
    var street by remember { mutableStateOf(contactToEdit?.address ?: "") }
    var postcode by remember { mutableStateOf(contactToEdit?.postcode ?: "") }
    var city by remember { mutableStateOf(contactToEdit?.city ?: "") }

    val isFormValid = name.isNotBlank()
    val title = if (contactToEdit != null) "Edit healthcare professional" else "Add healthcare professional"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF7F5F0) // Similar to the light cream color in the screenshot
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Avatar Placeholder
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color(0xFFE0E0E0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👨‍⚕️", fontSize = 48.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    CustomTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Name",
                        showClearIcon = name.isNotEmpty()
                    )
                    CustomTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        keyboardType = KeyboardType.Email
                    )
                    CustomTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = "Phone number",
                        keyboardType = KeyboardType.Phone
                    )
                    CustomTextField(
                        value = website,
                        onValueChange = { website = it },
                        label = "Website",
                        keyboardType = KeyboardType.Uri
                    )
                    CustomTextField(
                        value = speciality,
                        onValueChange = { speciality = it },
                        label = "Medical speciality"
                    )
                    CustomTextField(
                        value = street,
                        onValueChange = { street = it },
                        label = "Street"
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomTextField(
                            value = postcode,
                            onValueChange = { postcode = it },
                            label = "Postcode",
                            modifier = Modifier.weight(1f)
                        )
                        CustomTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = "City",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                Button(
                    onClick = {
                        if (isFormValid) {
                            onSave(
                                contactToEdit?.copy(
                                    name = name,
                                    email = email,
                                    phoneNumber = phoneNumber,
                                    website = website,
                                    speciality = speciality,
                                    address = street,
                                    postcode = postcode,
                                    city = city
                                ) ?: Contact(
                                    name = name,
                                    type = "DOCTOR",
                                    email = email,
                                    phoneNumber = phoneNumber,
                                    website = website,
                                    speciality = speciality,
                                    address = street,
                                    postcode = postcode,
                                    city = city
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D1E16),
                        contentColor = Color.White
                    ),
                    enabled = isFormValid
                ) {
                    Text(
                        text = if (contactToEdit != null) "Update profile" else "Create profile",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    showClearIcon: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label, color = Color.Gray) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        trailingIcon = if (showClearIcon) {
            {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Clear",
                    tint = Color.Gray,
                    modifier = Modifier.clickable { onValueChange("") }
                )
            }
        } else null
    )
}
