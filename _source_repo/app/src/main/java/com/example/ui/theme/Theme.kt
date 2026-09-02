package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmpireAmber,
    onPrimary = Color.Black,
    primaryContainer = EmpireSurfaceElevated,
    onPrimaryContainer = EmpireAmberLight,
    secondary = EmpireDiscordIndigo,
    onSecondary = Color.White,
    secondaryContainer = EmpireDiscordIndigoBg,
    onSecondaryContainer = Color.White,
    tertiary = EmpireCyan,
    onTertiary = Color.Black,
    tertiaryContainer = EmpireSurfaceElevated,
    onTertiaryContainer = EmpireCyan,
    background = EmpireBackground,
    onBackground = TextWhite,
    surface = EmpireSurface,
    onSurface = TextWhite,
    surfaceVariant = EmpireSurfaceCard,
    onSurfaceVariant = TextMuted,
    outline = EmpireSurfaceBorder,
    outlineVariant = EmpireSurfaceBorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
