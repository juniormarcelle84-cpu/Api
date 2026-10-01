package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Strict User Palette:
// #FFFFFF text, #312F30 cards and nav, #1B1A1B app background,
// #7477FF blue, #F66554 coral, #F9CD61 yellow, #C5D4CA sage.

val AppWhite = Color(0xFFFFFFFF)
val AppCardNav = Color(0xFF312F30)
val AppBackground = Color(0xFF1B1A1B)
val AppBlue = Color(0xFF7477FF)
val AppCoral = Color(0xFFF66554)
val AppYellow = Color(0xFFF9CD61)
val AppSage = Color(0xFFC5D4CA)

// Color cycling helper for cards
val CardFills = listOf(
    AppBlue,
    AppCoral,
    AppYellow,
    AppSage
)

fun getCardContentColor(cardFill: Color): Color {
    return if (cardFill == AppBlue) AppWhite else AppBackground
}

// Backwards compatibility bindings
val Obsidian = AppBackground
val SurfaceDark = AppCardNav
val SurfaceVariantDark = AppCardNav
val CardBackground = AppCardNav
val CardBorder = Color.Transparent

val GoldAccent = AppYellow
val GoldAccentDark = AppYellow
val GoldAccentLight = AppYellow
val GoldContainer = AppCardNav
val OnGold = AppBackground

val TextPrimary = AppWhite
val TextSecondary = Color(0xFFB0ADB0)
val TextMuted = Color(0xFF888486)

val OceanDeep = AppBackground
val OceanSurface = AppCardNav
val OceanSurfaceVariant = AppCardNav
val OceanCard = AppCardNav
val OceanCardElevated = AppCardNav
val OceanBorder = Color.Transparent
val OceanBluePrimary = AppBlue
val OceanCyanAccent = AppBlue
val OceanIceLight = AppWhite
val OceanWhite = AppWhite
val OceanTextPrimary = AppWhite
val OceanTextSecondary = Color(0xFFB0ADB0)
val OceanTextMuted = Color(0xFF888486)
val OceanPrimaryContainer = AppCardNav

val SuccessGreen = AppSage
val WarningOrange = AppYellow
val ErrorRed = AppCoral
val InfoBlue = AppBlue
val PurpleBadge = AppBlue
