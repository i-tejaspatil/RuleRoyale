package com.ruleroyale.engine.ecosystem.rules

import com.ruleroyale.engine.ecosystem.model.EntityType

data class ReproductionRules(
    val eaterMinEnergy: Int,
    val victimMinEnergy: Int,
    val minAge: Map<EntityType, Int>,
    val eaterEnergyCost: Int,
    val victimEnergyCost: Int
)
