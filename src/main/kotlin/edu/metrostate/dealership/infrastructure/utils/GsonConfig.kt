package edu.metrostate.dealership.infrastructure.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import edu.metrostate.dealership.domain.models.Price

object GsonConfig {
    @JvmStatic
    val gson: Gson
        get() = GsonBuilder()
            .registerTypeAdapter(Price::class.java, PriceDeserializer()) // Register custom deserializer
            .create()
}