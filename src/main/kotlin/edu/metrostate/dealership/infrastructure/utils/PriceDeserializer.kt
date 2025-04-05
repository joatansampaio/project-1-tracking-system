package edu.metrostate.dealership.infrastructure.utils

import com.google.gson.*
import edu.metrostate.dealership.domain.models.Price
import java.lang.reflect.Type

class PriceDeserializer : JsonDeserializer<Price> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Price {
        if (json.isJsonPrimitive && json.asJsonPrimitive.isNumber) {
            // If the JSON contains only a number, assume default currency as "dollars"
            return Price(json.asDouble, "dollars")
        } else if (json.isJsonObject) {
            // If it's a JSON object, parse normally
            val jsonObject = json.asJsonObject
            val price = if (jsonObject.has("price")) jsonObject["price"].asDouble else 0.0
            val currency = if (jsonObject.has("unit")) jsonObject["unit"].asString else "dollars"
            return Price(price, currency)
        }

        throw JsonParseException("Invalid format for Price field")
    }
}