package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmailVerificationResult
import com.example.data.model.MembershipState
import com.example.data.model.OnboardingProgressState
import com.example.ui.theme.*

@Composable
fun OnboardingWelcomeView(
    progress: OnboardingProgressState?,
    onVerifyEmail: (String) -> Unit,
    onConfirmName: (Boolean, String?) -> Unit,
    onSelectTimezone: (String) -> Unit,
    onAcceptRules: (Boolean) -> Unit,
    onAcknowledgeSafety: (Boolean) -> Unit,
    onActivateMembership: () -> Unit,
    onNavigateToTimeline: () -> Unit,
    modifier: Modifier = Modifier
) {
    var emailInput by remember { mutableStateOf("") }
    var showEmailModal by remember { mutableStateOf(false) }
    var verificationError by remember { mutableStateOf<String?>(null) }
    var isVerifying by remember { mutableStateOf(false) }
    var nameCorrectionNote by remember { mutableStateOf("") }
    var wantsNameCorrection by remember { mutableStateOf(false) }
    var selectedTz by remember { mutableStateOf(progress?.selectedTimezone ?: "Asia/Kolkata") }
    var rulesAgreed by remember { mutableStateOf(false) }
    var safetyAgreed by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val availableTimezones = listOf(
        "Asia/Kolkata" to "Asia/Kolkata (IST, UTC+5:30)",
        "America/New_York" to "America/New_York (EST, UTC-5:00)",
        "America/Los_Angeles" to "America/Los_Angeles (PST, UTC-8:00)",
        "Europe/London" to "Europe/London (GMT/BST, UTC+0)",
        "Europe/Berlin" to "Europe/Berlin (CET, UTC+1:00)",
        "Asia/Singapore" to "Asia/Singapore (SGT, UTC+8:00)",
        "Asia/Tokyo" to "Asia/Tokyo (JST, UTC+9:00)",
        "Australia/Sydney" to "Australia/Sydney (AEST, UTC+10:00)"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Channel Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "#",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = EmpireAmber,
                        fontWeight = FontWeight.Black
                    )
                )
                Text(
                    "welcome",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(EmpireSurfaceElevated)
                    .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    "ONBOARDING GATEWAY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = EmpireAmber,
                        fontSize = 9.sp
                    )
                )
            }
        }

        Divider(color = EmpireSurfaceBorder)

        // ----------------------------------------------------
        // STATE 1: ACTIVE MEMBER (Already onboarded)
        // ----------------------------------------------------
        if (progress?.membershipState == MembershipState.ACTIVE) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                EmpireEmerald.copy(alpha = 0.2f),
                                EmpireSurfaceCard
                            )
                        )
                    )
                    .border(1.dp, EmpireEmerald.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(EmpireEmerald.copy(alpha = 0.2f))
                        .border(2.dp, EmpireEmerald, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmpireEmerald,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    "WELCOME TO THE EMPIRE",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = TextWhite
                    )
                )

                Text(
                    "You are an active verified contributor.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextOffWhite)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IdBadge(progress.assignedJempId ?: "JEMP#001")
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(EmpireEmerald.copy(alpha = 0.15f))
                            .border(1.dp, EmpireEmerald.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "@Member",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmpireEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmpireSurfaceElevated)
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "DISCORD NICKNAME PREVIEW:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            "${progress.assignedJempId ?: "JEMP#001"} • ${progress.applicantName}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                color = EmpireAmberLight,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onNavigateToTimeline,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmpireAmber,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("MY TIMELINE", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
        // ----------------------------------------------------
        // STATE 2: SUSPENDED / TERMINATED
        // ----------------------------------------------------
        else if (progress?.membershipState == MembershipState.SUSPENDED || progress?.membershipState == MembershipState.TERMINATED) {
            val isTerminated = progress.membershipState == MembershipState.TERMINATED
            val bannerColor = if (isTerminated) Color(0xFF991B1B) else EmpireRose

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(bannerColor.copy(alpha = 0.15f))
                    .border(1.dp, bannerColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = null,
                    tint = bannerColor,
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    if (isTerminated) "MEMBERSHIP TERMINATED" else "MEMBERSHIP SUSPENDED",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = TextWhite
                    )
                )

                Text(
                    if (isTerminated)
                        "Your membership has been terminated due to zero-tolerance policy violation. Access to Jobless Empire channels is revoked."
                    else
                        "Your account is currently suspended pending administrative review. You cannot participate in project workspaces.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFFFDA4AF),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                )

                Text(
                    "Assigned Role: ${if (isTerminated) "@Terminated" else "@Suspended"}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = bannerColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        // ----------------------------------------------------
        // STATE 3: STEP 0 — UNVERIFIED JOINER (Needs Email Verification)
        // ----------------------------------------------------
        else if (progress == null || progress.currentStep == 0) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(EmpireSurface)
                    .border(1.dp, EmpireAmber.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Discord Bot Welcome Embed
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EmpireAmber.copy(alpha = 0.2f))
                            .border(1.dp, EmpireAmber, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🤖", fontSize = 18.sp)
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "EmpireBot",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(EmpireAmber)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    "BOT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.Black,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 8.sp
                                    )
                                )
                            }
                        }
                        Text(
                            "SYSTEM • Automated Attestation Gatekeeper",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                        )
                    }
                }

                Text(
                    "WELCOME TO JOBLESS EMPIRE.",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = TextWhite
                    )
                )

                Text(
                    "You applied. You were approved. Now let's get you set up.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = EmpireAmberLight,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Text(
                    "To enter the Empire, you must verify the email address you submitted in your website application. Once verified, you'll complete a quick 5-step onboarding sequence to receive your permanent JEMP member ID.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextOffWhite)
                )

                // Current Assigned Role
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Current Role:", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(EmpireSurfaceElevated)
                            .border(1.dp, EmpireAmber.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "@Unverified",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = EmpireAmber,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Quick Demo Pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmpireSurfaceElevated)
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "💡 APPROVED DEMO EMAILS FOR TESTING:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = EmpireAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "• alex.demo@example.invalid (Alex Demo)\n• saleha.shaikh@example.invalid (Saleha Shaikh)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = TextOffWhite,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Verification Input Box
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "ENTER YOUR APPLICATION EMAIL:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    )

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = {
                            emailInput = it
                            verificationError = null
                        },
                        placeholder = { Text("e.g. alex.demo@example.invalid") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = EmpireAmber)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmpireAmber,
                            unfocusedBorderColor = EmpireSurfaceBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextOffWhite
                        )
                    )

                    // Error Message Display (Non-enumerating error message)
                    if (verificationError != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmpireRose.copy(alpha = 0.12f))
                                .border(1.dp, EmpireRose.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = EmpireRose,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    verificationError!!,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFFFDA4AF),
                                        lineHeight = 16.sp
                                    )
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (emailInput.isNotBlank()) {
                                isVerifying = true
                                onVerifyEmail(emailInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmpireAmber,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        if (isVerifying) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            "VERIFY APPLICATION EMAIL",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
                        )
                    }
                }
            }
        }
        // ----------------------------------------------------
        // STATE 4: STEPS 1 TO 5 (INTERACTIVE ONBOARDING STEPPER)
        // ----------------------------------------------------
        else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(EmpireSurface)
                    .border(1.dp, EmpireAmber.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Stepper Progress Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "ONBOARDING PROGRESS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = EmpireAmber,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "Step ${progress.currentStep} of 5",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = TextWhite
                            )
                        )
                    }

                    // Progress Dots
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (step in 1..5) {
                            val isCompleted = step < progress.currentStep
                            val isCurrent = step == progress.currentStep
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCompleted -> EmpireEmerald
                                            isCurrent -> EmpireAmber
                                            else -> EmpireSurfaceBorderLight
                                        }
                                    )
                            )
                        }
                    }
                }

                Divider(color = EmpireSurfaceBorder)

                // ------------------------------------------------
                // STEP 1: CONFIRM NAME
                // ------------------------------------------------
                if (progress.currentStep == 1) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            "STEP 1: CONFIRM YOUR DISPLAY NAME",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        )

                        Text(
                            "Your application was submitted with the name: **${progress.applicantName}**.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextOffWhite)
                        )

                        Text(
                            "This name will be displayed alongside your JEMP ID across all public timelines, project workspaces, and contributor rosters.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilterChip(
                                selected = !wantsNameCorrection,
                                onClick = { wantsNameCorrection = false },
                                label = { Text("Use \"${progress.applicantName}\"") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmpireAmber,
                                    selectedLabelColor = Color.Black
                                )
                            )

                            FilterChip(
                                selected = wantsNameCorrection,
                                onClick = { wantsNameCorrection = true },
                                label = { Text("Request Correction") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmpireAmber,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }

                        if (wantsNameCorrection) {
                            OutlinedTextField(
                                value = nameCorrectionNote,
                                onValueChange = { nameCorrectionNote = it },
                                label = { Text("Corrected Full Name / Spelling") },
                                placeholder = { Text("e.g. Alexander Vance") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        Button(
                            onClick = {
                                onConfirmName(!wantsNameCorrection, if (wantsNameCorrection) nameCorrectionNote else null)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmpireAmber, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("CONFIRM NAME & CONTINUE (STEP 1/5)", fontWeight = FontWeight.Black)
                        }
                    }
                }

                // ------------------------------------------------
                // STEP 2: SELECT TIMEZONE
                // ------------------------------------------------
                else if (progress.currentStep == 2) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            "STEP 2: SELECT YOUR WORKING TIMEZONE",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        )

                        Text(
                            "Timelines, standups, and sprint deadlines are coordinated across global timezones. Select your primary region:",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextOffWhite)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            availableTimezones.forEach { (tzKey, tzLabel) ->
                                val isSelected = selectedTz == tzKey
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) EmpireAmber.copy(alpha = 0.15f) else EmpireSurfaceElevated)
                                        .border(
                                            1.dp,
                                            if (isSelected) EmpireAmber else EmpireSurfaceBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedTz = tzKey }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        tzLabel,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = if (isSelected) EmpireAmberLight else TextWhite,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                    if (isSelected) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = EmpireAmber, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = { onSelectTimezone(selectedTz) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmpireAmber, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("CONFIRM TIMEZONE & CONTINUE (STEP 2/5)", fontWeight = FontWeight.Black)
                        }
                    }
                }

                // ------------------------------------------------
                // STEP 3: ACCEPT COMMUNITY RULES
                // ------------------------------------------------
                else if (progress.currentStep == 3) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            "STEP 3: THE JOBLESS EMPIRE RULES",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        )

                        Text(
                            "Review and agree to the 7 core pillars of the Empire:",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextOffWhite)
                        )

                        val rules = listOf(
                            "1. No Mock/Hypothetical Projects" to "Everything built in the Empire is real, functional, and documented.",
                            "2. Ledger & Payout Transparency" to "Client quotations, advances, and splits are documented in public channels.",
                            "3. Anti-Scam Zero Tolerance" to "Fake resumes, payment fraud, and misleading claims result in permanent termination.",
                            "4. Public Thread Communication" to "No private DM project deals. Keeping terms public protects all builders.",
                            "5. Squad Accountability" to "If you accept a milestone, commit to delivery or communicate blockers proactively.",
                            "6. Respectful Collaboration" to "We are builders supporting each other during career transitions.",
                            "7. Build Anyway" to "Momentum is the antidote to waiting for callbacks."
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            rules.forEach { (title, desc) ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(EmpireSurfaceElevated)
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(title, style = MaterialTheme.typography.titleSmall.copy(color = EmpireAmberLight, fontWeight = FontWeight.Bold))
                                    Text(desc, style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp))
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmpireSurfaceElevated)
                                .clickable { rulesAgreed = !rulesAgreed }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Checkbox(
                                checked = rulesAgreed,
                                onCheckedChange = { rulesAgreed = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = EmpireAmber,
                                    checkmarkColor = Color.Black
                                )
                            )
                            Text(
                                "I have read, understood, and accept all 7 Jobless Empire community rules.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextWhite, fontWeight = FontWeight.SemiBold)
                            )
                        }

                        Button(
                            onClick = { onAcceptRules(rulesAgreed) },
                            enabled = rulesAgreed,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmpireAmber,
                                contentColor = Color.Black,
                                disabledContainerColor = EmpireSurfaceBorderLight,
                                disabledContentColor = TextDim
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("ACCEPT RULES & CONTINUE (STEP 3/5)", fontWeight = FontWeight.Black)
                        }
                    }
                }

                // ------------------------------------------------
                // STEP 4: SAFETY ACKNOWLEDGMENT
                // ------------------------------------------------
                else if (progress.currentStep == 4) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            "STEP 4: PROJECT SAFETY & COMMUNICATION CHARTER",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmpireRose.copy(alpha = 0.12f))
                                .border(1.dp, EmpireRose.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                "\"If it isn't in the project thread, it didn't happen.\"\nKeeping all milestone commitments, client agreements, and payout tables in documented public channels prevents disputes and protects contributors.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFFFDA4AF),
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        val safetyPoints = listOf(
                            "Never take client project agreements into private DMs",
                            "All financial advances must flow through documented admin escrow",
                            "Violations follow: Suspension → Investigation → Permanent Ban"
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            safetyPoints.forEach { pt ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = EmpireAmber, modifier = Modifier.size(16.dp))
                                    Text(pt, style = MaterialTheme.typography.bodySmall.copy(color = TextOffWhite))
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmpireSurfaceElevated)
                                .clickable { safetyAgreed = !safetyAgreed }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Checkbox(
                                checked = safetyAgreed,
                                onCheckedChange = { safetyAgreed = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = EmpireAmber,
                                    checkmarkColor = Color.Black
                                )
                            )
                            Text(
                                "I acknowledge that all project communication and payout commitments must remain in public project channels.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextWhite, fontWeight = FontWeight.SemiBold)
                            )
                        }

                        Button(
                            onClick = { onAcknowledgeSafety(safetyAgreed) },
                            enabled = safetyAgreed,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmpireAmber,
                                contentColor = Color.Black,
                                disabledContainerColor = EmpireSurfaceBorderLight,
                                disabledContentColor = TextDim
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("I UNDERSTAND & AGREE (STEP 4/5)", fontWeight = FontWeight.Black)
                        }
                    }
                }

                // ------------------------------------------------
                // STEP 5: MEMBER ID ASSIGNMENT & REVEAL
                // ------------------------------------------------
                else if (progress.currentStep == 5) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "STEP 5: YOUR JEMP IDENTITY IS READY",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = TextWhite
                            )
                        )

                        Text(
                            "Click below to atomically allocate your canonical Member ID in the Timeline Engine:",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextOffWhite, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        )

                        // Member Preview Card
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(EmpireSurfaceElevated)
                                .border(1.dp, EmpireAmber, RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "CANONICAL IDENTITY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = EmpireAmber,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text("STATUS: READY TO ACTIVATE", style = MaterialTheme.typography.labelSmall.copy(color = EmpireEmerald, fontWeight = FontWeight.Bold))
                            }

                            Divider(color = EmpireSurfaceBorder)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Full Name:", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                                    Text(progress.applicantName, style = MaterialTheme.typography.titleMedium.copy(color = TextWhite, fontWeight = FontWeight.Bold))
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Timezone:", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                                    Text(progress.selectedTimezone, style = MaterialTheme.typography.titleSmall.copy(color = EmpireAmberLight, fontWeight = FontWeight.Bold))
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EmpireSurface)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    "✨ Official Initial Timeline Event:\n\"Entered the Empire • Completed onboarding • Verified application email • Stamped on immutable ledger\"",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = TextOffWhite,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = onActivateMembership,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmpireAmber,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Key, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ALLOCATE JEMP# ID & ACTIVATE MEMBERSHIP", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}
