package edu.metrostate.dealership.infrastructure.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import edu.metrostate.dealership.domain.models.Price
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PriceDeserializerTest {
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Price::class.java, PriceDeserializer())
        .create()

    @Test
    fun deserializesNumberOnlyJson() {
        val json = "19.99"
        val price = gson.fromJson(json, Price::class.java)
        Assertions.assertEquals(19.99, price.price, 0.001)
        Assertions.assertEquals("dollars", price.currency)
    }

    @Test
    fun deserializesObjectWithPriceAndUnit() {
        val json = "{\"price\": 25.50, \"unit\": \"euros\"}"
        val price = gson.fromJson(json, Price::class.java)
        Assertions.assertEquals(25.50, price.price, 0.001)
        Assertions.assertEquals("euros", price.currency)
    }

    @Test
    fun deserializesObjectMissingUnit() {
        val json = "{\"price\": 10.00}"
        val price = gson.fromJson(json, Price::class.java)
        Assertions.assertEquals(10.00, price.price, 0.001)
        Assertions.assertEquals("dollars", price.currency)
    }

    @Test
    fun throwsOnInvalidFormat() {
        val json = "\"invalid\""
        Assertions.assertThrows(
            JsonParseException::class.java
        ) {
            gson.fromJson(json, Price::class.java)
        }
    }
}