package com.ruleroyale.ui.battle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ruleroyale.engine.lifefromrules.*
import kotlinx.coroutines.delay
import kotlin.random.Random

/* ===== Constants ===== */

private const val BATTLE_GRID_SIZE = 20
private const val BATTLE_TICK_DELAY_MS = 300L

/* ===== Colors (same as LifeFromRulesScreen) ===== */

private val ArenaBackground = Color(0xFF0E0E0E)
private val GridLineColor = Color(0xFF1F1F1F)
private val DeadCellColor = Color(0xFF2E2E2E)
private val AliveCellColor = Color.White

/**
 * Battle Mode arena screen for competitive Life From Rules gameplay.
 *
 * Features identical simulation mechanics to LifeFromRulesScreen but with:
 * - Deterministic initial grid based on shared battle code seed
 * - No Step/Rewind controls to prevent cheating
 * - Automatic pause when terminal state is reached
 * - Result card showing final status and tick count for manual comparison
 *
 * Players compete to find rule configurations that produce "better" outcomes
 * (STABLE > OSCILLATING > EXTINCT), with lower tick count as tiebreaker.
 *
 * UI matches LifeFromRulesScreen exactly: scrollable middle content (rules + grid),
 * fixed controls at bottom (Play/Pause, Reset).
 *
 * @param battleSeed Deterministic seed from battle code, ensures identical starting grid
 * @param onBack Callback invoked when user presses back button
 */
@Composable
fun BattleArenaScreen(
    battleSeed: Long,
    onBack: () -> Unit
) {
    // Rule parameters (same names/behavior as LifeFromRules)
    var underPop by remember { mutableStateOf(2) }
    var overPop by remember { mutableStateOf(3) }
    var reproduction by remember { mutableStateOf(3) }

    val engine = remember(underPop, overPop, reproduction) {
        LifeFromRulesEngine(
            LifeRules(
                underPopulation = underPop,
                overPopulation = overPop,
                reproduction = reproduction
            )
        )
    }

    var grid by remember {
        mutableStateOf(createSeededGrid(BATTLE_GRID_SIZE, BATTLE_GRID_SIZE, seed = battleSeed))
    }

    val history = remember { mutableStateListOf<Grid>() }

    var isRunning by remember { mutableStateOf(false) }
    var count by remember { mutableStateOf(0) }
    var status by remember { mutableStateOf(LifeStatus.RUNNING) }

    // Simulation loop — match LifeFromRulesScreen exactly
    LaunchedEffect(isRunning) {
        while (isRunning) {
            grid = advanceOneStep(engine, grid, history)
            count++
            status = LifeFromRulesAnalyzer.detectStatus(grid, history)
            delay(BATTLE_TICK_DELAY_MS)

            // Stop automatically once an outcome is detected
            if (status != LifeStatus.RUNNING) {
                isRunning = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ArenaBackground)
            .padding(top = 44.dp) // Shift entire content down for iOS status bar + Dynamic Island
    ) {

        /* ===== TOP BAR (match LifeFromRulesScreen layout) ===== */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp), // Keep original padding
            verticalAlignment = Alignment.CenterVertically
        ) {

            // LEFT — Back button (fixed width)
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

            // CENTER — title + battle status
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Battle Mode",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Count : $count  •  ${
                            status.name.lowercase().replaceFirstChar { it.uppercase() }
                        }",
                        color = Color.White.copy(alpha = 0.75f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            // RIGHT — Dummy spacer
            Spacer(modifier = Modifier.width(72.dp))
        }

        // ===== RULES + GRID (scrollable, like LifeFromRulesScreen) =====
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            RulesExplanation(
                underPop = underPop,
                overPop = overPop,
                reproduction = reproduction
            )

            RuleSliders(
                rule1 = underPop,
                rule2 = overPop,
                rule3 = reproduction,
                enabled = !isRunning,
                onRule1Change = {
                    underPop = it
                    isRunning = false
                    history.clear()
                    count = 0
                    status = LifeStatus.RUNNING
                },
                onRule2Change = {
                    overPop = it
                    isRunning = false
                    history.clear()
                    count = 0
                    status = LifeStatus.RUNNING
                },
                onRule3Change = {
                    reproduction = it
                    isRunning = false
                    history.clear()
                    count = 0
                    status = LifeStatus.RUNNING
                }
            )

            /* ===== GRID ===== */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(16.dp)
            ) {
                Column {
                    repeat(grid.rows) { r ->
                        Row(modifier = Modifier.weight(1f)) {
                            repeat(grid.cols) { c ->
                                val pos = Position(r, c)
                                val cell = grid.getCell(pos)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(GridLineColor)
                                        .padding(1.dp)
                                        .clickable(enabled = !isRunning) {
                                            grid = toggleCell(grid, pos)
                                            status = LifeFromRulesAnalyzer.detectStatus(grid, history)
                                        }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                if (cell == CellState.ALIVE) AliveCellColor else DeadCellColor
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Show battle result once finished
            if (status != LifeStatus.RUNNING && count > 0) {
                BattleResultCard(status = status, ticks = count.toLong())
            }
        }

        /* ===== CONTROLS (NO Step/Rewind) ===== */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            ControlButton(if (isRunning) "Pause" else "Play") {
                isRunning = !isRunning
            }

            Spacer(Modifier.width(10.dp))

            ControlButton("Reset", enabled = !isRunning) {
                isRunning = false
                history.clear()
                grid = createSeededGrid(BATTLE_GRID_SIZE, BATTLE_GRID_SIZE, seed = battleSeed)
                count = 0
                status = LifeStatus.RUNNING
            }
        }
    }
}


private fun snapshot(grid: Grid): Grid =
    Grid(
        grid.rows,
        grid.cols,
        Array(grid.rows) { r ->
            Array(grid.cols) { c -> grid.getCell(Position(r, c)) }
        }
    )

private fun advanceOneStep(
    engine: LifeFromRulesEngine,
    grid: Grid,
    history: MutableList<Grid>
): Grid {
    history.add(snapshot(grid))
    return engine.tick(grid)
}

private fun toggleCell(grid: Grid, pos: Position): Grid {
    val cells = Array(grid.rows) { r ->
        Array(grid.cols) { c ->
            if (r == pos.row && c == pos.col) {
                if (grid.getCell(pos) == CellState.ALIVE) CellState.DEAD else CellState.ALIVE
            } else {
                grid.getCell(Position(r, c))
            }
        }
    }
    return grid.copyWith(cells)
}

@Composable
private fun ControlButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.height(40.dp)
    ) {
        Text(text, textAlign = TextAlign.Center)
    }
}

