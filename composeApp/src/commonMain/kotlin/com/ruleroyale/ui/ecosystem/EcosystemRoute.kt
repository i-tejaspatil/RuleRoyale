package com.ruleroyale.ui.ecosystem

import androidx.compose.runtime.*
import com.ruleroyale.engine.ecosystem.engine.EcosystemEngine
import com.ruleroyale.engine.ecosystem.presets.DensityLevel
import com.ruleroyale.engine.ecosystem.presets.PresetFactory
import com.ruleroyale.engine.ecosystem.rng.SimulationRng
import com.ruleroyale.engine.ecosystem.rules.RuleMode
import com.ruleroyale.engine.ecosystem.rules.RuleSetFactory
import kotlin.random.Random

/* ===== Constants ===== */

private const val WORLD_ROWS = 20
private const val WORLD_COLS = 20
private const val DETERMINISTIC_SEED = 42L

/**
 * Route composable that manages the ecosystem simulation state and configuration.
 *
 * This composable handles:
 * - Configuration management for density levels and interaction rules
 * - RNG initialization based on deterministic or random mode
 * - World creation with configured parameters
 * - Engine and controller lifecycle management
 *
 * @param onBack Callback invoked when user navigates back
 */
@Composable
fun EcosystemRoute(
    onBack: () -> Unit
) {
    var config by remember {
        mutableStateOf(
            EcosystemConfig(
                grassDensity = DensityLevel.MEDIUM,
                preyDensity = DensityLevel.MEDIUM,
                predatorDensity = DensityLevel.MEDIUM,
                interactionMode = RuleMode.NORMAL,
                deterministic = true
            )
        )
    }


    val rng = remember(config) {
        SimulationRng(
            if (config.deterministic) DETERMINISTIC_SEED else Random.nextLong()
        )
    }

    val controller = remember(config) {

        val world = PresetFactory.createWorld(
            rows = WORLD_ROWS,
            cols = WORLD_COLS,
            grassLevel = config.grassDensity,
            preyLevel = config.preyDensity,
            predatorLevel = config.predatorDensity,
            rng = rng
        )

        val engine = EcosystemEngine(
            ruleSet = RuleSetFactory.create(config.interactionMode),
            rng = rng
        )

        EcosystemController(
            initialWorld = world,
            engine = engine
        )
    }


    EcosystemScreen(
        config = config,
        controller = controller,
        onBack = onBack,
        onConfigChange = { config = it },
        onReset = {
            controller.reset(
                PresetFactory.createWorld(
                    rows = WORLD_ROWS,
                    cols = WORLD_COLS,
                    grassLevel = config.grassDensity,
                    preyLevel = config.preyDensity,
                    predatorLevel = config.predatorDensity,
                    rng = rng
                )
            )
        }
    )

}


