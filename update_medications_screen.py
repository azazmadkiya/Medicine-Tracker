import sys

with open("app/src/main/java/com/example/ui/screens/MedicationsScreen.kt", "r") as f:
    content = f.read()

# Add imports
imports = """import androidx.compose.material.icons.filled.PictureAsPdf
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.utils.PdfGenerator
"""

if "import androidx.compose.material.icons.filled.PictureAsPdf" not in content:
    content = content.replace("import androidx.compose.material.icons.filled.SettingsBackupRestore", "import androidx.compose.material.icons.filled.SettingsBackupRestore\n" + imports)

# Add state and launcher
target_state = "    var showBackupDialog by remember { mutableStateOf(false) }"
new_state = target_state + """
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isGeneratingPdf by remember { mutableStateOf(false) }

    val pdfExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        uri?.let {
            isGeneratingPdf = true
            scope.launch {
                val result = PdfGenerator.generateReport(context, it)
                isGeneratingPdf = false
                if (result.isSuccess) {
                    Toast.makeText(context, "PDF Report generated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to generate PDF.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }"""

if "val pdfExportLauncher" not in content:
    content = content.replace(target_state, new_state)

# Add the button
target_buttons = """                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    IconButton(
                        onClick = { showBackupDialog = true },"""

new_buttons = """                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    IconButton(
                        onClick = { 
                            val dateStr = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
                            pdfExportLauncher.launch("Medical_Report_$dateStr.pdf") 
                        },
                        enabled = !isGeneratingPdf,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Export PDF Report",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { showBackupDialog = true },"""

if "Icons.Default.PictureAsPdf" not in content and target_buttons in content:
    content = content.replace(target_buttons, new_buttons)

with open("app/src/main/java/com/example/ui/screens/MedicationsScreen.kt", "w") as f:
    f.write(content)

print("Success")
