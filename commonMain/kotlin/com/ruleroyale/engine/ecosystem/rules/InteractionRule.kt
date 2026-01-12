package com.ruleroyale.engine.ecosystem.rules

import com.ruleroyale.engine.ecosystem.model.EntityType

data class InteractionRule(
    val eater: EntityType,
    val eaten: EntityType,
    val energyGain: Int
)