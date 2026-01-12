package com.ruleroyale.ui.battle

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruleroyale.engine.lifefromrules.LifeStatus

/**
 * Battle result card showing only the player's final outcome.
 * (No ranking / no comparison logic.)
 */
@Composable
fun BattleResultCard(
    status: LifeStatus,
    ticks: Long
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (status) {
                LifeStatus.STABLE -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                LifeStatus.OSCILLATING -> Color(0xFFFF9800).copy(alpha = 0.2f)
                LifeStatus.EXTINCT, LifeStatus.SATURATED -> Color(0xFFF44336).copy(alpha = 0.2f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val (icon, label, description) = when (status) {
                LifeStatus.STABLE -> Triple(
                    "✅",
                    "STABLE",
                    "Pattern froze — no more changes"
                )
                LifeStatus.OSCILLATING -> Triple(
                    "🔁",
                    "OSCILLATING",
                    "Pattern repeats in a cycle"
                )
                LifeStatus.EXTINCT -> Triple(
                    "💀",
                    "EXTINCT",
                    "All cells died"
                )
                LifeStatus.SATURATED -> Triple(
                    "🧱",
                    "SATURATED",
                    "Over 90% cells alive"
                )
                else -> Triple("❓", "UNKNOWN", "Status unclear")
            }

            Text(icon, fontSize = 56.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                label,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("⏱️", fontSize = 18.sp)
                    Text(
                        "Tick: $ticks",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
