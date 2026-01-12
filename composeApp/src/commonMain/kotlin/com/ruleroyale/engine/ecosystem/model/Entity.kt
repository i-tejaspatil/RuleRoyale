package com.ruleroyale.engine.ecosystem.model

/**
 * Represents a single entity in the ecosystem simulation.
 *
 * Entities can be grass, prey, or predators. Each entity has energy that depletes
 * over time and increases when consuming other entities. Entities age each tick
 * and die when energy reaches zero or maximum age is exceeded.
 *
 * @property id Unique identifier for this entity instance
 * @property type The kind of entity (GRASS, PREY, or PREDATOR)
 * @property position The entity's location in the world grid
 * @property energy Current energy level; entity dies when this reaches zero
 * @property age Number of ticks this entity has existed
 */
data class Entity(
    val id: Int,
    val type: EntityType,
    val position: Position,
    val energy: Int,
    val age: Int
)