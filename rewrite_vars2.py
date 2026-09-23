with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "r") as f:
    content = f.read()

# Replace inner todayStr and dStr check
content = content.replace("val todayStr = SimpleDateFormat(\"yyyy-MM-dd\", Locale.getDefault()).format(Date())", "val todayStrInner = SimpleDateFormat(\"yyyy-MM-dd\", Locale.getDefault()).format(Date())")
content = content.replace("dStr == todayStr", "dStr == todayStrInner")

# Inject top level todayStr
content = content.replace("val adherence by viewModel.adherence.collectAsState()\n    val daysList = remember {", "val adherence by viewModel.adherence.collectAsState()\n    val todayStr = remember { SimpleDateFormat(\"yyyy-MM-dd\", Locale.getDefault()).format(Date()) }\n    val isPastDay = selectedDate < todayStr\n    val daysList = remember {")

# Find // Doses Grouped by Period
target = "        // Doses Grouped by Period"
if target in content:
    parts = content.split(target)
    
    end_target = "        item {\n            Spacer(modifier = Modifier.height(72.dp))\n        }\n    }"
    if end_target in parts[1]:
        inner_parts = parts[1].split(end_target)
        
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
        // Doses Grouped by Period
""" + inner_parts[0] + """
        }
""" + end_target + inner_parts[1]
        
        new_content = parts[0] + replacement
        with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "w") as f:
            f.write(new_content)
        print("Success")
    else:
        print("End target not found")
else:
    print("Target not found")
