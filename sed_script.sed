/val adherence = by viewModel.adherence.collectAsState()/a\
\    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }\
\    val isPastDay = selectedDate < todayStr\

s/val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())/val todayStrInner = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())/g
s/dStr == todayStr/dStr == todayStrInner/g
