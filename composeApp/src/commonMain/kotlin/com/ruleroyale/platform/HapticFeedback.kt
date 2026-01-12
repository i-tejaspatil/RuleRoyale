package com.ruleroyale.platform

/**
 * Platform-specific haptic feedback interface.
 *
 * Provides tactile feedback for user interactions across different platforms.
 * iOS uses UIImpactFeedbackGenerator, Android uses Vibrator/HapticFeedbackConstants.
 */
expect fun provideHapticFeedback(): HapticFeedback

interface HapticFeedback {
    /**
     * Triggers a light impact haptic feedback.
     * Used for subtle interactions like button taps or slider changes.
     */
    fun light()

    /**
     * Triggers a medium impact haptic feedback.
     * Used for standard actions like confirming selections.
     */
    fun medium()

    /**
     * Triggers a heavy impact haptic feedback.
     * Used for important actions like starting/stopping simulations.
     */
    fun heavy()
}

