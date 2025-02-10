package com.metrostate.projectone.infrastructure.database;

import com.metrostate.projectone.infrastructure.Result;
import com.metrostate.projectone.models.Dealer;
import com.metrostate.projectone.models.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * - We are not really connected to a database.
 * - This singleton will have a list of dealers which is serve as our
 *   in-memory database, and it will also sort of be the repository/service of dealers
 */
public class DatabaseContext implements IDatabaseContext {
    private static DatabaseContext instance = null;
    private final List<Dealer> dealers;

    private DatabaseContext() {
        dealers = new ArrayList<>();
    }

    public static DatabaseContext getInstance() {
        if (instance == null) {
            instance = new DatabaseContext();
        }
        return instance;
    }

    /**
     * @return All in-memory dealers.
     */
    public List<Dealer> getDealers() {
        return dealers;
    }

    /**
     * flatMap comes pretty handy here as we've got a List of dealers and
     * each dealer has a list of vehicles.
     * @return All vehicles for all Dealers.
     */
    public List<Vehicle> getVehicles() {
        return dealers
            .stream()
            .flatMap(dealer -> dealer.getVehicles().stream())
            .collect(Collectors.toList());
    }

    /**
     * @param dealershipId the dealer ID
     * @return a list of vehicles for a dealer or an error message
     */
    public Result<List<Vehicle>> getVehiclesByDealershipId(String dealershipId) {
        var result = getDealerByDealershipId(dealershipId);
        if (result.IsSuccess()) {
            var dealer = result.getData();
            return new Result<List<Vehicle>>().Success(dealer.getVehicles());
        }
        return new Result<List<Vehicle>>().Fail(result.getErrorMessage());
    }

    /**
     * Gets a dealer by ID
     * @param dealershipId the dealer ID
     * @return the dealer for dealershipId or an error message
     */
    public Result<Dealer> getDealerByDealershipId(String dealershipId) {
        var dealer = dealers
            .stream()
            .filter(d -> d.getDealershipId().equals(dealershipId))
            .findFirst()
            .orElse(null);

        if (dealer != null) {
            return new Result<Dealer>().Success(dealer);
        }
        return new Result<Dealer>().Fail("Dealership ID not found...");
    }

    /**
     * Adds a dealer to our 'database', if the dealership ID already exists
     * it will return an error message stating that.
     * @param dealer the dealer to be added.
     * @return if successfully added and a possible error message.
     */
    public Result<Boolean> addDealer(Dealer dealer) {
        if (dealer == null) {
            return new Result<Boolean>().Fail("Dealer can't be null");
        }

        // dealer already exists...
        if (getDealerByDealershipId(dealer.getDealershipId()).IsSuccess()) {
            return new Result<Boolean>().Fail("Dealership ID already exists...");
        }

        dealers.add(dealer);
        return new Result<Boolean>().Success();
    }

    /**
     * Adds a vehicle to a dealer
     * - Checks if dealer is enabled for acquisition
     * - Checks if dealershipId exists (Adds a new one if it doesn't)
     * - Checks if vehicleId is already being used.
     * - Simple null validation
     * @param vehicle the vehicle
     * @return adds a new vehicle or returns an appropriate error message.
     */
    public Result<Boolean> addVehicleToDealer(Vehicle vehicle, String dealershipId) {
        Dealer dealer;
        if (vehicle == null) {
            return new Result<Boolean>().Fail("Vehicle can't be null");
        }

        var getDealerResult = getDealerByDealershipId(dealershipId);
        if (!getDealerResult.IsSuccess()) {
            dealer = new Dealer(dealershipId);
            dealers.add(dealer);
        } else {
            dealer = getDealerResult.getData();
        }

        if (!dealer.isEnabledForAcquisition()) {
            return new Result<Boolean>().Fail("Dealer ID \"" + dealershipId + "\" is disabled for acquisition.");
        }

        var vehicles = dealer.getVehicles();
        var existingVehicleWithSameId = vehicles
            .stream()
            .filter(v -> v.getVehicleId().equals(vehicle.getVehicleId()))
            .findFirst()
            .orElse(null);

        // ID is being used already.
        if (existingVehicleWithSameId != null) {
            return new Result<Boolean>().Fail("Vehicle ID \"" + vehicle.getVehicleId() + "\" is already being used...");
        }

        vehicles.add(vehicle);
        return new Result<Boolean>().Success();
    }

    /**
     * @deprecated
     */
    public Result<Boolean> addVehicleToDealer2(Vehicle vehicle, String dealershipId) {
    	Dealer dealer;
        if (vehicle == null) {
            return new Result<Boolean>().Fail("Vehicle can't be null");
        }
        var getDealerResult = getDealerByDealershipId(dealershipId);
        if (!getDealerResult.IsSuccess())
        {
        dealer = new Dealer(dealershipId);
           dealers.add(dealer);
        } else
        {
        dealer = getDealerResult.getData();
}

if (!(dealer).isEnabledForAcquisition()) {
            return new Result<Boolean>().Fail("Dealer ID \"" + dealershipId + "\" is disabled for acquisition.");
        }

        var vehicles = dealer.getVehicles();
        //Debug 1
        
        var existingVehicleWithSameId = vehicles
            .stream()
            .filter(v -> v.getVehicleId().equals(vehicle.getVehicleId()))
            .findFirst()
            .orElse(null);

        // ID is being used already.
        if (existingVehicleWithSameId != null) {
            return new Result<Boolean>().Fail("Vehicle ID \"" + vehicle.getVehicleId() + "\" is already being used...");
        }

        vehicles.add(vehicle);
        return new Result<Boolean>().Success();
    }
}
