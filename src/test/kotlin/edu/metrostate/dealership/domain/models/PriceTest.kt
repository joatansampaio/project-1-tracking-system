package edu.metrostate.dealership.domain.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PriceTest {

    @Test
    fun createPrice() {
        val price = Price(500.0, "dollars")
        assertNotNull(price)
    }

    @Test
    fun getPrice() {
        val price = Price(500.0, "dollars")
        assertEquals(500.0, price.price)
    }

    @Test
    fun setPrice() {
        val price = Price(0.0, "dollars")
        assertEquals(0.0, price.price)
        price.price = 5000.0
        assertEquals(5000.0, price.price)
    }

    @Test
    fun getCurrency() {
        val price = Price(0.0, "dollars")
        assertEquals("dollars", price.currency)
    }

    @Test
    fun setCurrency() {
        val price = Price(0.0, "dollars")
        assertEquals("dollars", price.currency)
        price.currency = "pounds"
        assertEquals("pounds", price.currency)
    }

    @Test
    fun testToString() {
        assertPriceToString("dollars", "$50.00 dollars")
        assertPriceToString("pounds", "£50.00 pounds")
        assertPriceToString("euros", "€50.00 euros")
        assertPriceToString("yen", "¥50.00 yen")
        assertPriceToString("rupees", "₹50.00 rupees")
        assertPriceToString("whatever", "50.00 whatever")
    }

    private fun assertPriceToString(currency: String, expected: String) {
        val price = Price(50.0, currency)
        assertEquals(expected, price.toString())
    }
}
