package com.ruleroyale.engine.ecosystem.model

/**
 * The three types of entities in the ecosystem simulation.
 *
 * The food chain typically flows: GRASS → PREY → PREDATOR
 * However, this can be inverted using different rule modes.
 */
enum class EntityType {
    /** Plants that regrow periodically and provide food for herbivores */
    GRASS,

    /** Herbivores that eat grass and are hunted by predators */
    PREY,

    /** Carnivores at the top of the food chain */
    PREDATOR
}