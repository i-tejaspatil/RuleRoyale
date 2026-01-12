package com.ruleroyale.ui.ecosystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.ruleroyale.engine.ecosystem.model.EntityType
import com.ruleroyale.engine.ecosystem.model.World
import kotlin.math.min

@Composable
fun EcosystemArena(world: World) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {

            val cellW = size.width / world.cols
            val cellH = size.height / world.rows
            val radius = min(cellW, cellH) / 2.5f

            world.entities.forEach { entity ->
                val color = when (entity.type) {
                    EntityType.GRASS -> Color.Green
                    EntityType.PREY -> Color.White
                    EntityType.PREDATOR -> Color.Red
                }

                drawCircle(
                    color = color,
                    radius = radius,
                    center = Offset(
                        x = entity.position.col * cellW + cellW / 2,
                        y = entity.position.row * cellH + cellH / 2
                    )
                )
            }
        }
    }
}
