package edu.metrostate.trackingsystem;

import edu.metrostate.trackingsystem.domain.models.Dealer;
import edu.metrostate.trackingsystem.domain.models.Price;
import edu.metrostate.trackingsystem.domain.models.Vehicle;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class Scene {

    private final Main.DependencyPackage dp = Main.getDependencies(null);

    private int lastDealerIndex = 0;
    private int lastVehicleIndex = 0;

    public Scene setup() {
        dp.database().getDealers().clear();
        return this;
    }

    public Scene dealer(String dealerName, boolean enabledForAcquisition) {
        var dealer = new Dealer("#" + ++lastDealerIndex, dealerName);
        if (!enabledForAcquisition) {
            dealer.setEnabledForAcquisition(false);
        }
        dp.database().getDealers().add(dealer);
        return this;
    }

    /**
     * @param vehicle dealer#1 ford f-150 pickup 50000 2/3/2025
     */
    public Scene vehicle(String vehicle) {
        addVehicle(vehicle);
        return this;
    }

    public boolean addVehicle(String vehicle) {
        var vehicleId = "#" + ++lastVehicleIndex;

        var parts = vehicle.split(" ");
        var dealershipId = parts[0].replace("dealer", "");
        var manufacturer = parts[1];
        var model = parts[2];
        var type = parts[3];
        var price = Double.parseDouble(parts[4]);
        long acquisitionDate = Instant.now().toEpochMilli();

        var newVehicle = new Vehicle(vehicleId, manufacturer, model, acquisitionDate, new Price(price, "dollars"), dealershipId, type);
        return dp.vehicleService().addVehicle(newVehicle).isSuccess();
    }

    public boolean deleteVehicle(String vehicleId) {
        var effectiveId = vehicleId.replace("vehicle", "");

        var vehicle = dp.vehicleService()
                .getVehicles()
                .stream()
                .filter(v -> Objects.equals(v.getVehicleId(), effectiveId)) // Filter the matching vehicle
                .findFirst()
                .orElse(null);

        assertNotNull(vehicle);
        return dp.vehicleService().deleteVehicle(effectiveId, vehicle.getDealershipId()).isSuccess();
    }

    public List<Dealer> getDealers() {
        return dp.dealerService().getDealers();
    }

    public Dealer getDealer(String dealershipId) {
        return dp.database().getDealerByID(dealershipId);
    }
}