@Composable
private fun RulesExplanation(
    underPop: Int,
    overPop: Int,
    reproduction: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(
            text = "Current Rules",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text = "Rule 1: Alive cell with alive neighbors < $underPop → Dies",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.85f)
        )

        Text(
            text = "Rule 2: Alive cell with alive neighbors > $overPop → Dies",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.85f)
        )

        Text(
            text = "Rule 3: Dead cell with alive neighbors = $reproduction → Becomes alive",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.85f)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Legend:  White = Alive   •   Dark = Dead",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.65f)
        )
    }
}

@Composable
private fun RuleSliders(
    rule1: Int,
    rule2: Int,
    rule3: Int,
    enabled: Boolean,
    onRule1Change: (Int) -> Unit,
    onRule2Change: (Int) -> Unit,
    onRule3Change: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        CompactRuleSlider(
            label = "Rule 1",
            value = rule1,
            range = 0..4,
            enabled = enabled,
            onValueChange = onRule1Change
        )

        CompactRuleSlider(
            label = "Rule 2",
            value = rule2,
            range = 2..6,
            enabled = enabled,
            onValueChange = onRule2Change
        )

        CompactRuleSlider(
            label = "Rule 3",
            value = rule3,
            range = 1..6,
            enabled = enabled,
            onValueChange = onRule3Change
        )
    }
}

@Composable
private fun CompactRuleSlider(
    label: String,
    value: Int,
    range: IntRange,
    enabled: Boolean,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label: $value",
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(80.dp)
        )

        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = range.last - range.first - 1,
            enabled = enabled,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun createSeededGrid(rows: Int, cols: Int, seed: Long): Grid {
    val random = Random(seed)
    val cells = Array(rows) {
        Array(cols) {
            if (random.nextFloat() < 0.25f) CellState.ALIVE else CellState.DEAD
        }
    }
    return Grid(rows, cols, cells)
}
