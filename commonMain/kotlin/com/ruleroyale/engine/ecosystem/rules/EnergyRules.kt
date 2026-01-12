package com.ruleroyale.engine.ecosystem.rules

data class EnergyRules(
    val eaterInitialEnergy: Int,
    val victimInitialEnergy: Int,
    val eaterDecay: Int,
    val victimDecay: Int
)
