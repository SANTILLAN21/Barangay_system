package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val passwordField: String, // Mock secure field
    val fullName: String,
    val role: String, // "Resident", "Staff", "Admin"
    val residentId: Int? = null
)

@Entity(tableName = "residents")
data class Resident(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val birthDate: String,
    val gender: String, // "Male", "Female", "Other"
    val civilStatus: String, // "Single", "Married", "Widowed", "Separated"
    val phone: String,
    val email: String,
    val purok: String, // "Purok 1" to "Purok 7"
    val householdId: Int? = null,
    val isHouseholdHead: Boolean = false,
    val isVerified: Boolean = true
)

@Entity(tableName = "households")
data class Household(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val householdNumber: String, // HH-2026-XXXX
    val purok: String,
    val headId: Int,
    val headName: String
)

@Entity(tableName = "document_requests")
data class DocumentRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val residentId: Int,
    val residentName: String,
    val documentType: String, // "Barangay Clearance", "Certificate of Indigency", "Certificate of Residency"
    val purpose: String,
    val status: String, // "Pending", "Processing", "Ready for Pickup", "Completed"
    val dateRequested: String,
    val adminMemo: String = "",
    val fee: Double = 50.0
)

@Entity(tableName = "blotters")
data class Blotter(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val caseNumber: String, // BLT-2026-XXXX
    val complainantName: String,
    val respondentName: String,
    val incidentType: String, // "Noise Complaint", "Property Damage", "Boundary Dispute", "Minor Altercation"
    val details: String,
    val status: String, // "Active (Ongoing)", "Resolved", "Dismissed"
    val dateFiled: String,
    val purok: String
)

@Entity(tableName = "complaints")
data class Complaint(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val complainantName: String,
    val complainantContact: String,
    val title: String,
    val description: String,
    val category: String, // "Health & Sanitation", "Public Safety", "Roads & Infrastructure", "Others"
    val status: String, // "Submitted", "Under Review", "Action Taken"
    val dateSubmitted: String,
    val purok: String
)

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val category: String, // "General Info", "Emergency Advisory", "Community Event", "Vaccination Drive"
    val datePosted: String,
    val postedBy: String
)
