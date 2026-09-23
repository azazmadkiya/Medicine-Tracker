import sys

with open("app/src/main/java/com/example/ui/screens/MedicationsScreen.kt", "r") as f:
    content = f.read()

target = """                Text(
                    text = "Times: ${
                        medication.doseTimes.split(",").map { time ->
                            try {
                                val parsed = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).parse(time)
                                java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(parsed!!).uppercase(java.util.Locale.getDefault())
                            } catch (e: Exception) { time }
                        }.joinToString(", ")
                    }","""

replacement = """                val formattedTimes = medication.doseTimes.split(",").map { time ->
                    try {
                        val parsed = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).parse(time)
                        java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(parsed!!).uppercase(java.util.Locale.getDefault())
                    } catch (e: Exception) { time }
                }.joinToString(", ")
                Text(
                    text = "Times: $formattedTimes","""

if target in content:
    new_content = content.replace(target, replacement)
    with open("app/src/main/java/com/example/ui/screens/MedicationsScreen.kt", "w") as f:
        f.write(new_content)
    print("Success")
else:
    print("Target not found")
