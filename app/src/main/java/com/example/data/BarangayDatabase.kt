package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Resident::class,
        Household::class,
        DocumentRequest::class,
        Blotter::class,
        Complaint::class,
        Announcement::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BarangayDatabase : RoomDatabase() {
    abstract fun barangayDao(): BarangayDao

    companion object {
        @Volatile
        private var INSTANCE: BarangayDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): BarangayDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BarangayDatabase::class.java,
                    "barangay_database"
                )
                .addCallback(BarangayDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class BarangayDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.barangayDao())
                }
            }
        }

        suspend fun populateDatabase(dao: BarangayDao) {
            // 1. Seed initial Residents
            val resId1 = dao.insertResident(
                Resident(
                    fullName = "Juan dela Cruz",
                    birthDate = "1988-05-12",
                    gender = "Male",
                    civilStatus = "Married",
                    phone = "09171234567",
                    email = "juan.delacruz@mail.com",
                    purok = "Purok 1",
                    isHouseholdHead = true,
                    isVerified = true
                )
            ).toInt()

            val resId2 = dao.insertResident(
                Resident(
                    fullName = "Maria Clara dela Cruz",
                    birthDate = "1990-10-24",
                    gender = "Female",
                    civilStatus = "Married",
                    phone = "09187654321",
                    email = "maria.clara@mail.com",
                    purok = "Purok 1",
                    householdId = 1,
                    isHouseholdHead = false,
                    isVerified = true
                )
            ).toInt()

            val resId3 = dao.insertResident(
                Resident(
                    fullName = "Jose Rizal",
                    birthDate = "1961-06-19",
                    gender = "Male",
                    civilStatus = "Single",
                    phone = "09202221133",
                    email = "pepe@bagumbayan.ph",
                    purok = "Purok 3",
                    isHouseholdHead = true,
                    isVerified = true
                )
            ).toInt()

            val resId4 = dao.insertResident(
                Resident(
                    fullName = "Andres Bonifacio",
                    birthDate = "1995-11-30",
                    gender = "Male",
                    civilStatus = "Single",
                    phone = "09995554422",
                    email = "supremo@katipunan.org",
                    purok = "Purok 4",
                    isHouseholdHead = true,
                    isVerified = true
                )
            ).toInt()

            val resId5 = dao.insertResident(
                Resident(
                    fullName = "Emilio Aguinaldo",
                    birthDate = "2002-03-22",
                    gender = "Male",
                    civilStatus = "Single",
                    phone = "09443332211",
                    email = "presidente@cavite.ph",
                    purok = "Purok 2",
                    isHouseholdHead = true,
                    isVerified = false // Needs verification
                )
            ).toInt()

            // 2. Seed initial Households
            val hh1 = dao.insertHousehold(
                Household(
                    householdNumber = "HH-2026-0001",
                    purok = "Purok 1",
                    headId = resId1,
                    headName = "Juan dela Cruz"
                )
            ).toInt()

            // Link resident 2 to household 1
            dao.updateResident(
                dao.getResidentById(resId2)!!.copy(householdId = hh1)
            )

            dao.insertHousehold(
                Household(
                    householdNumber = "HH-2026-0002",
                    purok = "Purok 3",
                    headId = resId3,
                    headName = "Jose Rizal"
                )
            )

            // 3. Seed initial Users
            dao.insertUser(
                User(
                    username = "admin",
                    passwordField = "admin123",
                    fullName = "Hon. Barangay Chairman",
                    role = "Admin",
                    residentId = null
                )
            )

            dao.insertUser(
                User(
                    username = "staff",
                    passwordField = "staff123",
                    fullName = "Hon. Barangay Secretary",
                    role = "Staff",
                    residentId = null
                )
            )

            dao.insertUser(
                User(
                    username = "resident",
                    passwordField = "resident123",
                    fullName = "Juan dela Cruz",
                    role = "Resident",
                    residentId = resId1
                )
            )

            // 4. Seed initial Document Requests
            dao.insertDocumentRequest(
                DocumentRequest(
                    residentId = resId1,
                    residentName = "Juan dela Cruz",
                    documentType = "Barangay Clearance",
                    purpose = "Employment Requirements",
                    status = "Ready for Pickup",
                    dateRequested = "2026-06-10",
                    adminMemo = "Please prepare 2 valid IDs upon collection.",
                    fee = 50.0
                )
            )

            dao.insertDocumentRequest(
                DocumentRequest(
                    residentId = resId3,
                    residentName = "Jose Rizal",
                    documentType = "Certificate of Residency",
                    purpose = "Bank Account Opening",
                    status = "Pending",
                    dateRequested = "2026-06-13",
                    fee = 75.0
                )
            )

            dao.insertDocumentRequest(
                DocumentRequest(
                    residentId = resId4,
                    residentName = "Andres Bonifacio",
                    documentType = "Certificate of Indigency",
                    purpose = "Medical Assistance Financial Aid",
                    status = "Completed",
                    dateRequested = "2026-06-08",
                    adminMemo = "Issued for medical assistance.",
                    fee = 0.0 // Free for Indigent
                )
            )

            // 5. Seed initial Blotters
            dao.insertBlotter(
                Blotter(
                    caseNumber = "BLT-2026-001",
                    complainantName = "Jose Rizal",
                    respondentName = "Andres Bonifacio",
                    incidentType = "Boundary Dispute",
                    details = "Disagreement over garden fence encroaching 1.5 meters into the complainant's property line area.",
                    status = "Resolved",
                    dateFiled = "2026-05-20",
                    purok = "Purok 3"
                )
            )

            dao.insertBlotter(
                Blotter(
                    caseNumber = "BLT-2026-002",
                    complainantName = "Maria Clara dela Cruz",
                    respondentName = "Local KTV Venue",
                    incidentType = "Noise Complaint",
                    details = "Loud music playing beyond 11:00 PM affecting residents' rest on weekdays.",
                    status = "Active (Ongoing)",
                    dateFiled = "2026-06-12",
                    purok = "Purok 1"
                )
            )

            // 6. Seed initial Complaints
            dao.insertComplaint(
                Complaint(
                    complainantName = "Juan dela Cruz",
                    complainantContact = "09171234567",
                    title = "Unclogging of Purok 1 Drainage",
                    description = "Heavy rain caused immediate flooding due to plastic garbage clogging the drainage canals near the main road intersection.",
                    category = "Roads & Infrastructure",
                    status = "Action Taken",
                    dateSubmitted = "2026-06-05",
                    purok = "Purok 1"
                )
            )

            dao.insertComplaint(
                Complaint(
                    complainantName = "Jose Rizal",
                    complainantContact = "09202221133",
                    title = "Busted Streetlight near Elementary School",
                    description = "The vital streetlight path going to the school has been dark for three nights, creating a risk for students going home late.",
                    category = "Public Safety",
                    status = "Submitted",
                    dateSubmitted = "2026-06-14",
                    purok = "Purok 3"
                )
            )

            // 7. Seed initial Announcements
            dao.insertAnnouncement(
                Announcement(
                    title = "Free Polio and Pneumonia Vaccination Drive",
                    content = "The Barangay Health Center will conduct a free routine vaccination drive for toddlers (aged 0-5) and senior citizens. Please bring your health cards.",
                    category = "Vaccination Drive",
                    datePosted = "2026-06-14",
                    postedBy = "Hon. Barangay Chairman"
                )
            )

            dao.insertAnnouncement(
                Announcement(
                    title = "Purok Clean-up Drive this Saturday",
                    content = "Let's work together to prevent dengue! General clean-up drive starts at 6:00 AM this Saturday. Residents are requested to clean their own backyards and participate in clearing public drainage waterways.",
                    category = "Community Event",
                    datePosted = "2026-06-12",
                    postedBy = "Hon. Barangay Secretary"
                )
            )

            dao.insertAnnouncement(
                Announcement(
                    title = "Distribution of Organic Vegetable Seeds",
                    content = "In support of the 'Urban Gardening Initiative', our agricultural unit will distribute seeds of tomato, eggplant, and chili along with starter pots. Distribution starts Monday onwards at the Barangay Hall.",
                    category = "General Info",
                    datePosted = "2026-06-08",
                    postedBy = "Hon. Barangay Chairman"
                )
            )
        }
    }
}
