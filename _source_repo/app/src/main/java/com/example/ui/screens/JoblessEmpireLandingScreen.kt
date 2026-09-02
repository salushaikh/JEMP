package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.*
import com.example.data.repository.JoblessEmpireRepository
import com.example.data.sample.JoblessEmpireData
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun JoblessEmpireLandingScreen(
    repository: JoblessEmpireRepository,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    // State bindings
    val brainstorms by repository.brainstorms.collectAsState()
    val projects by repository.projects.collectAsState()
    val clientProjects by repository.clientProjects.collectAsState()
    val memberProfiles by repository.memberProfiles.collectAsState()
    val selectedMemberId by repository.selectedMemberId.collectAsState()
    val isSubmitting by repository.isSubmitting.collectAsState()
    val latestSubmission by repository.latestSubmittedApplication.collectAsState()
    val errorMessage by repository.submissionErrorMessage.collectAsState()
    val simulatedBudget by repository.simulatedBudget.collectAsState()
    val isCharityModel by repository.isCharityModelActive.collectAsState()

    val currentProfile = memberProfiles[selectedMemberId] ?: memberProfiles.values.first()

    // Dialog state
    var adminPreviewSubmission by remember { mutableStateOf<ApplicationSubmission?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showAddMilestoneDialog by remember { mutableStateOf(false) }
    var activeWorkspaceTab by remember { mutableStateOf(0) } // 0: Brainstorms, 1: Projects, 2: Client Projects
    var selectedNavSection by remember { mutableStateOf(NavSection.OVERVIEW) }

    // Scroll targets
    val overviewIndex = 0
    val philosophyIndex = 2
    val rolesIndex = 3
    val howItWorksIndex = 4
    val discordIndex = 5
    val workspacesIndex = 6
    val timelineIndex = 7
    val revenueIndex = 9
    val safetyIndex = 11
    val expectationsIndex = 13
    val applyIndex = 14
    val faqIndex = 15

    fun scrollToSection(section: NavSection) {
        selectedNavSection = section
        coroutineScope.launch {
            val targetIdx = when (section) {
                NavSection.OVERVIEW -> overviewIndex
                NavSection.HOW_IT_WORKS -> howItWorksIndex
                NavSection.INSIDE -> workspacesIndex
                NavSection.TIMELINE -> timelineIndex
                NavSection.REVENUE -> revenueIndex
                NavSection.SAFETY -> safetyIndex
                NavSection.FAQ -> faqIndex
                NavSection.APPLY -> applyIndex
            }
            scrollState.animateScrollToItem(targetIdx)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = EmpireBackground,
        topBar = {
            JoblessTopBar(
                selectedSection = selectedNavSection,
                onNavClick = { scrollToSection(it) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(36.dp)
        ) {
            // ----------------------------------------------------
            // 1. HERO SECTION
            // ----------------------------------------------------
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    SectionEyebrow(text = "THE PRIVATE BUILDER COMMUNITY")

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "JOBLESS DOESN'T MEAN USELESS.",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = TextWhite,
                                letterSpacing = (-1.2).sp
                            )
                        )
                        Text(
                            text = "Build something anyway.",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = EmpireAmber,
                                letterSpacing = (-0.8).sp
                            )
                        )
                    }

                    Text(
                        text = "Jobless Empire is a private community for job seekers, unemployed professionals, builders and creators who refuse to sit still.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            color = TextOffWhite
                        )
                    )

                    // CTAs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { scrollToSection(NavSection.APPLY) },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmpireAmber,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "APPLY TO JOIN",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp
                                )
                            )
                        }

                        OutlinedButton(
                            onClick = { scrollToSection(NavSection.HOW_IT_WORKS) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TextWhite
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmpireSurfaceBorderLight),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "SEE HOW IT WORKS",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    // Disclaimer Underneath
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Private community • Manually reviewed applications • No guaranteed employment or income",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextDim,
                                fontSize = 11.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        )
                    }

                    // Hero Network Abstract Visual Node Ecosystem
                    HeroNetworkVisual(modifier = Modifier.fillMaxWidth())
                }
            }

            // ----------------------------------------------------
            // 2. PHILOSOPHY SECTION
            // ----------------------------------------------------
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(EmpireSurface)
                        .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionEyebrow(text = "CORE PHILOSOPHY")

                    Text(
                        "NO JOB. STILL BUILDING.",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                    )

                    Text(
                        "Losing a job shouldn't mean losing momentum.\n\nJobless Empire gives people a place to keep learning, collaborating, building and creating tangible proof of what they're capable of while they search for their next opportunity.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = TextOffWhite,
                            lineHeight = 24.sp
                        )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmpireAmber.copy(alpha = 0.1f))
                            .border(1.dp, EmpireAmber.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            "\"Your employment status is temporary. Your work can outlive it.\"",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmpireAmberLight,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        )
                    }

                    // Three Cards: BUILD, PROVE, EARN
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // BUILD
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmpireSurfaceElevated)
                                .border(1.dp, EmpireSurfaceBorderLight, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("BUILD", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = EmpireAmber))
                            Text(
                                "Work with other ambitious people on ideas, experiments and real projects.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, lineHeight = 16.sp)
                            )
                        }

                        // PROVE
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmpireSurfaceElevated)
                                .border(1.dp, EmpireSurfaceBorderLight, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("PROVE", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = EmpireCyan))
                            Text(
                                "Document your contributions through a permanent personal timeline.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, lineHeight = 16.sp)
                            )
                        }

                        // EARN
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmpireSurfaceElevated)
                                .border(1.dp, EmpireSurfaceBorderLight, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("EARN", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = EmpireEmerald))
                            Text(
                                "Participate in paid client projects when selected by project owners.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, lineHeight = 16.sp)
                            )
                        }
                    }
                }
            }

            // ----------------------------------------------------
            // 3. WHO BELONGS HERE?
            // ----------------------------------------------------
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionEyebrow(text = "MEMBERSHIP")

                    Text(
                        "WHO BELONGS HERE?",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                    )

                    Text(
                        "You don't need to be unemployed forever. You just need to be willing to build while you're here. Every applicant is manually reviewed.",
                        style = MaterialTheme.typography.bodyLarge.copy(color = TextOffWhite)
                    )

                    // Role Cards Grid
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        JoblessEmpireData.builderRoles.chunked(2).forEach { rowRoles ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowRoles.forEach { role ->
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(EmpireSurface)
                                            .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(12.dp))
                                            .padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            role.title,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextWhite
                                            )
                                        )
                                        Text(
                                            role.description,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextMuted,
                                                lineHeight = 16.sp
                                            )
                                        )
                                        // Skill pills
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.padding(top = 4.dp)
                                        ) {
                                            role.skills.take(2).forEach { skill ->
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(EmpireSurfaceElevated)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        skill,
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontSize = 9.sp,
                                                            color = TextOffWhite
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ----------------------------------------------------
            // 4. HOW THE EMPIRE WORKS (4-Step Process)
            // ----------------------------------------------------
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(EmpireSurface)
                        .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionEyebrow(text = "THE RECRUITMENT & ONBOARDING PIPELINE")

                    Text(
                        "HOW THE EMPIRE WORKS",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                    )

                    // Visual Flow Bar: APPLICATION -> REVIEW -> DISCORD -> BUILD
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmpireSurfaceElevated)
                            .padding(10.dp)
                    ) {
                        Text(
                            "APPLICATION → REVIEW → DISCORD → BUILD",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = EmpireAmber,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    val steps = listOf(
                        Triple("01 — APPLY", "Submit your basic information, resume and confirmation that you're genuinely currently jobless/unemployed.", EmpireAmber),
                        Triple("02 — GET REVIEWED", "Every application is manually reviewed by the administrator.", EmpireCyan),
                        Triple("03 — ENTER THE EMPIRE", "Approved members receive access to the private Discord server and complete onboarding.", EmpireDiscordIndigo),
                        Triple("04 — START BUILDING", "Join brainstorms, projects, client work, conversations and collaborations.", EmpireEmerald)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        steps.forEach { (stepTitle, stepDesc, stepColor) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(EmpireSurfaceElevated)
                                    .border(1.dp, stepColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .offset(y = 4.dp)
                                        .clip(CircleShape)
                                        .background(stepColor)
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Text(
                                        stepTitle,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = stepColor
                                        )
                                    )
                                    Text(
                                        stepDesc,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = TextOffWhite,
                                            lineHeight = 18.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ----------------------------------------------------
            // 5. THE DISCORD WORKSPACE OS
            // ----------------------------------------------------
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SectionEyebrow(text = "THE PRIVATE DISCORD WORKSPACE")

                    Text(
                        "DISCORD IS THE WORKPLACE.\nNOT JUST A CHATROOM.",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                    )

                    Text(
                        "Inside Jobless Empire, Discord functions as our real operating system. Real projects, applications, voice sprint syncs, escrow updates, and proof-of-work records live inside dedicated channels.",
                        style = MaterialTheme.typography.bodyLarge.copy(color = TextOffWhite)
                    )

                    DiscordWorkplaceSimulation(
                        repository = repository,
                        modifier = Modifier.fillMaxWidth(),
                        onNavigateToTimeline = { scrollToSection(NavSection.TIMELINE) }
                    )
                }
            }

            // ----------------------------------------------------
            // 6. THREE WAYS TO BUILD (Workspaces: Brainstorms, Projects, Client Work)
            // ----------------------------------------------------
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionEyebrow(text = "CORE WORKSPACES")

                    Text(
                        "THREE WAYS TO BUILD",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                    )

                    // Tab selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("A. Brainstorming (B#)", "B. Ongoing (P#)", "C. Client Projects (HC#)").forEachIndexed { index, tabTitle ->
                            val isSelected = activeWorkspaceTab == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) EmpireAmber else EmpireSurface)
                                    .border(
                                        1.dp,
                                        if (isSelected) EmpireAmber else EmpireSurfaceBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { activeWorkspaceTab = index }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    tabTitle,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Color.Black else TextOffWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    // Workspace Content Display
                    when (activeWorkspaceTab) {
                        0 -> {
                            // BRAINSTORMING CLUB (B#) - Interactive Bot Console
                            BrainstormingClubConsole(
                                repository = repository,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 560.dp, max = 760.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.dp, StatusBrainstorming.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            )
                        }
                        1 -> {
                            // ONGOING PROJECTS (P#)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(EmpireSurface)
                                    .border(1.dp, StatusInProgress.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        IdBadge("P#001")
                                        Text("ONGOING PROJECTS", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = TextWhite))
                                    }
                                    Text("FROM IDEA TO EXECUTION", style = MaterialTheme.typography.labelSmall.copy(color = StatusInProgress, fontFamily = FontFamily.Monospace))
                                }

                                Text(
                                    "Projects are where brainstorming becomes actual work. Every status change requires a logged reason. Milestones and meeting attendance are permanently verified.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = TextOffWhite)
                                )

                                Divider(color = EmpireSurfaceBorder)

                                projects.forEach { proj ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(EmpireSurfaceElevated)
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
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
                                                IdBadge(proj.id)
                                                Text(proj.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextWhite))
                                            }
                                            ProjectStatusBadge(proj.status)
                                        }

                                        Text(proj.description, style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))

                                        if (proj.statusReason != null) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(EmpireBackground)
                                                    .padding(8.dp)
                                            ) {
                                                Text(
                                                    "STATUS REASON: ${proj.statusReason}",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontFamily = FontFamily.Monospace,
                                                        color = EmpireAmberLight,
                                                        fontSize = 10.sp
                                                    )
                                                )
                                            }
                                        }

                                        Text("Contributors: ${proj.contributors.joinToString(", ")}", style = MaterialTheme.typography.labelSmall.copy(color = TextDim))
                                    }
                                }
                            }
                        }
                        2 -> {
                            // CLIENT PROJECTS (HC#)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(EmpireSurface)
                                    .border(1.dp, StatusClientProject.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        IdBadge("HC#001")
                                        Text("CLIENT PROJECTS", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = TextWhite))
                                    }
                                    Text("HC# → P# Opportunity Flow", style = MaterialTheme.typography.labelSmall.copy(color = StatusClientProject, fontFamily = FontFamily.Monospace))
                                }

                                Text(
                                    "\"This is where real opportunities enter the Empire.\" Verified freelance & client scopes with defined revenue shares, required skills, and admin escrow.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = TextOffWhite)
                                )

                                Divider(color = EmpireSurfaceBorder)

                                clientProjects.forEach { cp ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(EmpireSurfaceElevated)
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
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
                                                IdBadge(cp.id)
                                                Text(cp.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextWhite))
                                            }
                                            ClientProjectStatusBadge(cp.status)
                                        }

                                        Text(cp.description, style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Budget: $${cp.budgetUsd} USD • Timeline: ${cp.expectedTimeline}", style = MaterialTheme.typography.labelSmall.copy(color = EmpireEmerald, fontWeight = FontWeight.Bold))
                                            Text(cp.revShareSplit, style = MaterialTheme.typography.labelSmall.copy(color = TextDim, fontSize = 10.sp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ----------------------------------------------------
            // 7. THE TIMELINE SYSTEM (Proof of Work & Discord Bot OS)
            // ----------------------------------------------------
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionEyebrow(text = "PROOF OF WORK ATTESTATION & DISCORD BOT")

                    Text(
                        "YOUR WORK SHOULD LEAVE A TRAIL.",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                    )

                    Text(
                        "Every member gets a personal proof-of-work timeline from the moment they enter the Empire. Powered by the Jobless Empire Discord Timeline Bot with Room SQLite persistence, slash command execution, complexity-based navigation, and 24-hour cryptographic proof links.",
                        style = MaterialTheme.typography.bodyLarge.copy(color = TextOffWhite)
                    )

                    // Interactive Discord Timeline Bot Console
                    DiscordBotTimelineConsole(
                        repository = repository,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ----------------------------------------------------
            // 8. DISCORD MEETINGS & STANDUPS
            // ----------------------------------------------------
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(EmpireSurface)
                        .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SectionEyebrow(text = "VOICE & SPRINT LOGS")

                    Text(
                        "DISCORD MEETINGS TIED TO PROOF",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                    )

                    Text(
                        "Teams conduct voice and video syncs directly in Discord. Relevant participation and architecture decisions are recorded on member timelines with the associated project identifier (e.g. P#008 — Team Meeting).",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextOffWhite)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmpireSurfaceElevated)
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = EmpireCyan)
                            Text(
                                "P#008 — Team Meeting: \"Participated in project planning and API schema session.\"",
                                style = MaterialTheme.typography.bodySmall.copy(color = EmpireCyan, fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }
            }

            // ----------------------------------------------------
            // 9. REVENUE SHARING & SIMULATOR
            // ----------------------------------------------------
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionEyebrow(text = "TRANSPARENT ECONOMICS")

                    Text(
                        "WHEN THE EMPIRE EARNS, CONTRIBUTORS GET PAID.",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                    )

                    Text(
                        "Paid client projects use a strict revenue-sharing model: 5% Admin Fee • 95% Available for Project Distribution. The goal isn't identical payouts — it's fair payouts based on meaningful contribution.",
                        style = MaterialTheme.typography.bodyLarge.copy(color = TextOffWhite)
                    )

                    RevenueCalculator(
                        simulatedBudget = simulatedBudget,
                        isCharityModel = isCharityModel,
                        onBudgetChange = { repository.setSimulatedBudget(it) },
                        onToggleCharity = { repository.toggleCharityModel(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ----------------------------------------------------
            // 10. PAYMENT TRANSPARENCY
            // ----------------------------------------------------
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(EmpireSurface)
                        .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SectionEyebrow(text = "FINANCIAL INTEGRITY")

                    Text(
                        "NO BLACK-BOX MONEY.",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                    )

                    Text(
                        "Client-project owners document all quotations, agreed charges, advances, milestones, received payments, and contributor distribution tables directly in the project workspace. The administrator executes contributor payouts with complete ledger transparency.",
                        style = MaterialTheme.typography.bodyLarge.copy(color = TextOffWhite)
                    )

                    val ledgerPoints = listOf(
                        "Client quotation & milestone schedule verified upfront",
                        "Agreed charges & advances posted in Discord project channel",
                        "Project owner transfers project balance to admin escrow",
                        "Admin initiates transparent payouts to all squad contributors"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ledgerPoints.forEach { pt ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmpireEmerald, modifier = Modifier.size(16.dp))
                                Text(pt, style = MaterialTheme.typography.bodyMedium.copy(color = TextOffWhite))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Embedded Interactive Payroll Console
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 680.dp, max = 800.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, DiscordBlurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    ) {
                        PayrollConsole(
                            repository = repository,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // ----------------------------------------------------
            // 11. ANTI-SCAM / SAFETY & DISPUTE RESOLUTION
            // ----------------------------------------------------
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(EmpireSurface)
                        .border(1.dp, EmpireRose.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionEyebrow(text = "SAFETY & ANTI-SCAM POLICY", color = EmpireRose)

                    Text(
                        "IF IT ISN'T IN THE PROJECT THREAD, IT DIDN'T HAPPEN.",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
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
                            "\"Don't take project commitments into DMs.\"\nKeeping all agreements, milestones, and payout terms in public project threads creates undeniable evidence and protects contributors.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFFDA4AF),
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    // Zero Tolerance Rules
                    Text(
                        "ZERO TOLERANCE FOR:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = EmpireRose
                        )
                    )

                    val zeroToleranceList = listOf(
                        "Scams & payment fraud",
                        "Fake employment claims & fraudulent resumes",
                        "Spam, manipulation & harassment",
                        "Intentionally misleading project milestones"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        zeroToleranceList.forEach { rule ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Cancel, contentDescription = null, tint = EmpireRose, modifier = Modifier.size(16.dp))
                                Text(rule, style = MaterialTheme.typography.bodyMedium.copy(color = TextOffWhite))
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmpireSurfaceElevated)
                            .padding(10.dp)
                    ) {
                        Text(
                            "Disciplinary Pathway: Suspension → Investigation → Permanent Ban",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = EmpireAmber,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // ----------------------------------------------------
            // 12. CHARITY EVOLUTION (Future Governance)
            // ----------------------------------------------------
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(EmpireSurface)
                        .border(1.dp, EmpireViolet.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SectionEyebrow(text = "FUTURE COMMUNITY GOVERNANCE", color = EmpireViolet)

                    Text(
                        "ONE DAY, THE EMPIRE GIVES BACK.",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                    )

                    Text(
                        "As the community expands, our goal is to evolve the fee structure to: 4% Admin • 1% Community Charity • 95% Project Distribution. The 1% charity pool will be democratically allocated to humanitarian causes voted on by community members.",
                        style = MaterialTheme.typography.bodyLarge.copy(color = TextOffWhite)
                    )

                    val stepsCharity = listOf(
                        "1. Members submit worthy causes & initiatives",
                        "2. The community reviews impact and urgency",
                        "3. Members vote and rank allocations",
                        "4. Accumulated charity funds are disbursed publicly",
                        "5. Contributing builders receive public philanthropic recognition"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        stepsCharity.forEach { st ->
                            Text(st, style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                        }
                    }
                }
            }

            // ----------------------------------------------------
            // 13. WHAT YOU ACTUALLY GET (Momentum) & IMPORTANT EXPECTATIONS
            // ----------------------------------------------------
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionEyebrow(text = "VALUE PROPOSITION")

                    Text(
                        "YOU MAY NOT GET A JOB.\nYOU MIGHT GET SOMETHING BETTER: MOMENTUM.",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                    )

                    val valueCards = listOf(
                        Triple("REAL PROJECT EXPERIENCE", "Build production-grade systems instead of waiting endlessly for callbacks.", Icons.Default.Code),
                        Triple("PROOF OF WORK", "Immutable timeline records verifying your code, design, or writing contributions.", Icons.Default.Verified),
                        Triple("COLLABORATION", "Ship collaboratively with designers, marketers, devs, and product managers.", Icons.Default.Group),
                        Triple("PORTFOLIO MATERIAL", "Transform idle months into verifiable GitHub repos and live products.", Icons.Default.FolderSpecial),
                        Triple("POTENTIAL INCOME", "Participate in paid client projects when selected by squad leads.", Icons.Default.Payments),
                        Triple("PEER NETWORK", "Connect with ambitious builders navigating career transitions together.", Icons.Default.Hub)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        valueCards.chunked(2).forEach { rowCards ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowCards.forEach { (title, desc, icon) ->
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(EmpireSurface)
                                            .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(12.dp))
                                            .padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(imageVector = icon, contentDescription = null, tint = EmpireAmber, modifier = Modifier.size(22.dp))
                                        Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextWhite))
                                        Text(desc, style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, lineHeight = 16.sp))
                                    }
                                }
                            }
                        }
                    }

                    // READ THIS BEFORE YOU APPLY Disclaimer
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmpireSurfaceElevated)
                            .border(1.dp, EmpireAmber.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = EmpireAmber)
                            Text(
                                "READ THIS BEFORE YOU APPLY",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = TextWhite
                                )
                            )
                        }

                        val disclaimers = listOf(
                            "Jobless Empire does not guarantee employment or hiring.",
                            "Jobless Empire does not guarantee project selection or income.",
                            "Not every project is paid — some are experimental or open-source.",
                            "This is a builder community, not a substitute for active job searching."
                        )

                        disclaimers.forEach { d ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("•", color = EmpireAmber, fontWeight = FontWeight.Bold)
                                Text(d, style = MaterialTheme.typography.bodySmall.copy(color = TextOffWhite))
                            }
                        }
                    }
                }
            }

            // ----------------------------------------------------
            // 14. APPLICATION SECTION (CONVERSION POINT)
            // ----------------------------------------------------
            item {
                ApplicationFormCard(
                    isSubmitting = isSubmitting,
                    errorMessage = errorMessage,
                    latestSubmission = latestSubmission,
                    onSubmit = { name, email, rName, rSize, isJobless, consent ->
                        coroutineScope.launch {
                            repository.submitApplication(name, email, rName, rSize, isJobless, consent)
                        }
                    },
                    onViewAdminPayload = { sub ->
                        adminPreviewSubmission = sub
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ----------------------------------------------------
            // 15. FAQ ACCORDION SECTION
            // ----------------------------------------------------
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionEyebrow(text = "FREQUENTLY ASKED QUESTIONS")

                    Text(
                        "COMMON QUESTIONS",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                    )

                    FaqAccordionSection(
                        faqList = JoblessEmpireData.faqList,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ----------------------------------------------------
            // 16. FINAL CTA & FOOTER
            // ----------------------------------------------------
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(EmpireSurfaceCard, EmpireSurfaceElevated, EmpireBackground)
                            )
                        )
                        .border(1.dp, EmpireAmber.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "YOUR NEXT CHAPTER DOESN'T HAVE TO WAIT FOR A JOB OFFER.",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = TextWhite,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    )

                    Text(
                        "Build something. Meet people. Create proof. Maybe earn along the way.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = TextOffWhite,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Button(
                            onClick = { scrollToSection(NavSection.APPLY) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmpireAmber,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                "APPLY TO JOIN THE EMPIRE",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
                            )
                        }

                        OutlinedButton(
                            onClick = { scrollToSection(NavSection.HOW_IT_WORKS) },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmpireSurfaceBorderLight),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("EXPLORE THE IDEA", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            // ----------------------------------------------------
            // 17. FOOTER
            // ----------------------------------------------------
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Divider(color = EmpireSurfaceBorder)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "JOBLESS EMPIRE",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = TextWhite
                            )
                        )
                        Text("•", color = TextDim)
                        Text(
                            "\"Build anyway.\"",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = EmpireAmberLight,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        )
                    }

                    // Footer links
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        listOf("How It Works", "Community", "Revenue", "Rules", "FAQ", "Apply").forEach { link ->
                            Text(
                                link,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextDim,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Text(
                        "Private community • Applications manually reviewed",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextDim,
                            fontSize = 10.sp
                        )
                    )

                    Text(
                        "© 2026 Jobless Empire. All rights reserved.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextDim,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }

    // Dialogs
    if (adminPreviewSubmission != null) {
        AdminApplicationPreviewDialog(
            submission = adminPreviewSubmission!!,
            onDismiss = { adminPreviewSubmission = null }
        )
    }

    if (showExportDialog) {
        TimelineExportDialog(
            profile = currentProfile,
            onDismiss = { showExportDialog = false }
        )
    }

    if (showAddMilestoneDialog) {
        var milestoneTitle by remember { mutableStateOf("") }
        var milestoneDetail by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddMilestoneDialog = false },
            title = {
                Text(
                    "ADD MILESTONE TO PROOF-OF-WORK",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Add a custom verified milestone or contribution note to your ${currentProfile.id} timeline:",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                    OutlinedTextField(
                        value = milestoneTitle,
                        onValueChange = { milestoneTitle = it },
                        label = { Text("Milestone Title") },
                        placeholder = { Text("e.g. Completed API Benchmarking") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = milestoneDetail,
                        onValueChange = { milestoneDetail = it },
                        label = { Text("Contribution Details") },
                        placeholder = { Text("e.g. Achieved sub-50ms latency across 10k mock requests") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (milestoneTitle.isNotBlank()) {
                            repository.addCustomTimelineMilestone(
                                currentProfile.id,
                                milestoneTitle,
                                listOf(if (milestoneDetail.isNotBlank()) milestoneDetail else "Milestone attested by member")
                            )
                            showAddMilestoneDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmpireAmber, contentColor = Color.Black)
                ) {
                    Text("ADD MILESTONE", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMilestoneDialog = false }) {
                    Text("CANCEL", color = TextDim)
                }
            },
            containerColor = EmpireSurfaceElevated
        )
    }
}
