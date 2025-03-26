package edu.metrostate.trackingsystem.infrastructure.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import edu.metrostate.trackingsystem.domain.models.Price;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PriceDeserializerTest {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Price.class, new PriceDeserializer())
            .create();

    @Test
    public void deserializesNumberOnlyJson() {
        String json = "19.99";
        Price price = gson.fromJson(json, Price.class);
        assertEquals(19.99, price.getPrice(), 0.001);
        assertEquals("dollars", price.getCurrency());
    }

    @Test
    public void deserializesObjectWithPriceAndUnit() {
        String json = "{\"price\": 25.50, \"unit\": \"euros\"}";
        Price price = gson.fromJson(json, Price.class);
        assertEquals(25.50, price.getPrice(), 0.001);
        assertEquals("euros", price.getCurrency());
    }

    @Test
    public void deserializesObjectMissingUnit() {
        String json = "{\"price\": 10.00}";
        Price price = gson.fromJson(json, Price.class);
        assertEquals(10.00, price.getPrice(), 0.001);
        assertEquals("dollars", price.getCurrency());
    }

    @Test
    public void throwsOnInvalidFormat() {
        String json = "\"invalid\"";
        assertThrows(JsonParseException.class, () -> {
            gson.fromJson(json, Price.class);
        });
    }
}