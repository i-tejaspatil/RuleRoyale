package com.ruleroyale.ui.ecosystem

import com.ruleroyale.engine.ecosystem.engine.EcosystemEngine
import com.ruleroyale.engine.ecosystem.model.World
import com.ruleroyale.engine.ecosystem.status.StatusResolver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class EcosystemController(
    initialWorld: World,
    private val engine: EcosystemEngine
) {

    private val _state = MutableStateFlow(
        EcosystemUiState(
            world = initialWorld,
            status = StatusResolver.resolve(initialWorld),
            isRunning = false
        )
    )

    val state: StateFlow<EcosystemUiState> = _state

    fun play() {
        _state.update { it.copy(isRunning = true) }
    }

    fun pause() {
        _state.update { it.copy(isRunning = false) }
    }

    fun reset(newWorld: World) {
        _state.value = EcosystemUiState(
            world = newWorld,
            status = StatusResolver.resolve(newWorld),
            isRunning = false
        )
    }

    fun tick() {
        val current = _state.value
        if (!current.isRunning) return

        val nextWorld = engine.tick(current.world)
        _state.value = current.copy(
            world = nextWorld,
            status = StatusResolver.resolve(nextWorld)
        )
    }
}
