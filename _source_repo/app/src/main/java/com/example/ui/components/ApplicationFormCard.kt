package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
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
import com.example.data.model.ApplicationSubmission
import com.example.ui.theme.*

@Composable
fun ApplicationFormCard(
    isSubmitting: Boolean,
    errorMessage: String?,
    latestSubmission: ApplicationSubmission?,
    onSubmit: (fullName: String, email: String, resumeName: String, resumeSize: String, isJobless: Boolean, consent: Boolean) -> Unit,
    onViewAdminPayload: (ApplicationSubmission) -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var resumeFileName by remember { mutableStateOf("Resume_2026_Engineering.pdf") }
    var resumeFileSize by remember { mutableStateOf("1.4 MB") }
    var isCurrentlyUnemployed by remember { mutableStateOf<Boolean?>(true) }
    var consentChecked by remember { mutableStateOf(false) }

    var localValidationError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(EmpireSurface)
            .border(1.dp, EmpireAmber.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form Title
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(EmpireAmber)
                )
                Text(
                    "CONVERSION POINT • APPLICATION PORTAL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = EmpireAmber,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Text(
                "READY TO JOIN THE EMPIRE?",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = TextWhite
                )
            )
            Text(
                "Applications are manually reviewed. If accepted, you'll receive an email with your private Discord invitation and onboarding instructions.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
            )
        }

        Divider(color = EmpireSurfaceBorder)

        if (latestSubmission != null) {
            // SUCCESS STATE
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(EmpireEmerald.copy(alpha = 0.12f))
                    .border(1.dp, EmpireEmerald.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = EmpireEmerald,
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    "APPLICATION RECEIVED.",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                )

                Text(
                    "Your application has entered the Empire's review queue.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = EmpireAmberLight
                    )
                )

                Text(
                    "Applications are reviewed manually. If approved, you'll receive an email containing your Discord invitation and next steps.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextOffWhite,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                )

                // Generated ID preview
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmpireSurfaceElevated)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Provisional Queue Tag:",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                        IdBadge(latestSubmission.assignedJempId)
                    }
                }

                Button(
                    onClick = { onViewAdminPayload(latestSubmission) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmpireSurfaceElevated,
                        contentColor = EmpireAmber
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmpireAmber.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Inspect Admin Email Payload & Reply-To Setup",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        } else {
            // INPUT FIELDS

            // 1. Full Name
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "FULL NAME *",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TextOffWhite
                    )
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        localValidationError = null
                    },
                    placeholder = { Text("e.g. Alex Vance", color = TextDim) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmpireAmber,
                        unfocusedBorderColor = EmpireSurfaceBorderLight,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextOffWhite,
                        focusedContainerColor = EmpireSurfaceElevated,
                        unfocusedContainerColor = EmpireSurfaceElevated
                    ),
                    singleLine = true
                )
            }

            // 2. Best Communication Email
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "BEST COMMUNICATION EMAIL *",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TextOffWhite
                    )
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        localValidationError = null
                    },
                    placeholder = { Text("alex@example.com", color = TextDim) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmpireAmber,
                        unfocusedBorderColor = EmpireSurfaceBorderLight,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextOffWhite,
                        focusedContainerColor = EmpireSurfaceElevated,
                        unfocusedContainerColor = EmpireSurfaceElevated
                    ),
                    singleLine = true
                )
                Text(
                    "Use an email you actively monitor.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextDim,
                        fontSize = 11.sp
                    )
                )
            }

            // 3. Resume Upload UI
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "RESUME * (PDF, DOC, DOCX)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TextOffWhite
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmpireSurfaceElevated)
                        .border(1.dp, EmpireSurfaceBorderLight, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(EmpireAmber.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = EmpireAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    resumeFileName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                )
                                Text(
                                    "Attached • $resumeFileSize (Max 10 MB)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = EmpireEmerald,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // Quick sample selector buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(EmpireSurfaceCard)
                                    .clickable {
                                        resumeFileName = "Portfolio_Resume_2026.pdf"
                                        resumeFileSize = "2.1 MB"
                                    }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text("PDF", style = MaterialTheme.typography.labelSmall.copy(color = TextDim, fontSize = 9.sp))
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(EmpireSurfaceCard)
                                    .clickable {
                                        resumeFileName = "Resume_CV_Final.docx"
                                        resumeFileSize = "980 KB"
                                    }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text("DOCX", style = MaterialTheme.typography.labelSmall.copy(color = TextDim, fontSize = 9.sp))
                            }
                        }
                    }
                }
            }

            // 4. Jobless / Unemployed Status
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "ARE YOU CURRENTLY JOBLESS / UNEMPLOYED? *",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TextOffWhite
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Option: Yes
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isCurrentlyUnemployed == true) EmpireAmber.copy(alpha = 0.15f) else EmpireSurfaceElevated)
                            .border(
                                1.dp,
                                if (isCurrentlyUnemployed == true) EmpireAmber else EmpireSurfaceBorderLight,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { isCurrentlyUnemployed = true }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = isCurrentlyUnemployed == true,
                            onClick = { isCurrentlyUnemployed = true },
                            colors = RadioButtonDefaults.colors(selectedColor = EmpireAmber)
                        )
                        Text(
                            "Yes, currently unemployed / jobless",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isCurrentlyUnemployed == true) FontWeight.Bold else FontWeight.Normal,
                                color = TextWhite
                            )
                        )
                    }

                    // Option: No
                    Row(
                        modifier = Modifier
                            .weight(0.7f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isCurrentlyUnemployed == false) EmpireRose.copy(alpha = 0.15f) else EmpireSurfaceElevated)
                            .border(
                                1.dp,
                                if (isCurrentlyUnemployed == false) EmpireRose else EmpireSurfaceBorderLight,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { isCurrentlyUnemployed = false }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = isCurrentlyUnemployed == false,
                            onClick = { isCurrentlyUnemployed = false },
                            colors = RadioButtonDefaults.colors(selectedColor = EmpireRose)
                        )
                        Text(
                            "No",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isCurrentlyUnemployed == false) FontWeight.Bold else FontWeight.Normal,
                                color = TextWhite
                            )
                        )
                    }
                }

                // Dynamic explanation if "No" is chosen
                AnimatedVisibility(visible = isCurrentlyUnemployed == false) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmpireRose.copy(alpha = 0.1f))
                            .border(1.dp, EmpireRose.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            "Note: Jobless Empire is specifically created to support builders, job seekers, and creators currently without employment. If you are currently fully employed, our review team prioritizes applicants who have available time to contribute.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFFDA4AF),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // 5. Consent Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(EmpireSurfaceElevated)
                    .clickable { consentChecked = !consentChecked }
                    .padding(10.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Checkbox(
                    checked = consentChecked,
                    onCheckedChange = { consentChecked = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = EmpireAmber,
                        checkmarkColor = Color.Black
                    )
                )
                Text(
                    "I confirm that the information I have provided is true to the best of my knowledge, and I agree to be contacted at the email address provided regarding the status of my Jobless Empire application.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextOffWhite,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                )
            }

            // Error display if any
            val activeError = errorMessage ?: localValidationError
            if (activeError != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmpireRose.copy(alpha = 0.15f))
                        .border(1.dp, EmpireRose.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        activeError,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = EmpireRose,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (fullName.isBlank()) {
                        localValidationError = "Please enter your full name."
                        return@Button
                    }
                    if (!email.contains("@") || !email.contains(".")) {
                        localValidationError = "Please enter a valid communication email address."
                        return@Button
                    }
                    if (!consentChecked) {
                        localValidationError = "Please accept the verification consent checkbox."
                        return@Button
                    }

                    onSubmit(
                        fullName,
                        email,
                        resumeFileName,
                        resumeFileSize,
                        isCurrentlyUnemployed ?: true,
                        consentChecked
                    )
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmpireAmber,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.Black,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "DISPATCHING TO REVIEW QUEUE...",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "APPLY TO JOIN THE EMPIRE",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    )
                }
            }

            // Security & Privacy Guarantee Note
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = TextDim,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Secure submission • Zero credentials in client JS • Server-side API ready",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextDim,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
