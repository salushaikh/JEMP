package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.ui.theme.*

@Composable
fun RevenueCalculator(
    simulatedBudget: Int,
    isCharityModel: Boolean,
    onBudgetChange: (Int) -> Unit,
    onToggleCharity: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val presets = listOf(2000, 4500, 7500, 12000)

    val adminPercent = if (isCharityModel) 0.04f else 0.05f
    val charityPercent = if (isCharityModel) 0.01f else 0.00f
    val contributorPercent = 0.95f

    val adminAmount = (simulatedBudget * adminPercent).toInt()
    val charityAmount = (simulatedBudget * charityPercent).toInt()
    val contributorAmount = (simulatedBudget * contributorPercent).toInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(EmpireSurface)
            .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(16.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "INTERACTIVE REVENUE DISTRIBUTION MODEL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = EmpireAmber,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    "Simulate Client Project Revenue & Split",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Mode indicator
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isCharityModel) EmpireViolet.copy(alpha = 0.15f) else EmpireEmerald.copy(alpha = 0.15f))
                    .border(
                        1.dp,
                        if (isCharityModel) EmpireViolet.copy(alpha = 0.4f) else EmpireEmerald.copy(alpha = 0.4f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    if (isCharityModel) "FUTURE 4% + 1% MODEL" else "ACTIVE 5% ADMIN MODEL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = if (isCharityModel) EmpireViolet else EmpireEmerald,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Budget Presets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { preset ->
                val isSelected = simulatedBudget == preset
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) EmpireAmber else EmpireSurfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) EmpireAmber else EmpireSurfaceBorderLight,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onBudgetChange(preset) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "$$preset",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (isSelected) Color.Black else TextOffWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // Slider
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Simulated Client Quotation:",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                )
                Text(
                    "$$simulatedBudget USD",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                )
            }
            Slider(
                value = simulatedBudget.toFloat(),
                onValueChange = { onBudgetChange(it.toInt()) },
                valueRange = 1000f..25000f,
                steps = 23,
                colors = SliderDefaults.colors(
                    thumbColor = EmpireAmber,
                    activeTrackColor = EmpireAmber,
                    inactiveTrackColor = EmpireSurfaceBorderLight
                )
            )
        }

        // Visual Split Flow Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Contributor Pool (95%)
            Column(
                modifier = Modifier
                    .weight(1.3f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EmpireEmerald.copy(alpha = 0.08f))
                    .border(1.dp, EmpireEmerald.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(EmpireEmerald)
                    )
                    Text(
                        "95% CONTRIBUTORS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = EmpireEmerald
                        )
                    )
                }
                Text(
                    "$$contributorAmount",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                )
                Text(
                    "Distributed based on verified milestones & contributions",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                )
            }

            // Admin Fee (5% or 4%)
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EmpireSurfaceElevated)
                    .border(1.dp, EmpireSurfaceBorderLight, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(EmpireAmber)
                    )
                    Text(
                        if (isCharityModel) "4% ADMIN" else "5% ADMIN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = EmpireAmber
                        )
                    )
                }
                Text(
                    "$$adminAmount",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TextOffWhite
                    )
                )
                Text(
                    "Infrastructure, dispute resolution, review operations",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextDim,
                        fontSize = 10.sp
                    )
                )
            }

            // Charity Pool (if toggled)
            if (isCharityModel) {
                Column(
                    modifier = Modifier
                        .weight(0.9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmpireViolet.copy(alpha = 0.08f))
                        .border(1.dp, EmpireViolet.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(EmpireViolet)
                        )
                        Text(
                            "1% CHARITY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = EmpireViolet
                            )
                        )
                    }
                    Text(
                        "$$charityAmount",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    )
                    Text(
                        "Community voted causes",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        // Contributor Allocation Breakdown Table Example
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(EmpireSurfaceElevated)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Example Contributor Squad Allocation (Project Lead defines prior to work):",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontWeight = FontWeight.SemiBold
                )
            )

            val shares = listOf(
                Triple("Frontend Contributor (JEMP#001)", "35%", (contributorAmount * 0.35).toInt()),
                Triple("Backend / AI Pipeline (JEMP#027)", "35%", (contributorAmount * 0.35).toInt()),
                Triple("UI/UX Designer (JEMP#045)", "18%", (contributorAmount * 0.18).toInt()),
                Triple("Project Lead / QA Sync (JEMP#014)", "12%", (contributorAmount * 0.12).toInt())
            )

            shares.forEach { (role, pct, payout) ->
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
                            "•",
                            color = EmpireAmber,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            role,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextOffWhite)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            pct,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextDim,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Text(
                            "$$payout",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = EmpireEmerald,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Toggle Future Charity Model
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(EmpireSurfaceCard)
                .clickable { onToggleCharity(!isCharityModel) }
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.VolunteerActivism,
                    contentDescription = null,
                    tint = EmpireViolet,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        "Preview Future Charity Evolution (4% Admin + 1% Charity)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite
                        )
                    )
                    Text(
                        "Community governance voting on humanitarian causes (Future Goal)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = TextDim
                        )
                    )
                }
            }
            Switch(
                checked = isCharityModel,
                onCheckedChange = { onToggleCharity(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = EmpireViolet,
                    checkedTrackColor = EmpireViolet.copy(alpha = 0.3f),
                    uncheckedThumbColor = TextDim,
                    uncheckedTrackColor = EmpireSurfaceBorder
                )
            )
        }
    }
}
