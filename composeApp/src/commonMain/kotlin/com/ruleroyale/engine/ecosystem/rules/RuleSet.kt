package com.ruleroyale.engine.ecosystem.rules

data class RuleSet(
    val mode: RuleMode,
    val interactions: List<InteractionRule>,
    val energyRules: EnergyRules,
    val reproductionRules: ReproductionRules,
    val regrowthRules: RegrowthRules,
    val maxAge: Int
)