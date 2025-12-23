package com.hatchbro.app.domain.calculators

object EfficiencyCalculator {

    /**
     * Calculates Hatch Rate.
     * Formula: (hatched / (set - discardedPreLockdown)) * 100
     * @param hatchedCount Number of eggs hatched
     * @param eggsSet Total eggs set
     * @param discardedPreLockdown Eggs discarded before lockdown (infertile/early death)
     */
    fun calculateHatchRate(hatchedCount: Int, eggsSet: Int, discardedPreLockdown: Int): Float {
        val denominator = eggsSet - discardedPreLockdown
        if (denominator <= 0) return 0f
        return (hatchedCount.toFloat() / denominator) * 100
    }

    /**
     * Calculates simple "Hatch of Set"
     */
    fun calculateHatchOfSet(hatchedCount: Int, eggsSet: Int): Float {
        if (eggsSet <= 0) return 0f
        return (hatchedCount.toFloat() / eggsSet) * 100
    }
}
