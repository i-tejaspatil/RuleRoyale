package com.ruleroyale.engine.ecosystem.rules

import com.ruleroyale.engine.ecosystem.model.EntityType

object RuleSetFactory {

    fun create(mode: RuleMode): RuleSet {

        val interactions = when (mode) {
            RuleMode.NORMAL -> listOf(
                InteractionRule(
                    eater = EntityType.PREDATOR,
                    eaten = EntityType.PREY,
                    energyGain = 14
                ),
                InteractionRule(
                    eater = EntityType.PREY,
                    eaten = EntityType.GRASS,
                    energyGain = 6
                )
            )

            RuleMode.INVERTED -> listOf(
                InteractionRule(
                    eater = EntityType.PREY,
                    eaten = EntityType.PREDATOR,
                    energyGain = 14
                ),
                InteractionRule(
                    eater = EntityType.PREY,
                    eaten = EntityType.GRASS,
                    energyGain = 6
                )
            )
        }

        return RuleSet(
            mode = mode,
            interactions = interactions,
            energyRules = EnergyRules(
                eaterInitialEnergy = 20,
                victimInitialEnergy = 10,
                eaterDecay = 2,
                victimDecay = 1
            ),

            reproductionRules = ReproductionRules(
                eaterMinEnergy = 28,
                victimMinEnergy = 14,
                minAge = mapOf(
                    EntityType.PREY to 6,
                    EntityType.PREDATOR to 10
                ),
                eaterEnergyCost = 12,
                victimEnergyCost = 6
            ),


            regrowthRules = RegrowthRules(
                grassRegrowthTicks = 6
            ),
            maxAge = 40
        )
    }
}
