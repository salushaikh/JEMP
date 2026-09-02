package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*

// ----------------------------------------------------
// 1. ADD MEMBER TIMELINE ENTRY MODAL (/timeline add)
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTimelineEntryModal(
    memberId: String,
    onDismiss: () -> Unit,
    onSubmit: (title: String, bullets: List<String>, projectId: String?, daysAgo: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var bullet1 by remember { mutableStateOf("") }
    var bullet2 by remember { mutableStateOf("") }
    var bullet3 by remember { mutableStateOf("") }
    var projectId by remember { mutableStateOf("") }
    var daysAgo by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = EmpireSurfaceCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, EmpireSurfaceBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(EmpireAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✎", color = EmpireAmber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column {
                            Text(
                                "Add Timeline Entry",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            )
                            Text(
                                "/timeline add • $memberId",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = EmpireAmber
                                )
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDim)
                    }
                }

                Divider(color = EmpireSurfaceBorder)

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Entry Title") },
                    placeholder = { Text("e.g. Completed frontend architecture") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmpireAmber,
                        unfocusedBorderColor = EmpireSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                // Bullets Input (1-3 bullets, max 300 chars)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Contribution Bullets (1–3 key achievements, max 300 chars)",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )

                    OutlinedTextField(
                        value = bullet1,
                        onValueChange = { if (it.length <= 300) bullet1 = it },
                        placeholder = { Text("Bullet 1: Main milestone or delivery") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmpireAmber,
                            unfocusedBorderColor = EmpireSurfaceBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    OutlinedTextField(
                        value = bullet2,
                        onValueChange = { if (it.length <= 300) bullet2 = it },
                        placeholder = { Text("Bullet 2: Technical detail or metric (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmpireAmber,
                            unfocusedBorderColor = EmpireSurfaceBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    OutlinedTextField(
                        value = bullet3,
                        onValueChange = { if (it.length <= 300) bullet3 = it },
                        placeholder = { Text("Bullet 3: Impact or review result (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmpireAmber,
                            unfocusedBorderColor = EmpireSurfaceBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                }

                // Project identifier
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = projectId,
                        onValueChange = { projectId = it },
                        label = { Text("Project (Optional)") },
                        placeholder = { Text("e.g. P#003, B#007") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmpireAmber,
                            unfocusedBorderColor = EmpireSurfaceBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    // Quick Project chips
                    listOf("P#003", "B#007", "HC#004").forEach { chip ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (projectId == chip) EmpireAmber else EmpireSurfaceElevated)
                                .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(6.dp))
                                .clickable { projectId = chip }
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Text(
                                chip,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if (projectId == chip) Color.Black else TextOffWhite
                                )
                            )
                        }
                    }
                }

                // Date Backdating slider (up to 30 days)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Record Date:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                        Text(
                            if (daysAgo == 0) "Today (Current)" else "$daysAgo days ago (Backdated)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = EmpireAmber,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Slider(
                        value = daysAgo.toFloat(),
                        onValueChange = { daysAgo = it.toInt() },
                        valueRange = 0f..30f,
                        steps = 29,
                        colors = SliderDefaults.colors(
                            thumbColor = EmpireAmber,
                            activeTrackColor = EmpireAmber,
                            inactiveTrackColor = EmpireSurfaceBorder
                        )
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodySmall.copy(color = EmpireRose)
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextDim)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                errorMessage = "Please enter a title."
                                return@Button
                            }
                            val bullets = listOf(bullet1, bullet2, bullet3).filter { it.isNotBlank() }
                            if (bullets.isEmpty()) {
                                errorMessage = "Please provide at least 1 bullet point."
                                return@Button
                            }
                            onSubmit(
                                title.trim(),
                                bullets,
                                projectId.ifBlank { null },
                                daysAgo
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmpireAmber,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Post Entry", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 2. 24-HOUR PUBLIC SHARE MODAL (/timeline share)
// ----------------------------------------------------
@Composable
fun PublicShareModal(
    shareDetails: PublicShareDetails,
    onDismiss: () -> Unit,
    onRevoke: () -> Unit,
    onOpenPublicPreview: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = EmpireSurfaceCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, EmpireCyan.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Public, contentDescription = null, tint = EmpireCyan, modifier = Modifier.size(24.dp))
                        Column {
                            Text(
                                "24-Hour Public Proof Link",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            )
                            Text(
                                "Auto-expiring cryptographic token",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextDim)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDim)
                    }
                }

                Divider(color = EmpireSurfaceBorder)

                // Info Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmpireCyan.copy(alpha = 0.1f))
                        .border(1.dp, EmpireCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("STATUS: ACTIVE", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, color = EmpireCyan, fontWeight = FontWeight.Bold))
                            Text("Expires in ${shareDetails.remainingHoursMinutes}", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, color = EmpireAmber, fontWeight = FontWeight.Bold))
                        }
                        Text(
                            "Anyone with this link can view your public-safe verified proof of work. Private contact info, resume files, and admin notes remain hidden.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextOffWhite, lineHeight = 16.sp)
                        )
                    }
                }

                // Copyable URL box
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Secure Share URL", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmpireBackground)
                            .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            shareDetails.shareUrl,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = EmpireAmberLight,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Jobless Empire Share URL", shareDetails.shareUrl)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied Share URL to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = EmpireAmber, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenPublicPreview,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EmpireCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmpireCyan.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Preview Web View", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onRevoke,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmpireRose, contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.LinkOff, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Revoke Link", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 3. PUBLIC TIMELINE WEB VIEW DIALOG
