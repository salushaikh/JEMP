package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AuditLogEntity
import com.example.data.model.*
import com.example.data.repository.JoblessEmpireRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscordBotTimelineConsole(
    repository: JoblessEmpireRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val selectedMemberId by repository.selectedMemberId.collectAsState()
    val timelineHierarchy by repository.timelineHierarchy.collectAsState()
    val timelineStats by repository.timelineStats.collectAsState()
    val viewMode by repository.viewMode.collectAsState()
    val selectedYear by repository.selectedYear.collectAsState()
    val selectedMonth by repository.selectedMonth.collectAsState()
    val selectedDate by repository.selectedDate.collectAsState()
    val activePublicShare by repository.activePublicShare.collectAsState()
    val exportDocument by repository.exportDocument.collectAsState()
    val systemHealth by repository.systemHealth.collectAsState()
    val botTerminalOutput by repository.botTerminalOutput.collectAsState()

    // Modals
    var showAddModal by remember { mutableStateOf(false) }
    var showShareModal by remember { mutableStateOf(false) }
    var showPublicPreviewModal by remember { mutableStateOf(false) }
    var publicPreviewData by remember { mutableStateOf<PublicTimelineViewData?>(null) }
    var showExportModal by remember { mutableStateOf(false) }
    var showHealthModal by remember { mutableStateOf(false) }
    var showAuditModal by remember { mutableStateOf(false) }
    var showEventSimulatorModal by remember { mutableStateOf(false) }
    var showDeleteConfirmItem by remember { mutableStateOf<TimelineItem?>(null) }
    var showSettingsModal by remember { mutableStateOf(false) }
    var showHelpModal by remember { mutableStateOf(false) }

    // Interactive Command input
    var commandInput by remember { mutableStateOf("/timeline") }
    var lastExecutedCommand by remember { mutableStateOf<String?>(null) }

    val autocompleteSuggestions = listOf(
        "/timeline",
        "/timeline view compact",
        "/timeline view standard",
        "/timeline view detailed",
        "/timeline add",
        "/timeline stats",
        "/timeline share",
        "/timeline unshare",
        "/timeline export",
        "/timeline settings",
        "/timeline help",
        "/timeline-admin seed-demo",
        "/timeline-admin clear-demo",
        "/timeline-admin health",
        "/timeline-admin audit"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(EmpireSurface)
            .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(16.dp))
    ) {
        // ----------------------------------------------------
        // 1. DISCORD TOP STATUS & IDENTITY BAR
        // ----------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EmpireSurfaceElevated)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(EmpireDiscordIndigo),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🤖", fontSize = 12.sp)
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "JOBLESS EMPIRE // TIMELINE BOT",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = TextWhite,
                                fontSize = 13.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(EmpireDiscordIndigo)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("BOT", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold))
                        }
                    }
                    Text(
                        "Slash Command Engine • Room SQLite Persistent Backend",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                    )
                }
            }

            // Status indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(EmpireEmerald)
                )
                Text(
                    "ONLINE (18ms)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = EmpireEmerald,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Member Selector Strip (switch between members or create new)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EmpireSurfaceCard)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Active Member:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 11.sp))
                listOf("JEMP#001", "JEMP#014", "JEMP#038").forEach { id ->
                    val isSelected = id == selectedMemberId
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) EmpireAmber.copy(alpha = 0.2f) else EmpireSurface)
                            .border(1.dp, if (isSelected) EmpireAmber else EmpireSurfaceBorder, RoundedCornerShape(6.dp))
                            .clickable { repository.selectMember(id) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            id,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = if (isSelected) EmpireAmber else TextDim,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Health & Simulator quick shortcuts
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = { showEventSimulatorModal = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = "Simulate Bot Ingestion", tint = EmpireAmber, modifier = Modifier.size(16.dp))
                }
                IconButton(
                    onClick = { showHealthModal = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.MonitorHeart, contentDescription = "System Health", tint = EmpireEmerald, modifier = Modifier.size(16.dp))
                }
            }
        }

        Divider(color = EmpireSurfaceBorder)

        // ----------------------------------------------------
        // 2. DISCORD INTERACTIVE EPHEMERAL MESSAGE EMBED
        // ----------------------------------------------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Discord Message User & Bot Stamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EmpireDiscordIndigo),
                    contentAlignment = Alignment.Center
                ) {
                    Text("BOT", style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp))
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("EmpireBot", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = EmpireCyan))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(EmpireDiscordIndigo)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("BOT", style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold))
                        }
                        Text("Today at 20:42", style = MaterialTheme.typography.labelSmall.copy(color = TextDim, fontSize = 10.sp))
                    }

                    // Ephemeral badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.VisibilityOff, contentDescription = null, tint = TextDim, modifier = Modifier.size(12.dp))
                        Text(
                            "Only you can see this • Dismiss message",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextDim, fontSize = 10.sp)
                        )
                    }

                    // ----------------------------------------------------
                    // EMBED CARD (JOBLESS EMPIRE TIMELINE HIERARCHY)
                    // ----------------------------------------------------
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmpireBackground)
                            .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(8.dp))
                    ) {
                        // Left accent bar
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .fillMaxHeight()
                                    .background(EmpireAmber)
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Embed Title & Summary Header
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            IdBadge(selectedMemberId)
                                            Text(
                                                timelineHierarchy?.memberName ?: "Alex Vance",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Black,
                                                    color = TextWhite
                                                )
                                            )
                                        }
                                        Text(
                                            "TZ: ${timelineHierarchy?.timezone ?: "Asia/Kolkata"}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                color = TextDim,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }

                                    // Metric highlights
                                    val stats = timelineStats
                                    Text(
                                        "${stats?.totalEntries ?: 0} records · ${stats?.officialRecordsCount ?: 0} official · ${stats?.personalMilestonesCount ?: 0} personal · ${stats?.projectsCount ?: 0} projects · ${stats?.brainstormsCount ?: 0} brainstorms",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = EmpireAmberLight,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                Divider(color = EmpireSurfaceBorder)

                                // Density / View Mode Selector Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("View Density:", style = MaterialTheme.typography.labelSmall.copy(color = TextDim, fontSize = 10.sp))
                                    TimelineViewMode.values().forEach { mode ->
                                        val isSelected = viewMode == mode
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isSelected) EmpireCyan.copy(alpha = 0.2f) else EmpireSurface)
                                                .border(1.dp, if (isSelected) EmpireCyan else EmpireSurfaceBorder, RoundedCornerShape(6.dp))
                                                .clickable { repository.setViewMode(mode) }
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                mode.name.lowercase().replaceFirstChar { it.uppercase() },
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (isSelected) EmpireCyan else TextMuted,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                }

                                // ----------------------------------------------------
                                // BREADCRUMBS & HIERARCHY NAVIGATION BUTTONS
                                // ----------------------------------------------------
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(EmpireSurfaceElevated)
                                        .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Breadcrumbs Bar
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                "📁 Timeline",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    color = if (selectedYear == null) EmpireAmber else TextDim,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                ),
                                                modifier = Modifier.clickable { repository.resetHierarchyBreadcrumbs() }
                                            )

                                            if (selectedYear != null) {
                                                Text("›", color = TextDim, fontSize = 11.sp)
                                                Text(
                                                    "${selectedYear}",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontFamily = FontFamily.Monospace,
                                                        color = if (selectedMonth == null) EmpireAmber else TextDim,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp
                                                    ),
                                                    modifier = Modifier.clickable { repository.selectMonth(null) }
                                                )
                                            }

                                            if (selectedMonth != null) {
                                                Text("›", color = TextDim, fontSize = 11.sp)
                                                Text(
                                                    selectedMonth!!,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontFamily = FontFamily.Monospace,
                                                        color = if (selectedDate == null) EmpireAmber else TextDim,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp
                                                    ),
                                                    modifier = Modifier.clickable { repository.selectDate(null) }
                                                )
                                            }

                                            if (selectedDate != null) {
                                                Text("›", color = TextDim, fontSize = 11.sp)
                                                Text(
                                                    selectedDate!!,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontFamily = FontFamily.Monospace,
                                                        color = EmpireAmber,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp
                                                    )
                                                )
                                            }
                                        }

                                        // Back button if nested
                                        if (selectedYear != null || selectedMonth != null || selectedDate != null) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(EmpireSurface)
                                                    .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(4.dp))
                                                    .clickable {
                                                        when {
                                                            selectedDate != null -> repository.selectDate(null)
                                                            selectedMonth != null -> repository.selectMonth(null)
                                                            selectedYear != null -> repository.selectYear(null)
                                                        }
                                                    }
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    "← Back",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontFamily = FontFamily.Monospace,
                                                        color = TextOffWhite,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    // Hierarchy Accordion Level 1: YEAR BUTTONS
                                    if (selectedYear == null) {
                                        val yearGroups = timelineHierarchy?.yearGroups ?: emptyList()
                                        Text("Select Year to browse records:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            yearGroups.forEach { yg ->
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(EmpireSurface)
                                                        .border(1.dp, EmpireAmber.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                                        .clickable { repository.selectYear(yg.year) }
                                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                                ) {
                                                    Text(
                                                        "📁 ${yg.year} (${yg.totalEntriesCount})",
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontFamily = FontFamily.Monospace,
                                                            color = EmpireAmber,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    } else if (selectedMonth == null) {
                                        // Level 2: MONTH BUTTONS for selected Year
                                        val yearGroup = timelineHierarchy?.yearGroups?.firstOrNull { it.year == selectedYear }
                                        Text("Select Month in $selectedYear:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp))
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(yearGroup?.monthGroups ?: emptyList()) { mg ->
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(EmpireSurface)
                                                        .border(1.dp, EmpireCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                                        .clickable { repository.selectMonth(mg.monthName) }
                                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        "🗓 ${mg.monthName} (${mg.totalEntriesCount})",
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontFamily = FontFamily.Monospace,
                                                            color = EmpireCyan,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    } else if (selectedDate == null) {
                                        // Level 3: DATE BUTTONS for selected Month
                                        val monthGroup = timelineHierarchy?.yearGroups
                                            ?.firstOrNull { it.year == selectedYear }
                                            ?.monthGroups?.firstOrNull { it.monthName == selectedMonth }
                                        Text("Select Date in $selectedMonth $selectedYear:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp))
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(monthGroup?.dateGroups ?: emptyList()) { dg ->
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(EmpireSurface)
                                                        .border(1.dp, EmpireEmerald.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                                        .clickable { repository.selectDate(dg.dateKey) }
                                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        "📌 ${dg.displayDate} (${dg.entries.size})",
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontFamily = FontFamily.Monospace,
                                                            color = EmpireEmerald,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // ----------------------------------------------------
                                // RENDER TIMELINE ENTRIES
                                // ----------------------------------------------------
                                val displayedEntries = remember(timelineHierarchy, selectedYear, selectedMonth, selectedDate) {
                                    val allYearGroups = timelineHierarchy?.yearGroups ?: emptyList()
                                    when {
                                        selectedDate != null -> {
                                            allYearGroups
                                                .flatMap { it.monthGroups }
                                                .flatMap { it.dateGroups }
                                                .filter { it.dateKey == selectedDate }
                                                .flatMap { it.entries }
                                        }
                                        selectedMonth != null -> {
                                            allYearGroups
                                                .filter { it.year == selectedYear }
                                                .flatMap { it.monthGroups }
                                                .filter { it.monthName == selectedMonth }
                                                .flatMap { it.dateGroups }
                                                .flatMap { it.entries }
                                        }
                                        selectedYear != null -> {
                                            allYearGroups
                                                .filter { it.year == selectedYear }
                                                .flatMap { it.monthGroups }
                                                .flatMap { it.dateGroups }
                                                .flatMap { it.entries }
                                        }
                                        else -> {
                                            // All entries
                                            allYearGroups
                                                .flatMap { it.monthGroups }
                                                .flatMap { it.dateGroups }
                                                .flatMap { it.entries }
                                        }
                                    }
                                }

                                if (displayedEntries.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "No timeline entries found for this selection.",
                                            style = MaterialTheme.typography.bodySmall.copy(color = TextDim)
                                        )
                                    }
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        displayedEntries.forEach { item ->
                                            DiscordTimelineEntryCard(
                                                item = item,
                                                viewMode = viewMode,
                                                onDeleteClick = {
                                                    if (item.isImmutable) {
                                                        Toast.makeText(
                                                            context,
                                                            "🔒 This is an official Jobless Empire record and cannot be deleted.",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    } else {
                                                        showDeleteConfirmItem = item
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }

                                Divider(color = EmpireSurfaceBorder)

                                // ----------------------------------------------------
                                // DISCORD BOT ACTION BUTTONS BAR
                                // ----------------------------------------------------
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = { showAddModal = true },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = EmpireAmber,
                                            contentColor = Color.Black
                                        ),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("Add Entry", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                val shareRes = repository.timelineService.createPublicShare(selectedMemberId)
                                                if (shareRes.isSuccess) {
                                                    val share = shareRes.getOrNull()
                                                    if (share != null) {
                                                        showShareModal = true
                                                    }
                                                } else {
                                                    Toast.makeText(context, shareRes.exceptionOrNull()?.message, Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EmpireCyan),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, EmpireCyan.copy(alpha = 0.5f)),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("Share 24h", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                val doc = repository.timelineService.exportTimeline(selectedMemberId, ExportType.PROFESSIONAL, ExportFormat.MARKDOWN)
                                                showExportModal = true
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextOffWhite),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, EmpireSurfaceBorderLight),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("Export", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Terminal output if any command executed
            if (botTerminalOutput != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmpireSurfaceElevated)
                        .border(1.dp, EmpireAmber.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            botTerminalOutput!!,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = TextOffWhite,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ----------------------------------------------------
            // 3. SLASH COMMAND AUTOCOMPLETE INPUT PROMPT
            // ----------------------------------------------------
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Suggestions chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(autocompleteSuggestions) { sug ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (commandInput == sug) EmpireAmber.copy(alpha = 0.2f) else EmpireSurfaceElevated)
                                .border(1.dp, if (commandInput == sug) EmpireAmber else EmpireSurfaceBorder, RoundedCornerShape(4.dp))
                                .clickable {
                                    commandInput = sug
                                    repository.executeSlashCommand(sug)
                                }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                sug,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = if (commandInput == sug) EmpireAmber else TextDim,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                // Command text field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = commandInput,
                        onValueChange = { commandInput = it },
                        placeholder = { Text("Type /timeline command...") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmpireDiscordIndigo,
                            unfocusedBorderColor = EmpireSurfaceBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    IconButton(
                        onClick = {
                            if (commandInput.isNotBlank()) {
                                lastExecutedCommand = commandInput
                                repository.executeSlashCommand(commandInput)
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmpireDiscordIndigo)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }

    // ----------------------------------------------------
    // DIALOGS & MODALS
    // ----------------------------------------------------
    if (showAddModal) {
        AddTimelineEntryModal(
            memberId = selectedMemberId,
            onDismiss = { showAddModal = false },
            onSubmit = { title, bullets, projectId, daysAgo ->
                coroutineScope.launch {
                    val res = repository.timelineService.createMemberEntry(
                        memberId = selectedMemberId,
                        title = title,
                        bullets = bullets,
                        projectId = projectId,
                        daysAgo = daysAgo
                    )
                    showAddModal = false
                    if (res.isSuccess) {
                        Toast.makeText(context, "Milestone posted to timeline!", Toast.LENGTH_SHORT).show()
                        repository.refreshTimelineData()
                    } else {
                        Toast.makeText(context, res.exceptionOrNull()?.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }

    if (showShareModal && activePublicShare != null) {
        PublicShareModal(
            shareDetails = activePublicShare!!,
            onDismiss = { showShareModal = false },
            onRevoke = {
                coroutineScope.launch {
                    repository.timelineService.revokePublicShare(selectedMemberId)
                    showShareModal = false
                    Toast.makeText(context, "Public share revoked.", Toast.LENGTH_SHORT).show()
                    repository.refreshTimelineData()
                }
            },
            onOpenPublicPreview = {
                coroutineScope.launch {
                    val pubData = repository.timelineService.getPublicTimeline(activePublicShare!!.token)
                    publicPreviewData = pubData
                    showPublicPreviewModal = true
                }
            }
        )
    }

    if (showPublicPreviewModal && publicPreviewData != null) {
        PublicTimelineWebDialog(
            publicData = publicPreviewData!!,
            onDismiss = { showPublicPreviewModal = false }
        )
    }

    if (showExportModal) {
        var expType by remember { mutableStateOf(ExportType.PROFESSIONAL) }
        var expFmt by remember { mutableStateOf(ExportFormat.MARKDOWN) }
        var curDoc by remember { mutableStateOf<ExportDocument?>(null) }

        LaunchedEffect(expType, expFmt) {
            val doc = repository.timelineService.exportTimeline(selectedMemberId, expType, expFmt)
            curDoc = doc
        }

        if (curDoc != null) {
            TimelineExportModal(
                exportDoc = curDoc!!,
                onDismiss = { showExportModal = false },
                onFormatChange = { expFmt = it },
                onTypeChange = { expType = it }
            )
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmItem != null) {
        val itemToDelete = showDeleteConfirmItem!!
        AlertDialog(
            onDismissRequest = { showDeleteConfirmItem = null },
            title = { Text("Delete Timeline Entry?", color = TextWhite) },
            text = {
                Text(
                    "Are you sure you want to delete \"${itemToDelete.title}\"?\nThis cannot be undone.",
                    color = TextOffWhite
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val res = repository.timelineService.deleteMemberEntry(itemToDelete.id, selectedMemberId)
                            showDeleteConfirmItem = null
                            if (res.isSuccess) {
                                Toast.makeText(context, "Entry deleted.", Toast.LENGTH_SHORT).show()
                                repository.refreshTimelineData()
                            } else {
                                Toast.makeText(context, res.exceptionOrNull()?.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmpireRose)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmItem = null }) {
                    Text("Cancel", color = TextDim)
                }
            },
            containerColor = EmpireSurfaceCard
        )
    }

    // Health & System Health Modal
    if (showHealthModal) {
        AlertDialog(
            onDismissRequest = { showHealthModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.MonitorHeart, contentDescription = null, tint = EmpireEmerald)
                    Text("Timeline System Health", color = TextWhite)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🟢 Gateway: ${systemHealth.discordGatewayStatus}", style = MaterialTheme.typography.bodySmall.copy(color = TextOffWhite))
                    Text("🟢 Database: ${systemHealth.databaseStatus}", style = MaterialTheme.typography.bodySmall.copy(color = TextOffWhite))
                    Text("🟢 Event Ingestion: ${systemHealth.eventProcessorStatus}", style = MaterialTheme.typography.bodySmall.copy(color = TextOffWhite))
                    Text("🟢 24h Public Shares: ${systemHealth.publicShareServiceStatus}", style = MaterialTheme.typography.bodySmall.copy(color = TextOffWhite))
                }
            },
            confirmButton = {
                Button(
                    onClick = { showHealthModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmpireEmerald, contentColor = Color.Black)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = EmpireSurfaceCard
        )
    }

    // Multi-bot Ingestion Event Simulator Modal
    if (showEventSimulatorModal) {
        AlertDialog(
            onDismissRequest = { showEventSimulatorModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = EmpireAmber)
                    Text("Simulate Multi-Bot Event Ingestion", color = TextWhite)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Trigger incoming events from future Jobless Empire ecosystem bots. Tested with idempotency deduplication so repeated webhooks don't duplicate records.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextOffWhite)
                    )

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                repository.simulateExternalBotEvent(
                                    source = "project-bot",
                                    eventType = "PROJECT_MILESTONE",
                                    title = "Reached Sprint Milestone in P#003",
                                    bullets = listOf("Completed GraphQL resolver suite", "Tested sub-second latency"),
                                    projectId = "P#003",
                                    sourceEventId = "evt-sprint-${System.currentTimeMillis() % 1000}"
                                )
                                showEventSimulatorModal = false
                                Toast.makeText(context, "Event recorded by project-bot!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = EmpireSurfaceElevated, contentColor = EmpireAmber)
                    ) {
                        Text("Trigger: project-bot Milestone")
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                repository.simulateExternalBotEvent(
                                    source = "meeting-bot",
                                    eventType = "MEETING_PARTICIPATION",
                                    title = "Participated in P#003 Sprint Standup",
                                    bullets = listOf("Attended 30-min voice room sync", "Aligned sprint tasks with squad"),
                                    projectId = "P#003",
                                    sourceEventId = "evt-meet-${System.currentTimeMillis() % 1000}"
                                )
                                showEventSimulatorModal = false
                                Toast.makeText(context, "Meeting participation recorded!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = EmpireSurfaceElevated, contentColor = EmpireCyan)
                    ) {
                        Text("Trigger: meeting-bot Attendance")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showEventSimulatorModal = false }) {
                    Text("Done", color = TextDim)
                }
            },
            containerColor = EmpireSurfaceCard
        )
    }
}

// ----------------------------------------------------
// INDIVIDUAL DISCORD TIMELINE ENTRY CARD COMPOSABLE
// ----------------------------------------------------
@Composable
fun DiscordTimelineEntryCard(
    item: TimelineItem,
    viewMode: TimelineViewMode,
    onDeleteClick: () -> Unit
) {
    when (viewMode) {
        TimelineViewMode.COMPACT -> {
            // Single-line compact row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(EmpireSurfaceCard)
                    .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        item.formattedDate,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = EmpireAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    if (item.projectId != null) {
                        IdBadge(item.projectId)
                    }
                    Text(
                        item.title,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        maxLines = 1
                    )
                }

                Text(
                    if (item.isImmutable) "🔒" else "✎",
                    fontSize = 11.sp
                )
            }
        }
        TimelineViewMode.STANDARD, TimelineViewMode.DETAILED -> {
            // Standard / Detailed card with bullets and badges
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(EmpireSurfaceCard)
                    .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "${item.formattedDate} · ${item.formattedTime}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = EmpireAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                        if (item.projectId != null) {
                            IdBadge(item.projectId)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (item.isImmutable) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(EmpireBackground)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "🔒 Official",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = EmpireEmerald,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(EmpireBackground)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "✎ Member Entry",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = EmpireAmber,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        // Delete icon for editable entry
                        if (!item.isImmutable) {
                            IconButton(
                                onClick = onDeleteClick,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextDim, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }

                Text(
                    item.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        fontSize = 12.sp
                    )
                )

                // Bullets
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    item.bullets.forEach { bullet ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("•", color = TextDim, fontSize = 11.sp)
                            Text(
                                bullet,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextOffWhite,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            )
                        }
                    }
                }

                // Detailed view metadata footer
                if (viewMode == TimelineViewMode.DETAILED) {
                    Divider(color = EmpireSurfaceBorder)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Source: ${item.source} • Created by: ${item.createdBy}",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextDim, fontSize = 9.sp)
                        )
                        Text(
                            "ID: ${item.id}",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, color = TextDim, fontSize = 9.sp)
                        )
                    }
                }
            }
        }
    }
}
