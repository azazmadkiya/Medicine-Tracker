/        \/\/ Adherence Summary Card/i\
\        if (isPastDay) {\
\            item {\
\                androidx.compose.material3.TextButton(\
\                    onClick = { viewModel.setSelectedDate(todayStr) },\
\                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)\
\                ) {\
\                    Text(\
\                        text = "Go to Today",\
\                        color = Color(0xFF8A3A19),\
\                        fontWeight = FontWeight.Bold,\
\                        fontSize = 16.sp\
\                    )\
\                }\
\            }\
\            if (doses.isNotEmpty()) {\
\                item {\
\                    PastDayTasksCard(doses)\
\                }\
\            } else {\
\                item {\
\                    Card(\
\                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),\
\                        shape = RoundedCornerShape(16.dp),\
\                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)\
\                    ) {\
\                        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {\
\                            Text("No medications scheduled for this date", style = MaterialTheme.typography.titleMedium)\
\                        }\
\                    }\
\                }\
\            }\
\        } else {
/        item {/,$ {
  /            Spacer(modifier = Modifier.height(72.dp))/a\
\        }\
\        }
}
