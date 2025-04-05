package edu.metrostate.dealership.infrastructure.imports.models.json

import com.google.gson.annotations.SerializedName

data class DealerJson(
    @SerializedName("dealershipId")
    var dealershipId: String,

    @SerializedName("dealership_name")
    var name: String,

    @SerializedName("dealership_enabled")
    var enabledForAcquisition: Boolean,

    var vehicles: List<VehicleJson>
)