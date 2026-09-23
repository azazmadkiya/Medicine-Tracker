#!/bin/bash
sed -i '/val adherence by viewModel.adherence.collectAsState()/a \    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }\n    val isPastDay = selectedDate < todayStr' app/src/main/java/com/example/ui/screens/TodayScreen.kt
