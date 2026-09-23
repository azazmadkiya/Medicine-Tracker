
@Composable
fun TodayScreen(
    viewModel: MedicineViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val doses by viewModel.currentDoses.collectAsState()
    val adherence by viewModel.adherence.collectAsState()

    val daysList = remember {
