// This is the Dealer object used to store vehicles
package edu.metrostate.dealership.domain.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList

class Dealer() {
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    var dealershipId: String? = null
    var enabledForAcquisition: Boolean = true

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Vehicle")
    private var vehicles: MutableList<Vehicle>

    @JacksonXmlProperty(localName = "Name")
    private var name: String? = null

    // Ignore during serialization
    @get:JsonIgnore
    @JsonIgnore // Ignore this field during serialization
    @Transient
    val observableVehicles: ObservableList<Vehicle> = FXCollections.observableArrayList()

    init {
        this.vehicles = FXCollections.observableArrayList()
    }

    constructor(dealershipId: String?) : this() // Call the default constructor.
    {
        this.dealershipId = dealershipId
    }

    constructor(dealershipId: String?, name: String?) : this() {
        this.dealershipId = dealershipId
        this.name = name
    }

    constructor(dealershipId: String?, vehicles: List<Vehicle>, name: String?) : this() {
        setVehicles(vehicles)
        this.dealershipId = dealershipId
        this.name = name
    }

    /**
     * Only dealers with acquisition enabled should be able to add a vehicle.
     *
     * @param vehicle The vehicle to add.
     */
    fun addVehicle(vehicle: Vehicle): Boolean {
        val exists = vehicles.stream().anyMatch { v: Vehicle -> v.vehicleId == vehicle.vehicleId }
        // TODO: To maintain upper-case consistency
        if (!exists) {
            vehicles.add(vehicle)
            observableVehicles.add(vehicle) // Keep both lists in sync
            return true
        }
        return false
    }

    fun removeVehicle(id: String): Boolean {
        observableVehicles.removeIf { v: Vehicle -> v.vehicleId == id }
        return vehicles.removeIf { v: Vehicle -> v.vehicleId == id }
    }

    fun getVehicles(): List<Vehicle> {
        return vehicles
    }

    fun setVehicles(vehicles: List<Vehicle>) {
        this.vehicles = vehicles.toMutableList()
        observableVehicles.setAll(vehicles) // Convert List to ObservableList
    }

    fun getName(): String {
        return if (name != null) name!! else "Not Configured"
    }

    fun setName(name: String?) {
        this.name = name
    }
}