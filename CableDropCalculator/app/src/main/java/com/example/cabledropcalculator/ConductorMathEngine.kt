package com.example.cabledropcalculator

object ConductorMathEngine {
    private const val DEFAULT_R_PRIME = 0.00008
    private const val DEFAULT_SIN_PHI = 0.6
    private const val DEFAULT_COS_PHI = 0.8

    const val PHASE_SINGLE = 2
    const val PHASE_TRIPLE_SYMMETRIC = 1
    const val PHASE_TRIPLE_ASYMMETRIC = 2

    const val RHO_COPPER = 0.0225
    const val RHO_ALUMINIUM = 0.036

    /**
     * Расчет падения напряжения в вольтах по стандарту DIN VDE 0100-520.
     */
    fun calculateVoltageDropVolts(
        length: Double,
        phaseCoefficient: Int,
        rho: Double,
        gauge: Double,
        phi: Double? = null,
        rPrime: Double? = null,
        current: Double
    ): Double {
        val cosPhi = phi?.let { Math.sin(it) } ?: DEFAULT_COS_PHI
        val sinPhi = phi?.let { Math.cos(it) } ?: DEFAULT_SIN_PHI
        val realRPrime = rPrime ?: DEFAULT_R_PRIME

        return length * phaseCoefficient * (rho * cosPhi / gauge + realRPrime * sinPhi) * current
    }

    /**
     * Расчет падения напряжения в процентах.
     */
    fun calculateVoltageDropPercent(
        nominalVoltage: Double,
        length: Double,
        phaseCoefficient: Int,
        rho: Double,
        gauge: Double,
        phi: Double? = null,
        rPrime: Double? = null,
        current: Double
    ): Double {
        val dropVolts = calculateVoltageDropVolts(length, phaseCoefficient, rho, gauge, phi, rPrime, current)
        return (dropVolts * 100.0) / nominalVoltage
    }
}