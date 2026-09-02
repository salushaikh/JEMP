package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrainstormStatus
import com.example.data.model.ClientProjectStatus
import com.example.data.model.ProjectStatus
import com.example.ui.theme.*

@Composable
fun StatusBadge(
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(accentColor.copy(alpha = 0.12f))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(accentColor)
        )
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = accentColor,
                letterSpacing = 0.8.sp
            )
        )
    }
}

@Composable
fun BrainstormStatusBadge(status: BrainstormStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        BrainstormStatus.BRAINSTORMING -> StatusBrainstorming
        BrainstormStatus.COOKING -> StatusCooking
        BrainstormStatus.PAUSED -> StatusPaused
        BrainstormStatus.CANCELLED -> StatusCancelled
        BrainstormStatus.IT_S_ALIVE -> StatusAlive
    }
    StatusBadge(label = status.label, accentColor = color, modifier = modifier)
}

@Composable
fun ProjectStatusBadge(status: ProjectStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        ProjectStatus.IN_PROGRESS -> StatusInProgress
        ProjectStatus.PAUSED -> StatusPaused
        ProjectStatus.CANCELLED -> StatusCancelled
        ProjectStatus.COMPLETED -> StatusCompleted
    }
    StatusBadge(label = status.label, accentColor = color, modifier = modifier)
}

@Composable
fun ClientProjectStatusBadge(status: ClientProjectStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        ClientProjectStatus.OPEN_FOR_APPLICATIONS -> EmpireEmerald
        ClientProjectStatus.REVIEWING_APPLICANTS -> EmpireAmber
        ClientProjectStatus.CONVERTED_TO_PROJECT -> EmpireCyan
        ClientProjectStatus.COMPLETED -> StatusCompleted
    }
    StatusBadge(label = status.label, accentColor = color, modifier = modifier)
}

@Composable
fun IdBadge(
    id: String,
    modifier: Modifier = Modifier
) {
    val (bg, textColor, border) = when {
        id.startsWith("JEMP") -> Triple(EmpireAmber.copy(alpha = 0.15f), EmpireAmberLight, EmpireAmber.copy(alpha = 0.4f))
        id.startsWith("B#") -> Triple(StatusBrainstorming.copy(alpha = 0.15f), Color(0xFFD8B4FE), StatusBrainstorming.copy(alpha = 0.4f))
        id.startsWith("P#") -> Triple(StatusInProgress.copy(alpha = 0.15f), Color(0xFFBAE6FD), StatusInProgress.copy(alpha = 0.4f))
        id.startsWith("HC#") -> Triple(StatusClientProject.copy(alpha = 0.15f), Color(0xFFFEF08A), StatusClientProject.copy(alpha = 0.4f))
        else -> Triple(EmpireSurfaceElevated, TextOffWhite, EmpireSurfaceBorderLight)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = id,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = textColor,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun SectionEyebrow(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = EmpireAmber
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .width(16.dp)
                .height(2.dp)
                .background(color)
        )
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.8.sp,
                color = color
            )
        )
    }
}
