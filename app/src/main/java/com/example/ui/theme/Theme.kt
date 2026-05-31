package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CelestialColorScheme = darkColorScheme(
    primary = StarlightBlue,
    secondary = DayAmber,
    tertiary = NightIndigo,
    background = DeepMidnight,
    surface = SpaceSlate,
    onPrimary = DeepMidnight,
    onSecondary = DeepMidnight,
    onTertiary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite,
    surfaceVariant = LunarGrey,
    onSurfaceVariant = TextDim
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force visual twilight dark mode for high-contrast world maps
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CelestialColorScheme,
        typography = Typography,
        content = content
    )
}
