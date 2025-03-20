package edu.metrostate.trackingsystem.infrastructure.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.metrostate.trackingsystem.domain.models.Price;

public class GsonConfig {
    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Price.class, new PriceDeserializer()) // Register custom deserializer
                .create();
    }
}