package com.ruleroyale.engine.ecosystem.presets

import com.ruleroyale.engine.ecosystem.model.Entity
import com.ruleroyale.engine.ecosystem.model.EntityType
import com.ruleroyale.engine.ecosystem.model.Position
import com.ruleroyale.engine.ecosystem.model.World
import com.ruleroyale.engine.ecosystem.rng.SimulationRng

object PresetFactory {

    fun createWorld(
        rows: Int,
        cols: Int,
        grassLevel: DensityLevel,
        preyLevel: DensityLevel,
        predatorLevel: DensityLevel,
        rng: SimulationRng
    ): World {

        val totalCells = rows * cols

        val grassCount = densityToCount(grassLevel, totalCells)
        val preyCount = densityToCount(preyLevel, totalCells) / 4
        val predatorCount = densityToCount(predatorLevel, totalCells) / 8

        val allPositions = mutableListOf<Position>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                allPositions.add(Position(r, c))
            }
        }

        val shuffledPositions = rng.shuffle(allPositions).toMutableList()


        var nextId = 1
        val entities = mutableListOf<Entity>()

        fun spawn(type: EntityType, count: Int) {
            repeat(count.coerceAtMost(shuffledPositions.size)) {

                val pos = shuffledPositions.removeAt(0)

                val baseEnergy = when (type) {
                    EntityType.GRASS -> 0
                    else -> 10   // initial survival buffer
                }

                entities.add(
                    Entity(
                        id = nextId++,
                        type = type,
                        position = pos,
                        energy = baseEnergy + rng.nextInt(3), // small jitter
                        age = rng.nextInt(3)
                    )
                )
            }
        }


        spawn(EntityType.GRASS, grassCount)
        spawn(EntityType.PREY, preyCount)
        spawn(EntityType.PREDATOR, predatorCount)

        return World(
            rows = rows,
            cols = cols,
            entities = entities,
            tick = 0
        )
    }

    private fun densityToCount(
        level: DensityLevel,
        totalCells: Int
    ): Int {
        return when (level) {
            DensityLevel.LOW -> totalCells / 10
            DensityLevel.MEDIUM -> totalCells / 5
            DensityLevel.HIGH -> totalCells / 3
        }
    }
}