// ----------------------------------------------------
@Composable
fun PublicTimelineWebDialog(
    publicData: PublicTimelineViewData,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            color = EmpireBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, EmpireSurfaceBorderLight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header with Browser-like bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmpireSurfaceElevated)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmpireRose))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmpireAmber))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmpireEmerald))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "https://timeline.joblessempire.com/share/...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = TextDim,
                                fontSize = 10.sp
                            )
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDim, modifier = Modifier.size(16.dp))
                    }
                }

                if (!publicData.isValid) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = EmpireRose, modifier = Modifier.size(48.dp))
                            Text(
                                publicData.expirationNotice ?: "THIS TIMELINE SHARE HAS EXPIRED.",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmpireRose
                                )
                            )
                            Text(
                                "Public proof links are valid for exactly 24 hours.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextDim)
                            )
                        }
                    }
                } else {
                    // Valid Public Page Content
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IdBadge(publicData.memberDisplayId)
                                    Text(
                                        publicData.displayName,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            color = TextWhite
                                        )
                                    )
                                }
                                Text(
                                    "${publicData.specialty} • Member since ${publicData.joinedDateFormatted}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(EmpireCyan.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "24h Proof View",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = EmpireCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Divider(color = EmpireSurfaceBorder)

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(publicData.entries) { entry ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(EmpireSurface)
                                    .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "${entry.formattedDate} · ${entry.formattedTime}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = EmpireAmber,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    if (entry.projectId != null) {
                                        IdBadge(entry.projectId)
                                    }
                                }

                                Text(
                                    entry.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                )

                                entry.bullets.forEach { b ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text("•", color = TextDim)
                                        Text(b, style = MaterialTheme.typography.bodySmall.copy(color = TextOffWhite))
                                    }
                                }
                            }
                        }
                    }

                    // Footer
                    Text(
                        "🔒 Verified Jobless Empire Proof of Work • Auto-expires in ${publicData.shareExpiresIn}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextDim,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// 4. TIMELINE EXPORT MODAL (/timeline export)
// ----------------------------------------------------
@Composable
fun TimelineExportModal(
    exportDoc: ExportDocument,
    onDismiss: () -> Unit,
    onFormatChange: (ExportFormat) -> Unit,
    onTypeChange: (ExportType) -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            color = EmpireSurfaceCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, EmpireSurfaceBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = EmpireAmber)
                        Column {
                            Text(
                                "Export Proof of Work",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            )
                            Text(
                                "/timeline export • ${exportDoc.memberId}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = EmpireAmber
                                )
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDim)
                    }
                }

                // Export Type selector (Professional vs Full Personal)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExportType.values().forEach { t ->
                        val isSelected = exportDoc.exportType == t
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) EmpireAmber else EmpireSurfaceElevated)
                                .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable { onTypeChange(t) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (t == ExportType.PROFESSIONAL) "Professional Export" else "Full Personal Export",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else TextOffWhite
                                )
                            )
                        }
                    }
                }

                // Format pills (Markdown, JSON, Plaintext)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ExportFormat.values().forEach { fmt ->
                        val isSelected = exportDoc.format == fmt
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) EmpireCyan.copy(alpha = 0.2f) else EmpireSurface)
                                .border(1.dp, if (isSelected) EmpireCyan else EmpireSurfaceBorder, RoundedCornerShape(6.dp))
                                .clickable { onFormatChange(fmt) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                fmt.name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSelected) EmpireCyan else TextMuted,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Divider(color = EmpireSurfaceBorder)

                // Document Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmpireBackground)
                        .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    LazyColumn {
                        item {
                            Text(
                                text = exportDoc.content,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = TextOffWhite,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }

                // Copy & Share buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Jobless Empire Timeline Export", exportDoc.content)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Export copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmpireAmber, contentColor = Color.Black)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Document", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
