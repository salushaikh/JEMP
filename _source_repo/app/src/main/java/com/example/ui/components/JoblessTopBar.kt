package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

enum class NavSection(val title: String) {
    OVERVIEW("Overview"),
    HOW_IT_WORKS("How It Works"),
    INSIDE("Workspaces"),
    TIMELINE("Timeline"),
    REVENUE("Revenue"),
    SAFETY("Safety & Rules"),
    FAQ("FAQ"),
    APPLY("Apply")
}

@Composable
fun JoblessTopBar(
    selectedSection: NavSection,
    onNavClick: (NavSection) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMobileMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(EmpireBackground.copy(alpha = 0.95f))
            .border(1.dp, EmpireSurfaceBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // BRAND IDENTITY (LEFT)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.clickable { onNavClick(NavSection.OVERVIEW) }
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmpireSurfaceElevated)
                        .border(1.dp, EmpireAmber.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_jobless_empire_logo_1788289191561),
                        contentDescription = "Jobless Empire Emblem",
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column {
                    Text(
                        "JOBLESS EMPIRE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = TextWhite
                        )
                    )
                    Text(
                        "PRIVATE BUILDER GUILD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            color = EmpireAmber,
                            letterSpacing = 1.5.sp
                        )
                    )
                }
            }

            // RIGHT: Desktop/Tablet Quick Nav or Mobile Hamburger + Direct APPLY Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Apply Highlight CTA Button
                Button(
                    onClick = { onNavClick(NavSection.APPLY) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmpireAmber,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        "APPLY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    )
                }

                IconButton(
                    onClick = { showMobileMenu = !showMobileMenu },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open Menu",
                        tint = TextOffWhite
                    )
                }
            }
        }

        // Horizontal Quick Jump Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EmpireSurface)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val quickItems = listOf(
                NavSection.HOW_IT_WORKS,
                NavSection.INSIDE,
                NavSection.TIMELINE,
                NavSection.REVENUE,
                NavSection.FAQ
            )

            quickItems.forEach { sec ->
                val isSelected = selectedSection == sec
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) EmpireAmber.copy(alpha = 0.2f) else Color.Transparent)
                        .border(
                            1.dp,
                            if (isSelected) EmpireAmber.copy(alpha = 0.6f) else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { onNavClick(sec) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        sec.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSelected) EmpireAmber else TextMuted,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Dropdown menu for all sections
        DropdownMenu(
            expanded = showMobileMenu,
            onDismissRequest = { showMobileMenu = false },
            modifier = Modifier.background(EmpireSurfaceElevated)
        ) {
            NavSection.values().forEach { sec ->
                DropdownMenuItem(
                    text = {
                        Text(
                            sec.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (selectedSection == sec) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedSection == sec) EmpireAmber else TextWhite
                            )
                        )
                    },
                    onClick = {
                        onNavClick(sec)
                        showMobileMenu = false
                    }
                )
            }
        }
    }
}
