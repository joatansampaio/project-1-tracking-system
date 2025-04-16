package edu.metrostate.dealership.infrastructure.imports.models.json

import com.google.gson.annotations.SerializedName
import edu.metrostate.dealership.domain.models.Price

data class VehicleJson(
    @SerializedName("dealership_id")
    var dealershipId: String,

    @SerializedName("dealership_name")
    var dealershipName: String?,

    @SerializedName("vehicle_type")
    var vehicleType: String,

    @SerializedName("vehicle_manufacturer")
    var vehicleManufacturer: String,

    @SerializedName("vehicle_model")
    var vehicleModel: String,

    @SerializedName("vehicle_id")
    var vehicleId: String,

    var price: Price,

    @SerializedName("acquisition_date")
    var acquisitionDate: Long?,

    @SerializedName("dealership_enabled")
    var dealershipEnabled: Boolean?,

    var isRented: Boolean
)