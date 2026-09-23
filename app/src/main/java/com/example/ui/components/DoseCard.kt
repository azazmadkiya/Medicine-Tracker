package com.example.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.StatusGreenContainer
import com.example.ui.theme.StatusRed
import androidx.activity.result.PickVisualMediaRequest

import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.fillMaxSize
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.basicMarquee
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.DoseWithMedication
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed


@Composable
fun DoseCard(
    dose: DoseWithMedication,
    onTake: () -> Unit,
    onSkip: () -> Unit,
    onReset: () -> Unit,
    onTestNotification: () -> Unit,
    onPhotoSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isTaken = dose.status == "TAKEN"
    val isSkipped = dose.status == "SKIPPED"
    val isPending = dose.status == "PENDING"

    val medColor = try {
        Color(android.graphics.Color.parseColor(dose.medication.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    val cardBgColor by animateColorAsState(
        targetValue = when {
            isTaken -> StatusGreenContainer.copy(alpha = 0.45f)
            isSkipped -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            else -> MaterialTheme.colorScheme.surface
        },
        label = "cardBgColor"
    )
    
    val cardBorderColor by animateColorAsState(
        targetValue = when {
            isTaken -> StatusGreen.copy(alpha = 0.3f)
            isSkipped -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        },
        label = "cardBorderColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dose_card_${dose.medication.id}_${dose.scheduledTime}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left color pill indicator & time
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(60.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isTaken) StatusGreen else medColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isTaken) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Taken",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else if (isSkipped) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Skipped",
                            tint = StatusRed,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text(
                            text = dose.medication.form.take(2).uppercase(),
                            color = medColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = try {
                        val parsed = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).parse(dose.scheduledTime)
                        java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(parsed!!).uppercase(java.util.Locale.getDefault())
                    } catch (e: Exception) { dose.scheduledTime },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Middle: Name, dosage, instruction & stock warning
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dose.medication.name,
                    modifier = Modifier.basicMarquee(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (isSkipped) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (isSkipped) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = medColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = dose.medication.dosage,
                            color = medColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${dose.medication.instruction} • ${dose.medication.form}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                if (dose.medication.isLowStock) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Low stock",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Low: ${dose.medication.currentStock} left",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (isTaken) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Completed",
                        color = StatusGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                val photoLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia()
                ) { uri ->
                    uri?.let { onPhotoSelected(it.toString()) }
                }

                if (!dose.doseLog?.photoUri.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                    ) {
                        AsyncImage(
                            model = dose.doseLog?.photoUri ?: "",
                            contentDescription = "Attached photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { photoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Add Photo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    if (isPending) {
                        // Skip button
                        OutlinedButton(
                            onClick = onSkip,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("skip_dose_btn_${dose.medication.id}")
                        ) {
                            Text(
                                text = "Skip",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Take button
                        Button(
                            onClick = onTake,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("take_dose_btn_${dose.medication.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Take",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        // Undo / Reset action
                        OutlinedButton(
                            onClick = onReset,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("reset_dose_btn_${dose.medication.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Undo",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Undo", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
}
