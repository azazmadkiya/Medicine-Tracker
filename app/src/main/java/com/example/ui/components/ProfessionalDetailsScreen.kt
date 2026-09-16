package com.example.ui.components

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.Appointment
import com.example.data.local.entity.Contact
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalDetailsScreen(
    contact: Contact,
    appointments: List<Appointment> = emptyList(),
    onDismiss: () -> Unit,
    onAddAppointment: () -> Unit
) {
    val upcomingAppts = appointments.filter { it.isUpcoming }.sortedBy { it.dateTimeMillis }
    val pastAppts = appointments.filter { it.isPast }.sortedByDescending { it.dateTimeMillis }
    val nextAppt = upcomingAppts.firstOrNull()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF7F5F0) // Similar to light cream background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Close")
                    }
                    Text(
                        text = "Contact details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    IconButton(onClick = { /* Menu action */ }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
                    }
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Profile Section
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp, bottom = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(Color(0xFFE0E0E0), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👨‍⚕️", fontSize = 40.sp)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = contact.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            val subtitle = listOf(contact.address, contact.speciality).filter { it.isNotBlank() }.joinToString(" ")
                            if (subtitle.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = subtitle,
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Details List
                            val fullAddress = listOf(contact.address, contact.city, contact.postcode).filter { it.isNotBlank() }.joinToString(" ")
                            if (fullAddress.isNotBlank()) {
                                ContactInfoRow(icon = Icons.Default.LocationOn, text = fullAddress)
                            }
                            if (contact.phoneNumber.isNotBlank()) {
                                ContactInfoRow(icon = Icons.Default.Call, text = contact.phoneNumber)
                            }
                            if (contact.email.isNotBlank()) {
                                ContactInfoRow(icon = Icons.Default.Email, text = contact.email)
                            }
                            if (contact.website.isNotBlank()) {
                                ContactInfoRow(icon = Icons.Default.NorthEast, text = contact.website, showDivider = false)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Appointments Section
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = "Appointments",
                                tint = Color(0xFF4CAF50), // Greenish icon
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Upcoming appointments",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            if (nextAppt != null) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = SimpleDateFormat("EEE d MMM", Locale.getDefault()).format(Date(nextAppt.dateTimeMillis)),
                                        fontSize = 16.sp,
                                        color = Color.DarkGray
                                    )
                                    Text(
                                        text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(nextAppt.dateTimeMillis)),
                                        fontSize = 16.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "There are currently no upcoming appointments.",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = onAddAppointment,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFE0D2), // Peach color
                                    contentColor = Color(0xFF5A311F)
                                ),
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Text(
                                    text = "Add an appointment",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                    
                    if (pastAppts.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Past appointments",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        pastAppts.forEach { appt ->
                            PastAppointmentItem(appointment = appt)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun ContactInfoRow(icon: ImageVector, text: String, showDivider: Boolean = true) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF5F5F5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, fontSize = 16.sp, color = Color.DarkGray)
        }
        if (showDivider) {
            HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(horizontal = 24.dp))
        }
    }
}

@Composable
fun PastAppointmentItem(appointment: Appointment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            val relativeTime = DateUtils.getRelativeTimeSpanString(
                appointment.dateTimeMillis,
                System.currentTimeMillis(),
                DateUtils.DAY_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
            ).toString()
            Text(
                text = relativeTime,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            val format = SimpleDateFormat("d MMM yyyy 'at' h:mm a", Locale.getDefault())
            Text(
                text = format.format(Date(appointment.dateTimeMillis)),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Completed",
            tint = Color(0xFF00695C),
            modifier = Modifier.size(28.dp)
        )
    }
}
