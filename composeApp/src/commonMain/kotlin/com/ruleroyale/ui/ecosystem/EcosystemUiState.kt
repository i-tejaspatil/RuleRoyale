package com.ruleroyale.ui.ecosystem

import com.ruleroyale.engine.ecosystem.model.World
import com.ruleroyale.engine.ecosystem.status.WorldStatus

data class EcosystemUiState(
    val world: World,
    val status: WorldStatus,
    val isRunning: Boolean
)
