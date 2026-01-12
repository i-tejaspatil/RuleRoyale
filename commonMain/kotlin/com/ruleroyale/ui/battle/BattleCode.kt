package com.ruleroyale.ui.battle

/**
 * Battle code utilities.
 *
 * Design goals:
 * - Human-shareable short code
 * - Deterministic: same code -> same seed -> same initial grid
 * - Cross-platform stable (Android/iOS)
 */

private const val CODE_LENGTH = 6
private const val ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // no 0/O, 1/I

/**
 * Generates a 6-character battle code.
 */
fun generateBattleCode(): String {
    // Uses Kotlin's Random.Default under the hood; generation doesn't need to be deterministic.
    return buildString {
        repeat(CODE_LENGTH) {
            append(ALPHABET.random())
        }
    }
}

/**
 * Returns true if [code] is exactly 6 chars and all characters are in [ALPHABET].
 */
fun isValidBattleCode(code: String): Boolean {
    val normalized = code.trim().uppercase()
    if (normalized.length != CODE_LENGTH) return false
    return normalized.all { ch -> ALPHABET.indexOf(ch) >= 0 }
}

/**
 * Converts a battle [code] into a stable numeric seed.
 *
 * We interpret the code as a base-N number, where N = [ALPHABET].length.
 * This avoids relying on JVM hashCode() and is stable across platforms.
 *
 * Throws [IllegalArgumentException] if the code contains invalid characters.
 */
fun battleCodeToSeed(code: String): Long {
    val normalized = code.trim().uppercase()

    var acc = 0L
    for (ch in normalized) {
        val idx = ALPHABET.indexOf(ch)
        require(idx >= 0) { "Invalid battle code character: $ch" }
        acc = acc * ALPHABET.length + idx
    }
    return acc
}

/**
 * Safe version of [battleCodeToSeed]. Returns null if the code is invalid.
 * Use this from UI to avoid crashing.
 */
fun battleCodeToSeedOrNull(code: String): Long? {
    return try {
        if (!isValidBattleCode(code)) return null
        battleCodeToSeed(code)
    } catch (_: IllegalArgumentException) {
        null
    }
}
