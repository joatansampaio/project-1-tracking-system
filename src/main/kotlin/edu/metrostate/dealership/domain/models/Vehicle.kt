//This is the Vehicle object which should be stored in a dealer
package edu.metrostate.dealership.domain.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import edu.metrostate.dealership.application.exceptions.ValidationException
import edu.metrostate.dealership.infrastructure.utils.PriceDeserializer
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

@JsonIgnoreProperties(ignoreUnknown = true)
class Vehicle {
    @JvmField
	@JacksonXmlProperty(isAttribute = true, localName = "id")
    @SerializedName("vehicle_id")
    var vehicleId: String? = null

    @JacksonXmlProperty(localName = "Make")
    @SerializedName("vehicle_manufacturer")
    var manufacturer: String? = null

    @JacksonXmlProperty(localName = "Model")
    @SerializedName("vehicle_model")
    var model: String? = null

    @JacksonXmlProperty(isAttribute = true, localName = "type")
    @SerializedName("vehicle_type")
    private var type: String? = null

    @JacksonXmlProperty(localName = "AcquisitionDate")
    @SerializedName("acquisition_date")
    private var acquisitionDate: Long? = null

    @JacksonXmlProperty(localName = "Price")
    @JsonAdapter(PriceDeserializer::class)
    var price: Price? = null

    @JvmField
	@SerializedName("dealership_id")
    var dealershipId: String? = null

    @JvmField
	@SerializedName("dealership_name")
    var dealershipName: String? = null

    @SerializedName("is_rented")
    private var isRented = false

    constructor()

    constructor(
        vehicleId: String?, manufacturer: String?, model: String?, acquisitionDate: Long?,
        price: Price?, dealershipId: String?, type: String
    ) {
        this.vehicleId = vehicleId
        this.manufacturer = manufacturer
        this.model = model
        this.acquisitionDate = acquisitionDate
        this.price = price
        this.dealershipId = dealershipId
        this.type = type.uppercase(Locale.getDefault())
    }

    fun getAcquisitionDate(): Long {
        return if (acquisitionDate != null) acquisitionDate!! else 0L
    }


    fun setAcquisitionDate(acquisitionDate: Long?) {
        this.acquisitionDate = acquisitionDate
    }

    val priceAsString: String
        get() = if (price != null) price.toString() else "Unknown"

    fun getType(): String? {
        return type
    }

    fun setType(type: String) {
        this.type = type.uppercase(Locale.getDefault())
    }

    fun getIsRented(): Boolean {
        return isRented
    }

    fun setIsRented(rented: Boolean) {
        if (type == "SPORTS CAR" && rented) {
            return
        }
        this.isRented = rented
    }

    fun toggleIsRented() {
        if (getType().equals("sports car", ignoreCase = true)) {
            isRented = false
            println("bad") //Should probably be an error message
        } else {
            isRented = !isRented
        }
    }

    val isRentedAsString: String
        get() {
            return if (isRented) {
                "Yes"
            } else {
                "No"
            }
        }

    fun getVehicleId(): String? = vehicleId
    fun getDealershipId(): String? = dealershipId

    val formattedAcquisitionDate: String
        get() {
            if (acquisitionDate == null) {
                return "Unknown"
            }
            return DateTimeFormatter
                .ofPattern("MM/dd/yyyy HH:mm")
                .format(Instant.ofEpochMilli(acquisitionDate!!).atZone(ZoneId.systemDefault()))
        }

    // This is used when in the import process
    // We want to ensure consistency of UpperCase types
    // maybe an enum would be a better choice.
    // We can always probably change that later.
    fun normalize() {
        setType(type!!.uppercase(Locale.getDefault()))
    }

    override fun toString(): String {
        return "ID: " + vehicleId + "\n" +
                "Dealership ID: " + dealershipId + "\n" +
                "Manufacturer: " + manufacturer + " Model: " + model + "\tType: " + getType() + "\n" +
                "Price: " + price + "\n" +
                "Acquisition Date: " + formattedAcquisitionDate + "\n"
    }

    companion object {
        /**
         * Unless phase 2 requires specific actions for each vehicle in which case we would
         * need to abstract classes, this is enough.
         */
        private val VALID_VEHICLE_TYPES = listOf("sedan", "sports car", "suv", "pickup")

        /**
         * Creates a new Vehicle instance after validating the provided attributes.
         * If any of the input parameters fail validation, a [ValidationException] is thrown.
         *
         * @param vehicleId The unique identifier for the vehicle.
         * @param manufacturer The manufacturer of the vehicle.
         * @param model The model name of the vehicle.
         * @param priceText The price of the vehicle as a string (to be parsed as a decimal value).
         * @param acquisitionDate The acquisition date of the vehicle.
         * @param dealershipId The identifier of the dealership where the vehicle is located.
         * @param vehicleType The type of the vehicle.
         * @return A [Vehicle] instance with the provided and validated attributes.
         * @throws ValidationException If any validation errors occur with the input parameters.
         */
        @Throws(ValidationException::class)
        fun createVehicle(
            vehicleId: String?, manufacturer: String?, model: String?,
            priceText: String?, acquisitionDate: LocalDate,
            dealershipId: String?, vehicleType: String?
        ): Vehicle {
            val errors: MutableList<String> = ArrayList()

            if (vehicleId == null || vehicleId.trim { it <= ' ' }.isEmpty()) {
                errors.add("Vehicle ID is required.")
            }
            if (manufacturer == null || manufacturer.trim { it <= ' ' }.isEmpty()) {
                errors.add("Manufacturer is required.")
            }
            if (model == null || model.trim { it <= ' ' }.isEmpty()) {
                errors.add("Model is required.")
            }
            if (priceText == null || priceText.trim { it <= ' ' }.isEmpty()) {
                errors.add("Price is required.")
            }

            var price = -1.0
            if (priceText != null && !priceText.trim { it <= ' ' }.isEmpty()) {
                try {
                    price = priceText.toDouble()
                    if (price < 0) {
                        errors.add("Price must be a positive number.")
                    }
                } catch (e: NumberFormatException) {
                    errors.add("Invalid price format.")
                }
            }

            if (acquisitionDate == null) {
                errors.add("Acquisition date is required.")
            }
            if (dealershipId == null || dealershipId.trim { it <= ' ' }.isEmpty()) {
                errors.add("Dealership ID is required.")
            }
            if (vehicleType == null || vehicleType.trim { it <= ' ' }.isEmpty()) {
                errors.add("Vehicle type is required.")
            } else if (!VALID_VEHICLE_TYPES.contains(vehicleType.lowercase(Locale.getDefault()))) {
                errors.add("Invalid vehicle type.")
            }

            if (errors.isNotEmpty()) {
                throw ValidationException(errors)
            }

            val epochMillis = acquisitionDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            return Vehicle(
                vehicleId,
                manufacturer,
                model,
                epochMillis,
                Price(price, "dollars"),
                dealershipId,
                vehicleType!!.uppercase(Locale.getDefault())
            )
        }
    }
}
