package edu.metrostate.trackingsystem.infrastructure.database;

import edu.metrostate.trackingsystem.domain.models.Dealer;
import edu.metrostate.trackingsystem.domain.models.Vehicle;
import edu.metrostate.trackingsystem.infrastructure.database.models.DealersXMLModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 *   We are not really connected to a database.
 *   This singleton will have a list of dealers which is serve as our
 *   in-memory database
 */
public class DatabaseContext implements IDatabaseContext {
    private static IDatabaseContext instance = null;
    private final ObservableList<Dealer> dealers;
    private final ObservableList<Vehicle> vehicles;

    private DatabaseContext() {
        dealers = FXCollections.observableArrayList();
        vehicles = FXCollections.observableArrayList();
    }

    public static IDatabaseContext getInstance() {
        if (instance == null) {
            instance = new DatabaseContext();
        }
        return instance;
    }

    @Override
    public ObservableList<Dealer> getDealers() {
        return dealers;
    }

    /**
     * @return All registered dealership IDs.
     */
    @Override
    public List<String> getDealershipIDs() {
        return dealers
                .stream()
                .map(Dealer::getDealershipId)
                .collect(Collectors.toList());
    }

    @Override
    public Result<Boolean> deleteDealer(String dealershipId) {
        if (dealers.removeIf(d -> d.getDealershipId().equals(dealershipId))) {
            return Result.success();
        }
        return Result.failure("Can't delete... dealership ID not found!");
    }

    /**
     * flatMap comes pretty handy here as we've got a List of dealers and
     * each dealer has a list of vehicles.
     *
     * @return All vehicles for all Dealers.
     */
    @Override
    public ObservableList<Vehicle> getVehicles() {
        return vehicles;
    }

    /**
     * Gets a dealer by ID
     * @param dealershipId the dealer ID
     * @return the dealer for dealershipId or an error message
     */
    @Override
    public Result<Dealer> getDealerByID(String dealershipId) {
        var dealer = dealers
                .stream()
                .filter(d -> d.getDealershipId().equals(dealershipId))
                .findFirst()
                .orElse(null);

        if (dealer != null) {
            return Result.success(dealer);
        }
        return Result.failure("Dealership ID not found...");
    }

    @Override
    public void toggleAcquisition(String dealershipId) {
        var dealer = getDealerByID(dealershipId);
        if (dealer.isSuccess()) {
            dealer.getData().setEnabledForAcquisition(!dealer.getData().getEnabledForAcquisition());
        }
    }

    //TODO: Add a method to update the dealer's information
    // which right now should only allow name

    @Override
    public Result<Boolean> addVehicle(Vehicle vehicle) {
        Dealer dealer;

        var getDealerResult = getDealerByID(vehicle.getDealershipId());
        if (getDealerResult.isSuccess()) {
            dealer = getDealerResult.getData();
        } else {
            return Result.failure(getDealerResult.getErrorMessage());
        }

        if (!dealer.getEnabledForAcquisition()) {
            return Result.failure("Dealer is not enabled for acquisition.");
        }

        var vehicles = dealer.getObservableVehicles();
        var existingVehicleWithSameId = vehicles
                .stream()
                .filter(v -> v.getVehicleId().equals(vehicle.getVehicleId()))
                .findFirst()
                .orElse(null);

        // ID is being used already.
        if (existingVehicleWithSameId != null) {
            return Result.failure("Vehicle ID already exists...");
        }

        vehicles.add(vehicle);
        return Result.success();
    }

    @Override
    public Result<Boolean> deleteVehicle(String id, String dealerId) {
        var result = getDealerByID(dealerId);
        if (result.isSuccess()) {
            if (result.getData().removeVehicle(id)) {
                return Result.success();
            }
            return Result.failure("Vehicle ID not found.");
        }
        return Result.failure(result.getErrorMessage());
    }

    @Override
    public Vehicle getVehicle(String id, String dealershipId) {
        var dealer =  dealers
                .stream()
                .filter(d -> d.getDealershipId().equals(dealershipId))
                .findFirst()
                .orElse(null);

        if (dealer == null) {
            return null;
        }

        return dealer
                .getVehicles()
                .stream()
                .filter(v -> v.getVehicleId().equals((id)))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void importJson(List<Vehicle> incomingVehicles) {
        for (var incomingVehicle : incomingVehicles) {
            var dealer = dealers
                    .stream()
                    .filter(d -> d.getDealershipId().equals(incomingVehicle.getDealershipId()))
                    .findFirst()
                    .orElse(null);

            // create a dealer if one with the same ID does not exist.
            if (dealer == null) {
                dealer = new Dealer(incomingVehicle.getDealershipId());
                dealers.add((dealer));
            }

            if (!dealer.addVehicle(incomingVehicle)) {
                var vehicle = dealer
                        .getVehicles()
                        .stream()
                        .filter(v -> v.getVehicleId().equals(incomingVehicle.getVehicleId()))
                        .findFirst()
                        .orElse(null);

                assert vehicle != null;
                vehicle.setManufacturer(incomingVehicle.getManufacturer());
                vehicle.setModel(incomingVehicle.getModel());
                vehicle.setType(incomingVehicle.getType());
                vehicle.setPrice(incomingVehicle.getPrice());
                vehicle.setAcquisitionDate(incomingVehicle.getAcquisitionDate());
            }
        }
    }


    //Current problem is that dealer name is not showing or not getting added to database correctly
    public void importXML(DealersXMLModel model) {
        var incomingDealers = model.getDealers();

        // TODO: validate before pushing it into dealers
        // Make sure to deal with non-existent fields
        for (Dealer incomingDealer : incomingDealers) {

            var dealer = dealers
                    .stream()
                    .filter(d -> d.getDealershipId().equals(incomingDealer.getDealershipId()))
                    .findFirst()
                    .orElse(null);

            if (dealer != null) {
                //Set name if it is not set by using the new XML data
                String currentName = dealer.getName();
                if (currentName == null || currentName.isEmpty()) {
                    dealer.setName(incomingDealer.getName());
                }

                //Check the existing dealer for vehicle ID conflicts with the incoming XML data
                for (Vehicle vehicle : incomingDealer.getVehicles()) {
                    // see if the vehicle id is already added

                    //If there is no matching vehicle ID for the dealer, add the vehicle
                    if (!dealer.addVehicle(vehicle)) {
                        var existingVehicle = incomingDealer.getVehicles()
                                .stream()
                                .filter(v -> v.getVehicleId().equals(vehicle.getVehicleId()))
                                .findFirst()
                                .orElse(null);

                        assert existingVehicle != null;
                        existingVehicle.setManufacturer(vehicle.getManufacturer());
                        existingVehicle.setModel(vehicle.getModel());
                        existingVehicle.setType(vehicle.getType());
                        existingVehicle.setPrice(vehicle.getPrice());
                    }
                }
            } else {
                //The dealer is not in the database so add the dealer
                dealers.add(incomingDealer);
            }
        }
    }

    @Override
    public void toggleIsRented(Vehicle vehicle){
        vehicle.toggleIsRented();
    }
}
