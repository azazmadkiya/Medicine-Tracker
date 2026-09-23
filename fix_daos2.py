import sys

def modify_file(filepath, replace_dict):
    with open(filepath, "r") as f:
        content = f.read()
    
    for old, new in replace_dict.items():
        content = content.replace(old, new)
        
    with open(filepath, "w") as f:
        f.write(content)

import re

# MedicationDao:
with open("app/src/main/java/com/example/data/local/dao/MedicationDao.kt", "r") as f:
    content = f.read()
# Clean up MedicationDao - remove all inserted "getAllMedicationsSync" and then add it cleanly
content = re.sub(r'@Query\("SELECT \* FROM medications"\)\s*suspend fun getAllMedicationsSync\(\): List<Medication>\s*', '', content)
content = content.replace("fun getAllMedications(): Flow<List<Medication>>", "fun getAllMedications(): Flow<List<Medication>>\n\n    @Query(\"SELECT * FROM medications\")\n    suspend fun getAllMedicationsSync(): List<Medication>")
with open("app/src/main/java/com/example/data/local/dao/MedicationDao.kt", "w") as f:
    f.write(content)

# DoseLogDao
with open("app/src/main/java/com/example/data/local/dao/DoseLogDao.kt", "r") as f:
    content = f.read()
content = re.sub(r'@Query\("SELECT \* FROM dose_logs"\)\s*suspend fun getAllLogsSync\(\): List<DoseLog>\s*', '', content)
content = content.replace("interface DoseLogDao {", "interface DoseLogDao {\n    @Query(\"SELECT * FROM dose_logs\")\n    suspend fun getAllLogsSync(): List<DoseLog>\n")
with open("app/src/main/java/com/example/data/local/dao/DoseLogDao.kt", "w") as f:
    f.write(content)

# AppointmentDao
with open("app/src/main/java/com/example/data/local/dao/AppointmentDao.kt", "r") as f:
    content = f.read()
content = re.sub(r'@Query\("SELECT \* FROM appointments"\)\s*suspend fun getAllAppointmentsSync\(\): List<Appointment>\s*', '', content)
content = content.replace("interface AppointmentDao {", "interface AppointmentDao {\n    @Query(\"SELECT * FROM appointments\")\n    suspend fun getAllAppointmentsSync(): List<Appointment>\n")
with open("app/src/main/java/com/example/data/local/dao/AppointmentDao.kt", "w") as f:
    f.write(content)

# ContactDao
with open("app/src/main/java/com/example/data/local/dao/ContactDao.kt", "r") as f:
    content = f.read()
content = re.sub(r'@Query\("SELECT \* FROM contacts"\)\s*suspend fun getAllContactsSync\(\): List<Contact>\s*', '', content)
content = content.replace("interface ContactDao {", "interface ContactDao {\n    @Query(\"SELECT * FROM contacts\")\n    suspend fun getAllContactsSync(): List<Contact>\n")
with open("app/src/main/java/com/example/data/local/dao/ContactDao.kt", "w") as f:
    f.write(content)

