package edu.metrostate.dealership.domain.models

import edu.metrostate.dealership.application.exceptions.ValidationException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Represents a vehicle in the dealership management system.
 * This class contains all relevant information about a vehicle including
 * its identifiers, specifications, pricing, and status.
 *
 * @property vehicleId Unique identifier for the vehicle
 * @property manufacturer The company that manufactured the vehicle
 * @property model The model name of the vehicle
 * @property acquisitionDate The timestamp (in milliseconds) when the vehicle was acquired by the dealership
 * @property price The price of the vehicle, represented as a Price object
 * @property dealershipId The ID of the dealership that owns this vehicle
 * @property type The type of vehicle (sedan, sports car, etc.)
 * @property isRented Whether the vehicle is currently rented out
 */
data class Vehicle(
    var vehicleId: String,
    var manufacturer: String,
    var model: String,
    var acquisitionDate: Long?,
    var price: Price,
    var dealershipId: String,
    var type: VehicleType,
    var isRented: Boolean = false,
) {
    val priceAsString: String
        get() = price.toString()

    val isRentedAsString: String
        get() = if (isRented) "Yes" else "No"

    val formattedAcquisitionDate: String
        get() = acquisitionDate?.let {
            DateTimeFormatter
                .ofPattern("MM/dd/yyyy HH:mm")
                .format(Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()))
        } ?: "Unknown"

    fun toggleIsRented() {
        isRented = if (type == VehicleType.SPORTS_CAR) {
            false
        } else {
            !isRented
        }
    }

    companion object {
        @Throws(ValidationException::class)
        fun create(
            vehicleId: String?,
            manufacturer: String?,
            model: String?,
            priceText: String?,
            acquisitionDate: LocalDate?,
            dealershipId: String?,
            vehicleType: String?
        ): Vehicle {
            val errors = mutableListOf<String>()

            if (vehicleId.isNullOrBlank()) errors.add("Vehicle ID is required.")
            if (manufacturer.isNullOrBlank()) errors.add("Manufacturer is required.")
            if (model.isNullOrBlank()) errors.add("Model is required.")
            if (priceText.isNullOrBlank()) errors.add("Price is required.")
            if (acquisitionDate == null) errors.add("Acquisition date is required.")
            if (dealershipId.isNullOrBlank()) errors.add("Dealership ID is required.")

            val price = priceText?.toDoubleOrNull()
            if (price == null || price < 0) errors.add("Invalid price format or negative value.")

            val type = vehicleType
                ?.let { VehicleType.fromString(it) }
                ?: errors.add("Vehicle type is required.").let { null }

            if (type == null) {
                errors.add("Invalid vehicle type.")
            }

            if (errors.isNotEmpty()) throw ValidationException(errors)

            val epochMillis = acquisitionDate
                ?.atStartOfDay(ZoneId.systemDefault())
                ?.toInstant()
                ?.toEpochMilli()

            return Vehicle(
                vehicleId!!.trim(),
                manufacturer!!.trim(),
                model!!.trim(),
                epochMillis!!,
                Price(price!!, "dollars"),
                dealershipId!!.trim(),
                type!!
            )
        }
    }
}

enum class VehicleType {
    SEDAN,
    SPORTS_CAR,
    SUV,
    PICKUP,
    UNKNOWN;

    companion object {
        fun fromString(value: String?): VehicleType {
            return value
                ?.replace(" ", "_")
                ?.uppercase()
                ?.let { safeValue ->
                    enumValues<VehicleType>().firstOrNull { it.name == safeValue }
                } ?: UNKNOWN
        }
    }
}