package com.ruleroyale.engine.ecosystem.model

/**
 * Represents the complete state of the ecosystem simulation at a given moment.
 *
 * The world is an immutable snapshot containing the grid dimensions, all living
 * entities, and the current simulation time. A new World instance is created
 * for each tick of the simulation.
 *
 * @property rows The height of the world grid
 * @property cols The width of the world grid
 * @property entities All living entities currently in the world
 * @property tick The simulation time step counter (starts at 0)
 */
data class World(
    val rows: Int,
    val cols: Int,
    val entities: List<Entity>,
    val tick: Long
)