package com.ruleroyale.engine.ecosystem.rng

import kotlin.random.Random

class SimulationRng(seed: Long) {

    private val random = Random(seed)

    fun nextInt(bound: Int): Int {
        return random.nextInt(bound)
    }

    fun nextFloat(): Float {
        return random.nextFloat()
    }

    fun <T> pickOne(list: List<T>): T? {
        if (list.isEmpty()) return null
        return list[nextInt(list.size)]
    }

    fun <T> shuffle(list: List<T>): List<T> {
        return list.shuffled(random)
    }
}
