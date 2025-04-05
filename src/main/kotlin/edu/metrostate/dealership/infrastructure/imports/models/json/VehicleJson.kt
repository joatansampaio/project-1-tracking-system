package edu.metrostate.dealership.infrastructure.imports.models.json

import com.google.gson.annotations.SerializedName
import edu.metrostate.dealership.domain.models.Price

data class VehicleJson(
    @SerializedName("dealership_id")
    val dealershipId: String,

    @SerializedName("dealership_name")
    val dealershipName: String,

    @SerializedName("vehicle_type")
    val vehicleType: String,

    @SerializedName("vehicle_manufacturer")
    val vehicleManufacturer: String,

    @SerializedName("vehicle_model")
    val vehicleModel: String,

    @SerializedName("vehicle_id")
    val vehicleId: String,

    val price: Price,

    @SerializedName("acquisition_date")
    val acquisitionDate: Long,

    val isRented: Boolean
)