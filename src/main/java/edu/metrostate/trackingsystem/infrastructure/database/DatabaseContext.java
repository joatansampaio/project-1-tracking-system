package edu.metrostate.trackingsystem.infrastructure.database;

import edu.metrostate.trackingsystem.domain.models.Dealer;
import edu.metrostate.trackingsystem.domain.models.Vehicle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
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
    public Dealer getDealerByID(String dealershipId) {
        return dealers
                .stream()
                .filter(d -> d.getDealershipId().equals(dealershipId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void setDealers(List<Dealer> dealers) {
        this.dealers.setAll(dealers); // This works since `this.dealers` is an ObservableList
    }

    @Override
    public void toggleAcquisition(String dealershipId) {
        dealers.stream()
               .filter(d -> d.getDealershipId().equals(dealershipId))
               .findFirst()
               .ifPresent(dealer -> dealer.setEnabledForAcquisition(!dealer.getEnabledForAcquisition()));
    }

    @Override
    public boolean updateDealer(String dealershipId, String name){
        var dealer =  getDealerByID(dealershipId);
        if (dealer != null){
            if (name != null && !name.isEmpty())
                dealer.setName(name);
           return true;
        }
        return false;
    }
    
    @Override
    public Result<Boolean> addVehicle(Vehicle vehicle) {
        var dealer =  getDealerByID(vehicle.getDealershipId());

        if (dealer == null) {
            return Result.failure("Dealer ID not found...");
        }
        if (!dealer.getEnabledForAcquisition()) {
            return Result.failure("Dealer is not enabled for acquisition.");
        }
        // Double check because of the unit tests that do not keep 'vehicles' updated as the UI does.
        if (dealer.getVehicles().stream().anyMatch(v -> v.getVehicleId().equals(vehicle.getVehicleId()))
                || vehicles.stream().anyMatch(v -> v.getVehicleId().equals(vehicle.getVehicleId()))) {
            return Result.failure("A Vehicle with ID " + vehicle.getVehicleId() + " already exists.");
        }

        dealer.addVehicle(vehicle);
        return Result.success();
    }

    @Override
    public Result<Boolean> deleteVehicle(String id, String dealershipId) {
        var dealer = getDealerByID(dealershipId);
        if (dealer != null) {
            if (dealer.removeVehicle(id)) {
                return Result.success();
            }
            return Result.failure("Vehicle ID not found.");
        }
        return Result.failure("Dealer ID not found...");
    }

    @Override
    public void importJSON(List<Dealer> incomingDealers) {
        ImportInner(incomingDealers);
    }

    @Override
    public void importXML(List<Dealer> incomingDealers) {
        ImportInner(incomingDealers);
    }

    private void ImportInner(List<Dealer> incomingDealers) {
        // I want a map with <vehicleID, dealershipId> here.
        // That will be used as a prevention against duplicates in the system
        // If we had an actual database, none of that would be necessary.
        // We could also ignore duplicate vehicle IDs, but because we chose to de-duplicate
        // them by appending the dealer ID prefix, this ends up being necessary.
        Map<String, String> systemVehicleIdsMap = dealers.stream()
            .flatMap(dealer -> dealer.getVehicles().stream()
                    .map(vehicle -> new AbstractMap.SimpleEntry<>(vehicle.getVehicleId(), dealer.getDealershipId()))                ).collect(Collectors.toMap(
                        AbstractMap.SimpleEntry::getKey,
                        AbstractMap.SimpleEntry::getValue));

        for (Dealer incomingDealer : incomingDealers) {
            if (incomingDealer.getDealershipId() == null) {
                continue;
            }
            var dealer = dealers
                    .stream()
                    .filter(d -> d.getDealershipId().equals(incomingDealer.getDealershipId()))
                    .findFirst()
                    .orElse(null);

            // ################################
            // ##  Updating existing dealer  ##
            // ################################
            if (dealer != null) {
                // Set name if it is not set by using the new XML data
                String currentName = dealer.getName();
                if (currentName == null || currentName.isEmpty()) {
                    dealer.setName(incomingDealer.getName());
                }

                for (Vehicle incomingVehicle : incomingDealer.getVehicles()) {
                    var id = incomingVehicle.getVehicleId();
                    if (systemVehicleIdsMap.containsKey(incomingVehicle.getVehicleId())) {
                        if (!systemVehicleIdsMap.get(incomingVehicle.getVehicleId()).equals(dealer.getDealershipId())) {
                            // De-duplication
                            id = dealer.getDealershipId() + "_" + incomingVehicle.getVehicleId();
                            incomingVehicle.setVehicleId(id);
                            dealer.addVehicle(incomingVehicle);
                            systemVehicleIdsMap.put(id, dealer.getDealershipId());
                        }
                    } else {
                        dealer.addVehicle(incomingVehicle);
                        systemVehicleIdsMap.put(id, dealer.getDealershipId());
                    }
                }
            // ############################
            // ##  Inserting new dealer  ##
            // ############################
            } else {
                var uniqueVehicles = new ArrayList<Vehicle>();
                for (Vehicle incomingVehicle : incomingDealer.getVehicles()) {
                    var id = incomingVehicle.getVehicleId();
                    if (systemVehicleIdsMap.containsKey(incomingVehicle.getVehicleId())) {
                        // De-duplication
                        id = incomingDealer.getDealershipId() + "_" + incomingVehicle.getVehicleId();
                        incomingVehicle.setVehicleId(id);
                        uniqueVehicles.add(incomingVehicle);
                    } else {
                        uniqueVehicles.add(incomingVehicle);
                    }
                    systemVehicleIdsMap.put(id, incomingDealer.getDealershipId());
                }
                incomingDealer.setVehicles(uniqueVehicles);
                dealers.add(incomingDealer);
            }
        }
    }

    @Override
    public void toggleIsRented(Vehicle vehicle){
        vehicle.toggleIsRented();
    }
}
