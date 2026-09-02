package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.*
import com.example.data.model.*
import com.example.data.repository.JoblessEmpireRepository
import java.text.SimpleDateFormat
import java.util.*

enum class BrainstormConsoleTab(val label: String, val icon: String) {
    BROWSE("Explore Ideas", "💡"),
    WORKSPACE("Squad Workspace", "🛠️"),
    MY_SQUADS("My Squads", "👤"),
    ADMIN("Bot Admin", "🛡️")
}

enum class WorkspaceInnerTab(val label: String) {
    STREAM("💬 Discussion"),
    SQUAD("👥 Squad"),
    APPLICATIONS("📋 Applications"),
    DECISIONS("📊 Decisions & Milestones"),
    TASKS("📋 Tasks"),
    RESOURCES("🔗 Resources"),
    POLLS("🗳️ Polls")
}

@Composable
fun BrainstormingClubConsole(
    repository: JoblessEmpireRepository,
    modifier: Modifier = Modifier
) {
    var activeConsoleTab by remember { mutableStateOf(BrainstormConsoleTab.BROWSE) }
    var activeWorkspaceTab by remember { mutableStateOf(WorkspaceInnerTab.STREAM) }

    val brainstorms by repository.dbBrainstorms.collectAsState()
    val selectedId by repository.selectedBrainstormId.collectAsState()
    val detail by repository.selectedBrainstormDetail.collectAsState()
    val metrics by repository.brainstormMetrics.collectAsState()
    val health by repository.brainstormHealth.collectAsState()
    val terminalOutput by repository.botTerminalOutput.collectAsState()
    val activeMemberId by repository.selectedMemberId.collectAsState()

    val searchQuery by repository.brainstormSearchQuery.collectAsState()
    val filterStatus by repository.brainstormFilterStatus.collectAsState()
    val filterSkill by repository.brainstormFilterSkill.collectAsState()

    // Dialog States
    var showCreateDialog by remember { mutableStateOf(false) }
    var showApplyDialog by remember { mutableStateOf(false) }
    var showDecisionDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showConvertDialog by remember { mutableStateOf(false) }

    // Quick Command Bar Text
    var commandInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1117))
            .testTag("brainstorming_club_console")
    ) {
        // ----------------------------------------------------
        // TOP HEADER: Bot Identity & Status Bar
        // ----------------------------------------------------
        Surface(
            color = Color(0xFF161922),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, Color(0xFF222736))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF8B5CF6).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🤖", fontSize = 18.sp)
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Brainstorming Club Bot",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                                Surface(
                                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "v2.4 ONLINE",
                                        color = Color(0xFF34D399),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Automated B# registry, collaborative squad workspaces & project graduation",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = { showCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("create_brainstorm_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Idea", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Command Line Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0D0F14))
                        .border(1.dp, Color(0xFF272F3E), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("❯", color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = commandInput,
                        onValueChange = { commandInput = it },
                        placeholder = { Text("Run slash command (e.g. /brainstorm create, /brainstorm stats, /brainstorm-admin health)...", fontSize = 11.sp, color = Color(0xFF64748B)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color(0xFFE2E8F0),
                            unfocusedTextColor = Color(0xFFCBD5E1)
                        )
                    )
                    if (commandInput.isNotBlank()) {
                        IconButton(
                            onClick = {
                                repository.executeBrainstormBotCommand(commandInput)
                                commandInput = ""
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Run", tint = Color(0xFF8B5CF6), modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        // Navigation Tabs
        TabRow(
            selectedTabIndex = activeConsoleTab.ordinal,
            containerColor = Color(0xFF13161F),
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeConsoleTab.ordinal]),
                    color = Color(0xFF8B5CF6)
                )
            }
        ) {
            BrainstormConsoleTab.values().forEach { tab ->
                Tab(
                    selected = activeConsoleTab == tab,
                    onClick = { activeConsoleTab = tab },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(tab.icon, fontSize = 12.sp)
                            Text(tab.label, fontSize = 12.sp, fontWeight = if (activeConsoleTab == tab) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
            }
        }

        // Main Tab Content
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (activeConsoleTab) {
                BrainstormConsoleTab.BROWSE -> {
                    BrowseBrainstormsView(
                        brainstorms = brainstorms,
                        selectedId = selectedId,
                        searchQuery = searchQuery,
                        filterStatus = filterStatus,
                        filterSkill = filterSkill,
                        onSearchChange = { repository.setBrainstormSearchQuery(it) },
                        onStatusFilterChange = { repository.setBrainstormFilterStatus(it) },
                        onSkillFilterChange = { repository.setBrainstormFilterSkill(it) },
                        onSelect = {
                            repository.selectBrainstorm(it)
                            activeConsoleTab = BrainstormConsoleTab.WORKSPACE
                        },
                        onCreateClick = { showCreateDialog = true }
                    )
                }
                BrainstormConsoleTab.WORKSPACE -> {
                    if (detail != null) {
                        BrainstormWorkspaceView(
                            detail = detail!!,
                            activeInnerTab = activeWorkspaceTab,
                            onInnerTabChange = { activeWorkspaceTab = it },
                            onApplyClick = { showApplyDialog = true },
                            onStatusClick = { showStatusDialog = true },
                            onConvertClick = { showConvertDialog = true },
                            onLogDecisionClick = { showDecisionDialog = true },
                            onAcceptApplication = { repository.acceptBrainstormApplication(it) },
                            onRejectApplication = { repository.rejectBrainstormApplication(it) },
                            onCompleteMilestone = { repository.completeBrainstormMilestone(it) },
                            onTaskStatusChange = { taskId, status -> repository.updateBrainstormTaskStatus(taskId, status) },
                            onVotePoll = { pollId, opt -> repository.voteBrainstormPoll(pollId, opt) },
                            onPostDiscussion = { category, msg -> repository.postBrainstormDiscussionMessage(detail!!.brainstorm.id, category, msg) },
                            currentMemberId = activeMemberId
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("💡 No Brainstorm Selected", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Select an idea from Explore or create a new brainstorm.", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                Button(onClick = { activeConsoleTab = BrainstormConsoleTab.BROWSE }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))) {
                                    Text("Explore Ideas")
                                }
                            }
                        }
                    }
                }
                BrainstormConsoleTab.MY_SQUADS -> {
                    MySquadsView(
                        brainstorms = brainstorms,
                        currentMemberId = activeMemberId,
                        onSelectBrainstorm = {
                            repository.selectBrainstorm(it)
                            activeConsoleTab = BrainstormConsoleTab.WORKSPACE
                        }
                    )
                }
                BrainstormConsoleTab.ADMIN -> {
                    BrainstormAdminView(
                        metrics = metrics,
                        health = health,
                        terminalOutput = terminalOutput,
                        onReconcile = { repository.reconcileBrainstormSystem() },
                        onSeedDemo = { repository.seedBrainstormDemoData() },
                        onClearDemo = { repository.clearBrainstormDemoData() },
                        onRunCommand = { repository.executeBrainstormBotCommand(it) }
                    )
                }
            }
        }

        // Dialogs
        if (showCreateDialog) {
            CreateBrainstormDialog(
                currentMemberId = activeMemberId,
                onDismiss = { showCreateDialog = false },
                onSubmit = { draft ->
                    repository.createBrainstorm(draft) {
                        showCreateDialog = false
                        activeConsoleTab = BrainstormConsoleTab.WORKSPACE
                    }
                }
            )
        }

        if (showApplyDialog && detail != null) {
            ApplyToBrainstormDialog(
                brainstorm = detail!!.brainstorm,
                currentMemberId = activeMemberId,
                onDismiss = { showApplyDialog = false },
                onSubmit = { reason, skills, exp, avail, port ->
                    repository.submitBrainstormApplication(detail!!.brainstorm.id, reason, skills, exp, avail, port) {
                        showApplyDialog = false
                    }
                }
            )
        }

        if (showDecisionDialog && detail != null) {
            RecordDecisionDialog(
                brainstormId = detail!!.brainstorm.id,
                onDismiss = { showDecisionDialog = false },
                onSubmit = { text, reason ->
                    repository.recordBrainstormDecision(detail!!.brainstorm.id, text, reason) {
                        showDecisionDialog = false
                    }
                }
            )
        }

        if (showStatusDialog && detail != null) {
            ChangeStatusDialog(
                brainstorm = detail!!.brainstorm,
                onDismiss = { showStatusDialog = false },
                onSubmit = { newStatus, reason ->
                    repository.changeBrainstormStatus(detail!!.brainstorm.id, newStatus, reason) {
                        showStatusDialog = false
                    }
                }
            )
        }

        if (showConvertDialog && detail != null) {
            ConvertToProjectDialog(
                brainstorm = detail!!.brainstorm,
                onDismiss = { showConvertDialog = false },
                onSubmit = { title ->
                    repository.convertBrainstormToProject(detail!!.brainstorm.id, title) {
                        showConvertDialog = false
                    }
                }
            )
        }
    }
}

