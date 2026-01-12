package com.ruleroyale.engine.ecosystem.engine

import com.ruleroyale.engine.ecosystem.model.Entity
import com.ruleroyale.engine.ecosystem.model.EntityType
import com.ruleroyale.engine.ecosystem.model.Position
import com.ruleroyale.engine.ecosystem.model.World
import com.ruleroyale.engine.ecosystem.rules.RuleSet
import com.ruleroyale.engine.ecosystem.rng.SimulationRng

/**
 * Processes a single simulation tick for the ecosystem.
 *
 * This object implements the core simulation loop with the following phases:
 * 1. Eating resolution - entities consume adjacent food sources
 * 2. Energy decay - entities lose energy over time
 * 3. Death resolution - entities with no energy or max age die
 * 4. Movement - entities move toward food or random empty spaces
 * 5. Reproduction - entities spawn offspring when conditions are met
 * 6. Grass regrowth - new grass appears at regular intervals
 * 7. Age increment - all entities age by one tick
 *
 * All operations are performed on immutable snapshots to ensure deterministic
 * behavior and prevent race conditions.
 */
object TickProcessor {

    /**
     * Processes one complete simulation tick and returns the updated world state.
     *
     * @param world The current world state containing all entities
     * @param ruleSet The rule configuration defining interactions, energy, and reproduction
     * @param rng The random number generator for deterministic or random behavior
     * @return A new World instance with updated entities and incremented tick count
     */
    fun process(
        world: World,
        ruleSet: RuleSet,
        rng: SimulationRng
    ): World {

        val snapshotEntities = world.entities

        // Derive roles dynamically from rules
        val eaters: Set<EntityType> = ruleSet.interactions
            .map { it.eater }
            .toSet()

        val victims: Set<EntityType> = ruleSet.interactions
            .map { it.eaten }
            .toSet()


        // STEP 1 — EATING RESOLUTION
        val eatenEntityIds = mutableSetOf<Int>()
        val energyGains = mutableMapOf<Int, Int>()

        for (entity in snapshotEntities) {
            if (entity.id in eatenEntityIds) continue

            val adjacentEntities = snapshotEntities.filter { other ->
                other.id != entity.id &&
                        other.id !in eatenEntityIds &&
                        isAdjacent(entity.position, other.position)
            }

            val possibleMeals = adjacentEntities.mapNotNull { target ->
                val rule = ruleSet.interactions.firstOrNull {
                    it.eater == entity.type && it.eaten == target.type
                }
                if (rule != null) target to rule else null
            }

            val chosen = rng.pickOne(possibleMeals)

            if (chosen != null) {
                val (target, rule) = chosen
                eatenEntityIds.add(target.id)
                energyGains[entity.id] =
                    (energyGains[entity.id] ?: 0) + rule.energyGain
            }
        }

        val entitiesAfterEating = snapshotEntities
            .filterNot { it.id in eatenEntityIds }
            .map { entity ->
                val gain = energyGains[entity.id] ?: 0
                if (gain > 0) {
                    entity.copy(energy = entity.energy + gain)
                } else entity
            }

        // STEP 2 — ENERGY DECAY
        val entitiesAfterDecay = entitiesAfterEating.map { entity ->
            val decay = when {
                entity.type in eaters -> ruleSet.energyRules.eaterDecay
                entity.type in victims -> ruleSet.energyRules.victimDecay
                else -> 0
            }

            if (decay > 0) {
                entity.copy(energy = entity.energy - decay)
            } else {
                entity
            }
        }


        // STEP 3 - Death resolution
        val entitiesAfterDeath = entitiesAfterDecay.filter { entity ->
            when (entity.type) {
                EntityType.GRASS -> true   // grass never dies naturally
                else -> entity.energy > 0 && entity.age < ruleSet.maxAge
            }
        }


        // STEP 4 - Movement
        val occupiedPositions = entitiesAfterDeath
            .associateBy { it.position }
            .toMutableMap()

        val movedEntities = mutableListOf<Entity>()

        for (entity in entitiesAfterDeath) {

            // Grass does not move
            if (entity.type == EntityType.GRASS) {
                movedEntities.add(entity)
                continue
            }

            val neighbors = listOf(
                Position(entity.position.row - 1, entity.position.col),
                Position(entity.position.row + 1, entity.position.col),
                Position(entity.position.row, entity.position.col - 1),
                Position(entity.position.row, entity.position.col + 1)
            ).filter { pos ->
                pos.row in 0 until world.rows &&
                        pos.col in 0 until world.cols
            }

            // Find edible targets
            val edibleTargets = neighbors.mapNotNull { pos ->
                val target = occupiedPositions[pos]
                if (target != null) {
                    val ruleExists = ruleSet.interactions.any {
                        it.eater == entity.type && it.eaten == target.type
                    }
                    if (ruleExists) pos else null
                } else null
            }

            val targetPosition = when {
                edibleTargets.isNotEmpty() ->
                    rng.pickOne(edibleTargets)

                else -> {
                    val emptyNeighbors = neighbors.filter { it !in occupiedPositions }
                    rng.pickOne(emptyNeighbors)
                }
            }

            if (targetPosition != null && targetPosition !in occupiedPositions) {
                occupiedPositions.remove(entity.position)
                val moved = entity.copy(position = targetPosition)
                occupiedPositions[targetPosition] = moved
                movedEntities.add(moved)
            } else {
                movedEntities.add(entity)
            }
        }

        // STEP 5 — REPRODUCTION
        val occupiedAfterMove = movedEntities
            .associateBy { it.position }
            .toMutableMap()

        val reproducedEntities = mutableListOf<Entity>()
        var nextEntityId = (movedEntities.maxOfOrNull { it.id } ?: 0) + 1

        for (entity in movedEntities) {

            val minEnergy = if (entity.type in eaters) {
                ruleSet.reproductionRules.eaterMinEnergy
            } else {
                ruleSet.reproductionRules.victimMinEnergy
            }

            val minAge = ruleSet.reproductionRules.minAge[entity.type] ?: Int.MAX_VALUE
            val energyCost = if (entity.type in eaters) {
                ruleSet.reproductionRules.eaterEnergyCost
            } else {
                ruleSet.reproductionRules.victimEnergyCost
            }


            val canReproduce =
                entity.type != EntityType.GRASS &&
                        entity.energy >= minEnergy &&
                        entity.age >= minAge

            if (!canReproduce) {
                reproducedEntities.add(entity)
                continue
            }

            val neighbors = listOf(
                Position(entity.position.row - 1, entity.position.col),
                Position(entity.position.row + 1, entity.position.col),
                Position(entity.position.row, entity.position.col - 1),
                Position(entity.position.row, entity.position.col + 1)
            ).filter { pos ->
                pos.row in 0 until world.rows &&
                        pos.col in 0 until world.cols &&
                        pos !in occupiedAfterMove
            }

            val spawnPosition = rng.pickOne(neighbors)

            if (spawnPosition != null && spawnPosition !in occupiedAfterMove) {
                val parentAfterCost =
                    entity.copy(energy = entity.energy - energyCost)

                val baseEnergy = if (entity.type in eaters) {
                    ruleSet.energyRules.eaterInitialEnergy
                } else {
                    ruleSet.energyRules.victimInitialEnergy
                }

                val childInitialEnergy = baseEnergy + rng.nextInt(3) - 1

                val child = Entity(
                    id = nextEntityId++,
                    type = entity.type,
                    position = spawnPosition,
                    energy = childInitialEnergy,
                    age = 0  // All newborns start at age 0
                )

                // Reserve position immediately to prevent collision
                occupiedAfterMove[spawnPosition] = child
                reproducedEntities.add(parentAfterCost)
                reproducedEntities.add(child)
            } else {
                reproducedEntities.add(entity)
            }
        }

        // STEP 6 — GRASS REGROWTH (continuous independent growth every N ticks)
        val occupiedAfterReproduction = reproducedEntities
            .associateBy { it.position }
            .toMutableMap()

        val regrownEntities = reproducedEntities.toMutableList()

        if (world.tick % ruleSet.regrowthRules.grassRegrowthTicks == 0L) {

            val emptyPositions = (0 until world.rows).flatMap { row ->
                (0 until world.cols).mapNotNull { col ->
                    val pos = Position(row, col)
                    if (pos !in occupiedAfterReproduction) pos else null
                }
            }

            val grassToSpawn = minOf(
                ruleSet.regrowthRules.grassPerRegrowth,
                emptyPositions.size
            )

            val spawnPositions = rng
                .shuffle(emptyPositions)
                .take(grassToSpawn)

            for (pos in spawnPositions) {
                val grass = Entity(
                    id = nextEntityId++,
                    type = EntityType.GRASS,
                    position = pos,
                    energy = 0,
                    age = 0
                )
                occupiedAfterReproduction[pos] = grass
                regrownEntities.add(grass)
            }
        }


        // STEP 7 - Age increment
        val agedEntities = regrownEntities.map { entity ->
            entity.copy(age = entity.age + 1)
        }

        // FINAL WORLD UPDATE
        return world.copy(
            entities = agedEntities,
            tick = world.tick + 1
        )




    }

    /**
     * Checks if two positions are adjacent (horizontally or vertically, not diagonally).
     *
     * @param a First position
     * @param b Second position
     * @return true if positions are exactly one cell apart horizontally or vertically
     */
    private fun isAdjacent(a: Position, b: Position): Boolean {
        val dr = kotlin.math.abs(a.row - b.row)
        val dc = kotlin.math.abs(a.col - b.col)
        return (dr == 1 && dc == 0) || (dr == 0 && dc == 1)
    }
}
