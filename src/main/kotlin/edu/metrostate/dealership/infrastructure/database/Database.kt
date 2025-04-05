package edu.metrostate.dealership.infrastructure.database

import edu.metrostate.dealership.domain.models.Dealer
import edu.metrostate.dealership.domain.models.Vehicle
import javafx.collections.FXCollections
import javafx.collections.ObservableList

/**
 * In-memory singleton acting as a fake database
 */
class Database private constructor()  {

    var dealers: ObservableList<Dealer> = FXCollections.observableArrayList()

    val vehicles: ObservableList<Vehicle> = FXCollections.observableArrayList()

    /**
     * Gets a dealer by ID in the context of the database
     * @param dealershipId the dealer ID
     * @return the dealer for dealershipId or an error message
     */
    fun getDealershipIDs(): List<String> {
        return dealers.map { it.dealershipId.toString() }
    }

    /**
     * In the context of the database, remove or delete a dealer
     * @param dealershipId - The dealership ID of the dealer to be removed
     * @return a result of success if operation completed, otherwise return a result failure message.
     */
    fun deleteDealer(dealershipId: String): Result<Boolean> {
        return if (dealers.removeIf { it.dealershipId == dealershipId }) {
            Result.success()
        } else {
            Result.failure("Can't delete... dealership ID not found!")
        }
    }

    fun getDealerByID(dealershipId: String): Dealer? {
        return dealers.find { it.dealershipId == dealershipId }
    }

    fun setDealers(dealers: List<Dealer>) {
        this.dealers.setAll(dealers)
    }

    fun toggleAcquisition(dealershipId: String) {
        getDealerByID(dealershipId)?.let {
            it.enabledForAcquisition = !it.enabledForAcquisition
        }
    }

    /**
     * Update the information of a dealer, currently limited to name.
     * Does this in the context of the database
     * @param dealershipId - A dealership identification code
     * @param name - The name to set as the dealer name
     * @return true if dealer name was set, otherwise return false
     */
    fun updateDealer(dealershipId: String, name: String): Boolean {
        val dealer = getDealerByID(dealershipId) ?: return false
        if (!name.isNullOrBlank()) dealer.setName(name)
        return true
    }

    /**
     * Adds a vehicle object to a dealer by using the vehicle's dealer ID.
     * In the context of the database
     * @param vehicle - A vehicle object to add
     * @return a result of success if operation completed, otherwise return a result failure message.
     */
    fun addVehicle(vehicle: Vehicle): Result<Boolean> {
        val dealer = getDealerByID(vehicle.dealershipId!!) ?: return Result.failure("Dealer ID not found...")

        if (!dealer.enabledForAcquisition) {
            return Result.failure("Dealer is not enabled for acquisition.")
        }

        val alreadyExists = dealer.getVehicles().any { it.vehicleId == vehicle.vehicleId } ||
                vehicles.any { it.vehicleId == vehicle.vehicleId }

        if (alreadyExists) {
            return Result.failure("A Vehicle with ID ${vehicle.vehicleId} already exists.")
        }

        dealer.addVehicle(vehicle)
        return Result.success()
    }

    /**
     * In the context of the database, removes or deletes a vehicle from a dealer
     * @param id - The vehicle ID of the vehicle to be removed
     * @param dealerId - The dealer ID of the dealer that may contain the vehicle.
     * @return A result of success on completed operation or a result of failure with an errorMessage if unsuccessful.
     */
    fun deleteVehicle(id: String, dealershipId: String): Result<Boolean> {
        val dealer = getDealerByID(dealershipId) ?: return Result.failure("Dealer ID not found...")
        return if (dealer.removeVehicle(id)) {
            Result.success()
        } else {
            Result.failure("Vehicle ID not found.")
        }
    }

    /**
     * Passes a list of dealers to ImportInner function for handling
     * @param data - A list of dealers to be processed (imported)
     */
    fun importJSON(incomingDealers: List<Dealer?>) = importInner(incomingDealers)

    /**
     * Passes a list of dealers to ImportInner function for handling
     * @param data - A list of dealers to be processed (imported)
     */
    fun importXML(incomingDealers: List<Dealer>) = importInner(incomingDealers)

    private fun importInner(incomingDealers: List<Dealer?>) {
        val systemVehicleIdsMap = dealers
            .flatMap { dealer -> dealer.getVehicles().map { it.vehicleId to dealer.dealershipId } }
            .toMap()
            .toMutableMap()

        for (incomingDealer in incomingDealers) {
            val dealerId = incomingDealer?.dealershipId ?: continue

            val existingDealer = dealers.find { it.dealershipId == dealerId }

            if (existingDealer != null) {
                if (existingDealer.getName().isNullOrBlank()) {
                    if (incomingDealer != null) {
                        existingDealer.setName(incomingDealer.getName())
                    }
                }

                for (incomingVehicle in incomingDealer.getVehicles()) {
                    var id = incomingVehicle.vehicleId
                    val existingDealerId = systemVehicleIdsMap[id]
                    if (existingDealerId != null && existingDealerId != dealerId) {
                        id = "${dealerId}_$id"
                        incomingVehicle.vehicleId = id
                    }
                    if (!existingDealer.getVehicles().any { it.vehicleId == id }) {
                        existingDealer.addVehicle(incomingVehicle)
                        systemVehicleIdsMap[id] = dealerId
                    }
                }

            } else {
                val uniqueVehicles = incomingDealer.getVehicles().map { vehicle ->
                    val id = vehicle.vehicleId
                    if (systemVehicleIdsMap.containsKey(id)) {
                        val newId = "${dealerId}_$id"
                        vehicle.vehicleId = newId
                        systemVehicleIdsMap[newId] = dealerId
                    } else {
                        systemVehicleIdsMap[id] = dealerId
                    }
                    vehicle
                }
                incomingDealer.setVehicles(uniqueVehicles)
                dealers.add(incomingDealer)
            }
        }
    }

    fun toggleIsRented(vehicle: Vehicle) {
        vehicle.toggleIsRented()
    }

    companion object {
        val instance: Database by lazy { Database() }
    }
}
