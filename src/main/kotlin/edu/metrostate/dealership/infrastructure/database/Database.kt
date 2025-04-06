package edu.metrostate.dealership.infrastructure.database

import edu.metrostate.dealership.Main.Companion.logger
import edu.metrostate.dealership.domain.models.Dealer
import edu.metrostate.dealership.domain.models.Vehicle
import edu.metrostate.dealership.infrastructure.imports.mappers.toDomainDealer
import edu.metrostate.dealership.infrastructure.imports.mappers.toDomainVehicle
import edu.metrostate.dealership.infrastructure.imports.mappers.updateFrom
import edu.metrostate.dealership.infrastructure.imports.models.json.DealerJson
import edu.metrostate.dealership.infrastructure.imports.models.xml.DealerXml
import javafx.collections.FXCollections
import javafx.collections.ObservableList

/**
 * In-memory singleton acting as a fake database
 */
class Database private constructor()  {

    var dealers: ObservableList<Dealer> = FXCollections.observableArrayList()
    var vehicles: ObservableList<Vehicle> = FXCollections.observableArrayList()

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

    private fun getDealerByID(dealershipId: String): Dealer? {
        return dealers.find { it.dealershipId == dealershipId }
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
        if (name.isNotBlank()) dealer.setName(name)
        return true
    }

    /**
     * Adds a vehicle object to a dealer by using the vehicle's dealer ID.
     * In the context of the database
     * @param vehicle - A vehicle object to add
     * @return a result of success if operation completed, otherwise return a result failure message.
     */
    fun addVehicle(vehicle: Vehicle): Result<Boolean> {
        val dealer = getDealerByID(vehicle.dealershipId) ?: return Result.failure("Dealer ID not found...")

        if (!dealer.enabledForAcquisition) {
            return Result.failure("Dealer is not enabled for acquisition.")
        }

        val alreadyExists = vehicles.any { it.vehicleId == vehicle.vehicleId } ||
                vehicles.any { it.vehicleId == vehicle.vehicleId }

        if (alreadyExists) {
            return Result.failure("A Vehicle with ID ${vehicle.vehicleId} already exists.")
        }

        vehicles.add(vehicle);
        return Result.success()
    }

    /**
     * In the context of the database, removes or deletes a vehicle from a dealer
     * @param id - The vehicle ID of the vehicle to be removed
     */
    fun deleteVehicle(id: String) {
        vehicles.removeIf { it.vehicleId == id }
    }

    /**
     * Passes a list of dealers to ImportInner function for handling
     * @param data - A list of dealers to be processed (imported)
     */
    fun importJSON(incomingDealers: List<DealerJson>) = processJson(incomingDealers)

    /**
     * Passes a list of dealers to ImportInner function for handling
     * @param data - A list of dealers to be processed (imported)
     */
    fun importXML(incomingDealers: List<DealerXml>) = processXml(incomingDealers)

    private fun processJson(incomingDealers: List<DealerJson>) {
        val dealers = incomingDealers.map { it.toDomainDealer() }
        val vehicles = incomingDealers
            .flatMap { dealer ->
                dealer.vehicles.map { it.toDomainVehicle() }
            }

        importInner(dealers, vehicles)
    }

    private fun processXml(incomingDealers: List<DealerXml>) {
        val dealers = incomingDealers.map { it.toDomainDealer() }
        val vehicles = incomingDealers
            .flatMap { dealer ->
                dealer.vehicles.map { it.toDomainVehicle(dealer.dealershipId) }
            }

        importInner(dealers, vehicles)
    }

    private fun importInner(incomingDealers: List<Dealer>, incomingVehicles: List<Vehicle>) {
        for (dealer in incomingDealers) {
            val existing = dealers.find { it.dealershipId == dealer.dealershipId }

            if (existing != null) {
                existing.setName(dealer.getName())
                existing.enabledForAcquisition = dealer.enabledForAcquisition
            } else {
                dealers.add(dealer)
            }
        }

        for (vehicle in incomingVehicles) {
            // Try to find match by dealership + vehicle ID
            val exactMatch = vehicles.find {
                it.vehicleId == vehicle.vehicleId && it.dealershipId == vehicle.dealershipId
            }

            if (exactMatch != null) {
                // Update existing
                exactMatch.updateFrom(vehicle)
                continue
            }

            // Check if vehicle ID exists at all (any dealership)
            val idConflict = vehicles.any { it.vehicleId == vehicle.vehicleId }

            if (idConflict) {
                // ID already used by another dealer – only dedupe once
                val deDupedId = "${vehicle.vehicleId}-${vehicle.dealershipId}"
                if (vehicles.none { it.vehicleId == deDupedId }) {
                    val deDuped = vehicle.copy(vehicleId = deDupedId)
                    vehicles.add(deDuped)
                    logger.warn("Duplicate vehicleId '${vehicle.vehicleId}' across dealers. Added as '${deDuped.vehicleId}'")
                } else {
                    val existing = vehicles.find { it.vehicleId == deDupedId }
                    existing?.updateFrom(vehicle)
                    logger.warn("Vehicle with deDuped ID '${vehicle.vehicleId}' already exists. Updating it.")
                }
            } else {
                // No conflict – add as is
                vehicles.add(vehicle)
            }
        }
        logger.info("Imported ${incomingDealers.size} dealers and ${incomingVehicles.size} vehicles.")
    }

    fun toggleIsRented(vehicleId: String) {
        vehicles.first { it.vehicleId == vehicleId }.toggleIsRented()
    }

    fun setDealers(dealers: List<Dealer>) {
        this.dealers = FXCollections.observableArrayList(dealers);
    }

    fun setVehicles(vehicles: List<Vehicle>) {
        this.vehicles = FXCollections.observableArrayList(vehicles);
    }

    companion object {
        val instance: Database by lazy { Database() }
    }
}
