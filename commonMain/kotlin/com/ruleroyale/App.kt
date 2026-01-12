package com.ruleroyale

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ruleroyale.ui.battle.BattleArenaScreen
import com.ruleroyale.ui.battle.BattleSetupScreen
import com.ruleroyale.ui.ecosystem.EcosystemRoute
import com.ruleroyale.ui.home.HomeScreen
import com.ruleroyale.ui.lifefromrules.LifeFromRulesScreen
import com.ruleroyale.ui.theme.RuleRoyaleTheme

sealed class Screen {
    object Home : Screen()
    object LifeFromRules : Screen()
    object Ecosystem : Screen()
    object BattleSetup : Screen()
    data class BattleArena(val seed: Long) : Screen()
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    RuleRoyaleTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val screen = currentScreen) {

                Screen.Home -> {
                    HomeScreen(
                        onEnterLifeFromRules = {
                            currentScreen = Screen.LifeFromRules
                        },
                        onEnterEcosystem = {
                            currentScreen = Screen.Ecosystem
                        },
                        onEnterBattle = {
                            currentScreen = Screen.BattleSetup
                        }
                    )
                }

                Screen.LifeFromRules -> {
                    LifeFromRulesScreen(
                        onBack = {
                            currentScreen = Screen.Home
                        }
                    )
                }

                Screen.Ecosystem -> {
                    EcosystemRoute(
                        onBack = {
                            currentScreen = Screen.Home
                        }
                    )
                }

                Screen.BattleSetup -> {
                    BattleSetupScreen(
                        onStartBattle = { seed ->
                            currentScreen = Screen.BattleArena(seed)
                        },
                        onBack = {
                            currentScreen = Screen.Home
                        }
                    )
                }

                is Screen.BattleArena -> {
                    BattleArenaScreen(
                        battleSeed = screen.seed,
                        onBack = {
                            currentScreen = Screen.BattleSetup
                        }
                    )
                }
            }
        }
    }
}
