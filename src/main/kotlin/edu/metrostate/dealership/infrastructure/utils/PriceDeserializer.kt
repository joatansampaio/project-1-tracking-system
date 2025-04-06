package edu.metrostate.dealership.infrastructure.utils

import com.google.gson.*
import edu.metrostate.dealership.domain.models.Price
import java.lang.reflect.Type

class PriceDeserializer : JsonDeserializer<Price> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Price {
        return when {
            json.isJsonPrimitive && json.asJsonPrimitive.isNumber ->
                Price(json.asDouble, "dollars")

            json.isJsonObject -> {
                val obj = json.asJsonObject
                val price = obj["price"]?.asDouble ?: 0.0
                val currency = obj["currency"]?.asString ?: "dollars"
                Price(price, currency)
            }

            else -> throw JsonParseException("Invalid format for Price field")
        }
    }
}