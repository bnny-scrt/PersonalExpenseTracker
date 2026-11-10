package com.example.personalexpensetracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Branded "Midnight Steel" theme — deliberately dark-only. A dark navy canvas with
 * blue/green accents reads confident and masculine, and it keeps money figures
 * punchy on the screen. Dynamic OS color is intentionally disabled so the brand
 * stays consistent across devices.
 */
private val MidnightSteelColorScheme = darkColorScheme(
    primary = SteelPrimary,
    onPrimary = SteelForeground,
    primaryContainer = SteelDeepBlue,
    onPrimaryContainer = Color(0xFFBFDBFE),
    secondary = MoneyGreen,
    onSecondary = Color(0xFF052E16),
    secondaryContainer = MoneyGreenDark,
    onSecondaryContainer = Color(0xFFDCFCE7),
    tertiary = SteelOrange,
    onTertiary = Color(0xFF431407),
    background = SteelBackground,
    onBackground = SteelForeground,
    surface = SteelBackground,
    onSurface = SteelForeground,
    surfaceVariant = SteelSurface,
    onSurfaceVariant = SteelMuted,
    outline = Color(0xFF334155),
    error = Color(0xFFEF4444),
    onError = SteelForeground,
    surfaceContainer = SteelCard,
    surfaceContainerHigh = SteelSurface,
    surfaceContainerHighest = Color(0xFF273449),
)

@Composable
fun PersonalExpenseTrackerTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = MidnightSteelColorScheme,
        typography = Typography,
        content = content,
    )
}
