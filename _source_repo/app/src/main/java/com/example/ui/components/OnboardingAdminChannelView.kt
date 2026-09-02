package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.local.entities.ApplicationEntity
import com.example.data.model.OnboardingDashboardMetrics
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OnboardingAdminChannelView(
    applications: List<ApplicationEntity>,
    dashboardMetrics: OnboardingDashboardMetrics?,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onWaitlist: (String) -> Unit,
    onRevoke: (String) -> Unit,
    onWhitelistDirect: (String, String) -> Unit,
    onRunSlashCommand: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterStatus by remember { mutableStateOf("ALL") }
    var showDirectWhitelistDialog by remember { mutableStateOf(false) }
    var directEmail by remember { mutableStateOf("") }
    var directName by remember { mutableStateOf("") }

    val filteredApps = remember(applications, searchQuery, filterStatus) {
        applications.filter { app ->
            val matchesQuery = searchQuery.isBlank() ||
                    app.fullName.contains(searchQuery, ignoreCase = true) ||
                    app.email.contains(searchQuery, ignoreCase = true) ||
                    app.applicationId.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (filterStatus) {
                "ALL" -> true
                else -> app.status.equals(filterStatus, ignoreCase = true)
            }
            matchesQuery && matchesFilter
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Channel Header
        item {
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
                        "applications",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(EmpireRose.copy(alpha = 0.2f))
                            .border(1.dp, EmpireRose.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "ADMIN ONLY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = EmpireRose,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Button(
                    onClick = { showDirectWhitelistDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EmpireAmber, contentColor = Color.Black),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("DIRECT WHITELIST", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        // Metrics Banner
        item {
            if (dashboardMetrics != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmpireSurfaceElevated)
                        .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "ONBOARDING & MEMBERSHIP PIPELINE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = EmpireAmber,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricPill("Pending", dashboardMetrics.pendingApplicationsCount.toString(), EmpireAmber)
                        MetricPill("Approved", dashboardMetrics.approvedApplicationsCount.toString(), EmpireEmerald)
                        MetricPill("Waitlisted", dashboardMetrics.waitlistedApplicationsCount.toString(), Color(0xFFFBBF24))
                        MetricPill("Active Mbrs", dashboardMetrics.activeMembersCount.toString(), Color(0xFF38BDF8))
                    }
                }
            }
        }

        // Quick Slash Commands Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onRunSlashCommand("/onboarding-admin dashboard") },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmpireAmberLight),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("/dashboard", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace))
                }
                OutlinedButton(
                    onClick = { onRunSlashCommand("/onboarding-admin seed-demo") },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmpireEmerald),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+ Seed Demo", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace))
                }
                OutlinedButton(
                    onClick = { onRunSlashCommand("/onboarding-admin clear-demo") },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear Demo", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace))
                }
            }
        }

        // Filter and Search Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search applicants by name, email, or APP ID...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmpireAmber,
                        unfocusedBorderColor = EmpireSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextOffWhite
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("ALL", "PENDING", "APPROVED", "WAITLISTED", "REJECTED").forEach { status ->
                        FilterChip(
                            selected = filterStatus == status,
                            onClick = { filterStatus = status },
                            label = { Text(status, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmpireAmber,
                                selectedLabelColor = Color.Black,
                                containerColor = EmpireSurfaceElevated,
                                labelColor = TextMuted
                            )
                        )
                    }
                }
            }
        }

        // List of Applications
        if (filteredApps.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No applications match the selected filter.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                    )
                }
            }
        } else {
            items(filteredApps, key = { it.applicationId }) { app ->
                ApplicationAdminCard(
                    application = app,
                    onApprove = { onApprove(app.applicationId) },
                    onReject = { onReject(app.applicationId) },
                    onWaitlist = { onWaitlist(app.applicationId) },
                    onRevoke = { onRevoke(app.applicationId) }
                )
            }
        }
    }

    if (showDirectWhitelistDialog) {
        AlertDialog(
            onDismissRequest = { showDirectWhitelistDialog = false },
            title = { Text("Direct Candidate Whitelist", color = TextWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Whitelisting an email bypasses standard website review and generates an active invite code.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                    OutlinedTextField(
                        value = directName,
                        onValueChange = { directName = it },
                        label = { Text("Candidate Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = directEmail,
                        onValueChange = { directEmail = it },
                        label = { Text("Candidate Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (directEmail.isNotBlank() && directName.isNotBlank()) {
                            onWhitelistDirect(directEmail.trim(), directName.trim())
                            showDirectWhitelistDialog = false
                            directEmail = ""
                            directName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmpireAmber, contentColor = Color.Black)
                ) {
                    Text("WHITELIST NOW")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDirectWhitelistDialog = false }) {
                    Text("CANCEL", color = TextMuted)
                }
            },
            containerColor = EmpireSurfaceCard
        )
    }
}

@Composable
fun ApplicationAdminCard(
    application: ApplicationEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onWaitlist: () -> Unit,
    onRevoke: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = remember(application.submittedAtUtc) {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US)
        sdf.format(Date(application.submittedAtUtc))
    }

    val statusColor = when (application.status) {
        "APPROVED" -> EmpireEmerald
        "PENDING" -> EmpireAmber
        "WAITLISTED" -> Color(0xFFFBBF24)
        "REJECTED" -> EmpireRose
        "REVOKED" -> Color(0xFF991B1B)
        else -> TextMuted
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(EmpireSurfaceElevated)
            .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    application.fullName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                )
                Text(
                    application.applicationId,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = EmpireAmberLight,
                        fontSize = 11.sp
                    )
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    application.status,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Divider(color = EmpireSurfaceBorder)

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Email:", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                Text(application.email, style = MaterialTheme.typography.bodySmall.copy(color = TextWhite, fontFamily = FontFamily.Monospace))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Resume:", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                Text("${application.resumeFileName} (${application.resumeFileSize})", style = MaterialTheme.typography.bodySmall.copy(color = TextOffWhite))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Submitted:", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                Text(dateStr, style = MaterialTheme.typography.bodySmall.copy(color = TextDim))
            }
            if (application.inviteCode != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Invite Code:", style = MaterialTheme.typography.bodySmall.copy(color = EmpireEmerald))
                    Text(application.inviteCode, style = MaterialTheme.typography.bodySmall.copy(color = EmpireEmerald, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                }
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (application.status == "PENDING" || application.status == "WAITLISTED") {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = EmpireEmerald, contentColor = Color.Black),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("APPROVE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
                OutlinedButton(
                    onClick = onWaitlist,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFBBF24)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("WAITLIST", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmpireRose),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("REJECT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            } else if (application.status == "APPROVED") {
                OutlinedButton(
                    onClick = onRevoke,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmpireRose),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("REVOKE APPROVAL", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@Composable
private fun MetricPill(label: String, count: String, color: Color) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(EmpireSurface)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            count,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = color
            )
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextMuted,
                fontSize = 10.sp
            )
        )
    }
}
