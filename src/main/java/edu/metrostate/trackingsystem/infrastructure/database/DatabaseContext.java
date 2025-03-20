package edu.metrostate.trackingsystem.infrastructure.database;

import edu.metrostate.trackingsystem.domain.models.Dealer;
import edu.metrostate.trackingsystem.domain.models.Vehicle;
import edu.metrostate.trackingsystem.infrastructure.database.models.DealersXMLModel;
import javafx.collections.FXCollections;

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
    private final List<Dealer> dealers;

    private DatabaseContext() {
        dealers = FXCollections.observableArrayList();
    }

    public static IDatabaseContext getInstance() {
        if (instance == null) {
            instance = new DatabaseContext();
        }
        return instance;
    }

    @Override
    public List<Dealer> getDealers() {
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

    /**
     * flatMap comes pretty handy here as we've got a List of dealers and
     * each dealer has a list of vehicles.
     * @return All vehicles for all Dealers.
     */
    @Override
    public List<Vehicle> getVehicles() {
        return dealers
                .stream()
                .flatMap(dealer -> dealer.getVehicles().stream())
                .collect(Collectors.toList());
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

        var vehicles = dealer.getVehicles();
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
            if (result.getData()
                    .getVehicles()
                    .removeIf(v -> v.getVehicleId().equals(id))) {
                return Result.success();
            }
            return Result.failure("Vehicle ID not found.");
        }
        return Result.failure(result.getErrorMessage());
    }

    @Override
    public Vehicle getVehicle(String id, String dealerId) {
        var dealer =  dealers
                .stream()
                .filter(d -> d.getDealershipId().equals(dealerId))
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
    public void importJson(List<Vehicle> data) {
        for (var item : data) {
            var dealer = dealers
                    .stream()
                    .filter(d -> d.getDealershipId().equals(item.getDealershipId()))
                    .findFirst()
                    .orElse(null);

            // create a dealer if one with the same ID does not exist.
            if (dealer == null) {
                dealer = new Dealer(item.getDealershipId());
                dealers.add((dealer));
            }

            // if vehicle ID is in use, update its information, add a new one otherwise.
            var vehicle = getVehicle(item.getVehicleId(), item.getDealershipId());
            if (vehicle == null) {
                vehicle = new Vehicle(
                        item.getVehicleId(),
                        item.getManufacturer(),
                        item.getModel(),
                        item.getAcquisitionDate(),
                        item.getPrice(),
                        item.getDealershipId(),
                        item.getType()
                );
                dealer.addVehicle(vehicle);
            } else {
                vehicle.setManufacturer(item.getManufacturer());
                vehicle.setModel(item.getModel());
                vehicle.setType(item.getType());
                vehicle.setPrice(item.getPrice());
                vehicle.setAcquisitionDate(item.getAcquisitionDate());
            }
        }
    }


    //Current problem is that dealer name is not showing or not getting added to database correctly
    public void importXML(DealersXMLModel model) {
        var incomingDealers = model.getDealers();

        // TODO: validate before pushing it into dealers
        // Make sure to deal with non-existent fields
        for (Dealer incomingDealer : incomingDealers) {

            //If dealer ID is already in database
                //Get the vehicle list for the existing dealer

            var result = getDealerByID(incomingDealer.getDealershipId());
            if (result.isSuccess()) {
                List<Vehicle> vehicleListToUpdate = result.getData().getVehicles();

                //Set name if it is not set by using the new XML data
                String currentName = result.getData().getName();
                if (currentName == null || currentName.isEmpty()) {
                    result.getData().setName(incomingDealer.getName());
                }

                //Check the existing dealer for vehicle ID conflicts with the incoming XML data
                for (Vehicle xmlVehicle : incomingDealer.getVehicles()) {
                    // see if the vehicle id is already added
                    var test = vehicleListToUpdate
                            .stream()
                            .filter(v -> v.getVehicleId().equals(xmlVehicle.getVehicleId()))
                            .findFirst()
                            .orElse(null);
                    //If there is no matching vehicle ID for the dealer, add the vehicle
                    if (test == null) {
                        Vehicle vehicleToAdd = new Vehicle(
                                xmlVehicle.getVehicleId(),
                                xmlVehicle.getManufacturer(),
                                xmlVehicle.getModel(),
                                xmlVehicle.getAcquisitionDate(),
                                xmlVehicle.getPrice(),
                                incomingDealer.getDealershipId(),
                                xmlVehicle.getType());

                        long acquisitionDate = Instant.now().toEpochMilli();
                        vehicleToAdd.setAcquisitionDate(acquisitionDate);

                        result.getData().getVehicles().add(vehicleToAdd);

                        //Discarding current data means possibly replacing the current data with old outdated data
                    } else {
                        test.setManufacturer(xmlVehicle.getManufacturer());
                        test.setModel(xmlVehicle.getModel());
                        test.setType(xmlVehicle.getType());
                        test.setPrice(xmlVehicle.getPrice());

                        //TODO: Does this make sense to use or use something else?
                        long acquisitionDate = Instant.now().toEpochMilli();
                        test.setAcquisitionDate(acquisitionDate);
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
