with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "r") as f:
    content = f.read()

content = content.replace("val adherence by viewModel.adherence.collectAsState()\\n    val daysList = remember {", 
                          "val adherence by viewModel.adherence.collectAsState()\\n    val todayStr = remember { SimpleDateFormat(\\"yyyy-MM-dd\\", Locale.getDefault()).format(Date()) }\\n    val isPastDay = selectedDate < todayStr\\n    val daysList = remember {")

content = content.replace("val todayStr = SimpleDateFormat(\\"yyyy-MM-dd\\", Locale.getDefault()).format(Date())", 
                          "val todayStrInner = SimpleDateFormat(\\"yyyy-MM-dd\\", Locale.getDefault()).format(Date())", 1) # Only first occurrence inside daysList

# But wait, there are two occurrences now. The one I just injected and the one inside daysList.
