package com.ruleroyale.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ruleroyale.ui.theme.RuleYellow

/**
 * Main home screen of RuleRoyale application.
 *
 * Displays three arena cards: Life From Rules, Ecosystem Arena, and Battle Mode.
 * Features fade-in animation on first appearance to avoid white flash on iOS.
 *
 * @param onEnterLifeFromRules Callback invoked when user enters Life From Rules arena
 * @param onEnterEcosystem Callback invoked when user enters Ecosystem Arena
 * @param onEnterBattle Callback invoked when user enters Battle Mode
 */
@Composable
fun HomeScreen(
    onEnterLifeFromRules: () -> Unit,
    onEnterEcosystem: () -> Unit,
    onEnterBattle: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(false) }

    val contentAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "homeAlpha"
    )

    LaunchedEffect(Unit) {
        // Let the first frame draw with the correct themed background.
        // Then fade in the content to avoid a brief "flash" on iOS.
        visible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 48.dp)
            .alpha(contentAlpha),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(48.dp))

        // App Title
        Text(
            text = "RuleRoyale",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tagline
        Text(
            text = "Create worlds from rules",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // ===== ARENA CARDS =====

        ArenaCard(
            title = "Life From Rules",
            description = "How complex patterns emerge from simple rules.",
            enabled = true,
            onEnter = onEnterLifeFromRules
        )

        Spacer(modifier = Modifier.height(24.dp))

        ArenaCard(
            title = "Ecosystem Arena",
            description = "Predator–Prey–Grass interaction lab",
            enabled = true,
            onEnter = onEnterEcosystem
        )

        Spacer(modifier = Modifier.height(24.dp))

        BattleModeCard(
            onEnter = onEnterBattle
        )
    }
}

/**
 * Standard arena card component for Life From Rules and Ecosystem modes.
 *
 * Features centered layout with title, description, and button.
 * Includes yellow accent border matching the app theme and scale animation on tap.
 *
 * @param title The arena name (e.g., "Life From Rules")
 * @param description Brief description of the arena's purpose
 * @param enabled Whether the arena is currently accessible
 * @param onEnter Callback invoked when user taps the card or "Enter Arena" button
 */
@Composable
fun ArenaCard(
    title: String,
    description: String,
    enabled: Boolean,
    onEnter: () -> Unit
) {
    val accent = RuleYellow

    val scale by animateFloatAsState(
        targetValue = if (enabled) 0.98f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "cardScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.6f)
            .scale(scale)
            .clickable(enabled = enabled, onClick = onEnter),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.55f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onEnter,
                enabled = enabled
            ) {
                Text("Enter Arena")
            }
        }
    }
}

/**
 * Special highlighted card for Battle Mode feature.
 *
 * Styled with centered layout, larger emoji icon, and yellow border
 * to distinguish it from standard arena cards. Emphasizes the competitive
 * nature of this mode.
 *
 * @param onEnter Callback invoked when user taps to start Battle Mode
 */
@Composable
fun BattleModeCard(
    onEnter: () -> Unit
) {
    val accent = RuleYellow

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEnter),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            // Match app theme (no orange)
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.55f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚔️",
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "BATTLE MODE",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Compete with friends!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onEnter,
                colors = ButtonDefaults.buttonColors(
                    containerColor = accent,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Start Battle", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }
        }
    }
}