// -------------------------------------------------------------------------
// 1. EXPLORE & BROWSE VIEW
// -------------------------------------------------------------------------
@Composable
private fun BrowseBrainstormsView(
    brainstorms: List<BrainstormEntity>,
    selectedId: String?,
    searchQuery: String,
    filterStatus: String?,
    filterSkill: String?,
    onSearchChange: (String) -> Unit,
    onStatusFilterChange: (String?) -> Unit,
    onSkillFilterChange: (String?) -> Unit,
    onSelect: (String) -> Unit,
    onCreateClick: () -> Unit
) {
    val filtered = remember(brainstorms, searchQuery, filterStatus, filterSkill) {
        brainstorms.filter { bs ->
            val matchesSearch = searchQuery.isBlank() ||
                    bs.title.contains(searchQuery, ignoreCase = true) ||
                    bs.id.contains(searchQuery, ignoreCase = true) ||
                    bs.description.contains(searchQuery, ignoreCase = true) ||
                    bs.tagsJson.contains(searchQuery, ignoreCase = true)

            val matchesStatus = filterStatus == null || bs.status.equals(filterStatus, ignoreCase = true)
            val matchesSkill = filterSkill == null || bs.desiredSkillsJson.contains(filterSkill, ignoreCase = true)

            matchesSearch && matchesStatus && matchesSkill
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Search & Filter Toolbar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF94A3B8))
                    }
                }
            },
            placeholder = { Text("Search by title, tag (#AI), B# ID, or keyword...", fontSize = 12.sp, color = Color(0xFF64748B)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF8B5CF6),
                unfocusedBorderColor = Color(0xFF272F3E),
                focusedContainerColor = Color(0xFF131620),
                unfocusedContainerColor = Color(0xFF131620),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        // Status Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            item {
                FilterChip(
                    selected = filterStatus == null,
                    onClick = { onStatusFilterChange(null) },
                    label = { Text("All (${brainstorms.size})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF8B5CF6),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF1A1F2C),
                        labelColor = Color(0xFF94A3B8)
                    )
                )
            }
            items(BrainstormStatus.values()) { status ->
                val isSel = filterStatus.equals(status.name, ignoreCase = true)
                val count = brainstorms.count { it.status.equals(status.name, ignoreCase = true) }
                FilterChip(
                    selected = isSel,
                    onClick = { onStatusFilterChange(if (isSel) null else status.name) },
                    label = {
                        Text("${status.label} ($count)", fontSize = 11.sp)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = getStatusColor(status.name),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF1A1F2C),
                        labelColor = Color(0xFF94A3B8)
                    )
                )
            }
        }

        // Skill Filters
        val skills = listOf("Developer", "Designer", "Product", "AI/ML", "Marketing")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(skills) { skill ->
                val isSel = filterSkill == skill
                FilterChip(
                    selected = isSel,
                    onClick = { onSkillFilterChange(if (isSel) null else skill) },
                    label = { Text("Wanted: $skill", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF06B6D4),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF161B26),
                        labelColor = Color(0xFF94A3B8)
                    )
                )
            }
        }

        // List of Brainstorms
        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🔍 No brainstorms found matching criteria", color = Color(0xFF94A3B8), fontSize = 13.sp)
                    Button(onClick = onCreateClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))) {
                        Text("Create First Brainstorm")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered, key = { it.id }) { bs ->
                    BrainstormCardItem(
                        brainstorm = bs,
                        isSelected = bs.id == selectedId,
                        onClick = { onSelect(bs.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BrainstormCardItem(
    brainstorm: BrainstormEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val skills = try {
        val arr = org.json.JSONArray(brainstorm.desiredSkillsJson)
        List(arr.length()) { arr.getString(it) }
    } catch (e: Exception) { emptyList<String>() }

    val tags = try {
        val arr = org.json.JSONArray(brainstorm.tagsJson)
        List(arr.length()) { arr.getString(it) }
    } catch (e: Exception) { emptyList<String>() }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF1E2333) else Color(0xFF151822),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF8B5CF6) else Color(0xFF242A3B))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = Color(0xFF8B5CF6).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = brainstorm.id,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC084FC),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = brainstorm.title,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                StatusBadge(brainstorm.status)
            }

            // Overview Text
            Text(
                text = brainstorm.description,
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Converted Project Link
            if (brainstorm.linkedProjectId != null) {
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🚀", fontSize = 11.sp)
                        Text(
                            text = "Promoted to Project ${brainstorm.linkedProjectId}",
                            color = Color(0xFF34D399),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Skills & Metadata Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Owner:", color = Color(0xFF64748B), fontSize = 11.sp)
                    Text(brainstorm.ownerDisplayName, color = Color(0xFFE2E8F0), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    skills.take(2).forEach { s ->
                        Surface(color = Color(0xFF0F172A), shape = RoundedCornerShape(4.dp)) {
                            Text(s, color = Color(0xFF38BDF8), fontSize = 10.sp, modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp))
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 2. BRAINSTORM WORKSPACE VIEW
// -------------------------------------------------------------------------
@Composable
private fun BrainstormWorkspaceView(
    detail: BrainstormDetailModel,
    activeInnerTab: WorkspaceInnerTab,
    onInnerTabChange: (WorkspaceInnerTab) -> Unit,
    onApplyClick: () -> Unit,
    onStatusClick: () -> Unit,
    onConvertClick: () -> Unit,
    onLogDecisionClick: () -> Unit,
    onAcceptApplication: (String) -> Unit,
    onRejectApplication: (String) -> Unit,
    onCompleteMilestone: (String) -> Unit,
    onTaskStatusChange: (String, String) -> Unit,
    onVotePoll: (String, Int) -> Unit,
    onPostDiscussion: (category: String, message: String) -> Unit,
    currentMemberId: String
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Hero Header Card
        Surface(
            color = Color(0xFF181C26),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, Color(0xFF262D3D))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(detail.brainstorm.id, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color(0xFFC084FC), fontSize = 14.sp)
                        Text(detail.brainstorm.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    }
                    StatusBadge(detail.brainstorm.status)
                }

                if (detail.brainstorm.linkedProjectId != null) {
                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🚀 Active Project:", color = Color(0xFF34D399), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("${detail.brainstorm.linkedProjectId} (${detail.brainstorm.title})", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }

                Text(detail.brainstorm.description, color = Color(0xFFCBD5E1), fontSize = 12.sp)

                // Problem & Direction snippets
                if (detail.brainstorm.problem.isNotBlank()) {
                    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("🎯 Problem:", color = Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(detail.brainstorm.problem, color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }

                // Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!detail.isContributor && !detail.hasPendingApplication && detail.brainstorm.status != "CANCELLED") {
                        Button(
                            onClick = onApplyClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("✍ Apply to Squad", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (detail.hasPendingApplication) {
                        Surface(color = Color(0xFFF59E0B).copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                            Text("⏳ Application Pending Review", color = Color(0xFFFBBF24), fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    if (detail.isOwner) {
                        OutlinedButton(
                            onClick = onStatusClick,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC084FC)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("⚙ Status / Reason", fontSize = 11.sp)
                        }

                        if (detail.brainstorm.linkedProjectId == null && detail.brainstorm.status != "CANCELLED") {
                            Button(
                                onClick = onConvertClick,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("🚀 Convert to P#", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = onLogDecisionClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("+ Decision", fontSize = 11.sp)
                    }
                }
            }
        }

        // Inner Workspace Tabs (Scrollable)
        ScrollableTabRow(
            selectedTabIndex = activeInnerTab.ordinal,
            containerColor = Color(0xFF12151D),
            contentColor = Color.White,
            edgePadding = 8.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeInnerTab.ordinal]),
                    color = Color(0xFF8B5CF6)
                )
            }
        ) {
            WorkspaceInnerTab.values().forEach { tab ->
                Tab(
                    selected = activeInnerTab == tab,
                    onClick = { onInnerTabChange(tab) },
                    text = {
                        val countLabel = when (tab) {
                            WorkspaceInnerTab.APPLICATIONS -> if (detail.applications.isNotEmpty()) " (${detail.applications.size})" else ""
                            WorkspaceInnerTab.SQUAD -> " (${detail.contributors.size})"
                            WorkspaceInnerTab.DECISIONS -> " (${detail.decisions.size})"
                            WorkspaceInnerTab.TASKS -> " (${detail.tasks.size})"
                            else -> ""
                        }
                        Text("${tab.label}$countLabel", fontSize = 11.sp, fontWeight = if (activeInnerTab == tab) FontWeight.Bold else FontWeight.Normal)
                    }
                )
            }
        }

        // Sub-Tab Content
        Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(12.dp)) {
            when (activeInnerTab) {
                WorkspaceInnerTab.STREAM -> {
                    DiscussionStreamView(
                        activities = detail.recentActivity,
                        onPost = onPostDiscussion
                    )
                }
                WorkspaceInnerTab.SQUAD -> {
                    SquadListView(contributors = detail.contributors, isOwner = detail.isOwner)
                }
                WorkspaceInnerTab.APPLICATIONS -> {
                    ApplicationsReviewView(
                        applications = detail.applications,
                        isOwner = detail.isOwner,
                        onAccept = onAcceptApplication,
                        onReject = onRejectApplication
                    )
                }
                WorkspaceInnerTab.DECISIONS -> {
                    DecisionsAndMilestonesView(
                        decisions = detail.decisions,
                        milestones = detail.milestones,
                        onCompleteMilestone = onCompleteMilestone
                    )
                }
                WorkspaceInnerTab.TASKS -> {
                    TasksListView(tasks = detail.tasks, onTaskStatusChange = onTaskStatusChange)
                }
                WorkspaceInnerTab.RESOURCES -> {
                    ResourcesVaultView(resources = detail.resources)
                }
                WorkspaceInnerTab.POLLS -> {
                    PollsListView(polls = detail.polls, onVote = onVotePoll)
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 3. WORKSPACE SUB-VIEWS
// -------------------------------------------------------------------------
@Composable
private fun DiscussionStreamView(
    activities: List<BrainstormActivityEntity>,
    onPost: (category: String, message: String) -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val categories = listOf("💡 Idea", "🔬 Research", "🎨 Design", "💻 Technical", "📊 Product", "📣 Marketing", "❓ Question", "✅ Decision", "⚠ Blocker", "📌 Important")
    var selectedCategory by remember { mutableStateOf("💡 Idea") }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Message Input
        Surface(
            color = Color(0xFF181C26),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color(0xFF262D3D))
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(categories) { cat ->
                        Surface(
                            modifier = Modifier.clickable { selectedCategory = cat },
                            color = if (selectedCategory == cat) Color(0xFF8B5CF6) else Color(0xFF0F172A),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(cat, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Post an idea, note, or update to brainstorm thread...", fontSize = 12.sp, color = Color(0xFF64748B)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                onPost(selectedCategory, messageText)
                                messageText = ""
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color(0xFF8B5CF6))
                    }
                }
            }
        }

        // Stream Items
        if (activities.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("No messages in this brainstorm thread yet. Be the first to share an idea!", color = Color(0xFF64748B), fontSize = 12.sp)
            }
        } else {
            val sdf = remember { SimpleDateFormat("HH:mm · dd MMM", Locale.US) }
            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(activities, key = { it.id }) { act ->
                    Surface(
                        color = Color(0xFF141722),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF222736))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF8B5CF6).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(act.actorDisplayName.take(1).uppercase(Locale.US), fontWeight = FontWeight.Bold, color = Color(0xFFC084FC))
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(act.actorDisplayName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                        Text(act.actorMemberId, color = Color(0xFF64748B), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    }
                                    Text(sdf.format(Date(act.timestampUtc)), color = Color(0xFF64748B), fontSize = 10.sp)
                                }
                                Surface(color = Color(0xFF0F172A), shape = RoundedCornerShape(4.dp)) {
                                    Text(act.title, color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                }
                                Text(act.description, color = Color(0xFFCBD5E1), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SquadListView(contributors: List<BrainstormContributorItem>, isOwner: Boolean) {
    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(contributors, key = { it.id }) { c ->
            Surface(
                color = Color(0xFF141722),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFF222736))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier.size(34.dp).clip(CircleShape).background(Color(c.role.badgeColorHex).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(c.displayName.take(1).uppercase(Locale.US), fontWeight = FontWeight.Bold, color = Color(c.role.badgeColorHex))
                        }
                        Column {
                            Text(c.displayName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            Text("${c.memberId} • Joined ${c.joinedDateFormatted}", color = Color(0xFF64748B), fontSize = 11.sp)
                        }
                    }

                    Surface(
                        color = Color(c.role.badgeColorHex).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            c.role.label,
                            color = Color(c.role.badgeColorHex),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ApplicationsReviewView(
    applications: List<BrainstormApplicationItem>,
    isOwner: Boolean,
    onAccept: (String) -> Unit,
    onReject: (String) -> Unit
) {
    if (applications.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No pending or reviewed applications for this brainstorm.", color = Color(0xFF64748B), fontSize = 12.sp)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(applications, key = { it.id }) { app ->
                Surface(
                    color = Color(0xFF141722),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF222736))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(app.displayName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                Text("(${app.memberId})", color = Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                            Surface(
                                color = when (app.status) {
                                    "ACCEPTED" -> Color(0xFF10B981).copy(alpha = 0.2f)
                                    "REJECTED" -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                    else -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    app.status,
                                    color = when (app.status) {
                                        "ACCEPTED" -> Color(0xFF34D399)
                                        "REJECTED" -> Color(0xFFF87171)
                                        else -> Color(0xFFFBBF24)
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text("Reason: ${app.reason}", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        Text("Skills: ${app.skills}", color = Color(0xFF38BDF8), fontSize = 11.sp)
                        if (app.availability.isNotBlank()) {
                            Text("Availability: ${app.availability}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }

                        if (isOwner && app.status == "PENDING") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                                OutlinedButton(
                                    onClick = { onReject(app.id) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Decline", fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { onAccept(app.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Accept Contributor", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DecisionsAndMilestonesView(
    decisions: List<BrainstormDecisionItem>,
    milestones: List<BrainstormMilestoneItem>,
    onCompleteMilestone: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text("📊 Architectural Decisions", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
        }
        if (decisions.isEmpty()) {
            item { Text("No formal decisions recorded yet.", color = Color(0xFF64748B), fontSize = 12.sp) }
        } else {
            items(decisions, key = { it.id }) { dec ->
                Surface(color = Color(0xFF141722), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFF222736))) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("DECISION #${dec.decisionNumber}", fontWeight = FontWeight.Bold, color = Color(0xFFC084FC), fontSize = 11.sp)
                            Text("• ${dec.madeByDisplayName}", color = Color(0xFF64748B), fontSize = 10.sp)
                        }
                        Text(dec.decisionText, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("Rationale: ${dec.reason}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }
            }
        }

        item {
            Text("🎯 Milestones & Timeline Sync", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
        }
        if (milestones.isEmpty()) {
            item { Text("No milestones registered yet.", color = Color(0xFF64748B), fontSize = 12.sp) }
        } else {
            items(milestones, key = { it.id }) { ms ->
                Surface(color = Color(0xFF141722), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFF222736))) {
                    Row(
                        modifier = Modifier.padding(10.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(ms.title, fontWeight = FontWeight.Bold, color = if (ms.isCompleted) Color(0xFF34D399) else Color.White, fontSize = 12.sp)
                            Text(ms.description, color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                        if (!ms.isCompleted) {
                            Button(
                                onClick = { onCompleteMilestone(ms.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Complete", fontSize = 10.sp)
                            }
                        } else {
                            Text("✔ Completed", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TasksListView(
    tasks: List<BrainstormTaskItem>,
    onTaskStatusChange: (String, String) -> Unit
) {
    if (tasks.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active tasks in this brainstorm.", color = Color(0xFF64748B), fontSize = 12.sp)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tasks, key = { it.id }) { task ->
                Surface(color = Color(0xFF141722), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFF222736))) {
                    Row(
                        modifier = Modifier.padding(10.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(task.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                            Text(task.description, color = Color(0xFF94A3B8), fontSize = 11.sp)
                            task.assigneeDisplayName?.let {
                                Text("Assigned to: $it", color = Color(0xFF38BDF8), fontSize = 10.sp)
                            }
                        }

                        Button(
                            onClick = {
                                val next = when (task.status) {
                                    "TODO" -> "IN_PROGRESS"
                                    "IN_PROGRESS" -> "DONE"
                                    else -> "TODO"
                                }
                                onTaskStatusChange(task.id, next)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2333)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(task.status, fontSize = 10.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResourcesVaultView(resources: List<BrainstormResourceItem>) {
    if (resources.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No shared resources linked yet.", color = Color(0xFF64748B), fontSize = 12.sp)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(resources, key = { it.id }) { res ->
                Surface(color = Color(0xFF141722), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFF222736))) {
                    Row(
                        modifier = Modifier.padding(10.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(res.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                            Text(res.url, color = Color(0xFF38BDF8), fontSize = 11.sp)
                            Text(res.description, color = Color(0xFF94A3B8), fontSize = 10.sp)
                        }
                        Surface(color = Color(0xFF0F172A), shape = RoundedCornerShape(4.dp)) {
                            Text(res.category, color = Color(0xFFC084FC), fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PollsListView(
    polls: List<BrainstormPollItem>,
    onVote: (String, Int) -> Unit
) {
    if (polls.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active polls in this brainstorm.", color = Color(0xFF64748B), fontSize = 12.sp)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(polls, key = { it.id }) { poll ->
                Surface(color = Color(0xFF141722), shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, Color(0xFF222736))) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(poll.question, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        poll.options.forEachIndexed { index, option ->
                            val votes = poll.voteCounts[index] ?: 0
                            val pct = if (poll.totalVotes > 0) (votes.toFloat() / poll.totalVotes) else 0f
                            val isVoted = poll.userVotedOptionIndex == index

                            Surface(
                                modifier = Modifier.fillMaxWidth().clickable { onVote(poll.id, index) },
                                color = if (isVoted) Color(0xFF8B5CF6).copy(alpha = 0.2f) else Color(0xFF0F172A),
                                shape = RoundedCornerShape(6.dp),
                                border = if (isVoted) BorderStroke(1.dp, Color(0xFF8B5CF6)) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(option, color = Color.White, fontSize = 12.sp)
                                    Text("$votes votes (${(pct * 100).toInt()}%)", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 4. MY SQUADS VIEW
// -------------------------------------------------------------------------
@Composable
private fun MySquadsView(
    brainstorms: List<BrainstormEntity>,
    currentMemberId: String,
    onSelectBrainstorm: (String) -> Unit
) {
    val owned = brainstorms.filter { it.ownerMemberId == currentMemberId }

    Column(modifier = Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("👤 My Brainstorming Club Profile ($currentMemberId)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)

        Surface(color = Color(0xFF141722), shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, Color(0xFF222736))) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Ideas You Created (${owned.size})", fontWeight = FontWeight.Bold, color = Color(0xFFC084FC), fontSize = 13.sp)
                if (owned.isEmpty()) {
                    Text("You have not created any brainstorms yet.", color = Color(0xFF64748B), fontSize = 12.sp)
                } else {
                    owned.forEach { bs ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { onSelectBrainstorm(bs.id) }.padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${bs.id} — ${bs.title}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            StatusBadge(bs.status)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 5. ADMIN & DIAGNOSTICS VIEW (/brainstorm-admin)
// -------------------------------------------------------------------------
@Composable
private fun BrainstormAdminView(
    metrics: BrainstormAdminDashboardMetrics,
    health: BrainstormHealthStatus,
    terminalOutput: String?,
    onReconcile: () -> Unit,
    onSeedDemo: () -> Unit,
    onClearDemo: () -> Unit,
    onRunCommand: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("🛡️ Brainstorm Bot Administration & Health", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)

        // Metrics Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Total Ideas", metrics.totalBrainstorms.toString(), Color(0xFF8B5CF6), Modifier.weight(1f))
            MetricCard("Active", metrics.activeCount.toString(), Color(0xFF38BDF8), Modifier.weight(1f))
            MetricCard("Cooking", metrics.cookingCount.toString(), Color(0xFFF59E0B), Modifier.weight(1f))
            MetricCard("It's Alive", metrics.aliveCount.toString(), Color(0xFF10B981), Modifier.weight(1f))
        }

        // Action Toolbar
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onReconcile,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                modifier = Modifier.weight(1f)
            ) {
                Text("Reconcile System", fontSize = 11.sp)
            }
            Button(
                onClick = onSeedDemo,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4)),
                modifier = Modifier.weight(1f)
            ) {
                Text("Seed Demo", fontSize = 11.sp)
            }
            OutlinedButton(
                onClick = onClearDemo,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear Demo", fontSize = 11.sp)
            }
        }

        // Health Indicators
        Surface(color = Color(0xFF141722), shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, Color(0xFF222736))) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("System Health Monitor", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                HealthRow("Database Engine", health.databaseStatus)
                HealthRow("Discord Gateway", health.discordApiStatus)
                HealthRow("Timeline Sync", health.timelineEngineStatus)
                HealthRow("Member Service", health.memberServiceStatus)
            }
        }

        // Terminal Console
        Surface(
            color = Color(0xFF090B10),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color(0xFF222736)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("💻", fontSize = 12.sp)
                    Text("Bot Console Terminal Output", fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), fontSize = 12.sp)
                }
                Text(
                    text = terminalOutput ?: "Console ready. Run /brainstorm or /brainstorm-admin commands.",
                    color = Color(0xFF34D399),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, accent: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        color = Color(0xFF141722),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFF222736))
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.Bold, color = accent, fontSize = 18.sp)
            Text(label, color = Color(0xFF94A3B8), fontSize = 10.sp)
        }
    }
}

@Composable
private fun HealthRow(label: String, status: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color(0xFF94A3B8), fontSize = 11.sp)
        Text("🟢 $status", color = Color(0xFF34D399), fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (label, bg, fg) = when (status) {
        "COOKING" -> Triple("🔥 Cooking", Color(0xFFF59E0B).copy(alpha = 0.2f), Color(0xFFFBBF24))
        "PAUSED" -> Triple("⏸ Paused", Color(0xFF6B7280).copy(alpha = 0.2f), Color(0xFF9CA3AF))
        "CANCELLED" -> Triple("❌ Cancelled", Color(0xFFEF4444).copy(alpha = 0.2f), Color(0xFFF87171))
        "IT_S_ALIVE" -> Triple("🚀 It's Alive!", Color(0xFF10B981).copy(alpha = 0.2f), Color(0xFF34D399))
        else -> Triple("💡 Brainstorming", Color(0xFF8B5CF6).copy(alpha = 0.2f), Color(0xFFC084FC))
    }

    Surface(color = bg, shape = RoundedCornerShape(4.dp)) {
        Text(label, color = fg, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
    }
}

private fun getStatusColor(statusName: String): Color = when (statusName) {
    "COOKING" -> Color(0xFFF59E0B)
    "PAUSED" -> Color(0xFF6B7280)
    "CANCELLED" -> Color(0xFFEF4444)
    "IT_S_ALIVE" -> Color(0xFF10B981)
    else -> Color(0xFF8B5CF6)
}
