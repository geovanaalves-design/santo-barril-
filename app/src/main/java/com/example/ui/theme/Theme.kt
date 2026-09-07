package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = WinePrimary,
    onPrimary = TextOnWine,
    primaryContainer = WineContainer,
    onPrimaryContainer = OnWineContainer,
    secondary = GoldAccent,
    onSecondary = TextPrimary,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = GoldDark,
    tertiary = WineLight,
    onTertiary = TextOnWine,
    background = OffWhiteBackground,
    onBackground = TextPrimary,
    surface = CardWhite,
    onSurface = TextPrimary,
    surfaceVariant = CardSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    error = StatusRed,
    onError = Color.White,
    errorContainer = StatusRedContainer,
    onErrorContainer = StatusRed
)

private val DarkColorScheme = darkColorScheme(
    primary = WineLight,
    onPrimary = TextOnWine,
    primaryContainer = WineDark,
    onPrimaryContainer = WineContainer,
    secondary = GoldAccent,
    onSecondary = TextPrimary,
    secondaryContainer = GoldDark,
    onSecondaryContainer = GoldContainer,
    background = Color(0xFF1B1718),
    onBackground = Color(0xFFEDE9EA),
    surface = Color(0xFF262022),
    onSurface = Color(0xFFEDE9EA),
    surfaceVariant = Color(0xFF332B2E),
    onSurfaceVariant = Color(0xFFC7BFC2),
    outline = Color(0xFF4A4043)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
