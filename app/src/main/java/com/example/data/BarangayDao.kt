package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BarangayDao {

    // --- User Queries ---
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    // --- Resident Queries ---
    @Query("SELECT * FROM residents ORDER BY fullName ASC")
    fun getAllResidentsFlow(): Flow<List<Resident>>

    @Query("SELECT * FROM residents WHERE id = :id")
    suspend fun getResidentById(id: Int): Resident?

    @Query("SELECT * FROM residents WHERE householdId = :householdId")
    fun getResidentsByHouseholdFlow(householdId: Int): Flow<List<Resident>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResident(resident: Resident): Long

    @Update
    suspend fun updateResident(resident: Resident)

    @Delete
    suspend fun deleteResident(resident: Resident)

    // --- Household Queries ---
    @Query("SELECT * FROM households ORDER BY householdNumber DESC")
    fun getAllHouseholdsFlow(): Flow<List<Household>>

    @Query("SELECT * FROM households WHERE id = :id")
    suspend fun getHouseholdById(id: Int): Household?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHousehold(household: Household): Long

    @Delete
    suspend fun deleteHousehold(household: Household)

    // --- Document Requests Queries ---
    @Query("SELECT * FROM document_requests ORDER BY id DESC")
    fun getAllDocumentRequestsFlow(): Flow<List<DocumentRequest>>

    @Query("SELECT * FROM document_requests WHERE residentId = :residentId ORDER BY id DESC")
    fun getDocumentRequestsByResidentFlow(residentId: Int): Flow<List<DocumentRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocumentRequest(request: DocumentRequest): Long

    @Update
    suspend fun updateDocumentRequest(request: DocumentRequest)

    @Delete
    suspend fun deleteDocumentRequest(request: DocumentRequest)

    // --- Blotter Queries ---
    @Query("SELECT * FROM blotters ORDER BY dateFiled DESC")
    fun getAllBlottersFlow(): Flow<List<Blotter>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlotter(blotter: Blotter): Long

    @Update
    suspend fun updateBlotter(blotter: Blotter)

    @Query("SELECT COUNT(*) FROM blotters WHERE status = 'Active (Ongoing)'")
    fun getActiveBlottersCountFlow(): Flow<Int>

    // --- Complaint Queries ---
    @Query("SELECT * FROM complaints ORDER BY dateSubmitted DESC")
    fun getAllComplaintsFlow(): Flow<List<Complaint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaint(complaint: Complaint): Long

    @Update
    suspend fun updateComplaint(complaint: Complaint)

    // --- Announcement Queries ---
    @Query("SELECT * FROM announcements ORDER BY datePosted DESC")
    fun getAllAnnouncementsFlow(): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement): Long

    @Delete
    suspend fun deleteAnnouncement(announcement: Announcement)
}
