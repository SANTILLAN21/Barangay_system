package com.example.data

import kotlinx.coroutines.flow.Flow

class BarangayRepository(private val dao: BarangayDao) {

    // --- Users ---
    suspend fun getUserByUsername(username: String): User? = dao.getUserByUsername(username)
    suspend fun getUserById(id: Int): User? = dao.getUserById(id)
    suspend fun insertUser(user: User): Long = dao.insertUser(user)

    // --- Residents ---
    val allResidents: Flow<List<Resident>> = dao.getAllResidentsFlow()
    suspend fun getResidentById(id: Int): Resident? = dao.getResidentById(id)
    fun getResidentsByHousehold(householdId: Int): Flow<List<Resident>> = dao.getResidentsByHouseholdFlow(householdId)
    suspend fun insertResident(resident: Resident): Long = dao.insertResident(resident)
    suspend fun updateResident(resident: Resident) = dao.updateResident(resident)
    suspend fun deleteResident(resident: Resident) = dao.deleteResident(resident)

    // --- Households ---
    val allHouseholds: Flow<List<Household>> = dao.getAllHouseholdsFlow()
    suspend fun getHouseholdById(id: Int): Household? = dao.getHouseholdById(id)
    suspend fun insertHousehold(household: Household): Long = dao.insertHousehold(household)
    suspend fun deleteHousehold(household: Household) = dao.deleteHousehold(household)

    // --- Document Requests ---
    val allDocumentRequests: Flow<List<DocumentRequest>> = dao.getAllDocumentRequestsFlow()
    fun getDocumentRequestsByResident(residentId: Int): Flow<List<DocumentRequest>> = dao.getDocumentRequestsByResidentFlow(residentId)
    suspend fun insertDocumentRequest(request: DocumentRequest): Long = dao.insertDocumentRequest(request)
    suspend fun updateDocumentRequest(request: DocumentRequest) = dao.updateDocumentRequest(request)
    suspend fun deleteDocumentRequest(request: DocumentRequest) = dao.deleteDocumentRequest(request)

    // --- Blotters ---
    val allBlotters: Flow<List<Blotter>> = dao.getAllBlottersFlow()
    val activeBlottersCount: Flow<Int> = dao.getActiveBlottersCountFlow()
    suspend fun insertBlotter(blotter: Blotter): Long = dao.insertBlotter(blotter)
    suspend fun updateBlotter(blotter: Blotter) = dao.updateBlotter(blotter)

    // --- Complaints ---
    val allComplaints: Flow<List<Complaint>> = dao.getAllComplaintsFlow()
    suspend fun insertComplaint(complaint: Complaint): Long = dao.insertComplaint(complaint)
    suspend fun updateComplaint(complaint: Complaint) = dao.updateComplaint(complaint)

    // --- Announcements ---
    val allAnnouncements: Flow<List<Announcement>> = dao.getAllAnnouncementsFlow()
    suspend fun insertAnnouncement(announcement: Announcement): Long = dao.insertAnnouncement(announcement)
    suspend fun deleteAnnouncement(announcement: Announcement) = dao.deleteAnnouncement(announcement)
}
