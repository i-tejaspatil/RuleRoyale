package com.ruleroyale.ui.battle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Battle setup screen for Life From Rules competitive mode.
 *
 * Allows users to either create a new battle by generating a shareable code,
 * or join an existing battle by entering a friend's code. Uses deterministic
 * seeding to ensure both players start with identical initial grids for fair
 * competition.
 *
 * Features:
 * - Two tabs: Create Battle and Join Battle
 * - 6-character alphanumeric codes (no ambiguous characters like 0/O, 1/I)
 * - Copy-to-clipboard functionality for easy sharing
 * - Offline competition (no internet required)
 *
 * @param onStartBattle Callback invoked with deterministic seed when battle starts
 * @param onBack Callback invoked when user presses back button
 */
@Composable
fun BattleSetupScreen(
    onStartBattle: (Long) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Create, 1 = Join
    var generatedCode by remember { mutableStateOf(generateBattleCode()) }
    var enteredCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 44.dp) // Shift entire content down for iOS status bar + Dynamic Island
    ) {
        // Fixed Header (not scrollable)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar (true-center like LifeFromRules)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.width(72.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚔️ Battle Mode",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(72.dp))
            }

            Text(
                "Compete with friends to find the best rules!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Create",
                            color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Join",
                            color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }

        // Scrollable content area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Content based on selected tab
            when (selectedTab) {
                0 -> {
                    val seed = remember(generatedCode) { battleCodeToSeedOrNull(generatedCode) }
                    CreateBattleContent(
                        code = generatedCode,
                        onRegenerateCode = { generatedCode = generateBattleCode() },
                        onStartBattle = {
                            seed?.let(onStartBattle)
                        },
                        startEnabled = seed != null
                    )
                }

                1 -> {
                    val seed = remember(enteredCode) { battleCodeToSeedOrNull(enteredCode) }
                    JoinBattleContent(
                        enteredCode = enteredCode,
                        onCodeChange = { enteredCode = it.uppercase().take(6) },
                        onStartBattle = {
                            seed?.let(onStartBattle)
                        },
                        joinEnabled = seed != null
                    )
                }
            }
        }
    }
}

/**
 * Content shown in the "Create Battle" tab.
 *
 * Displays a generated battle code with copy/regenerate options,
 * instructions for sharing with friends, and rules for ranking outcomes.
 *
 * @param code The 6-character battle code to display
 * @param onRegenerateCode Callback to generate a new random code
 * @param onStartBattle Callback invoked when user taps "Start Battle"
 * @param startEnabled Whether the start button should be enabled (code is valid)
 */
@Composable
private fun CreateBattleContent(
    code: String,
    onRegenerateCode: () -> Unit,
    onStartBattle: () -> Unit,
    startEnabled: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Battle code card
        BattleCodeCard(
            code = code,
            onRegenerateCode = onRegenerateCode
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Instructions
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "How to Play",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "1. Share this code with your friend\n" +
                        "2. Friend enters it in 'Join Battle'\n" +
                        "3. Both choose rules\n" +
                        "4. Start simulation\n" +
                        "5. Compare final Status + Tick",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // How to Decide Winner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "🏆 How to Decide Winner",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "After both players finish, compare your results:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                WinnerRankRow("🥇", "STABLE", "Pattern stopped changing")
                WinnerRankRow("🥈", "OSCILLATING", "Pattern repeats in a cycle")
                WinnerRankRow("🥉", "EXTINCT / SATURATED", "All died or overpopulated")

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "⏱️ Tie-breaker: Lower Tick count wins!",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    "Example: STABLE at tick 150 beats STABLE at tick 200",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Start button
        Button(
            onClick = onStartBattle,
            enabled = startEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text("Start Battle", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Content shown in the "Join Battle" tab.
 *
 * Provides a text field for entering a friend's battle code,
 * validates the input, and explains how battle codes work.
 *
 * @param enteredCode Current value in the code input field
 * @param onCodeChange Callback invoked when user types (automatically uppercased)
 * @param onStartBattle Callback invoked when user taps "Join Battle"
 * @param joinEnabled Whether the join button should be enabled (code is valid)
 */
@Composable
private fun JoinBattleContent(
    enteredCode: String,
    onCodeChange: (String) -> Unit,
    onStartBattle: () -> Unit,
    joinEnabled: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Code input card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Enter your friend's battle code:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Code input field
                OutlinedTextField(
                    value = enteredCode,
                    onValueChange = onCodeChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    ),
                    placeholder = {
                        Text(
                            "ABC123",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = MaterialTheme.typography.displaySmall.copy(
                                letterSpacing = 4.sp
                            )
                        )
                    },
                    singleLine = true,
                    isError = enteredCode.isNotEmpty() && enteredCode.length != 6
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "${enteredCode.length}/6 characters",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enteredCode.length == 6)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Instructions
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "About Battle Codes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "• Battle codes ensure both players start with the same initial grid\n" +
                        "• Each player configures their own rules\n" +
                        "• No internet needed - works offline!\n" +
                        "• Compare results manually after simulation",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // How to Decide Winner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "🏆 How to Decide Winner",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "After both players finish, compare your results:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                WinnerRankRow("🥇", "STABLE", "Pattern stopped changing")
                WinnerRankRow("🥈", "OSCILLATING", "Pattern repeats in a cycle")
                WinnerRankRow("🥉", "EXTINCT / SATURATED", "All died or overpopulated")

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "⏱️ Tie-breaker: Lower Tick count wins!",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    "Example: STABLE at tick 150 beats STABLE at tick 200",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Join button
        Button(
            onClick = onStartBattle,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = joinEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text("Join Battle", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Card displaying the battle code with copy and regenerate actions.
 *
 * Shows the code in large, spaced letters on a primary-colored background
 * for easy readability. Provides buttons to copy the code to clipboard
 * or generate a new random code.
 *
 * @param code The battle code to display
 * @param onRegenerateCode Callback to generate a new code
 */
@Composable
private fun BattleCodeCard(
    code: String,
    onRegenerateCode: () -> Unit
) {
    @Suppress("DEPRECATION")
    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Share this code with friends:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Large battle code display
            Text(
                code,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { clipboardManager.setText(AnnotatedString(code)) }
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy", color = MaterialTheme.colorScheme.primary)
                    }

                    OutlinedButton(onClick = onRegenerateCode) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "New Code",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("New", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}


/**
 * Helper composable to display a single winner ranking row.
 *
 * @param rank Text rank indicator (1st, 2nd, 3rd)
 * @param outcome The outcome status (STABLE, OSCILLATING, etc.)
 * @param description Brief description of what this outcome means
 */
@Composable
private fun WinnerRankRow(rank: String, outcome: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            rank,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(40.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                outcome,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
