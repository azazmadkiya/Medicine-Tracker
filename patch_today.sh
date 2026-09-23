#!/bin/bash
cat app/src/main/java/com/example/ui/screens/TodayScreen.kt > temp.kt

# Inject the PastDayTasksCard at the end
cat << 'END_APPEND' >> temp.kt

@Composable
fun PastDayTasksCard(doses: List<DoseWithMedication>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Text(
                text = "Resolved tasks",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )

            val groupedByTime = doses.groupBy { it.scheduledTime }.toSortedMap()
            groupedByTime.forEach { (time, timeDoses) ->
                val displayTime = try {
                    val parsed = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).parse(time)
                    java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(parsed!!).lowercase(java.util.Locale.getDefault())
                } catch (e: Exception) { time }

                Text(
                    text = displayTime,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                timeDoses.forEach { dose ->
                    val isTaken = dose.status == "TAKEN"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFF5F5F5), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💊", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = dose.medication.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isTaken) Color.Gray else MaterialTheme.colorScheme.onSurface,
                            textDecoration = if (isTaken) androidx.compose.ui.text.style.TextDecoration.LineThrough else androidx.compose.ui.text.style.TextDecoration.None,
                            modifier = Modifier.weight(1f)
                        )
                        if (isTaken) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = Color(0xFF00C853),
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Box(modifier = Modifier.size(24.dp).background(Color.Transparent, CircleShape).border(2.dp, Color.LightGray, CircleShape))
                        }
                    }
                }
            }
        }
    }
}
END_APPEND

# Now we need to modify the TodayScreen component to use it.
# We will use sed to inject the 'Go to Today' button and replace the grouping logic if it's a past day.

