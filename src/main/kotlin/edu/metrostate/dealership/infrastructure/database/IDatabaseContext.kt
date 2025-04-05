package edu.metrostate.dealership.infrastructure.database

import edu.metrostate.dealership.domain.models.Dealer
import edu.metrostate.dealership.domain.models.Vehicle
import javafx.collections.ObservableList

interface IDatabaseContext {
    var dealers: ObservableList<Dealer>

    /**
     * flatMap comes pretty handy here as we've got a List of dealers and
     * each dealer has a list of vehicles.
     *
     * @return All vehicles for all Dealers.
     */
    val vehicles: ObservableList<Vehicle>

    /**
     * @return All registered dealership IDs.
     */
    val dealershipIDs: List<String>

    /**
     * Gets a dealer by ID in the context of the database
     * @param dealershipId the dealer ID
     * @return the dealer for dealershipId or an error message
     */
    fun getDealerByID(dealershipId: String): Dealer?

    /**
     * In the context of the database, remove or delete a dealer
     * @param dealershipId - The dealership ID of the dealer to be removed
     * @return a result of success if operation completed, otherwise return a result failure message.
     */
    fun deleteDealer(dealershipId: String): Result<Boolean>

    /**
     * Adds a vehicle object to a dealer by using the vehicle's dealer ID.
     * In the context of the database
     * @param vehicle - A vehicle object to add
     * @return a result of success if operation completed, otherwise return a result failure message.
     */
    fun addVehicle(vehicle: Vehicle): Result<Boolean>

    /**
     * In the context of the database, removes or deletes a vehicle from a dealer
     * @param id - The vehicle ID of the vehicle to be removed
     * @param dealerId - The dealer ID of the dealer that may contain the vehicle.
     * @return A result of success on completed operation or a result of failure with an errorMessage if unsuccessful.
     */
    fun deleteVehicle(id: String, dealerId: String): Result<Boolean>

    /**
     * Update the information of a dealer, currently limited to name.
     * Does this in the context of the database
     * @param dealershipId - A dealership identification code
     * @param name - The name to set as the dealer name
     * @return true if dealer name was set, otherwise return false
     */
    fun updateDealer(dealershipId: String, name: String): Boolean

    /**
     * Toggles the acquisition of vehicles for a dealer in the context of the database
     * @param dealershipId - A dealership identification code
     */
    fun toggleAcquisition(dealershipId: String)

    /**
     * Toggle the state of renting for a vehicle
     * @param vehicle - A vehicle object
     */
    fun toggleIsRented(vehicle: Vehicle)

    /**
     * Passes a list of dealers to ImportInner function for handling
     * @param data - A list of dealers to be processed (imported)
     */
    fun importJSON(data: List<Dealer?>)

    /**
     * Passes a list of dealers to ImportInner function for handling
     * @param data - A list of dealers to be processed (imported)
     */
    fun importXML(data: List<Dealer>)
}
