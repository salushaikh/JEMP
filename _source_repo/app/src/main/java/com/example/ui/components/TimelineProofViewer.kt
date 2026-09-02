package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.data.model.MemberProfile
import com.example.data.model.TimelineCategory
import com.example.data.model.TimelineEntry
import com.example.ui.theme.*

@Composable
fun TimelineProofViewer(
    profile: MemberProfile,
    allProfiles: Map<String, MemberProfile>,
    onSelectMember: (String) -> Unit,
    onToggle24hShare: (String) -> Unit,
    onExportPreview: () -> Unit,
    onAddMilestoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(EmpireSurface)
            .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(16.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Switcher & Identification
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IdBadge(profile.id)
                    Text(
                        profile.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    )
                }
                Text(
                    "${profile.specialty} • Joined ${profile.joinedDate}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                )
            }

            // Member Switcher Dropdown/Chips
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                allProfiles.keys.forEach { id ->
                    val isSelected = id == profile.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) EmpireAmber.copy(alpha = 0.2f) else EmpireSurfaceElevated)
                            .border(
                                1.dp,
                                if (isSelected) EmpireAmber else EmpireSurfaceBorderLight,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { onSelectMember(id) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            id,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = if (isSelected) EmpireAmber else TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Privacy & Share Controls Bar (PRIVATE 🔒 -> 24H SHARE -> EXPIRES)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(EmpireSurfaceCard)
                .border(1.dp, EmpireSurfaceBorderLight, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (profile.isPublic24hShared) Icons.Default.Public else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (profile.isPublic24hShared) EmpireCyan else EmpireAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                if (profile.isPublic24hShared) "PUBLIC PROOF LINK (24H TEMPORARY)" else "PRIVATE TIMELINE (DEFAULT)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if (profile.isPublic24hShared) EmpireCyan else TextWhite
                                )
                            )
                            Text(
                                if (profile.isPublic24hShared) "Auto-expires in 17h 48m • Anyone with link can view verified entries" else "Only you can see this timeline until you create a temporary 24h link",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextDim,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Toggle Button
                    Button(
                        onClick = { onToggle24hShare(profile.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (profile.isPublic24hShared) EmpireCyan.copy(alpha = 0.2f) else EmpireSurfaceElevated,
                            contentColor = if (profile.isPublic24hShared) EmpireCyan else TextWhite
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (profile.isPublic24hShared) EmpireCyan else EmpireSurfaceBorderLight
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            if (profile.isPublic24hShared) "Disable Link" else "Share for 24h",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                // Action Links: Export & Add Milestone
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onExportPreview,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = EmpireAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Export Proof Document",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmpireAmber,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    TextButton(
                        onClick = onAddMilestoneClick,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = null,
                            tint = TextOffWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Add Custom Milestone",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextOffWhite,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }

        // Timeline Entries (The Living Trail)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            profile.timeline.forEachIndexed { index, entry ->
                TimelineEntryRow(
                    entry = entry,
                    isLast = index == profile.timeline.lastIndex
                )
            }
        }
    }
}

@Composable
fun TimelineEntryRow(
    entry: TimelineEntry,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Timeline stem & node
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(
                        when (entry.category) {
                            TimelineCategory.ONBOARDING -> EmpireAmber
                            TimelineCategory.BRAINSTORM -> StatusBrainstorming
                            TimelineCategory.PROJECT -> StatusInProgress
                            TimelineCategory.CLIENT_WORK -> StatusClientProject
                            TimelineCategory.MEETING -> EmpireCyan
                            TimelineCategory.MILESTONE -> EmpireEmerald
                        }
                    )
                    .border(2.dp, EmpireSurface, CircleShape)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(EmpireSurfaceBorderLight)
                )
            }
        }

        // Entry content
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(EmpireSurfaceElevated)
                .border(1.dp, EmpireSurfaceBorderLight, RoundedCornerShape(10.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    entry.date,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = EmpireAmber,
                        fontWeight = FontWeight.Bold
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (entry.linkedReferenceId != null) {
                        IdBadge(entry.linkedReferenceId)
                    }
                    if (entry.isSystemGenerated) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(EmpireBackground)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "VERIFIED RECORD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.sp,
                                    color = EmpireEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            Text(
                entry.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                entry.details.forEach { bullet ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            "•",
                            color = TextDim,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            bullet,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextOffWhite,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
