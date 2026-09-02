package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiscordPersonaType
import com.example.data.model.MembershipState
import com.example.data.repository.JoblessEmpireRepository
import com.example.ui.theme.*

enum class DiscordChannelType(val channelName: String, val category: String, val badge: String, val requiresMemberRole: Boolean = true, val adminOnly: Boolean = false) {
    WELCOME("welcome", "ONBOARDING GATEWAY", "GATE", requiresMemberRole = false),
    APPLICATIONS("applications", "ADMINISTRATIVE", "ADMIN", requiresMemberRole = false, adminOnly = true),
    GENERAL("general-hq", "COMMUNITY", "Active"),
    BRAINSTORMING("brainstorming-club", "WORKSPACES", "B#007"),
    PROJECTS("ongoing-projects", "WORKSPACES", "P#003"),
    CLIENT_PROJECTS("client-opportunities", "REVENUE", "HC#004"),
    PAYROLL_ESCROW("payroll-transparency", "REVENUE", "PAY#"),
    TIMELINE_LOGS("timeline-attestations", "PROOF OF WORK", "JEMP#"),
    VOICE_SYNC("Sprint Standup Room", "VOICE & VIDEO", "LIVE")
}

data class DiscordMessage(
    val authorName: String,
    val authorTag: String, // e.g. "JEMP#014"
    val authorRole: String,
    val timestamp: String,
    val content: String,
    val linkedId: String? = null,
    val isSystemBot: Boolean = false
)

