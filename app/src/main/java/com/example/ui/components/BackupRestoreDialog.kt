package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.utils.BackupManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BackupRestoreDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            isLoading = true
            scope.launch {
                val result = BackupManager.exportData(context, it)
                isLoading = false
                if (result.isSuccess) {
                    Toast.makeText(context, "Backup successful!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Backup failed.", Toast.LENGTH_SHORT).show()
                }
                onDismiss()
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            isLoading = true
            scope.launch {
                val result = BackupManager.importData(context, it)
                isLoading = false
                if (result.isSuccess) {
                    Toast.makeText(context, "Restore successful!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Restore failed.", Toast.LENGTH_SHORT).show()
                }
                onDismiss()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Backup & Restore") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("You can export all your data safely to a file on your device, or import a previous backup file.")
                Spacer(modifier = Modifier.height(16.dp))
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val dateStr = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
                    exportLauncher.launch("MedicineTracker_Backup_$dateStr.json") 
                },
                enabled = !isLoading
            ) {
                Text("Backup")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = { importLauncher.launch(arrayOf("application/json")) },
                enabled = !isLoading
            ) {
                Text("Restore")
            }
        }
    )
}
