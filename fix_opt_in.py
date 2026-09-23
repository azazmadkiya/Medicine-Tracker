import sys

with open("app/src/main/java/com/example/ui/components/AddMedicationDialog.kt", "r") as f:
    content = f.read()

# Add OptIn
if "@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)" not in content:
    content = content.replace("fun AddMedicationDialog(", "@androidx.compose.material3.ExperimentalMaterial3Api\nfun AddMedicationDialog(")

with open("app/src/main/java/com/example/ui/components/AddMedicationDialog.kt", "w") as f:
    f.write(content)

print("Success")
