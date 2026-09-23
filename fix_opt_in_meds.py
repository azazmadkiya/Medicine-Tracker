import sys

with open("app/src/main/java/com/example/ui/screens/MedicationsScreen.kt", "r") as f:
    content = f.read()

# Add OptIn
if "@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)" not in content:
    content = content.replace("@Composable\nfun MedicationsScreen", "@androidx.compose.material3.ExperimentalMaterial3Api\n@Composable\nfun MedicationsScreen")
    # also add @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class) just in case
    # Actually, using @androidx.compose.material3.ExperimentalMaterial3Api propagates it. 
    # Let's just use @androidx.compose.material3.ExperimentalMaterial3Api. Wait, if I propagate it, it will cause errors in MainActivity where MedicationsScreen is called!
    # Instead, we should use @OptIn.
    
    content = content.replace("@androidx.compose.material3.ExperimentalMaterial3Api\n@Composable\nfun MedicationsScreen", "@androidx.annotation.OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)\n@Composable\nfun MedicationsScreen")
    
    # Wait, the correct OptIn annotation in kotlin is `@kotlin.OptIn` or just `@OptIn` if imported. Let's use `@androidx.compose.material3.ExperimentalMaterial3Api`?
    # Wait, in Kotlin, it's @OptIn(ExperimentalMaterial3Api::class).
    
    content = content.replace("@Composable\nfun MedicationsScreen", "@kotlin.OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)\n@Composable\nfun MedicationsScreen")

with open("app/src/main/java/com/example/ui/screens/MedicationsScreen.kt", "w") as f:
    f.write(content)

print("Success")
