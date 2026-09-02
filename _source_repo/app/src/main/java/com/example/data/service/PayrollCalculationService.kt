package com.example.data.service

import com.example.data.model.*
import java.math.BigDecimal
import java.math.RoundingMode

object PayrollCalculationService {

    private const val SCALE = 2
    private val ROUNDING_MODE = RoundingMode.HALF_UP

    fun round(value: Double): Double {
        return BigDecimal.valueOf(value)
            .setScale(SCALE, ROUNDING_MODE)
            .toDouble()
    }

    /**
     * Calculates Admin Fee, Charity Fee, Distributable Work Pool, and Owner Transfer.
     */
    fun computeFinancialBreakdown(
        grossAmount: Double,
        currency: String = "INR",
        adminFeePercent: Double = 5.0,
        charityFeePercent: Double = 0.0,
        ownerSharePercent: Double = 0.0
    ): FinancialCalculationResult {
        val gross = BigDecimal.valueOf(grossAmount).setScale(SCALE, ROUNDING_MODE)
        val adminPercent = BigDecimal.valueOf(adminFeePercent).setScale(4, ROUNDING_MODE)
        val charityPercent = BigDecimal.valueOf(charityFeePercent).setScale(4, ROUNDING_MODE)
        val ownerPercent = BigDecimal.valueOf(ownerSharePercent).setScale(4, ROUNDING_MODE)
        val hundred = BigDecimal.valueOf(100.0)

        // 1. Admin Fee
        val adminFee = gross.multiply(adminPercent).divide(hundred, SCALE, ROUNDING_MODE)

        // 2. Charity Fee
        val charityFee = gross.multiply(charityPercent).divide(hundred, SCALE, ROUNDING_MODE)

        // 3. Work Distribution Pool = Gross - Admin Fee - Charity Fee
        val workPool = gross.subtract(adminFee).subtract(charityFee)

        // 4. Owner Share = Work Pool * (Owner Percent / 100)
        val ownerShare = workPool.multiply(ownerPercent).divide(hundred, SCALE, ROUNDING_MODE)

        // 5. Required Transfer To Admin = Gross - Owner Share
        // Note: Gross - Owner Work Share = Admin Fee + Charity Fee + Team Payouts
        val requiredTransfer = gross.subtract(ownerShare)

        // 6. Team Payout Pool = Work Pool - Owner Share
        val teamPayoutPool = workPool.subtract(ownerShare)

        return FinancialCalculationResult(
            grossAmount = gross.toDouble(),
            currency = currency,
            adminFeePercent = adminFeePercent,
            adminFeeAmount = adminFee.toDouble(),
            charityFeePercent = charityFeePercent,
            charityFeeAmount = charityFee.toDouble(),
            workPoolAmount = workPool.toDouble(),
            ownerSharePercent = ownerSharePercent,
            ownerShareAmount = ownerShare.toDouble(),
            requiredTransferToAdmin = requiredTransfer.toDouble(),
            teamPayoutPool = teamPayoutPool.toDouble(),
            roundingDifference = 0.0
        )
    }

    /**
     * Validates that allocations sum to exactly 100.00% without negative or missing values.
     */
    fun validateAllocations(
        allocations: List<Triple<String, String, Double>> // (MemberId, Name, Percent)
    ): Pair<Boolean, String?> {
        if (allocations.isEmpty()) {
            return Pair(false, "No contributors specified in distribution proposal.")
        }

        val seenMembers = mutableSetOf<String>()
        var sum = BigDecimal.ZERO

        for ((memberId, name, percent) in allocations) {
            if (memberId.isBlank()) {
                return Pair(false, "Invalid member ID specified.")
            }
            if (!seenMembers.add(memberId)) {
                return Pair(false, "Duplicate recipient in distribution: $name ($memberId).")
            }
            if (percent < 0.0) {
                return Pair(false, "Allocation for $name cannot be negative ($percent%).")
            }
            sum = sum.add(BigDecimal.valueOf(percent).setScale(4, ROUNDING_MODE))
        }

        val targetHundred = BigDecimal.valueOf(100.0).setScale(4, ROUNDING_MODE)
        val diff = sum.subtract(targetHundred).abs()

        if (diff > BigDecimal.valueOf(0.001)) {
            val formattedSum = sum.setScale(2, ROUNDING_MODE).toPlainString()
            return Pair(
                false,
                "Allocations must total exactly 100.00%. Current total: $formattedSum% (Difference: ${diff.setScale(2, ROUNDING_MODE).toPlainString()}%)."
            )
        }

        return Pair(true, null)
    }

    /**
     * Computes exact payout amounts for all members in the distribution pool,
     * maintaining clean separation of work pool share and administrative fee.
     */
    fun calculateMemberAllocations(
        workPoolAmount: Double,
        adminFeeAmount: Double,
        adminMemberId: String,
        allocations: List<Triple<String, String, Double>> // (MemberId, Name, Percent)
    ): List<DistributionAllocationModel> {
        val workPool = BigDecimal.valueOf(workPoolAmount).setScale(SCALE, ROUNDING_MODE)
        val hundred = BigDecimal.valueOf(100.0)

        var runningSum = BigDecimal.ZERO
        val results = mutableListOf<DistributionAllocationModel>()

        for (i in allocations.indices) {
            val (memberId, name, percent) = allocations[i]
            val pct = BigDecimal.valueOf(percent).setScale(4, ROUNDING_MODE)

            val memberWorkAmount = if (i == allocations.size - 1) {
                // Ensure exact penny balancing on last item
                val calculated = workPool.subtract(runningSum)
                calculated.max(BigDecimal.ZERO)
            } else {
                val amt = workPool.multiply(pct).divide(hundred, SCALE, ROUNDING_MODE)
                runningSum = runningSum.add(amt)
                amt
            }

            val isAdmin = memberId == adminMemberId
            val sepAdminFee = if (isAdmin) adminFeeAmount else 0.0

            results.add(
                DistributionAllocationModel(
                    memberId = memberId,
                    memberDisplayName = name,
                    percentage = percent,
                    calculatedAmount = memberWorkAmount.toDouble(),
                    role = if (isAdmin) "Administrator / Contributor" else "Project Contributor",
                    workContributionRationale = "",
                    isAdmin = isAdmin,
                    separateAdminFeeAmount = sepAdminFee
                )
            )
        }

        return results
    }

    /**
     * Formats currency with clean human-readable representation.
     */
    fun formatCurrency(amount: Double, currency: String = "INR"): String {
        val rounded = String.format(java.util.Locale.US, "%,.2f", amount)
        return when (currency.uppercase()) {
            "INR" -> "₹$rounded INR"
            "USD" -> "$$rounded USD"
            "EUR" -> "€$rounded EUR"
            "GBP" -> "£$rounded GBP"
            "AED" -> "AED $rounded"
            "CAD" -> "CA$$rounded"
            "AUD" -> "AU$$rounded"
            "SGD" -> "SG$$rounded"
            "JPY" -> "¥$rounded JPY"
            else -> "$rounded $currency"
        }
    }
}
