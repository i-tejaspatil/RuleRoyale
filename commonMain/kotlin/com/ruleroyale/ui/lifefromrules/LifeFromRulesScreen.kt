package com.ruleroyale.ui.lifefromrules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ruleroyale.engine.lifefromrules.*
import kotlinx.coroutines.delay
import com.ruleroyale.engine.lifefromrules.LifeStatus
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import kotlin.random.Random

/* ===== Constants ===== */

private const val DEFAULT_GRID_SIZE = 20
private const val TICK_DELAY_MS = 300L
private const val DEFAULT_UNDER_POPULATION = 2
private const val DEFAULT_OVER_POPULATION = 3
private const val DEFAULT_REPRODUCTION = 3

/* ===== Colors ===== */

private val ArenaBackground = Color(0xFF0E0E0E)
private val GridLineColor   = Color(0xFF1F1F1F)
private val DeadCellColor   = Color(0xFF2E2E2E)
private val AliveCellColor  = Color.White

@Composable
fun LifeFromRulesScreen(
    onBack: () -> Unit = {}
) {
    // Life From Rules - rule parameters
    var underPop by remember { mutableStateOf(DEFAULT_UNDER_POPULATION) }
    var overPop by remember { mutableStateOf(DEFAULT_OVER_POPULATION) }
    var reproduction by remember { mutableStateOf(DEFAULT_REPRODUCTION) }

    // Create engine that updates when rules change
    val engine = remember(underPop, overPop, reproduction) {
        LifeFromRulesEngine(
            LifeRules(
                underPopulation = underPop,
                overPopulation = overPop,
                reproduction = reproduction
            )
        )
    }

    var grid by remember { mutableStateOf(createSeededGrid(DEFAULT_GRID_SIZE, DEFAULT_GRID_SIZE)) }

    val history = remember { mutableStateListOf<Grid>() }

    var isRunning by remember { mutableStateOf(false) }
    var count by remember { mutableStateOf(0) }

    var status by remember { mutableStateOf(LifeStatus.RUNNING) }


    LaunchedEffect(isRunning) {
        while (isRunning) {
            grid = advanceOneStep(engine, grid, history)
            count++
            status = LifeFromRulesAnalyzer.detectStatus(grid, history)
            delay(TICK_DELAY_MS)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ArenaBackground)
            .padding(top = 44.dp) // Shift entire content down for iOS status bar + Dynamic Island
    ) {

        /* ===== TOP BAR (NO WRAP, TRUE CENTER) ===== */
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
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }

            // CENTER — Truly centered title + status
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = "Life From Rules",
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

            // RIGHT — Dummy spacer (same width as back button)
            Spacer(modifier = Modifier.width(72.dp))
        }


        // ===== RULES =====

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

            RulesExplanation(
                underPop = underPop,
                overPop = overPop,
                reproduction = reproduction
            )

            // ===== RULE SLIDERS =====

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
                },
                onRule2Change = {
                    overPop = it
                    isRunning = false
                    history.clear()
                    count = 0
                },
                onRule3Change = {
                    reproduction = it
                    isRunning = false
                    history.clear()
                    count = 0
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
                                                if (cell == CellState.ALIVE)
                                                    AliveCellColor
                                                else
                                                    DeadCellColor
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        /* ===== CONTROLS (NO OVERFLOW) ===== */
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

            Spacer(Modifier.width(6.dp))

            ControlButton("Step", enabled = !isRunning) {
                grid = advanceOneStep(engine, grid, history)
                count++
                status = LifeFromRulesAnalyzer.detectStatus(grid, history)
            }

            Spacer(Modifier.width(6.dp))

            ControlButton(
                "Rewind",
                enabled = !isRunning && history.isNotEmpty()
            ) {
                val i = history.lastIndex
                grid = history[i]
                history.removeAt(i)
                count--
                status = LifeFromRulesAnalyzer.detectStatus(grid, history)

            }

            Spacer(Modifier.width(6.dp))

            ControlButton("Reset", enabled = !isRunning) {
                isRunning = false
                history.clear()
                grid = createSeededGrid(DEFAULT_GRID_SIZE, DEFAULT_GRID_SIZE)
                count = 0
                status = LifeStatus.RUNNING

            }
        }
    }
}

/* ===== Button ===== */

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
            if (r == pos.row && c == pos.col)
                if (grid.getCell(pos) == CellState.ALIVE) CellState.DEAD else CellState.ALIVE
            else grid.getCell(Position(r, c))
        }
    }
    return grid.copyWith(cells)
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

        // Title
        Text(
            text = "Current Rules",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White
        )

        Spacer(Modifier.height(2.dp))

        // Rules
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

        // Legend (compact, inline)
        Text(
            text = "Legend:  White = Alive   •   Dark = Dead",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.65f)
        )
    }
}


@Composable
private fun RuleLine(text: String) {
    Text(
        text = "• $text",
        color = Color.White.copy(alpha = 0.85f),
        style = MaterialTheme.typography.bodySmall
    )
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


@Composable
private fun RuleSlider(
    label: String,
    value: Int,
    range: IntRange,
    enabled: Boolean,
    onValueChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Text(
            text = "$label : $value",
            color = Color.White,
            style = MaterialTheme.typography.bodySmall
        )

        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = range.last - range.first - 1,
            enabled = enabled
        )
    }
}

private fun createSeededGrid(rows: Int, cols: Int): Grid {
    val random = Random.Default
    val cells = Array(rows) {
        Array(cols) {
            if (random.nextFloat() < 0.18f) CellState.ALIVE else CellState.DEAD
        }
    }
    return Grid(rows, cols, cells)
}
