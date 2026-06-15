package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.BarangayViewModel
import com.example.ui.theme.*

@Composable
fun BarangayAppUI(viewModel: BarangayViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        if (currentUser == null) {
            AuthScreen(viewModel = viewModel)
        } else {
            MainAppShell(viewModel = viewModel, user = currentUser!!)
        }
    }
}

// --- SECURE AUTHENTICATION SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(viewModel: BarangayViewModel) {
    var isLoginMode by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Registration extras
    var fullName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("1995-01-01") }
    var gender by remember { mutableStateOf("Male") }
    var civilStatus by remember { mutableStateOf("Single") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var purok by remember { mutableStateOf("Purok 1") }

    val loginError by viewModel.loginError.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    val gradColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                )
            )
            .imePadding()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Emblem / Icon (Bento Style rounded!)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Brush.linearGradient(gradColors)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = "Civic Emblem",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "BARANGAY LINK",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Empowering Communities Through Digital Services",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Auth Card (Generous Bento rounded!)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_card"),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isLoginMode) "Account Log In" else "Resident Register",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (loginError != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Error icon",
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = loginError ?: "",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    // Credentials fields
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User icon") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock icon") },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordTransformation,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Additional Register Form Fields
                    AnimatedVisibility(visible = !isLoginMode) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("Full Name (Last, First Middle)") },
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = "Name icon") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = phone,
                                    onValueChange = { phone = it },
                                    label = { Text("Mobile No.") },
                                    placeholder = { Text("09xxxxxxxxx") },
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone icon") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Email Address") },
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email icon") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Purok Selector
                                var expandedPurok by remember { mutableStateOf(false) }
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = purok,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Purok / Area") },
                                        trailingIcon = {
                                            IconButton(onClick = { expandedPurok = true }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Purok")
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    DropdownMenu(
                                        expanded = expandedPurok,
                                        onDismissRequest = { expandedPurok = false }
                                    ) {
                                        (1..7).forEach { num ->
                                            DropdownMenuItem(
                                                text = { Text("Purok $num") },
                                                onClick = {
                                                    purok = "Purok $num"
                                                    expandedPurok = false
                                                }
                                            )
                                        }
                                    }
                                }

                                var expandedGender by remember { mutableStateOf(false) }
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = gender,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Gender") },
                                        trailingIcon = {
                                            IconButton(onClick = { expandedGender = true }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Gender")
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    DropdownMenu(
                                        expanded = expandedGender,
                                        onDismissRequest = { expandedGender = false }
                                    ) {
                                        listOf("Male", "Female", "Other").forEach { gen ->
                                            DropdownMenuItem(
                                                text = { Text(gen) },
                                                onClick = {
                                                    gender = gen
                                                    expandedGender = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = birthDate,
                                    onValueChange = { birthDate = it },
                                    label = { Text("Birthdate (YYYY-MM-DD)") },
                                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Calendar icon") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                var expandedStatus by remember { mutableStateOf(false) }
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = civilStatus,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Civil Status") },
                                        trailingIcon = {
                                            IconButton(onClick = { expandedStatus = true }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Civil Status")
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    DropdownMenu(
                                        expanded = expandedStatus,
                                        onDismissRequest = { expandedStatus = false }
                                    ) {
                                        listOf("Single", "Married", "Widowed", "Separated").forEach { status ->
                                            DropdownMenuItem(
                                                text = { Text(status) },
                                                onClick = {
                                                    civilStatus = status
                                                    expandedStatus = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Primary Action Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if (isLoginMode) {
                                viewModel.login(username, password) { success ->
                                    if (success) {
                                        username = ""
                                        password = ""
                                    }
                                }
                            } else {
                                viewModel.registerResidentUser(
                                    usernameInput = username,
                                    passwordInput = password,
                                    fullNameInput = fullName,
                                    birthDateInput = birthDate,
                                    genderInput = gender,
                                    civilStatusInput = civilStatus,
                                    phoneInput = phone,
                                    emailInput = email,
                                    purokInput = purok
                                ) { success ->
                                    if (success) {
                                        isLoginMode = true
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (isLoginMode) "LOG IN" else "REGISTER CITIZEN",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Secondary Swap Button
                    TextButton(onClick = {
                        isLoginMode = !isLoginMode
                        viewModel.login("", "") {} // Clear errors
                    }) {
                        Text(
                            text = if (isLoginMode) "Don't have a login account? Register" else "Already have an account? Log In",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Demo Accounts Help Ribbon
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Demo Credentials Quick Access:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Admin/Staff: admin / admin123 (Manage records & documents)\n• Resident account: resident / resident123 (Track, apply, report help)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

private val PasswordTransformation = PasswordVisualTransformation()

// --- MAIN SHELL CONTAINER ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppShell(viewModel: BarangayViewModel, user: User) {
    var selectedTab by remember { mutableStateOf(0) }
    var expandedProfileMenu by remember { mutableStateOf(false) }

    val role = user.role // "Resident", "Staff", "Admin"
    val isDark = isSystemInDarkTheme()

    val initials = remember(user.fullName, user.username) {
        if (user.fullName.isNotBlank()) {
            val parts = user.fullName.split(" ")
            if (parts.size >= 2) {
                "${parts[0].firstOrNull() ?: ""}${parts[1].firstOrNull() ?: ""}".uppercase()
            } else {
                user.fullName.take(2).uppercase()
            }
        } else {
            user.username.take(2).uppercase()
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "BRGY. SAN JOSE • LINK",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                        Text(
                            text = when (selectedTab) {
                                0 -> "Management Hub"
                                1 -> "Certificates Portal"
                                2 -> "Citizen Directory"
                                3 -> "Safety & Blotter"
                                else -> "Barangay Link"
                            },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Box {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF1E293B) else Color(0xFFD1E1FF))
                                .border(2.dp, if (isDark) Color(0xFF2E3A52) else Color.White, CircleShape)
                                .clickable { expandedProfileMenu = true }
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (isDark) Color(0xFF93C5FD) else Color(0xFF1E3A8A)
                            )
                        }

                        DropdownMenu(
                            expanded = expandedProfileMenu,
                            onDismissRequest = { expandedProfileMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Role: ${user.role}", fontWeight = FontWeight.Bold) },
                                onClick = {},
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                enabled = false
                            )
                            DropdownMenuItem(
                                text = { Text("Citizen ID: #${user.residentId ?: 0}") },
                                onClick = {},
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                enabled = false
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Log Out Account", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    expandedProfileMenu = false
                                    viewModel.logout()
                                },
                                leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Hub", maxLines = 1) }
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = "Requests") },
                    label = { Text("Documents", maxLines = 1) }
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.People, contentDescription = "Citizens") },
                    label = { Text(if (role == "Resident") "Community" else "Citizens", maxLines = 1) }
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Gavel, contentDescription = "Security") },
                    label = { Text("Security", maxLines = 1) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HubDashboardTab(viewModel = viewModel, user = user)
                1 -> DocumentRequestsTab(viewModel = viewModel, user = user)
                2 -> CitizensManagerTab(viewModel = viewModel, user = user)
                3 -> SecurityAndBlotterTab(viewModel = viewModel, user = user)
            }
        }
    }
}

// ======================== TAB 0: HOME HUB DASHBOARD ========================
@Composable
fun HubDashboardTab(viewModel: BarangayViewModel, user: User) {
    val announcements by viewModel.allAnnouncements.collectAsStateWithLifecycle()

    // Analytics state bindings
    val resCount by viewModel.totalResidentCount.collectAsStateWithLifecycle()
    val householdCount by viewModel.totalHouseholdCount.collectAsStateWithLifecycle()
    val pendingDocCount by viewModel.pendingDocumentsCount.collectAsStateWithLifecycle()
    val activeBlotters by viewModel.activeBlotterCount.collectAsStateWithLifecycle()
    val verifiedCount by viewModel.verifiedResidentCount.collectAsStateWithLifecycle()

    var showAnnouncementDialog by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Welcome Header Banner (Bento Style!)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Kamusta, ${user.fullName}!",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Authorized as ${user.role}. Securely tracking development and state parameters.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                Icon(
                    imageVector = Icons.Default.WavingHand,
                    contentDescription = "Wave",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(54.dp)
                )
            }
        }

        // Bento Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(48.dp)
                .clip(CircleShape)
                .background(if (isDark) Color(0xFF1E293B) else Color(0xFFEDF0F7))
                .border(
                    BorderStroke(
                        1.dp,
                        if (isDark) Color(0xFF2E3B52) else Color(0xFFEDF0F7)
                    ),
                    CircleShape
                )
                .clickable { /* decorative */ }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Search dynamic records, citizens, and clearances...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) Color(0xFF94A3B8).copy(alpha = 0.6f) else Color(0xFF64748B).copy(alpha = 0.7f)
                )
            }
        }

        // Analytical Statistics Grid (Bento Board)
        Text(
            text = "Barangay Metrics Dashboard",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Bento Card 1: Community Overview (Full Width)
        val verPercent = remember(resCount, verifiedCount) {
            if (resCount > 0) (verifiedCount * 100) / resCount else 92
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) BentoBlueBgDark else BentoBlueBg),
            border = BorderStroke(1.dp, if (isDark) BentoBlueBorderDark else BentoBlueBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📊", fontSize = 18.sp)
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF2E3D52) else Color(0x33FFFFFF))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LIVE STATS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = if (isDark) Color(0xFF93C5FD) else Color(0xFF1E3A8A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Community Overview",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E3A8A)
                )
                Text(
                    text = "Total Active Households: $householdCount",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) Color(0xFF93C5FD).copy(alpha = 0.8f) else Color(0xFF1D4ED8).copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column {
                        Text(
                            text = resCount.toString(),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 32.sp
                            ),
                            color = if (isDark) Color.White else Color(0xFF172554)
                        )
                        Text(
                            text = "Residents",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF1E3A8A).copy(alpha = 0.7f)
                        )
                    }

                    Column {
                        Text(
                            text = "$verPercent%",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 32.sp
                            ),
                            color = if (isDark) Color.White else Color(0xFF172554)
                        )
                        Text(
                            text = "Verified",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF1E3A8A).copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Bento Cards 2 & 3: Side-by-Side Symmetrical row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 2: Certificates / Requests Portal (Bento Purple)
            BentoDocPortalCard(pendingDocCount = pendingDocCount, viewModel = viewModel, modifier = Modifier.weight(1f))

            // Card 3: Blotters / Dispute Board (Bento Red)
            BentoBlottersGridCard(activeBlotters = activeBlotters, viewModel = viewModel, modifier = Modifier.weight(1f))
        }

        // Bento Cards 4 & 5: Health & Advisories Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 4: Health Unit (Bento Green)
            BentoHealthCard(modifier = Modifier.weight(1f))

            // Card 5: Assembly/Advisory (Bento Yellow)
            BentoAdvisoryCard(modifier = Modifier.weight(1f))
        }

        // Announcements Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Announcements & News Feed",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            // Staff and Admins can publish announcements directly
            if (user.role != "Resident") {
                TextButton(
                    onClick = { showAnnouncementDialog = true },
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add announcement")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Publish News")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (announcements.isEmpty()) {
            EmptyListPlaceholder(
                icon = Icons.Default.Campaign,
                title = "No Announcements Yet",
                subtitle = "Active circulars and schedules will be listed here."
            )
        } else {
            announcements.forEach { announce ->
                AnnouncementItemCard(announce = announce)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    // New Announcement Dialog
    if (showAnnouncementDialog) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("General Info") }

        var showValidationError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAnnouncementDialog = false },
            title = { Text("Publish Announcement", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (showValidationError) {
                        Text(
                            "Please fill in all blanks.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Bullet Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    var expandCat by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("News Tag Category") },
                            trailingIcon = {
                                IconButton(onClick = { expandCat = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Details")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(expanded = expandCat, onDismissRequest = { expandCat = false }) {
                            listOf("General Info", "Emergency Advisory", "Community Event", "Vaccination Drive").forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        category = item
                                        expandCat = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Announcement Body Content") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (title.isBlank() || content.isBlank()) {
                        showValidationError = true
                    } else {
                        viewModel.addAnnouncement(
                            title = title,
                            content = content,
                            category = category,
                            postedBy = user.fullName
                        ) {
                            showAnnouncementDialog = false
                        }
                    }
                }) {
                    Text("Publish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAnnouncementDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun BentoDocPortalCard(pendingDocCount: Int, viewModel: BarangayViewModel, modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    val totalDocs = viewModel.allDocumentRequests.collectAsStateWithLifecycle().value.size
    val completedDocs = totalDocs - pendingDocCount
    val progress = if (totalDocs > 0) (completedDocs.toFloat() / totalDocs) else 0.8f

    Card(
        modifier = modifier.height(240.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF2E1B4E) else Color(0xFFE8DEF8)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📜", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Certificates",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "$pendingDocCount Pending Forms",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Completion",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) Color(0xFFD0BCFF) else Color(0xFF6750A4)
                    )
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = if (isDark) Color(0xFFD0BCFF) else Color(0xFF6750A4),
                    trackColor = if (isDark) Color(0xFF1E1F22) else Color(0xFFF1F5F9),
                )
            }
        }
    }
}

@Composable
fun BentoBlottersGridCard(activeBlotters: Int, viewModel: BarangayViewModel, modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    val blotters = viewModel.allBlotters.collectAsStateWithLifecycle().value

    Card(
        modifier = modifier.height(240.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF4A1515) else Color(0xFFFFDADA)),
                contentAlignment = Alignment.Center
            ) {
                Text("⚖️", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Blotter Logs",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "$activeBlotters active cases",
                style = MaterialTheme.typography.bodySmall,
                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val displayItems = blotters.take(2)
                if (displayItems.isNotEmpty()) {
                    displayItems.forEach { blot ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC))
                                .border(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFEEF2F6), RoundedCornerShape(12.dp))
                                .padding(6.dp)
                        ) {
                            Column {
                                Text(
                                    text = "CASE #${blot.caseNumber}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (blot.status.startsWith("Active")) Color.Red else Color.Green,
                                    fontSize = 8.sp
                                )
                                Text(
                                    text = "${blot.incidentType} - ${blot.purok}",
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                } else {
                    listOf(
                        "CASE-204" to "Boundary Dispute",
                        "CASE-205" to "Noise Complaint"
                    ).forEach { (caseNumber, dispute) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC))
                                .border(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFEEF2F6), RoundedCornerShape(12.dp))
                                .padding(6.dp)
                        ) {
                            Column {
                                Text(
                                    text = caseNumber,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.Red,
                                    fontSize = 8.sp
                                )
                                Text(
                                    text = "$dispute - Area",
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BentoHealthCard(modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    val greenBg = if (isDark) Color(0xFF064E3B) else Color(0xFFE2F7ED)
    val greenBorder = if (isDark) Color(0xFF065F46) else Color(0xFFA7F3D0)
    val greenText = if (isDark) Color(0xFF6EE7B7) else Color(0xFF047857)

    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = greenBg),
        border = BorderStroke(1.dp, greenBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("🏥", fontSize = 24.sp)
            Column {
                Text(
                    text = "Health Unit",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) Color.White else Color(0xFF064E3B)
                )
                Text(
                    text = "Free Vaccination Drive ongoing in Gym.",
                    style = MaterialTheme.typography.bodySmall,
                    color = greenText,
                    lineHeight = 12.sp,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun BentoAdvisoryCard(modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    val yellowBg = if (isDark) Color(0xFF452D12) else Color(0xFFFDEFD3)
    val yellowBorder = if (isDark) Color(0xFF5F3E11) else Color(0xFFFED7AA)
    val yellowText = if (isDark) Color(0xFFFDE047) else Color(0xFFB45309)

    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = yellowBg),
        border = BorderStroke(1.dp, yellowBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("📢", fontSize = 24.sp)
            Column {
                Text(
                    text = "Civic Notice",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) Color.White else Color(0xFF452D12)
                )
                Text(
                    text = "General Assembly tomorrow. Be updated with live news.",
                    style = MaterialTheme.typography.bodySmall,
                    color = yellowText,
                    lineHeight = 12.sp,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun CanvasRatingBar(label: String, progress: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun AnnouncementItemCard(announce: Announcement) {
    val tagColor = when (announce.category) {
        "Emergency Advisory" -> MaterialTheme.colorScheme.errorContainer
        "Community Event" -> MaterialTheme.colorScheme.secondaryContainer
        "Vaccination Drive" -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val tagTextColor = when (announce.category) {
        "Emergency Advisory" -> MaterialTheme.colorScheme.onErrorContainer
        "Community Event" -> MaterialTheme.colorScheme.onSecondaryContainer
        "Vaccination Drive" -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(announce.category, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = tagColor,
                        labelColor = tagTextColor
                    )
                )

                Text(
                    text = announce.datePosted,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = announce.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = announce.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Posted by: ${announce.postedBy}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// ======================== TAB 1: DOCUMENT REQUESTS & TRACKING ========================
@Composable
fun DocumentRequestsTab(viewModel: BarangayViewModel, user: User) {
    val requests by viewModel.residentDocumentRequests.collectAsStateWithLifecycle()
    var showRequestDialog by remember { mutableStateOf(false) }

    // Admin state holders
    var selectedRequestToManage by remember { mutableStateOf<DocumentRequest?>(null) }

    val isResident = user.role == "Resident"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isResident) "My Documents" else "Document Clearances Portal",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isResident) "Track your requested barangay certificates" else "Process citizen clearance and indigency requests",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            if (isResident) {
                Button(
                    onClick = { showRequestDialog = true },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Req")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Apply")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (requests.isEmpty()) {
            EmptyListPlaceholder(
                icon = Icons.Default.History,
                title = "No Document Requests",
                subtitle = if (isResident) "You have not requested any barangay clearances yet. Tap 'Apply' to file one." else "No pending citizen document applications found."
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(requests) { req ->
                    DocumentRequestCard(
                        req = req,
                        canManage = !isResident,
                        onClick = {
                            if (!isResident) {
                                selectedRequestToManage = req
                            }
                        }
                    )
                }
            }
        }
    }

    // Resident request dialog
    if (showRequestDialog) {
        var docType by remember { mutableStateOf("Barangay Clearance") }
        var purpose by remember { mutableStateOf("") }
        var errorMsg by remember { mutableStateOf("") }

        val fee = when (docType) {
            "Barangay Clearance" -> 50.0
            "Certificate of Residency" -> 75.0
            "Certificate of Indigency" -> 0.0 // Free
            else -> 40.0
        }

        AlertDialog(
            onDismissRequest = { showRequestDialog = false },
            title = { Text("Request Barangay Document", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (errorMsg.isNotBlank()) {
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Text("Document Type:", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    listOf("Barangay Clearance", "Certificate of Residency", "Certificate of Indigency").forEach { type ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { docType = type }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = docType == type, onClick = { docType = type })
                            Text(type, style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Processing Fee: ₱${fee.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = purpose,
                        onValueChange = { purpose = it },
                        label = { Text("Purpose of Request (e.g. Job, Bank)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (purpose.isBlank()) {
                        errorMsg = "Please state the purpose of your document."
                    } else {
                        viewModel.submitDocumentRequest(
                            residentId = user.residentId ?: 0,
                            residentName = user.fullName,
                            type = docType,
                            purpose = purpose,
                            fee = fee
                        ) {
                            showRequestDialog = false
                        }
                    }
                }) {
                    Text("Submit Request")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRequestDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Staff Document Management / Update Status Dialog
    if (selectedRequestToManage != null) {
        val req = selectedRequestToManage!!
        var status by remember { mutableStateOf(req.status) }
        var memo by remember { mutableStateOf(req.adminMemo) }

        AlertDialog(
            onDismissRequest = { selectedRequestToManage = null },
            title = { Text("Update Document Status #DR-${req.id}", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Resident: ${req.residentName}", fontWeight = FontWeight.SemiBold)
                    Text("Document: ${req.documentType}", color = MaterialTheme.colorScheme.secondary)
                    Text("Purpose: ${req.purpose}", style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Select Process Status:", fontWeight = FontWeight.Bold)
                    listOf("Pending", "Processing", "Ready for Pickup", "Completed").forEach { st ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { status = st }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = status == st, onClick = { status = st })
                            Text(st, style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = memo,
                        onValueChange = { memo = it },
                        label = { Text("Staff / Administrator Memo") },
                        placeholder = { Text("E.g., Please bring exact amount.") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateRequestStatus(req.id, status, memo) {
                        selectedRequestToManage = null
                    }
                }) {
                    Text("Confirm status")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRequestToManage = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DocumentRequestCard(req: DocumentRequest, canManage: Boolean, onClick: () -> Unit) {
    val statusColor = when (req.status) {
        "Pending" -> Color(0xFFF59E0B) // Amber
        "Processing" -> Color(0xFF3B82F6) // Blue
        "Ready for Pickup" -> Color(0xFF10B981) // Green
        "Completed" -> Color(0xFF6B7280) // Gray
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = canManage) { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = req.documentType,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Reference #DR-${req.id} • ${req.dateRequested}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                // Custom Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = req.status,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Purpose: ${req.purpose}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            if (req.adminMemo.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Memo icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = req.adminMemo,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Requestor: ${req.residentName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Text(
                    text = "₱${req.fee.toInt()}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (canManage) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        "Tap to manage status",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}


// ======================== TAB 2: CITIZEN DIRECTORY & HOUSEHOLDS ========================
@Composable
fun CitizensManagerTab(viewModel: BarangayViewModel, user: User) {
    var inHouseholdView by remember { mutableStateOf(false) }

    val role = user.role
    val isResident = role == "Resident"

    val residents by viewModel.filteredResidents.collectAsStateWithLifecycle()
    val households by viewModel.filteredHouseholds.collectAsStateWithLifecycle()

    val residentQuery by viewModel.residentSearchQuery.collectAsStateWithLifecycle()
    val residentPurokFilter by viewModel.residentPurokFilter.collectAsStateWithLifecycle()

    val householdQuery by viewModel.householdSearchQuery.collectAsStateWithLifecycle()
    val householdPurokFilter by viewModel.householdPurokFilter.collectAsStateWithLifecycle()

    var showAddResidentDialog by remember { mutableStateOf(false) }
    var showCreateHouseholdDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Tab Segment toggles (Residents Directory vs Household Units)
        Text(
            text = if (isResident) "Community Directory" else "Government Census Managers",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(12.dp))

        TabRow(selectedTabIndex = if (inHouseholdView) 1 else 0) {
            Tab(
                selected = !inHouseholdView,
                onClick = { inHouseholdView = false },
                text = { Text("Residents List") }
            )
            Tab(
                selected = inHouseholdView,
                onClick = { inHouseholdView = true },
                text = { Text("Household Units") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!inHouseholdView) {
            // RESIDENTS SEARCH BAR & FILTER ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = residentQuery,
                    onValueChange = { viewModel.residentSearchQuery.value = it },
                    placeholder = { Text("Search Residents...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                    modifier = Modifier.weight(1.0f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Purok Filter
                var expandedFilter by remember { mutableStateOf(false) }
                Box {
                    Button(
                        onClick = { expandedFilter = true },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(residentPurokFilter)
                    }
                    DropdownMenu(expanded = expandedFilter, onDismissRequest = { expandedFilter = false }) {
                        listOf("All", "Purok 1", "Purok 2", "Purok 3", "Purok 4", "Purok 5", "Purok 6", "Purok 7").forEach { prk ->
                            DropdownMenuItem(
                                text = { Text(prk) },
                                onClick = {
                                    viewModel.residentPurokFilter.value = prk
                                    expandedFilter = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action line for staff to register a census manually
            if (!isResident) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { showAddResidentDialog = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add citizen")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Resident")
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (residents.isEmpty()) {
                EmptyListPlaceholder(
                    icon = Icons.Default.SearchOff,
                    title = "No Residents Found",
                    subtitle = "Verify searching term or change active Area filter."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(residents) { res ->
                        ResidentListItemCard(
                            res = res,
                            clickable = !isResident,
                            onVerifyClick = { viewModel.verifyResident(res) }
                        )
                    }
                }
            }
        } else {
            // HOUSEHOLDS SEARCH BAR & FILTER ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = householdQuery,
                    onValueChange = { viewModel.householdSearchQuery.value = it },
                    placeholder = { Text("Search Households / Heads...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                    modifier = Modifier.weight(1.0f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Purok Filter
                var expandedFilter by remember { mutableStateOf(false) }
                Box {
                    Button(
                        onClick = { expandedFilter = true },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(householdPurokFilter)
                    }
                    DropdownMenu(expanded = expandedFilter, onDismissRequest = { expandedFilter = false }) {
                        listOf("All", "Purok 1", "Purok 2", "Purok 3", "Purok 4", "Purok 5", "Purok 6", "Purok 7").forEach { prk ->
                            DropdownMenuItem(
                                text = { Text(prk) },
                                onClick = {
                                    viewModel.householdPurokFilter.value = prk
                                    expandedFilter = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!isResident) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { showCreateHouseholdDialog = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "HH Create")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Create Household")
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (households.isEmpty()) {
                EmptyListPlaceholder(
                    icon = Icons.Default.SearchOff,
                    title = "No Households Found",
                    subtitle = "Verify search query or add a brand new household record."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(households) { hh ->
                        HouseholdListItemCard(hh = hh)
                    }
                }
            }
        }
    }

    // Add Resident Dialog
    if (showAddResidentDialog) {
        var name by remember { mutableStateOf("") }
        var birthDate by remember { mutableStateOf("1995-01-01") }
        var gender by remember { mutableStateOf("Male") }
        var civilStatus by remember { mutableStateOf("Single") }
        var phone by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var purok by remember { mutableStateOf("Purok 1") }
        var isHead by remember { mutableStateOf(false) }

        var validationIssue by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddResidentDialog = false },
            title = { Text("Add Barangay Resident", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (validationIssue) {
                        Text(
                            "Full Name and Phone Number are required.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = birthDate,
                        onValueChange = { birthDate = it },
                        label = { Text("Birthdate (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Mobile No.") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        var expPurok by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = purok,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Purok") },
                                trailingIcon = {
                                    IconButton(onClick = { expPurok = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Arrow")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            DropdownMenu(expanded = expPurok, onDismissRequest = { expPurok = false }) {
                                (1..7).forEach { n ->
                                    DropdownMenuItem(
                                        text = { Text("Purok $n") },
                                        onClick = {
                                            purok = "Purok $n"
                                            expPurok = false
                                        }
                                    )
                                }
                            }
                        }

                        var expGender by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = gender,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Gender") },
                                trailingIcon = {
                                    IconButton(onClick = { expGender = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Arrow")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            DropdownMenu(expanded = expGender, onDismissRequest = { expGender = false }) {
                                listOf("Male", "Female", "Other").forEach { g ->
                                    DropdownMenuItem(
                                        text = { Text(g) },
                                        onClick = {
                                            gender = g
                                            expGender = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isHead, onCheckedChange = { isHead = it })
                        Text("This person is a Head of Household", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isBlank() || phone.isBlank()) {
                        validationIssue = true
                    } else {
                        viewModel.createResident(
                            name = name,
                            bday = birthDate,
                            gender = gender,
                            civil = civilStatus,
                            phone = phone,
                            email = email,
                            purok = purok,
                            isHead = isHead
                        ) {
                            showAddResidentDialog = false
                        }
                    }
                }) {
                    Text("Register Resident")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddResidentDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Create Household Dialog
    if (showCreateHouseholdDialog) {
        var hNumber by remember { mutableStateOf("") }
        var selectedHeadId by remember { mutableIntStateOf(0) }
        var selectedHeadName by remember { mutableStateOf("") }
        var householdPurok by remember { mutableStateOf("Purok 1") }

        val availResidents = viewModel.allResidents.collectAsStateWithLifecycle().value.filter { !it.isHouseholdHead && it.isVerified }

        var showValidationError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showCreateHouseholdDialog = false },
            title = { Text("Form: Register New Household", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (showValidationError) {
                        Text(
                            "Household Number and Selected Head required.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    val initialAutoNo = "HH-2026-${(households.size + 1).toString().padStart(4, '0')}"
                    LaunchedEffect(Unit) {
                        hNumber = initialAutoNo
                    }

                    OutlinedTextField(
                        value = hNumber,
                        onValueChange = { hNumber = it },
                        label = { Text("Household Serial ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Select Resident Head of Household:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(6.dp))

                    if (availResidents.isEmpty()) {
                        Text(
                            "No available verified citizens without assigned household. Register or verify new citizens first.",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        availResidents.forEach { civ ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedHeadId = civ.id
                                        selectedHeadName = civ.fullName
                                        householdPurok = civ.purok
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selectedHeadId == civ.id, onClick = {
                                    selectedHeadId = civ.id
                                    selectedHeadName = civ.fullName
                                    householdPurok = civ.purok
                                })
                                Text("${civ.fullName} (${civ.purok})")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (hNumber.isBlank() || selectedHeadId == 0) {
                            showValidationError = true
                        } else {
                            viewModel.createHousehold(
                                number = hNumber,
                                purok = householdPurok,
                                headId = selectedHeadId,
                                headName = selectedHeadName
                            ) {
                                showCreateHouseholdDialog = false
                            }
                        }
                    },
                    enabled = selectedHeadId != 0
                ) {
                    Text("Establish Unit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateHouseholdDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ResidentListItemCard(res: Resident, clickable: Boolean, onVerifyClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = res.fullName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    if (res.isHouseholdHead) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text("Head", fontSize = 10.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (res.isVerified) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (res.isVerified) "Verified Citizen" else "Unverified",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (res.isVerified) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "📍 ${res.purok} • 🎂 ${res.birthDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = if (res.gender == "Male") "♂ Male" else "♀ Female",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "📞 Mobile: ${res.phone} | ✉ ${res.email.ifBlank { "N/A" }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            if (!res.isVerified && clickable) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onVerifyClick() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = "Verify button")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Approve Verification Census")
                }
            }
        }
    }
}

@Composable
fun HouseholdListItemCard(hh: Household) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Household: ${hh.householdNumber}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                AssistChip(
                    onClick = {},
                    label = { Text(hh.purok) },
                    modifier = Modifier.height(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row {
                Column {
                    Text(
                        "Head of Household",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Text(
                        hh.headName,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}


// ======================== TAB 3: BLOTTER & CITIZEN COMPLAINTS ========================
@Composable
fun SecurityAndBlotterTab(viewModel: BarangayViewModel, user: User) {
    var isComplaintsView by remember { mutableStateOf(true) }

    val role = user.role
    val isResident = role == "Resident"

    val complaints by viewModel.filteredComplaints.collectAsStateWithLifecycle()
    val blotters by viewModel.filteredBlotters.collectAsStateWithLifecycle()

    var showComplaintDialog by remember { mutableStateOf(false) }
    var showBlotterDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Public Safety & Dispute Resolution",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Report civic issues or track active litigation blotter logs",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tab selection (Complaints, then Blotter (Staff view only or display read-only for residents support feedback))
        TabRow(selectedTabIndex = if (isComplaintsView) 0 else 1) {
            Tab(
                selected = isComplaintsView,
                onClick = { isComplaintsView = true },
                text = { Text("Citizen Complaints") }
            )
            Tab(
                selected = !isComplaintsView,
                onClick = { isComplaintsView = false },
                text = { Text("Security Blotters") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isComplaintsView) {
            // COMPLAINTS LISTING
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Filter
                var expandedFilter by remember { mutableStateOf(false) }
                val currentFilter by viewModel.complaintStatusFilter.collectAsStateWithLifecycle()
                Box {
                    Button(onClick = { expandedFilter = true }) {
                        Text("Status: $currentFilter")
                    }
                    DropdownMenu(expanded = expandedFilter, onDismissRequest = { expandedFilter = false }) {
                        listOf("All", "Submitted", "Under Review", "Action Taken").forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st) },
                                onClick = {
                                    viewModel.complaintStatusFilter.value = st
                                    expandedFilter = false
                                }
                            )
                        }
                    }
                }

                // Citizens file complaints
                Button(onClick = { showComplaintDialog = true }) {
                    Icon(Icons.Default.AddComment, contentDescription = "File issue")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("File Report")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (complaints.isEmpty()) {
                EmptyListPlaceholder(
                    icon = Icons.Default.ChatBubbleOutline,
                    title = "No Complaints submitted",
                    subtitle = "Tap 'File Report' to highlight issues like street flooding, noise, trash or road damage."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(complaints) { comp ->
                        ComplaintListItemCard(
                            comp = comp,
                            canManage = !isResident,
                            onActionUpdate = { next -> viewModel.updateComplaintStatus(comp.id, next) }
                        )
                    }
                }
            }
        } else {
            // BLOTTERS LISTING
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Filter
                var expandedFilter by remember { mutableStateOf(false) }
                val currentFilter by viewModel.blotterStatusFilter.collectAsStateWithLifecycle()
                Box {
                    Button(onClick = { expandedFilter = true }) {
                        Text("Status: $currentFilter")
                    }
                    DropdownMenu(expanded = expandedFilter, onDismissRequest = { expandedFilter = false }) {
                        listOf("All", "Active", "Resolved", "Dismissed").forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st) },
                                onClick = {
                                    viewModel.blotterStatusFilter.value = st
                                    expandedFilter = false
                                }
                            )
                        }
                    }
                }

                // Direct addition of new legal cases by staff only
                if (!isResident) {
                    Button(onClick = { showBlotterDialog = true }) {
                        Icon(Icons.Default.Gavel, contentDescription = "Add case")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Case")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (blotters.isEmpty()) {
                EmptyListPlaceholder(
                    icon = Icons.Default.AssignmentLate,
                    title = "Case Board Empty",
                    subtitle = "There are no security dispute cases registered."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(blotters) { blot ->
                        BlotterListItemCard(
                            blot = blot,
                            canManage = !isResident,
                            onUpdateStatus = { next -> viewModel.updateBlotterStatus(blot.id, next) }
                        )
                    }
                }
            }
        }
    }

    // Submit user complaint dialog
    if (showComplaintDialog) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Roads & Infrastructure") }
        var purok by remember { mutableStateOf("Purok 1") }
        var contact by remember { mutableStateOf(if (isResident) user.username else "") }

        var validationIssue by remember { mutableStateOf(false) }

        val contactPlaceholder = if (isResident) user.fullName else "Anonymous Citizen"

        AlertDialog(
            onDismissRequest = { showComplaintDialog = false },
            title = { Text("Submit Barangay Complaint Feed", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (validationIssue) {
                        Text(
                            "Title and Detail description are required.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Complaint Heading Title") },
                        placeholder = { Text("E.g., Clogged Sewer Drainage") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    var expandCat by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = {
                                IconButton(onClick = { expandCat = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(expanded = expandCat, onDismissRequest = { expandCat = false }) {
                            listOf("Health & Sanitation", "Public Safety", "Roads & Infrastructure", "Others").forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c) },
                                    onClick = {
                                        category = c
                                        expandCat = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    var expandPurok by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = purok,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Location Area (Purok)") },
                            trailingIcon = {
                                IconButton(onClick = { expandPurok = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(expanded = expandPurok, onDismissRequest = { expandPurok = false }) {
                            (1..7).forEach { num ->
                                DropdownMenuItem(
                                    text = { Text("Purok $num") },
                                    onClick = {
                                        purok = "Purok $num"
                                        expandPurok = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = contact,
                        onValueChange = { contact = it },
                        label = { Text("Your Contact phone / callback name") },
                        placeholder = { Text(contactPlaceholder) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Elaborate description of the incident") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (title.isBlank() || description.isBlank()) {
                        validationIssue = true
                    } else {
                        viewModel.createComplaint(
                            complainant = if (contact.isBlank()) contactPlaceholder else contact,
                            contact = "0945-LocalCitizen",
                            title = title,
                            description = description,
                            category = category,
                            purok = purok
                        ) {
                            showComplaintDialog = false
                        }
                    }
                }) {
                    Text("Submit Report")
                }
            },
            dismissButton = {
                TextButton(onClick = { showComplaintDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add security blotter details (Logged by Staff / Admin only)
    if (showBlotterDialog) {
        var complainantName by remember { mutableStateOf("") }
        var respondentName by remember { mutableStateOf("") }
        var incidentType by remember { mutableStateOf("Noise Complaint") }
        var details by remember { mutableStateOf("") }
        var purok by remember { mutableStateOf("Purok 1") }

        var validationIssue by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showBlotterDialog = false },
            title = { Text("Log Citizen Dispute Case", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (validationIssue) {
                        Text(
                            "Complainant, Respondent and explanation require complete inputs.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    OutlinedTextField(
                        value = complainantName,
                        onValueChange = { complainantName = it },
                        label = { Text("Complainant Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = respondentName,
                        onValueChange = { respondentName = it },
                        label = { Text("Respondent Accused Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    var expandInc by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = incidentType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Incident Category Dispute") },
                            trailingIcon = {
                                IconButton(onClick = { expandInc = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(expanded = expandInc, onDismissRequest = { expandInc = false }) {
                            listOf("Noise Complaint", "Property Damage", "Boundary Dispute", "Minor Altercation").forEach { inc ->
                                DropdownMenuItem(
                                    text = { Text(inc) },
                                    onClick = {
                                        incidentType = inc
                                        expandInc = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    var expandPurok by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = purok,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Location (Purok)") },
                            trailingIcon = {
                                IconButton(onClick = { expandPurok = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(expanded = expandPurok, onDismissRequest = { expandPurok = false }) {
                            (1..7).forEach { num ->
                                DropdownMenuItem(
                                    text = { Text("Purok $num") },
                                    onClick = {
                                        purok = "Purok $num"
                                        expandPurok = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = details,
                        onValueChange = { details = it },
                        label = { Text("Case details summary & logs") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (complainantName.isBlank() || respondentName.isBlank() || details.isBlank()) {
                        validationIssue = true
                    } else {
                        viewModel.createBlotter(
                            complainant = complainantName,
                            respondent = respondentName,
                            incidentType = incidentType,
                            details = details,
                            purok = purok
                        ) {
                            showBlotterDialog = false
                        }
                    }
                }) {
                    Text("Register Incident")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlotterDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ComplaintListItemCard(
    comp: Complaint,
    canManage: Boolean,
    onActionUpdate: (String) -> Unit
) {
    val statusColor = when (comp.status) {
        "Submitted" -> Color(0xFFF59E0B) // Amber
        "Under Review" -> Color(0xFF3B82F6) // Blue
        "Action Taken" -> Color(0xFF10B981) // Green
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comp.category,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.secondary
                )

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = comp.status,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = comp.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = comp.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reported by: ${comp.complainantName} (${comp.purok})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                Text(
                    text = comp.dateSubmitted,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }

            // Quick Resolution Controls for staff
            if (canManage && comp.status != "Action Taken") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (comp.status == "Submitted") {
                        Button(
                            onClick = { onActionUpdate("Under Review") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Acknowledge", fontSize = 11.sp)
                        }
                    }

                    Button(
                        onClick = { onActionUpdate("Action Taken") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Mark Resolved", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun BlotterListItemCard(
    blot: Blotter,
    canManage: Boolean,
    onUpdateStatus: (String) -> Unit
) {
    val statusColor = when (blot.status) {
        "Active (Ongoing)" -> Color(0xFFEF4444) // Red
        "Resolved" -> Color(0xFF10B981) // Green
        "Dismissed" -> Color(0xFF6B7280) // Gray
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ref Code: ${blot.caseNumber}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = blot.status,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${blot.complainantName} vs ${blot.respondentName}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.error
            )

            Text(
                text = "Incident: ${blot.incidentType} (Area Location: ${blot.purok})",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = blot.details,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Docket Date: ${blot.dateFiled}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )

                if (canManage && blot.status == "Active (Ongoing)") {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextButton(
                            onClick = { onUpdateStatus("Dismissed") },
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text("Dismiss", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                        }

                        Button(
                            onClick = { onUpdateStatus("Resolved") },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("Mark Settled", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}


// --- GENERIC EMPYT STATE COMPONENT ---
@Composable
fun EmptyListPlaceholder(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}
