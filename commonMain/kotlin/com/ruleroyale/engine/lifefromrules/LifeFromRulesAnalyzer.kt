package com.ruleroyale.engine.lifefromrules

/**
 * Analyzes high-level behavior of the simulation.
 * Uses bounded history to detect stability and oscillation.
 */
object LifeFromRulesAnalyzer {

    private const val OSCILLATION_WINDOW = 6
    private const val SATURATION_THRESHOLD = 0.9

    fun detectStatus(
        current: Grid,
        history: List<Grid>
    ): LifeStatus {

        val aliveCount = current.countAlive()
        val totalCells = current.rows * current.cols

        // Extinction
        if (aliveCount == 0) {
            return LifeStatus.EXTINCT
        }

        // Saturation (> 90% alive)
        if (aliveCount >= totalCells * SATURATION_THRESHOLD) {
            return LifeStatus.SATURATED
        }

        // Stable (same as previous step)
        if (history.isNotEmpty() && current == history.last()) {
            return LifeStatus.STABLE
        }

        // Oscillating (match in bounded history)
        val recent = history.takeLast(OSCILLATION_WINDOW)
        if (recent.any { it == current }) {
            return LifeStatus.OSCILLATING
        }

        // Otherwise
        return LifeStatus.RUNNING
    }
}
