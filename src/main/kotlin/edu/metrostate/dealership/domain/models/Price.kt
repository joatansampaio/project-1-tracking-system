// This is the Price object used to store price information of a vehicle object
package edu.metrostate.dealership.domain.models

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText
import java.util.*

class Price {
    @JacksonXmlText
    var price: Double = 0.0

    @JacksonXmlProperty(isAttribute = true, localName = "unit")
    var currency: String? = null

    constructor()

    constructor(price: Double, currency: String?) {
        this.price = price
        this.currency = currency
    }

    override fun toString(): String {
        val symbol = when (currency!!.lowercase(Locale.getDefault())) {
            "dollars" -> "$ "
            "pounds" -> "£ "
            "euros" -> "€ "
            "yen" -> "¥ "
            "rupees" -> "₹ "
            else -> ""
        }

        return String.format("%s%.2f %s", (symbol), price, currency)
    }
}