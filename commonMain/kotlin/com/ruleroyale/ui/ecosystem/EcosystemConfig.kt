package com.ruleroyale.ui.ecosystem

import com.ruleroyale.engine.ecosystem.presets.DensityLevel
import com.ruleroyale.engine.ecosystem.rules.RuleMode

data class EcosystemConfig(
    val grassDensity: DensityLevel,
    val preyDensity: DensityLevel,
    val predatorDensity: DensityLevel,
    val interactionMode: RuleMode,
    val deterministic: Boolean
)

