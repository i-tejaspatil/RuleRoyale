package com.ruleroyale.ui.ecosystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ruleroyale.engine.ecosystem.model.EntityType
import com.ruleroyale.engine.ecosystem.rules.RuleMode
import com.ruleroyale.engine.ecosystem.model.World
import androidx.compose.runtime.collectAsState
import com.ruleroyale.engine.ecosystem.presets.DensityLevel
import com.ruleroyale.engine.ecosystem.status.WorldStatus





@Composable
fun EcosystemScreen(
    config: EcosystemConfig,
    controller: EcosystemController,
    onBack: () -> Unit,
    onConfigChange: (EcosystemConfig) -> Unit,
    onReset: () -> Unit
)


{
    val uiState by controller.state.collectAsState()


    LaunchedEffect(uiState.isRunning) {
        while (uiState.isRunning) {
            kotlinx.coroutines.delay(600L)
            controller.tick()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = 44.dp, start = 12.dp, end = 12.dp, bottom = 12.dp)
    ) {

        /* ───────────────── TOP BAR ───────────────── */
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
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

            Spacer(Modifier.width(8.dp))

            Text(
                text = "Ecosystem Arena",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = uiState.status.name
                    .replace("_", " ")
                    .lowercase()
                    .replaceFirstChar { it.uppercase() },
                color = when (uiState.status) {
                    WorldStatus.BALANCED -> Color.Green
                    WorldStatus.PREY_STARVATION -> Color.Yellow
                    WorldStatus.PREDATOR_STARVATION -> Color.Yellow
                    WorldStatus.PREY_EXTINCTION -> Color.Red
                    WorldStatus.PREDATOR_EXTINCTION -> Color.Red
                    WorldStatus.EMPTY_WORLD -> Color.Gray
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Mode: ${config.interactionMode} | ${if (config.deterministic) "Deterministic" else "Random"}",
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )

        /* ───────────────── LEGEND ───────────────── */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(color = Color.Green, label = "Grass")
            LegendItem(color = Color.White, label = "Prey")
            LegendItem(color = Color.Red, label = "Predator")
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            /* ───────────────── MODES ───────────────── */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ModeSelectorRow(
                    title = "Interaction",
                    left = "Normal",
                    right = "Inverted",
                    selected = if (config.interactionMode == RuleMode.NORMAL) "Normal" else "Inverted",
                    onSelect = { selected ->
                        onConfigChange(
                            config.copy(
                                interactionMode = if (selected == "Normal") {
                                    RuleMode.NORMAL
                                } else {
                                    RuleMode.INVERTED
                                }
                            )
                        )
                    }
                )

                ModeSelectorRow(
                    title = "Simulation",
                    left = "Deterministic",
                    right = "Random",
                    selected = if (config.deterministic) "Deterministic" else "Random",
                    onSelect = { selected ->
                        onConfigChange(
                            config.copy(
                                deterministic = selected == "Deterministic"
                            )
                        )
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            /* ───────────────── INITIAL CONDITIONS ───────────────── */
            DensityRow(
                title = "Grass",
                value = config.grassDensity,
                onChange = { onConfigChange(config.copy(grassDensity = it)) }
            )

            DensityRow(
                title = "Prey",
                value = config.preyDensity,
                onChange = { onConfigChange(config.copy(preyDensity = it)) }
            )

            DensityRow(
                title = "Predator",
                value = config.predatorDensity,
                onChange = { onConfigChange(config.copy(predatorDensity = it)) }
            )

            Spacer(Modifier.height(16.dp))

            /* ───────────────── ARENA PLACEHOLDER ───────────────── */
            Arena(
                world = uiState.world
            )
        }

        Spacer(Modifier.height(16.dp))

        /* ───────────────── CONTROLS (FIXED AT BOTTOM) ───────────────── */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { controller.play() },
                enabled = !uiState.isRunning

            ) {
                Text("Play")
            }

            Button(
                onClick = { controller.pause() },
                enabled = uiState.isRunning

            ) {
                Text("Pause")
            }

            Button(onClick = onReset) {
                Text("Reset")
            }

        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color)
        )
        Spacer(Modifier.width(6.dp))
        Text(text = label, color = Color.White)
    }
}

@Composable
private fun ModeSelectorRow(
    title: String,
    left: String,
    right: String,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = Color.White, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(Modifier.height(4.dp))

        Row {
            SegmentButton(
                text = left,
                selected = selected == left,
                onClick = { onSelect(left) }
            )
            SegmentButton(
                text = right,
                selected = selected == right,
                onClick = { onSelect(right) }
            )
        }
    }
}



@Composable
private fun DensityRow(
    title: String,
    value: DensityLevel,
    onChange: (DensityLevel) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            modifier = Modifier.width(70.dp)
        )

        val levels: List<DensityLevel> = listOf(
            DensityLevel.LOW,
            DensityLevel.MEDIUM,
            DensityLevel.HIGH
        )

        levels.forEach { level ->
            SegmentButton(
                text = level.name.lowercase().replaceFirstChar { it.uppercase() },
                selected = value == level,
                onClick = { onChange(level) }
            )
        }

    }

    Spacer(Modifier.height(6.dp))
}



@Composable
private fun SegmentButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(end = 4.dp)
            .border(
                width = 1.dp,
                color = if (selected) Color.White else Color.DarkGray
            )
            .background(
                if (selected) Color.DarkGray else Color.Transparent
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .selectable(
                selected = selected,
                onClick = onClick
            )
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun Arena(
    world: World
) {
    if (world == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .border(1.dp, Color.DarkGray)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text("No World", color = Color.Gray)
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.Black)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {

            val cellSize = size.width / world.cols

            /* ───── Grid ───── */
            for (i in 0..world.cols) {
                drawLine(
                    color = Color.DarkGray,
                    start = Offset(i * cellSize, 0f),
                    end = Offset(i * cellSize, size.height),
                    strokeWidth = 0.5f
                )
                drawLine(
                    color = Color.DarkGray,
                    start = Offset(0f, i * cellSize),
                    end = Offset(size.width, i * cellSize),
                    strokeWidth = 0.5f
                )
            }

            /* ───── Entities ───── */
            world.entities.forEach { entity ->

                val color = when (entity.type) {
                    EntityType.GRASS -> Color.Green
                    EntityType.PREY -> Color.White
                    EntityType.PREDATOR -> Color.Red
                }

                drawCircle(
                    color = color,
                    radius = cellSize * 0.35f,
                    center = Offset(
                        x = (entity.position.col + 0.5f) * cellSize,
                        y = (entity.position.row + 0.5f) * cellSize
                    )
                )
            }
        }
    }
}
