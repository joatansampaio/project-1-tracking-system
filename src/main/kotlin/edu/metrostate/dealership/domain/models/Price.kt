package edu.metrostate.dealership.domain.models

import java.util.*

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
