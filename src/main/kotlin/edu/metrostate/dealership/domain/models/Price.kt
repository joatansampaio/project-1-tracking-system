package edu.metrostate.dealership.domain.models

import java.util.*

/**
 * Represents a monetary price in the dealership management system.
 * Encapsulates a numeric amount and a currency unit, providing
 * formatting capabilities.
 *
 * @property price The numeric value of the price
 * @property currency The currency unit (e.g., "dollars")
 */
data class Price(
    var price: Double,
    var currency: String = "dollars"
) {
    override fun toString(): String {
        val symbol = when (currency.lowercase(Locale.getDefault())) {
            "dollars" -> "$"
            "pounds" -> "£"
            "euros" -> "€"
            "yen" -> "¥"
            "rupees" -> "₹"
            else -> ""
        }

        return String.format("%s%.2f %s", symbol, price, currency)
    }
}
