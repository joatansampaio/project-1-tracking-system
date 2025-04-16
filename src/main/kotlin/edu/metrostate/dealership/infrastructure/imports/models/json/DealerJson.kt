package edu.metrostate.dealership.infrastructure.imports.models.json

import com.google.gson.annotations.SerializedName

data class DealerJson(
    @SerializedName("dealershipId")
    var dealershipId: String,

    @SerializedName("dealership_name")
    var name: String? = "Unknown",

    @SerializedName("dealership_enabled")
    var enabledForAcquisition: Boolean? = true,

    var vehicles: List<VehicleJson>
)