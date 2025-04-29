package edu.metrostate.dealership.infrastructure.imports.models.json

import com.google.gson.annotations.SerializedName

/**
 * Data class that mirrors the JSON structure of dealer information in car_inventory.json import files.
 * This class serves as a deserialization target for GSON when importing dealer data.
 *
 * The field names use SerializedName annotations to match the exact JSON property names
 * in the import files, accommodating the mixed naming conventions found in the source data
 * (camelCase, snake_case, etc.).
 *
 * enabledForAcquisition was not originally in car_inventory.json and was manually added so that the
 * export json process could maintain the current state for a given dealer.
 */
data class DealerJson(
    @SerializedName("dealershipId")
    var dealershipId: String,

    @SerializedName("dealership_name")
    var name: String? = "Unknown",

    @SerializedName("dealership_enabled")
    var enabledForAcquisition: Boolean? = true,

    var vehicles: List<VehicleJson>
)