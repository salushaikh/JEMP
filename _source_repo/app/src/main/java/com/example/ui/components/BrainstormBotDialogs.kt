package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.data.local.entities.BrainstormEntity

// -------------------------------------------------------------------------
// 1. CREATE BRAINSTORM DIALOG (Interactive Wizard)
// -------------------------------------------------------------------------
@Composable
fun CreateBrainstormDialog(
    currentMemberId: String,
    onDismiss: () -> Unit,
    onSubmit: (BrainstormCreationDraft) -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: Concept, 2: Squad & Scope, 3: Review
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var problem by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf("") }
    var targetAudience by remember { mutableStateOf("") }
    var commitmentLevel by remember { mutableStateOf("Serious build candidate") }

    val availableSkills = listOf("Developer", "Designer", "Product", "AI/ML", "Marketing", "Research", "Sales", "DevOps")
    val selectedSkills = remember { mutableStateListOf("Developer", "Designer") }

    val availableTags = listOf("AI", "SaaS", "Mobile", "Web", "Automation", "Career", "OpenSource", "Community", "Tools", "Fintech")
    val selectedTags = remember { mutableStateListOf("AI", "SaaS") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E222B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF8B5CF6).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFC084FC), modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text(
                                text = "Create Brainstorm",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Step $step of 3 • /brainstorm create",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                // Step progress bar
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.weight(1f).height(4.dp).clip(CircleShape).background(if (step >= 1) Color(0xFF8B5CF6) else Color(0xFF334155)))
                    Box(modifier = Modifier.weight(1f).height(4.dp).clip(CircleShape).background(if (step >= 2) Color(0xFF8B5CF6) else Color(0xFF334155)))
                    Box(modifier = Modifier.weight(1f).height(4.dp).clip(CircleShape).background(if (step >= 3) Color(0xFF8B5CF6) else Color(0xFF334155)))
                }

                errorMessage?.let { err ->
                    Surface(
                        color = Color(0xFFEF4444).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⚠ $err",
                            color = Color(0xFFFCA5A5),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                when (step) {
                    1 -> {
                        // Step 1: Core Concept
                        Text("1. Concept & Problem Definition", fontWeight = FontWeight.SemiBold, color = Color(0xFFE2E8F0), fontSize = 14.sp)

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it; errorMessage = null },
                            label = { Text("Idea Title * (max 80 chars)") },
                            placeholder = { Text("e.g. AI Resume Analyzer") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B5CF6),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it; errorMessage = null },
                            label = { Text("Detailed Overview *") },
                            placeholder = { Text("What is the core idea? What will the squad build?") },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B5CF6),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = problem,
                            onValueChange = { problem = it },
                            label = { Text("Problem Being Solved") },
                            placeholder = { Text("Why does this problem exist? Who suffers from it?") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B5CF6),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = direction,
                            onValueChange = { direction = it },
                            label = { Text("Proposed Technical / Product Direction") },
                            placeholder = { Text("e.g. On-device parser + Gemini API + Jetpack Compose") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B5CF6),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }
                    2 -> {
                        // Step 2: Squad, Commitment, & Audience
                        Text("2. Target Audience & Collaboration Squad", fontWeight = FontWeight.SemiBold, color = Color(0xFFE2E8F0), fontSize = 14.sp)

                        OutlinedTextField(
                            value = targetAudience,
                            onValueChange = { targetAudience = it },
                            label = { Text("Target Audience") },
                            placeholder = { Text("e.g. Job seekers, indie creators, freelancers") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B5CF6),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Text("Commitment Level", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                        val commitments = listOf(
                            "Casual brainstorming",
                            "Research & exploration",
                            "Short experiment",
                            "Serious build candidate",
                            "Founder/startup exploration"
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            commitments.forEach { comm ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { commitmentLevel = comm },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (commitmentLevel == comm) Color(0xFF8B5CF6).copy(alpha = 0.25f) else Color(0xFF0F172A),
                                    border = if (commitmentLevel == comm) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8B5CF6)) else null
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        RadioButton(
                                            selected = commitmentLevel == comm,
                                            onClick = { commitmentLevel = comm },
                                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFC084FC))
                                        )
                                        Text(comm, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }

                        Text("Desired Collaborator Skills", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(availableSkills) { skill ->
                                val isSel = selectedSkills.contains(skill)
                                FilterChip(
                                    selected = isSel,
                                    onClick = {
                                        if (isSel) selectedSkills.remove(skill) else selectedSkills.add(skill)
                                    },
                                    label = { Text(skill, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF8B5CF6),
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFF0F172A),
                                        labelColor = Color(0xFF94A3B8)
                                    )
                                )
                            }
                        }

                        Text("Tags & Domains", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(availableTags) { tag ->
                                val isSel = selectedTags.contains(tag)
                                FilterChip(
                                    selected = isSel,
                                    onClick = {
                                        if (isSel) selectedTags.remove(tag) else selectedTags.add(tag)
                                    },
                                    label = { Text("#$tag", fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF06B6D4),
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFF0F172A),
                                        labelColor = Color(0xFF94A3B8)
                                    )
                                )
                            }
                        }
                    }
                    3 -> {
                        // Step 3: Review & Submit
                        Text("3. Confirm & Publish to Discord", fontWeight = FontWeight.SemiBold, color = Color(0xFFE2E8F0), fontSize = 14.sp)

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("PROPOSED TITLE:", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Text("OVERVIEW: $description", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                                if (problem.isNotBlank()) {
                                    Text("PROBLEM: $problem", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                }
                                Text("COMMITMENT: $commitmentLevel", color = Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("CREATOR: $currentMemberId (Owner)", color = Color(0xFF10B981), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                Text("DESIRED SKILLS: ${selectedSkills.joinToString(", ")}", color = Color(0xFFC084FC), fontSize = 11.sp)
                                Text("TAGS: ${selectedTags.joinToString(" ") { "#$it" }}", color = Color(0xFF38BDF8), fontSize = 11.sp)
                            }
                        }

                        Surface(
                            color = Color(0xFF10B981).copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Creating will automatically assign you as OWNER, generate a unique B# ID, create a Discord thread, and emit an attestation to your canonical Timeline.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFA7F3D0)
                                )
                            }
                        }
                    }
                }

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { step-- },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFCBD5E1))
                        ) {
                            Text("Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Button(
                        onClick = {
                            if (step == 1) {
                                if (title.isBlank()) {
                                    errorMessage = "Title is required."
                                    return@Button
                                }
                                if (description.isBlank()) {
                                    errorMessage = "Description is required."
                                    return@Button
                                }
                                step = 2
                            } else if (step == 2) {
                                step = 3
                            } else {
                                onSubmit(
                                    BrainstormCreationDraft(
                                        title = title,
                                        description = description,
                                        problem = problem,
                                        direction = direction,
                                        targetAudience = targetAudience,
                                        desiredSkills = selectedSkills.toList(),
                                        commitmentLevel = commitmentLevel,
                                        tags = selectedTags.toList()
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                    ) {
                        Text(if (step == 3) "🚀 Publish Brainstorm" else "Continue")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 2. APPLY TO CONTRIBUTE DIALOG (/brainstorm apply)
// -------------------------------------------------------------------------
@Composable
fun ApplyToBrainstormDialog(
    brainstorm: BrainstormEntity,
    currentMemberId: String,
    onDismiss: () -> Unit,
    onSubmit: (reason: String, skills: String, experience: String, availability: String, portfolio: String) -> Unit
) {
    var reason by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var availability by remember { mutableStateOf("4-6 hours / week") }
    var portfolio by remember { mutableStateOf("Timeline $currentMemberId") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E222B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Apply to Squad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${brainstorm.id} — ${brainstorm.title}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFC084FC))
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Your application will be submitted directly to the project owner (${brainstorm.ownerDisplayName}). Once accepted, your role and contributions will be attested to your permanent Timeline.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(10.dp)
                    )
                }

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Why do you want to contribute? *") },
                    placeholder = { Text("What excites you about this idea?") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = skills,
                    onValueChange = { skills = it },
                    label = { Text("Skills You Bring *") },
                    placeholder = { Text("e.g. Kotlin, Compose, Room, UX Wireframing") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = experience,
                    onValueChange = { experience = it },
                    label = { Text("Relevant Experience / Past Builds") },
                    placeholder = { Text("Briefly highlight similar systems you've worked on.") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = availability,
                    onValueChange = { availability = it },
                    label = { Text("Weekly Availability") },
                    placeholder = { Text("e.g. 5-8 hours/week (evenings & weekends)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = portfolio,
                    onValueChange = { portfolio = it },
                    label = { Text("Portfolio / Timeline Proof-of-Work Link") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color(0xFF94A3B8))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (reason.isBlank() || skills.isBlank()) {
                                errorMessage = "Please provide your reason and skills."
                                return@Button
                            }
                            onSubmit(reason, skills, experience, availability, portfolio)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text("Submit Application")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 3. RECORD DECISION DIALOG (/brainstorm decision)
// -------------------------------------------------------------------------
@Composable
fun RecordDecisionDialog(
    brainstormId: String,
    onDismiss: () -> Unit,
    onSubmit: (decisionText: String, reason: String) -> Unit
) {
    var decisionText by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E222B))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Log Official Decision", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Lock in architectural, design, or scope choices for $brainstormId.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))

                OutlinedTextField(
                    value = decisionText,
                    onValueChange = { decisionText = it },
                    label = { Text("Decision Statement *") },
                    placeholder = { Text("e.g. Adopt Room database with full offline fallback") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Rationale / Justification *") },
                    placeholder = { Text("Why was this chosen over other alternatives?") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF94A3B8)) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (decisionText.isNotBlank() && reason.isNotBlank()) {
                                onSubmit(decisionText, reason)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                    ) {
                        Text("Record Decision")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 4. CHANGE STATUS DIALOG (/brainstorm status)
// -------------------------------------------------------------------------
@Composable
fun ChangeStatusDialog(
    brainstorm: BrainstormEntity,
    onDismiss: () -> Unit,
    onSubmit: (newStatus: BrainstormStatus, reason: String) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(BrainstormStatus.COOKING) }
    var reason by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E222B))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Update Brainstorm Lifecycle State", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Target: ${brainstorm.id} — ${brainstorm.title}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    BrainstormStatus.values().forEach { st ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedStatus = st },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedStatus == st) Color(0xFF8B5CF6).copy(alpha = 0.2f) else Color(0xFF0F172A),
                            border = if (selectedStatus == st) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8B5CF6)) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                RadioButton(
                                    selected = selectedStatus == st,
                                    onClick = { selectedStatus = st },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFC084FC))
                                )
                                Column {
                                    Text(st.label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(
                                        when (st) {
                                            BrainstormStatus.BRAINSTORMING -> "Initial exploration, gathering ideas & contributors."
                                            BrainstormStatus.COOKING -> "Team assembled, active architectural planning in progress."
                                            BrainstormStatus.PAUSED -> "Temporarily on hold (waiting on API, assets, or bandwith)."
                                            BrainstormStatus.CANCELLED -> "Decided not to pursue. Reason will be archived."
                                            BrainstormStatus.IT_S_ALIVE -> "Promoted to formal Jobless Empire execution project."
                                        },
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason for Status Change * (Mandatory)") },
                    placeholder = { Text("Explain why this state transition is occurring...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF94A3B8)) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (reason.isNotBlank()) {
                                onSubmit(selectedStatus, reason)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                    ) {
                        Text("Apply Status")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 5. CONVERT TO PROJECT DIALOG (/brainstorm convert)
// -------------------------------------------------------------------------
@Composable
fun ConvertToProjectDialog(
    brainstorm: BrainstormEntity,
    onDismiss: () -> Unit,
    onSubmit: (projectTitle: String?) -> Unit
) {
    var projectTitle by remember { mutableStateOf(brainstorm.title) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E222B))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🚀", fontSize = 24.sp)
                    Column {
                        Text("Promote to Project (It's Alive!)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${brainstorm.id} → Formal P# Project", style = MaterialTheme.typography.bodySmall, color = Color(0xFF34D399))
                    }
                }

                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("This will execute the following automated steps:", color = Color(0xFFA7F3D0), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("• Allocate sequential project identifier (e.g. P#014)", color = Color(0xFFE2E8F0), fontSize = 11.sp)
                        Text("• Transition status to IT'S ALIVE", color = Color(0xFFE2E8F0), fontSize = 11.sp)
                        Text("• Transfer all active brainstorm contributors to project squad", color = Color(0xFFE2E8F0), fontSize = 11.sp)
                        Text("• Publish verified Timeline event to community feed", color = Color(0xFFE2E8F0), fontSize = 11.sp)
                    }
                }

                OutlinedTextField(
                    value = projectTitle,
                    onValueChange = { projectTitle = it },
                    label = { Text("Formal Project Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF10B981),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF94A3B8)) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSubmit(projectTitle.ifBlank { brainstorm.title }) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text("Confirm Promotion")
                    }
                }
            }
        }
    }
}
