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
 * In-memory singleton database implementation for the dealership management system.
 * Stores all dealers and vehicles data and provides CRUD operations for them.
 * This class follows the singleton pattern for application-wide access to a single
 * data source.
 */
class Database private constructor()  {

    /**
     * Observable list of all dealers in the system.
     * Uses JavaFX ObservableList for automatic UI updates.
     */
    var dealers: ObservableList<Dealer> = FXCollections.observableArrayList()

    /**
     * Observable list of all vehicles in the system.
     * Uses JavaFX ObservableList for automatic UI updates.
     */
    var vehicles: ObservableList<Vehicle> = FXCollections.observableArrayList()

    /**
     * Retrieves all dealership IDs in the system.
     *
     * @return A list of all dealership IDs as strings
     */
    fun getDealershipIDs(): List<String> {
        return dealers.map { it.dealershipId.toString() }
    }

    /**
     * Deletes a dealer from the database by its ID.
     *
     * @param dealershipId The ID of the dealer to delete
     * @return A success result if the dealer was deleted, or a failure result with error message if not found
     */
    fun deleteDealer(dealershipId: String): Result<Boolean> {
        return if (dealers.removeIf { it.dealershipId == dealershipId }) {
            Result.success()
        } else {
            Result.failure("Can't delete... dealership ID not found!")
        }
    }

    /**
     * Retrieves a dealer by its ID.
     *
     * @param dealershipId The ID of the dealer to retrieve
     * @return The dealer object if found, or null if no dealer with the given ID exists
     */
    fun getDealerByID(dealershipId: String): Dealer? {
        return dealers.find { it.dealershipId == dealershipId }
    }

    /**
     * Toggles the acquisition status of a dealer.
     * If the dealer is currently enabled for acquisition, it will be disabled, and vice versa.
     *
     * @param dealershipId The ID of the dealer whose acquisition status should be toggled
     */
    fun toggleAcquisition(dealershipId: String) {
        getDealerByID(dealershipId)?.let {
            it.enabledForAcquisition = !it.enabledForAcquisition
        }
    }

    /**
     * Updates a dealer's information.
     * Currently only supports updating the dealer's name.
     *
     * @param dealershipId The ID of the dealer to update
     * @param name The new name for the dealer
     * @return true if the dealer was found and updated, false if the dealer wasn't found or the name was blank
     */
    fun updateDealer(dealershipId: String, name: String): Boolean {
        val dealer = getDealerByID(dealershipId) ?: return false
        if (name.isNotBlank()) dealer.name = name
        return true
    }

    /**
     * Adds a vehicle to the database.
     * The vehicle will be associated with its dealership using the dealershipId property.
     *
     * @param vehicle The vehicle to add
     * @return A success result if the vehicle was added, or a failure result with an error message if:
     *         - The specified dealer doesn't exist
     *         - The dealer is not enabled for acquisition
     *         - A vehicle with the same ID already exists
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
     * Deletes a vehicle from the database by its ID.
     *
     * @param id The ID of the vehicle to delete
     * @return true if a vehicle with the specified ID was found and deleted, false otherwise
     */
    fun deleteVehicle(id: String): Boolean {
        return vehicles.removeIf { it.vehicleId == id }
    }

    /**
     * Imports dealer and vehicle data from JSON format.
     * Processes the incoming JSON models and adds them to the database.
     *
     * @param incomingDealers List of dealer objects in JSON format
     */
    fun importJSON(incomingDealers: List<DealerJson>) = processJson(incomingDealers)

    /**
     * Imports dealer and vehicle data from XML format.
     * Processes the incoming XML models and adds them to the database.
     *
     * @param incomingDealers List of dealer objects in XML format
     */
    fun importXML(incomingDealers: List<DealerXml>) = processXml(incomingDealers)

    /**
     * Processes JSON data for import.
     * Converts the JSON dealer models to domain models and calls the internal import function.
     *
     * @param incomingDealers List of dealer objects from JSON
     */
    private fun processJson(incomingDealers: List<DealerJson>) {
        val dealers = incomingDealers.map { it.toDomainDealer() }
        val vehicles = incomingDealers
            .flatMap { dealer ->
                dealer.vehicles.map { it.toDomainVehicle() }
            }

        importInner(dealers, vehicles)
    }

    /**
     * Processes XML data for import.
     * Converts the XML dealer models to domain models and calls the internal import function.
     *
     * @param incomingDealers List of dealer objects from XML
     */
    private fun processXml(incomingDealers: List<DealerXml>) {
        val dealers = incomingDealers.map { it.toDomainDealer() }
        val vehicles = incomingDealers
            .flatMap { dealer ->
                dealer.vehicles!!.map { it.toDomainVehicle(dealer.dealershipId) }
            }

        importInner(dealers, vehicles)
    }

    /**
     * Internal method to handle the import of dealers and vehicles.
     * This method performs the following operations:
     * 1. Updates existing dealers or adds new ones
     * 2. Updates existing vehicles if they match by ID and dealership
     * 3. Handles ID conflicts across dealerships by creating de-duped IDs
     * 4. Adds new vehicles
     *
     * @param incomingDealers List of dealers to import
     * @param incomingVehicles List of vehicles to import
     */
    private fun importInner(incomingDealers: List<Dealer>, incomingVehicles: List<Vehicle>) {
        for (dealer in incomingDealers) {
            val existing = dealers.find { it.dealershipId == dealer.dealershipId }

            if (existing != null) {
                existing.name = dealer.name
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

    /**
     * Toggles the rental status of a vehicle.
     * If the vehicle is currently rented, it will be marked as available, and vice versa.
     *
     * @param vehicleId The ID of the vehicle whose rental status should be toggled
     * @throws NoSuchElementException if no vehicle with the specified ID exists
     */
    fun toggleIsRented(vehicleId: String) {
        vehicles.first { it.vehicleId == vehicleId }.toggleIsRented()
    }

    /**
     * Sets the list of dealers in the database.
     * Replaces the current observable list with a new one created from the provided list.
     * Used primarily during database loading operations.
     *
     * @param dealers The list of dealers to set in the database
     */
    fun setDealers(dealers: List<Dealer>) {
        this.dealers = FXCollections.observableArrayList(dealers);
    }

    /**
     * Sets the list of vehicles in the database.
     * Replaces the current observable list with a new one created from the provided list.
     * Used primarily during database loading operations.
     *
     * @param vehicles The list of vehicles to set in the database
     */
    fun setVehicles(vehicles: List<Vehicle>) {
        this.vehicles = FXCollections.observableArrayList(vehicles);
    }

    /**
     * Singleton companion object for the Database class.
     */
    companion object {
        /**
         * The singleton instance of the Database.
         * Use this property to access the Database throughout the application.
         */
        val instance: Database by lazy { Database() }
    }
}