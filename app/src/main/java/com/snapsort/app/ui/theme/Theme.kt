package com.snapsort.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val ColorBg = Color(0xFFFFFFFF)
val ColorSurface = Color(0xFFF8FAFC)
val ColorSurfaceElevated = Color(0xFFFFFFFF)
val ColorBorder = Color(0xFFE2E8F0)
val ColorBorderInput = Color(0xFF64748B)
val ColorText = Color(0xFF0F172A)
val ColorTextMuted = Color(0xFF475569)

val ColorAccent = Color(0xFF0369A1)
val ColorOnAccent = Color(0xFFFFFFFF)
val ColorAccentSubtle = Color(0xFFE0F2FE)

val ColorDanger = Color(0xFFB91C1C)
val ColorOnDanger = Color(0xFFFFFFFF)
val ColorSuccess = Color(0xFF15803D)
val ColorOnSuccess = Color(0xFFFFFFFF)
val ColorWarning = Color(0xFFB45309)
val ColorOnWarning = Color(0xFFFFFFFF)

val SnapSortColorScheme: ColorScheme = lightColorScheme(
    primary = ColorAccent,
    onPrimary = ColorOnAccent,
    primaryContainer = ColorAccentSubtle,
    onPrimaryContainer = ColorAccent,
    background = ColorBg,
    onBackground = ColorText,
    surface = ColorSurface,
    onSurface = ColorText,
    surfaceVariant = ColorBorder,
    onSurfaceVariant = ColorTextMuted,
    outline = ColorBorderInput,
    error = ColorDanger,
    onError = ColorOnDanger
)

val SnapSortTypography = Typography(
    displayMedium = TextStyle(
        fontSize = 32.sp,
        lineHeight = 38.sp,
        fontWeight = FontWeight.ExtraBold,
        color = ColorText
    ),
    headlineMedium = TextStyle(
        fontSize = 24.sp,
        lineHeight = 30.sp,
        fontWeight = FontWeight.Bold,
        color = ColorText
    ),
    titleLarge = TextStyle(
        fontSize = 20.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.SemiBold,
        color = ColorText
    ),
    titleMedium = TextStyle(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.SemiBold,
        color = ColorText
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Normal,
        color = ColorText
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
        color = ColorTextMuted
    ),
    labelSmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium,
        color = ColorTextMuted
    )
)

@Composable
fun SnapSortTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SnapSortColorScheme,
        typography = SnapSortTypography,
        content = content
    )
}
