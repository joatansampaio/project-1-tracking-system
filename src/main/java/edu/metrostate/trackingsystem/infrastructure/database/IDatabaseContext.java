package edu.metrostate.trackingsystem.infrastructure.database;

import edu.metrostate.trackingsystem.domain.models.Dealer;
import edu.metrostate.trackingsystem.domain.models.Vehicle;
import javafx.collections.ObservableList;

import java.util.List;

/**
 *   We are not really connected to a database.
 *   This singleton will have a list of dealers which is serve as our
 *   in-memory database
 */
public interface IDatabaseContext {
    ObservableList<Dealer> getDealers();

    /**
     * flatMap comes pretty handy here as we've got a List of dealers and
     * each dealer has a list of vehicles.
     *
     * @return All vehicles for all Dealers.
     */
    ObservableList<Vehicle> getVehicles();

    /**
     * @return All registered dealership IDs.
     */
    List<String> getDealershipIDs();

    /**
     * Gets a dealer by ID in the context of the database
     * @param dealershipId the dealer ID
     * @return the dealer for dealershipId or an error message
     */
    Dealer getDealerByID(String dealershipId);

    /**
     * In the context of the database, remove or delete a dealer
     * @param dealershipId - The dealership ID  of the dealer to be removed
     * @return a result of success if operation completed, otherwise return a result failure message.
     */
    Result<Boolean> deleteDealer(String dealershipId);

    /**
     * Adds a vehicle object to a dealer by using the vehicle's dealer ID. In the context of the database
     * @param vehicle - A vehicle object to add
     * @return a result of success if operation completed, otherwise return a result failure message.
     */
    Result<Boolean> addVehicle(Vehicle vehicle);

    /**
     * In the context of the database, removes or deletes a vehicle from a dealer
     * @param id - The vehicle ID of the vehicle to be removed
     * @param dealerId - The dealer ID of the dealer that may contain the vehicle.
     * @return A result of success on completed operation or a result of failure with an errorMessage if unsuccessful.
     */
    Result<Boolean> deleteVehicle(String id, String dealerId);

    /**
     * Update the information of a dealer, currently limited to name. Does this in the context of the database
     * @param dealershipId - A dealership identification code
     * @param name - The name to set as the dealer name
     * @return true if dealer name was set, otherwise return false
     */
    boolean updateDealer(String dealershipId, String name);

    /**
     * Toggles the acquisition of vehicles for a dealer in the context of the database
     * @param dealershipId - A dealership identification code
     */
    void toggleAcquisition(String dealershipId);

    /**
     * Toggle the state of renting for a vehicle
     * @param vehicle - A vehicle object
     */
    void toggleIsRented(Vehicle vehicle);

    void setDealers(List<Dealer> dealers);

    /**
     * Passes a list of dealers to ImportInner function for handling
     * @param data - A list of dealers to be processed (imported)
     */
    void importJSON(List<Dealer> data);

    /**
     * Passes a list of dealers to ImportInner function for handling
     * @param data - A list of dealers to be processed (imported)
     */
    void importXML(List<Dealer> data);



}