@Composable
fun DiscordWorkplaceSimulation(
    repository: JoblessEmpireRepository,
    modifier: Modifier = Modifier,
    onNavigateToTimeline: () -> Unit = {}
) {
    val activePersona by repository.activePersona.collectAsState()
    val onboardingProgress by repository.onboardingProgress.collectAsState()
    val applicationsList by repository.allApplicationsList.collectAsState()
    val dashboardMetrics by repository.onboardingDashboard.collectAsState()
    val botTerminalOutput by repository.botTerminalOutput.collectAsState()
    val memberCard by repository.memberCard.collectAsState()

    var selectedChannel by remember { mutableStateOf(DiscordChannelType.WELCOME) }
    var commandInput by remember { mutableStateOf("") }
    var showMemberCardDialog by remember { mutableStateOf(false) }

    // Auto-route to Welcome for unverified persona, or stay on channel
    LaunchedEffect(activePersona) {
        if (activePersona == DiscordPersonaType.APPROVED_APPLICANT || activePersona == DiscordPersonaType.UNKNOWN_JOINER) {
            selectedChannel = DiscordChannelType.WELCOME
        } else if (activePersona == DiscordPersonaType.ADMINISTRATOR) {
            selectedChannel = DiscordChannelType.APPLICATIONS
        }
    }

    val isPersonaMember = onboardingProgress?.membershipState == MembershipState.ACTIVE || activePersona == DiscordPersonaType.ACTIVE_MEMBER
    val isPersonaAdmin = activePersona == DiscordPersonaType.ADMINISTRATOR

    val channelMessages = remember(selectedChannel) {
        when (selectedChannel) {
            DiscordChannelType.WELCOME -> emptyList()
            DiscordChannelType.APPLICATIONS -> emptyList()
            DiscordChannelType.GENERAL -> listOf(
                DiscordMessage(
                    "EmpireBot", "SYSTEM", "Automated Attestation", "Today at 09:15",
                    "🚀 Welcome JEMP#072 to Jobless Empire! Onboarding checklist completed.",
                    isSystemBot = true
                ),
                DiscordMessage(
                    "Marcus Chen", "JEMP#014", "Product Lead", "Today at 09:22",
                    "Welcome! Reminder to all members: if you want to join an active brainstorming squad, submit your proof-of-work link directly in the thread."
                ),
                DiscordMessage(
                    "Alex Vance", "JEMP#001", "Core Dev", "Today at 09:40",
                    "Reminder: All project sprint notes are automatically reflected on your verified timeline after meeting logs are submitted."
                )
            )
            DiscordChannelType.BRAINSTORMING -> listOf(
                DiscordMessage(
                    "EmpireBot", "SYSTEM", "Laboratory Bot", "Yesterday at 14:00",
                    "💡 New Brainstorm Session Opened: [B#007] AI-Powered Resume Intelligence. Status: COOKING.",
                    linkedId = "B#007",
                    isSystemBot = true
                ),
                DiscordMessage(
                    "Marcus Chen", "JEMP#014", "Author / Lead", "Yesterday at 14:15",
                    "We need 1 frontend specialist and 1 ML inference engineer for B#007. Application review closes tonight.",
                    linkedId = "B#007"
                ),
                DiscordMessage(
                    "Alex Vance", "JEMP#001", "Frontend Contributor", "Yesterday at 15:30",
                    "Submitted application with timeline proof JEMP#001. Ready to build the Compose UI tokens.",
                    linkedId = "B#007"
                ),
                DiscordMessage(
                    "Marcus Chen", "JEMP#014", "Author / Lead", "Yesterday at 17:00",
                    "Accepted @Alex Vance (JEMP#001). Promoting B#007 → Project P#003! Discussion moved to private workspace.",
                    linkedId = "P#003"
                )
            )
            DiscordChannelType.PROJECTS -> listOf(
                DiscordMessage(
                    "EmpireBot", "SYSTEM", "Sprint Bot", "Today at 10:00",
                    "⚡ Project Status Update [P#003]: Milestone 2 'Design Tokens & UI Tree' marked COMPLETED by JEMP#001.",
                    linkedId = "P#003",
                    isSystemBot = true
                ),
                DiscordMessage(
                    "Alex Vance", "JEMP#001", "Frontend Contributor", "Today at 10:30",
                    "Pull request #14 merged. Milestone logged and verified in project thread.",
                    linkedId = "P#003"
                ),
                DiscordMessage(
                    "Marcus Chen", "JEMP#014", "Project Lead", "Today at 11:00",
                    "Great velocity squad. Next milestone is the OCR parser bridge. Standup in voice room at 14:00.",
                    linkedId = "P#003"
                )
            )
            DiscordChannelType.CLIENT_PROJECTS -> listOf(
                DiscordMessage(
                    "EmpireBot", "SYSTEM", "Escrow Bot", "01 SEP 2026",
                    "💼 Verified Opportunity [HC#004]: $4,500 Client Scope. Revenue Split: 5% Admin ($225) • 95% Contributor Pool ($4,275).",
                    linkedId = "HC#004",
                    isSystemBot = true
                ),
                DiscordMessage(
                    "David Miller", "JEMP#009", "Project Owner", "01 SEP 2026",
                    "Posting client agreement and milestone breakdown in thread. Milestone 1 advance is secured.",
                    linkedId = "HC#004"
                ),
                DiscordMessage(
                    "EmpireBot", "SYSTEM", "Escrow Bot", "02 SEP 2026",
                    "🔒 Milestone 1 advance received ($1,500). Contributor allocation unlocked. Work converted HC#004 → P#002.",
                    linkedId = "P#002",
                    isSystemBot = true
                )
            )
            DiscordChannelType.PAYROLL_ESCROW -> listOf(
                DiscordMessage(
                    "PayrollBot", "SYSTEM", "Financial Bot", "Today at 09:00",
                    "💳 Financial Stage Initialized: [PAY#001] Client Advance Payment (₹30,000 INR). Quorum: 3/3 Approvals.",
                    linkedId = "PAY#001",
                    isSystemBot = true
                ),
                DiscordMessage(
                    "Alex Vance", "JEMP#014", "Project Owner", "Today at 09:30",
                    "Uploaded verified banking statement & tax invoice for PAY#001. Required transfer to Admin: ₹18,000 (Gross - Owner 40%).",
                    linkedId = "PAY#001"
                ),
                DiscordMessage(
                    "Sarah Connor", "JEMP#022", "Frontend Contributor", "Today at 09:45",
                    "Verified proof matches bank UTR. Distribution approved: Sarah (35% = ₹9,975 INR), John (25% = ₹7,125 INR).",
                    linkedId = "PAY#001"
                ),
                DiscordMessage(
                    "EmpireAdmin", "JEMP#001", "Administrator", "Today at 10:15",
                    "✅ Verified owner transfer receipt ₹18,000. Initiating contributor payouts and recording 5% admin fee (₹1,500) to immutable ledger.",
                    linkedId = "PAY#001",
                    isSystemBot = true
                )
            )
            DiscordChannelType.TIMELINE_LOGS -> listOf(
                DiscordMessage(
                    "EmpireBot", "SYSTEM", "Proof-of-Work Log", "Just now",
                    "📜 Automatic Timeline Record: JEMP#001 recorded 'Sprint 3 Standup Participation [P#003]'. Non-deletable hash stamped.",
                    linkedId = "JEMP#001",
                    isSystemBot = true
                ),
                DiscordMessage(
                    "EmpireBot", "SYSTEM", "Proof-of-Work Log", "15 mins ago",
                    "🔒 Member JEMP#014 generated 24-Hour Public Proof Link (Expires in 23h 45m).",
                    linkedId = "JEMP#014",
                    isSystemBot = true
                )
            )
            DiscordChannelType.VOICE_SYNC -> listOf(
                DiscordMessage(
                    "EmpireBot", "SYSTEM", "Voice Activity Logger", "Active Now",
                    "🎙️ Voice Room: Sprint Standup (4 Members connected). Activity logged on member timelines under P#003.",
                    linkedId = "P#003",
                    isSystemBot = true
                )
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(EmpireSurface)
            .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(16.dp))
    ) {
        // ----------------------------------------------------
        // 1. PERSONA SWITCHER BAR
        // ----------------------------------------------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EmpireSurfaceElevated)
                .border(1.dp, EmpireSurfaceBorder)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmpireAmber))
                    Text(
                        "DISCORD SIMULATOR // ACTIVE USER PERSONA:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = EmpireAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }

                Text(
                    "Switch persona to test role gating",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextDim, fontSize = 9.sp)
                )
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(DiscordPersonaType.values()) { persona ->
                    val isSelected = activePersona == persona
                    val color = when (persona) {
                        DiscordPersonaType.APPROVED_APPLICANT -> EmpireAmber
                        DiscordPersonaType.UNKNOWN_JOINER -> TextMuted
                        DiscordPersonaType.ACTIVE_MEMBER -> EmpireEmerald
                        DiscordPersonaType.ACTIVE_MEMBER_2 -> Color(0xFF38BDF8)
                        DiscordPersonaType.SUSPENDED_MEMBER -> EmpireRose
                        DiscordPersonaType.ADMIN_USER -> EmpireCyan
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) color.copy(alpha = 0.2f) else EmpireSurface)
                            .border(1.dp, if (isSelected) color else EmpireSurfaceBorder, RoundedCornerShape(6.dp))
                            .clickable { repository.selectDiscordPersona(persona) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                persona.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) color else TextMuted,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                "(${persona.roleDescription})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextDim,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // ----------------------------------------------------
        // 2. DISCORD WINDOW TITLEBAR
        // ----------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EmpireSurfaceCard)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(EmpireRose))
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(EmpireAmber))
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(EmpireEmerald))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "JOBLESS EMPIRE DISCORD // SERVER OS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isPersonaMember || isPersonaAdmin) EmpireEmerald else EmpireAmber)
                )
                Text(
                    if (isPersonaAdmin) "@Admin" else if (isPersonaMember) "@Member" else "@Unverified",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = if (isPersonaMember || isPersonaAdmin) EmpireEmerald else EmpireAmber,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // ----------------------------------------------------
        // 3. CHANNEL SELECTOR PILLS
        // ----------------------------------------------------
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(EmpireSurface)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(DiscordChannelType.values()) { channel ->
                val isSelected = selectedChannel == channel
                val isLocked = (channel.requiresMemberRole && !isPersonaMember && !isPersonaAdmin) ||
                        (channel.adminOnly && !isPersonaAdmin)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) EmpireDiscordIndigo.copy(alpha = 0.25f) else Color.Transparent)
                        .border(
                            1.dp,
                            if (isSelected) EmpireDiscordIndigo else EmpireSurfaceBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedChannel = channel }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (isLocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = EmpireRose,
                                modifier = Modifier.size(12.dp)
                            )
                        } else {
                            Text(
                                "#",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) EmpireAmber else TextDim,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Text(
                            channel.channelName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) TextWhite else if (isLocked) TextDim else TextMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        Divider(color = EmpireSurfaceBorder)

        // ----------------------------------------------------
        // 4. MAIN CHANNEL CONTENT AREA
        // ----------------------------------------------------
        when {
            selectedChannel == DiscordChannelType.WELCOME -> {
                OnboardingWelcomeView(
                    progress = onboardingProgress,
                    onVerifyEmail = { email ->
                        repository.verifyEmail(email)
                    },
                    onConfirmName = { useSubmitted, note ->
                        repository.confirmNameStep(useSubmitted, note)
                    },
                    onSelectTimezone = { tz ->
                        repository.selectTimezoneStep(tz)
                    },
                    onAcceptRules = { agree ->
                        repository.acceptRulesStep(agree)
                    },
                    onAcknowledgeSafety = { agree ->
                        repository.acknowledgeSafetyStep(agree)
                    },
                    onActivateMembership = {
                        repository.activateMembershipStep()
                    },
                    onNavigateToTimeline = onNavigateToTimeline,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 360.dp, max = 540.dp)
                )
            }
            selectedChannel == DiscordChannelType.APPLICATIONS -> {
                if (isPersonaAdmin) {
                    OnboardingAdminChannelView(
                        applications = applicationsList,
                        dashboardMetrics = dashboardMetrics,
                        onApprove = { appId -> repository.adminApproveApplication(appId) },
                        onReject = { appId -> repository.adminRejectApplication(appId) },
                        onWaitlist = { appId -> repository.adminWaitlistApplication(appId) },
                        onRevoke = { appId -> repository.adminRevokeApplication(appId) },
                        onWhitelistDirect = { email, name -> repository.adminWhitelistEmail(email, name) },
                        onRunSlashCommand = { cmd -> repository.executeSlashCommand(cmd) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 360.dp, max = 540.dp)
                    )
                } else {
                    LockedChannelBanner(
                        channelName = "applications",
                        requiredRole = "@Admin",
                        reason = "This channel contains confidential applicant PII and is restricted to administrators."
                    )
                }
            }
            selectedChannel == DiscordChannelType.BRAINSTORMING -> {
                BrainstormingClubConsole(
                    repository = repository,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 520.dp, max = 680.dp)
                )
            }
            selectedChannel.requiresMemberRole && !isPersonaMember && !isPersonaAdmin -> {
                LockedChannelBanner(
                    channelName = selectedChannel.channelName,
                    requiredRole = "@Member",
                    reason = "Access locked. Please complete the email verification and onboarding in #welcome."
                )
            }
            else -> {
                // Regular Workspace Messages Feed
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    channelMessages.forEach { msg ->
                        DiscordMessageItem(msg)
                    }
                }
            }
        }

        // ----------------------------------------------------
        // 5. BOT TERMINAL FEEDBACK CARD (If available)
        // ----------------------------------------------------
        if (botTerminalOutput != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EmpireSurfaceElevated)
                    .border(1.dp, EmpireAmber.copy(alpha = 0.5f))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🤖", fontSize = 14.sp)
                        Text(
                            "BOT RESPONSE (EPHEMERAL):",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = EmpireAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                    Text(
                        "Visible only to you",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextDim, fontSize = 9.sp)
                    )
                }
                Text(
                    botTerminalOutput!!,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextWhite,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                )
            }
        }

        // ----------------------------------------------------
        // 6. INTERACTIVE SLASH COMMAND BAR
        // ----------------------------------------------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EmpireSurfaceCard)
                .border(1.dp, EmpireSurfaceBorder)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Suggestion chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val chips = listOf(
                    "/membership",
                    "/timeline",
                    "/timeline stats",
                    "/payroll me",
                    "/payroll project P#014",
                    "/payroll-admin dashboard",
                    "/payroll-admin reconcile P#014",
                    "/payroll help",
                    "/brainstorm browse"
                )
                items(chips) { chip ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(EmpireSurfaceElevated)
                            .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(4.dp))
                            .clickable {
                                commandInput = chip
                                repository.executeSlashCommand(chip)
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            chip,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = EmpireAmberLight,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Command input bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = commandInput,
                    onValueChange = { commandInput = it },
                    placeholder = { Text("Type a slash command (e.g. /membership, /timeline)...", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmpireAmber,
                        unfocusedBorderColor = EmpireSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextOffWhite
                    )
                )

                Button(
                    onClick = {
                        if (commandInput.isNotBlank()) {
                            repository.executeSlashCommand(commandInput)
                            commandInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmpireAmber, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun LockedChannelBanner(
    channelName: String,
    requiredRole: String,
    reason: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(EmpireRose.copy(alpha = 0.15f))
                .border(1.dp, EmpireRose.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = EmpireRose,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            "#$channelName IS LOCKED",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = TextWhite
            )
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(EmpireSurfaceElevated)
                .border(1.dp, EmpireAmber.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                "Requires $requiredRole Role",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = EmpireAmber,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Text(
            reason,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        )
    }
}

@Composable
fun DiscordMessageItem(msg: DiscordMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (msg.isSystemBot) EmpireDiscordIndigoBg.copy(alpha = 0.4f) else EmpireSurfaceElevated)
            .border(
                1.dp,
                if (msg.isSystemBot) EmpireDiscordIndigo.copy(alpha = 0.3f) else EmpireSurfaceBorderLight.copy(alpha = 0.5f),
                RoundedCornerShape(8.dp)
            )
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (msg.isSystemBot) EmpireDiscordIndigo else EmpireAmberDark),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (msg.isSystemBot) "BOT" else msg.authorName.take(1),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 10.sp
                )
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    msg.authorName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (msg.isSystemBot) EmpireCyan else TextWhite
                    )
                )
                IdBadge(msg.authorTag)
                Text(
                    msg.timestamp,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextDim,
                        fontSize = 10.sp
                    )
                )
            }
            Text(
                msg.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextOffWhite,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            )
        }
    }
}
