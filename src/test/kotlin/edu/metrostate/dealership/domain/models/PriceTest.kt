package edu.metrostate.dealership.domain.models

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class PriceTest {
    @Test
    fun createPrice() {
        val price = Price(500.0, "dollars")
        Assertions.assertNotNull(price)
    }

    @get:Test
    val price: Unit
        get() {
            val price = Price(500.0, "dollars")
            Assertions.assertEquals(500.0, price.price)
        }

    @Test
    fun setPrice() {
        val price = Price(0.0, "dollars")
        Assertions.assertEquals(0.0, price.price)
        price.price = 5000
        Assertions.assertEquals(5000.0, price.price)
    }

    @get:Test
    val currency: Unit
        get() {
            val price = Price(0.0, "dollars")
            Assertions.assertEquals("dollars", price.currency)
        }

    @Test
    fun setCurrency() {
        val price = Price(0.0, "dollars")
        Assertions.assertEquals("dollars", price.currency)
        price.currency = "pounds"
        Assertions.assertEquals("pounds", price.currency)
    }

    @Test
    fun testToString() {
        assertPrice("dollars", "$ 50.00 dollars")
        assertPrice("pounds", "£ 50.00 pounds")
        assertPrice("euros", "€ 50.00 euros")
        assertPrice("yen", "¥ 50.00 yen")
        assertPrice("rupees", "₹ 50.00 rupees")
        assertPrice("whatever", "50.00 whatever")
    }

    fun assertPrice(currency: String, expected: String?) {
        val price = Price(50.0, currency)
        Assertions.assertEquals(expected, price.toString())
    }
}