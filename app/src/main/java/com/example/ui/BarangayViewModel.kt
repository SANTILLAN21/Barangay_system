package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BarangayViewModel(private val repository: BarangayRepository) : ViewModel() {

    // --- Authentication States ---
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess.asStateFlow()

    // --- Search & Filter Filters ---
    val residentSearchQuery = MutableStateFlow("")
    val residentPurokFilter = MutableStateFlow("All")

    val householdSearchQuery = MutableStateFlow("")
    val householdPurokFilter = MutableStateFlow("All")

    val blotterStatusFilter = MutableStateFlow("All")
    val complaintStatusFilter = MutableStateFlow("All")

    // --- Reactive Database Flows ---
    val allResidents: StateFlow<List<Resident>> = repository.allResidents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHouseholds: StateFlow<List<Household>> = repository.allHouseholds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDocumentRequests: StateFlow<List<DocumentRequest>> = repository.allDocumentRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBlotters: StateFlow<List<Blotter>> = repository.allBlotters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allComplaints: StateFlow<List<Complaint>> = repository.allComplaints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAnnouncements: StateFlow<List<Announcement>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Secondary State Holders (e.g. Selected item for details) ---
    private val _selectedResident = MutableStateFlow<Resident?>(null)
    val selectedResident: StateFlow<Resident?> = _selectedResident.asStateFlow()

    // --- Screen State: Filtered Outputs ---
    val filteredResidents: StateFlow<List<Resident>> = combine(
        allResidents, residentSearchQuery, residentPurokFilter
    ) { residents, query, purok ->
        residents.filter {
            val matchesQuery = it.fullName.contains(query, ignoreCase = true) ||
                    it.phone.contains(query) ||
                    it.email.contains(query, ignoreCase = true)
            val matchesPurok = purok == "All" || it.purok == purok
            matchesQuery && matchesPurok
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredHouseholds: StateFlow<List<Household>> = combine(
        allHouseholds, householdSearchQuery, householdPurokFilter
    ) { households, query, purok ->
        households.filter {
            val matchesQuery = it.householdNumber.contains(query, ignoreCase = true) ||
                    it.headName.contains(query, ignoreCase = true)
            val matchesPurok = purok == "All" || it.purok == purok
            matchesQuery && matchesPurok
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredBlotters: StateFlow<List<Blotter>> = combine(
        allBlotters, blotterStatusFilter
    ) { blotters, status ->
        if (status == "All") blotters else blotters.filter { it.status.contains(status, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredComplaints: StateFlow<List<Complaint>> = combine(
        allComplaints, complaintStatusFilter
    ) { complaints, status ->
        if (status == "All") complaints else complaints.filter { it.status == status }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Document Requests Specific to Logged In Resident ---
    val residentDocumentRequests: StateFlow<List<DocumentRequest>> = combine(
        allDocumentRequests, currentUser
    ) { requests, user ->
        if (user?.role == "Resident" && user.residentId != null) {
            requests.filter { it.residentId == user.residentId }
        } else {
            requests // staff or admin see everything
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Analytics States ---
    val totalResidentCount = allResidents.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val verifiedResidentCount = allResidents.map { list -> list.count { it.isVerified } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalHouseholdCount = allHouseholds.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingDocumentsCount = allDocumentRequests.map { list -> list.count { it.status == "Pending" || it.status == "Processing" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val solvedBlotterCount = allBlotters.map { list -> list.count { it.status == "Resolved" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeBlotterCount = allBlotters.map { list -> list.count { it.status.startsWith("Active") } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val resolvedComplaintsCount = allComplaints.map { list -> list.count { it.status == "Action Taken" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // --- Authentication Actions ---
    fun login(usernameInput: String, passwordInput: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _loginError.value = null
            if (usernameInput.isBlank() || passwordInput.isBlank()) {
                _loginError.value = "Please complete all fields"
                onComplete(false)
                return@launch
            }
            val user = repository.getUserByUsername(usernameInput.trim())
            if (user == null) {
                _loginError.value = "Username does not exist"
                onComplete(false)
            } else if (user.passwordField != passwordInput) {
                _loginError.value = "Incorrect password"
                onComplete(false)
            } else {
                _currentUser.value = user
                _loginError.value = null
                onComplete(true)
            }
        }
    }

    fun registerResidentUser(
        usernameInput: String,
        passwordInput: String,
        fullNameInput: String,
        birthDateInput: String,
        genderInput: String,
        civilStatusInput: String,
        phoneInput: String,
        emailInput: String,
        purokInput: String,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _loginError.value = null
            _registerSuccess.value = false
            if (usernameInput.isBlank() || passwordInput.isBlank() || fullNameInput.isBlank() || phoneInput.isBlank()) {
                _loginError.value = "Please complete primary registration fields"
                onComplete(false)
                return@launch
            }

            // Check if username already exists
            val existingUser = repository.getUserByUsername(usernameInput.trim())
            if (existingUser != null) {
                _loginError.value = "Username already taken"
                onComplete(false)
                return@launch
            }

            // 1. Create Resident
            val residentId = repository.insertResident(
                Resident(
                    fullName = fullNameInput.trim(),
                    birthDate = birthDateInput,
                    gender = genderInput,
                    civilStatus = civilStatusInput,
                    phone = phoneInput.trim(),
                    email = emailInput.trim(),
                    purok = purokInput,
                    isHouseholdHead = false,
                    isVerified = false // Needs staff audit approval
                )
            ).toInt()

            // 2. Create User account linked to resident
            val user = User(
                username = usernameInput.trim().lowercase(),
                passwordField = passwordInput,
                fullName = fullNameInput.trim(),
                role = "Resident",
                residentId = residentId
            )
            repository.insertUser(user)

            _registerSuccess.value = true
            // Auto login after registration
            _currentUser.value = user
            onComplete(true)
        }
    }

    fun logout() {
        _currentUser.value = null
        _loginError.value = null
        _registerSuccess.value = false
    }

    // --- Record Actions ---
    fun selectResident(resident: Resident?) {
        _selectedResident.value = resident
    }

    fun createResident(
        name: String,
        bday: String,
        gender: String,
        civil: String,
        phone: String,
        email: String,
        purok: String,
        isHead: Boolean,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            repository.insertResident(
                Resident(
                    fullName = name.trim(),
                    birthDate = bday,
                    gender = gender,
                    civilStatus = civil,
                    phone = phone,
                    email = email,
                    purok = purok,
                    isHouseholdHead = isHead,
                    isVerified = true
                )
            )
            onComplete()
        }
    }

    fun verifyResident(resident: Resident) {
        viewModelScope.launch {
            repository.updateResident(resident.copy(isVerified = true))
        }
    }

    fun createHousehold(
        number: String,
        purok: String,
        headId: Int,
        headName: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val hId = repository.insertHousehold(
                Household(
                    householdNumber = number,
                    purok = purok,
                    headId = headId,
                    headName = headName
                )
            ).toInt()

            // Link resident to household
            val headResident = repository.getResidentById(headId)
            if (headResident != null) {
                repository.updateResident(headResident.copy(householdId = hId, isHouseholdHead = true, purok = purok))
            }
            onComplete()
        }
    }

    // --- Transaction Actions ---
    fun submitDocumentRequest(
        residentId: Int,
        residentName: String,
        type: String,
        purpose: String,
        fee: Double,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.insertDocumentRequest(
                DocumentRequest(
                    residentId = residentId,
                    residentName = residentName,
                    documentType = type,
                    purpose = purpose,
                    status = "Pending",
                    dateRequested = dateStr,
                    fee = fee
                )
            )
            onComplete()
        }
    }

    fun updateRequestStatus(
        requestId: Int,
        newStatus: String,
        memo: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val requests = allDocumentRequests.value
            val target = requests.find { it.id == requestId }
            if (target != null) {
                repository.updateDocumentRequest(
                    target.copy(status = newStatus, adminMemo = memo)
                )
            }
            onComplete()
        }
    }

    fun createBlotter(
        complainant: String,
        respondent: String,
        incidentType: String,
        details: String,
        purok: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val count = allBlotters.value.size + 1
            val caseNo = "BLT-2026-${count.toString().padStart(4, '0')}"
            repository.insertBlotter(
                Blotter(
                    caseNumber = caseNo,
                    complainantName = complainant,
                    respondentName = respondent,
                    incidentType = incidentType,
                    details = details,
                    status = "Active (Ongoing)",
                    dateFiled = dateStr,
                    purok = purok
                )
            )
            onComplete()
        }
    }

    fun updateBlotterStatus(id: Int, newStatus: String) {
        viewModelScope.launch {
            val target = allBlotters.value.find { it.id == id }
            if (target != null) {
                repository.updateBlotter(target.copy(status = newStatus))
            }
        }
    }

    fun createComplaint(
        complainant: String,
        contact: String,
        title: String,
        description: String,
        category: String,
        purok: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.insertComplaint(
                Complaint(
                    complainantName = complainant,
                    complainantContact = contact,
                    title = title,
                    description = description,
                    category = category,
                    status = "Submitted",
                    dateSubmitted = dateStr,
                    purok = purok
                )
            )
            onComplete()
        }
    }

    fun updateComplaintStatus(id: Int, newStatus: String) {
        viewModelScope.launch {
            val target = allComplaints.value.find { it.id == id }
            if (target != null) {
                repository.updateComplaint(target.copy(status = newStatus))
            }
        }
    }

    fun addAnnouncement(
        title: String,
        content: String,
        category: String,
        postedBy: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.insertAnnouncement(
                Announcement(
                    title = title,
                    content = content,
                    category = category,
                    datePosted = dateStr,
                    postedBy = postedBy
                )
            )
            onComplete()
        }
    }
}

class BarangayViewModelFactory(private val repository: BarangayRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BarangayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BarangayViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
