package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrainstormStatus
import com.example.data.model.ProjectStatus
import com.example.ui.theme.*

@Composable
fun HeroNetworkVisual(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    val lineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "linePhase"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        EmpireSurfaceElevated,
                        EmpireSurfaceCard,
                        EmpireBackground
                    ),
                    radius = 500f
                )
            )
            .border(1.dp, EmpireSurfaceBorder, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        // Canvas Network Graph
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Node coordinates (normalized)
            val n1 = Offset(w * 0.18f, h * 0.30f) // JEMP#014
            val n2 = Offset(w * 0.50f, h * 0.22f) // B#007
            val n3 = Offset(w * 0.82f, h * 0.38f) // P#003
            val n4 = Offset(w * 0.32f, h * 0.78f) // HC#004
            val n5 = Offset(w * 0.72f, h * 0.80f) // JEMP#001
            val n6 = Offset(w * 0.52f, h * 0.55f) // Center Hub

            val stroke = Stroke(
                width = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), lineOffset)
            )

            // Draw connecting lines
            drawLine(
                color = EmpireAmber.copy(alpha = 0.4f),
                start = n1,
                end = n2,
                strokeWidth = 2f
            )
            drawLine(
                color = StatusBrainstorming.copy(alpha = 0.45f),
                start = n2,
                end = n3,
                strokeWidth = 2f
            )
            drawLine(
                color = StatusInProgress.copy(alpha = 0.35f),
                start = n2,
                end = n6,
                strokeWidth = 1.5f
            )
            drawLine(
                color = StatusClientProject.copy(alpha = 0.4f),
                start = n4,
                end = n6,
                strokeWidth = 2f
            )
            drawLine(
                color = EmpireCyan.copy(alpha = 0.35f),
                start = n6,
                end = n5,
                strokeWidth = 1.5f
            )
            drawLine(
                color = EmpireAmberDark.copy(alpha = 0.3f),
                start = n3,
                end = n5,
                strokeWidth = 1.5f
            )

            // Dynamic dashed halo
            drawCircle(
                color = EmpireAmber.copy(alpha = pulseAlpha * 0.2f),
                radius = 36f,
                center = n6
            )
            drawCircle(
                color = EmpireAmber.copy(alpha = 0.6f),
                radius = 18f,
                center = n6,
                style = stroke
            )
            drawCircle(
                color = EmpireAmber,
                radius = 5f,
                center = n6
            )

            // Minor background star/signal dots
            val minorNodes = listOf(
                Offset(w * 0.08f, h * 0.70f),
                Offset(w * 0.88f, h * 0.15f),
                Offset(w * 0.92f, h * 0.75f),
                Offset(w * 0.38f, h * 0.10f),
                Offset(w * 0.12f, h * 0.15f)
            )
            for (pt in minorNodes) {
                drawCircle(
                    color = TextDim.copy(alpha = 0.3f),
                    radius = 3f,
                    center = pt
                )
            }
        }

        // Floating Interactive Hub Badges on Top of Canvas
        // Top Left Node: JEMP#014
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 8.dp, y = 28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(EmpireSurfaceElevated.copy(alpha = 0.9f))
                .border(1.dp, EmpireAmber.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IdBadge("JEMP#014")
                    Text(
                        "Marcus (Lead)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                Text(
                    "Proof of Work: 4 Commits",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = EmpireAmberLight,
                        fontSize = 9.sp
                    )
                )
            }
        }

        // Top Middle Node: B#007
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(EmpireSurfaceElevated.copy(alpha = 0.9f))
                .border(1.dp, StatusBrainstorming.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IdBadge("B#007")
                Text(
                    "AI Resume Spec",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                BrainstormStatusBadge(BrainstormStatus.COOKING)
            }
        }

        // Top Right Node: P#003
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-8).dp, y = 48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(EmpireSurfaceElevated.copy(alpha = 0.9f))
                .border(1.dp, StatusInProgress.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IdBadge("P#003")
                    Text(
                        "Resume Engine",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                Text(
                    "B#007 → P#003 Executing",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = StatusInProgress,
                        fontSize = 9.sp
                    )
                )
            }
        }

        // Bottom Left Node: HC#004 (Client Project)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 12.dp, y = (-20).dp)
                .clip(RoundedCornerShape(8.dp))
                .background(EmpireSurfaceElevated.copy(alpha = 0.9f))
                .border(1.dp, StatusClientProject.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IdBadge("HC#004")
                    Text(
                        "$4,500 Client Scope",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Text(
                    "95% Split to Contributors",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = EmpireEmerald,
                        fontSize = 9.sp
                    )
                )
            }
        }

        // Bottom Right Node: JEMP#001
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-12).dp, y = (-16).dp)
                .clip(RoundedCornerShape(8.dp))
                .background(EmpireSurfaceElevated.copy(alpha = 0.9f))
                .border(1.dp, EmpireCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IdBadge("JEMP#001")
                    Text(
                        "Alex Vance",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                Text(
                    "24h Public Timeline: Active",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = EmpireCyan,
                        fontSize = 9.sp
                    )
                )
            }
        }

        // Center Live Pulse indicator
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(20.dp))
                .background(EmpireBackground.copy(alpha = 0.85f))
                .border(1.dp, EmpireSurfaceBorderLight, RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
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
                    "DISCORD ECOSYSTEM LIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = TextOffWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
