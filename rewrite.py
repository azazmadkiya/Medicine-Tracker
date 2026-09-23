import sys

with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "r") as f:
    content = f.read()

marker = "        // Adherence Summary Card"

if marker in content:
    parts = content.split(marker)
    
    # We want to wrap from marker down to the end of the LazyColumn with `if (isPastDay) { ... } else { ... }`
    # Let's find the end of LazyColumn by looking for `item {\n            Spacer(modifier = Modifier.height(72.dp))\n        }`
    
    end_marker = "        item {\n            Spacer(modifier = Modifier.height(72.dp))\n        }\n    }"
    
    if end_marker in parts[1]:
        inner_parts = parts[1].split(end_marker)
        
        replacement = """
        if (isPastDay) {
            item {
                androidx.compose.material3.TextButton(
                    onClick = { viewModel.setSelectedDate(todayStr) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Go to Today",
                        color = Color(0xFF8A3A19),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
            if (doses.isNotEmpty()) {
                item {
                    PastDayTasksCard(doses)
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No medications scheduled for this date", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        } else {
            // Adherence Summary Card
""" + inner_parts[0] + """
        }
""" + end_marker + inner_parts[1]
        
        new_content = parts[0] + replacement
        with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "w") as f:
            f.write(new_content)
        print("Success")
    else:
        print("End marker not found")
else:
    print("Marker not found")
