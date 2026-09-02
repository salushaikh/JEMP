package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ApplicationSubmission
import com.example.data.model.MemberProfile
import com.example.ui.theme.*

@Composable
fun AdminApplicationPreviewDialog(
    submission: ApplicationSubmission,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(EmpireSurface)
                .border(1.dp, EmpireAmber.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
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
                        imageVector = Icons.Default.MarkEmailRead,
                        contentDescription = null,
                        tint = EmpireAmber
                    )
                    Text(
                        "ADMIN EMAIL DISPATCH ARCHITECTURE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = EmpireAmber
                        )
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextDim)
                }
            }

            Text(
                "In production, our server-side API dispatches this structured email directly to the Administrator inbox with Reply-To set to the applicant.",
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
            )

            // Email Card Mockup
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(EmpireSurfaceElevated)
                    .border(1.dp, EmpireSurfaceBorderLight, RoundedCornerShape(10.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Subject:",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextDim)
                    )
                    Text(
                        "${submission.fullName}'s application for joining Jobless Empire",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EmpireAmberLight,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Reply-To:",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextDim)
                    )
                    Text(
                        submission.email,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EmpireCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                Divider(color = EmpireSurfaceBorder)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(EmpireBackground)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Applicant: ${submission.fullName}", style = MaterialTheme.typography.bodySmall.copy(color = TextWhite, fontWeight = FontWeight.Bold))
                    Text("Email: ${submission.email}", style = MaterialTheme.typography.bodySmall.copy(color = TextOffWhite))
                    Text("Status: ${if (submission.isCurrentlyUnemployed) "Currently Jobless/Unemployed (Verified)" else "Employed"}", style = MaterialTheme.typography.bodySmall.copy(color = EmpireEmerald))
                    Text("Consent: True (Accepted Review Terms)", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                    Text("Timestamp: ${submission.timestamp}", style = MaterialTheme.typography.bodySmall.copy(color = TextDim))
                    Text("Provisional ID: ${submission.assignedJempId}", style = MaterialTheme.typography.bodySmall.copy(color = EmpireAmber))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AttachFile, contentDescription = null, tint = EmpireAmber, modifier = Modifier.size(14.dp))
                        Text("Attached: ${submission.resumeFileName} (${submission.resumeFileSize})", style = MaterialTheme.typography.labelSmall.copy(color = EmpireAmberLight))
                    }
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmpireAmber,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("GOT IT — SECURE SYSTEM", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun TimelineExportDialog(
    profile: MemberProfile,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(EmpireSurface)
                .border(1.dp, EmpireEmerald.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = EmpireEmerald)
                    Text(
                        "PROOF-OF-WORK VERIFICATION DOCUMENT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = EmpireEmerald
                        )
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextDim)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 340.dp)
                    .verticalScroll(rememberScrollState())
                    .clip(RoundedCornerShape(8.dp))
                    .background(EmpireSurfaceElevated)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "JOBLESS EMPIRE // PERMANENT TIMELINE ATTESTATION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = EmpireAmber,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    "Member: ${profile.name} (${profile.id})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextWhite)
                )
                Text(
                    "Specialty: ${profile.specialty} • Timezone: ${profile.timezone}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                )
                Divider(color = EmpireSurfaceBorder)

                profile.timeline.forEach { entry ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(entry.date, style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, color = EmpireAmber))
                            if (entry.linkedReferenceId != null) {
                                IdBadge(entry.linkedReferenceId)
                            }
                        }
                        Text(entry.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = TextWhite))
                        entry.details.forEach { detail ->
                            Text("• $detail", style = MaterialTheme.typography.bodySmall.copy(color = TextOffWhite, fontSize = 11.sp))
                        }
                    }
                    Divider(color = EmpireSurfaceBorder.copy(alpha = 0.5f))
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EmpireEmerald, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("CLOSE EXPORT PREVIEW", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}
