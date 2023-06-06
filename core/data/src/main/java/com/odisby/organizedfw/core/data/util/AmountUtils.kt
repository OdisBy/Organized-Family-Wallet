package com.odisby.organizedfw.core.data.util

import kotlin.math.abs

class AmountUtils {
    fun formatTotalBalance(amount: Double): String {
        val prefix = if (amount < 0) "-" else ""
        val formattedAmount = String.format("R$%.2f", abs(amount))
        return "$prefix$formattedAmount"
    }

    fun formatBalance(amount: Double): String {
        val prefix = if (amount < 0) "-" else "+"
        val formattedAmount = String.format("R$%.2f", abs(amount))
        return "$prefix$formattedAmount"
    }
}