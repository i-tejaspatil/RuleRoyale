package com.ruleroyale.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = Black,
    surface = DarkSurface,
    primary = RuleYellow,
    onPrimary = Black,
    onBackground = White,
    onSurface = White
)

@Composable
fun RuleRoyaleTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = RuleRoyaleTypography,
        shapes = RuleRoyaleShapes,
        content = content
    )

}
