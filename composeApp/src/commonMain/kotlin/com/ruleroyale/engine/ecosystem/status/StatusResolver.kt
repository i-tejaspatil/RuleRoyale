package com.ruleroyale.engine.ecosystem.status

import com.ruleroyale.engine.ecosystem.model.EntityType
import com.ruleroyale.engine.ecosystem.model.World

object StatusResolver {

    private const val GRASS_PRESSURE_RATIO = 0.5
    private const val PREY_PRESSURE_RATIO = 0.5

    fun resolve(world: World): WorldStatus {

        val grass = world.entities.count { it.type == EntityType.GRASS }
        val prey = world.entities.count { it.type == EntityType.PREY }
        val predator = world.entities.count { it.type == EntityType.PREDATOR }

        return when {

            prey == 0 && predator == 0 ->
                WorldStatus.EMPTY_WORLD

            prey == 0 && predator > 0 ->
                WorldStatus.PREY_EXTINCTION

            predator == 0 && prey > 0 ->
                WorldStatus.PREDATOR_EXTINCTION

            prey > 0 && grass < prey * GRASS_PRESSURE_RATIO ->
                WorldStatus.PREY_STARVATION

            predator > 0 && prey < predator * PREY_PRESSURE_RATIO ->
                WorldStatus.PREDATOR_STARVATION

            else ->
                WorldStatus.BALANCED
        }
    }
}
