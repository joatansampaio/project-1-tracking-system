package edu.metrostate.trackingsystem;

import edu.metrostate.trackingsystem.domain.models.Dealer;
import edu.metrostate.trackingsystem.domain.models.Price;
import edu.metrostate.trackingsystem.domain.models.Vehicle;
import edu.metrostate.trackingsystem.infrastructure.utils.XmlHandler;

import java.io.File;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

public class Scene {

    private final Main.DependencyPackage dp = Main.getDependencies(null);
    private static final HashMap<String, String> vehicleDealerMap = new HashMap<>();

    private static int lastDealerIndex = 0;
    private static int lastVehicleIndex = 0;

    public Scene setup() {
        dp.database().getDealers().clear();
        vehicleDealerMap.clear();
        lastVehicleIndex = 0;
        lastDealerIndex = 0;
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

    public boolean addVehicle(String vehicle, int overrideLastVehicleIndex) {
        lastVehicleIndex = overrideLastVehicleIndex;
        return addVehicle(vehicle);
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
        var isSuccess = dp.vehicleService().addVehicle(newVehicle).isSuccess();
        if (isSuccess) {
            vehicleDealerMap.put(vehicleId, dealershipId);
        }
        return isSuccess;
    }

    public boolean deleteVehicle(String vehicleId) {
        var effectiveVehicleId = vehicleId.replace("vehicle", "");
        var effectiveDealerId = vehicleDealerMap.get(effectiveVehicleId);
        return dp.vehicleService().deleteVehicle(effectiveVehicleId, effectiveDealerId).isSuccess();
    }

    public List<Dealer> getDealers() {
        return dp.dealerService().getDealers();
    }

    public Dealer getDealer(String dealershipId) {
        return dp.database().getDealerByID(dealershipId);
    }

    public boolean importJson() {
        File file = new File("src/test/jsonTest.json");
        return dp.jsonHandler().importFile(file);
    }

    public boolean importXml() {
        File file = new File("src/test/xmlTest.xml");
        return XmlHandler.getInstance().importFile(file);
    }
}
