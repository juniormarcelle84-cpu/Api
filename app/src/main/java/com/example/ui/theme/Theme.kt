package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AppCoral,
    onPrimary = AppBackground,
    primaryContainer = AppBlue,
    onPrimaryContainer = AppWhite,
    secondary = AppYellow,
    onSecondary = AppBackground,
    secondaryContainer = AppSage,
    onSecondaryContainer = AppBackground,
    background = AppBackground,
    onBackground = AppWhite,
    surface = AppCardNav,
    onSurface = AppWhite,
    surfaceVariant = AppCardNav,
    onSurfaceVariant = AppWhite,
    error = AppCoral,
    onError = AppBackground
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
