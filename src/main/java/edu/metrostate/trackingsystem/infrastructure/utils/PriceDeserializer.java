package edu.metrostate.trackingsystem.infrastructure.utils;

import com.google.gson.*;
import edu.metrostate.trackingsystem.domain.models.Price;

import java.lang.reflect.Type;

public class PriceDeserializer implements JsonDeserializer<Price> {
    @Override
    public Price deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
            // If the JSON contains only a number, assume default currency as "dollars"
            return new Price(json.getAsDouble(), "dollars");
        } else if (json.isJsonObject()) {
            // If it's a JSON object, parse normally
            JsonObject jsonObject = json.getAsJsonObject();
            double price = jsonObject.has("price") ? jsonObject.get("price").getAsDouble() : 0.0;
            String currency = jsonObject.has("unit") ? jsonObject.get("unit").getAsString() : "dollars";
            return new Price(price, currency);
        }

        throw new JsonParseException("Invalid format for Price field");
    }
}