package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Sky400,
    onPrimary = Color.Black,
    primaryContainer = Sky900,
    onPrimaryContainer = Sky200,
    secondary = Sky300,
    onSecondary = Color.Black,
    secondaryContainer = Sky800,
    onSecondaryContainer = Sky100,
    tertiary = SuccessGreenLight,
    background = DarkBackground,
    onBackground = Color(0xFFF1F5F9),
    surface = DarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = DarkBorder,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Sky600,
    onPrimary = Color.White,
    primaryContainer = Sky100,
    onPrimaryContainer = Sky900,
    secondary = Sky500,
    onSecondary = Color.White,
    secondaryContainer = Sky200,
    onSecondaryContainer = Sky800,
    tertiary = SuccessGreen,
    background = LightBackground,
    onBackground = Color(0xFF0F172A),
    surface = LightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = Color(0xFF475569),
    outline = LightBorder,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun HaafedkTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
