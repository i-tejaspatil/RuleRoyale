package com.ruleroyale.engine.ecosystem.engine

import com.ruleroyale.engine.ecosystem.model.World
import com.ruleroyale.engine.ecosystem.rules.RuleSet
import com.ruleroyale.engine.ecosystem.rng.SimulationRng

class EcosystemEngine(
    private val ruleSet: RuleSet,
    private val rng: SimulationRng
) {

    fun tick(world: World): World {
        return TickProcessor.process(world, ruleSet, rng)
    }
}