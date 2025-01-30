package com.metrostate.projectone.controllers;

import com.metrostate.projectone.models.Dealer;
import com.metrostate.projectone.models.Vehicle;
import com.metrostate.projectone.utils.FileHandler;
import com.metrostate.projectone.utils.IFileHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DealershipController implements IDealershipController {
    private final List<Dealer> dealers;
    private final IFileHandler fileHandler = new FileHandler();

    public DealershipController() {
        this.dealers = new ArrayList<>();
    }

    @Override
    public List<Vehicle> getVehicles() {
        return dealers.stream()
            .flatMap(dealer -> dealer.getVehicles().stream())
            .collect(Collectors.toList());
    }

    @Override
    public List<Vehicle> getVehiclesByDealershipId(String dealershipId) {
        return List.of();
    }

    @Override
    public boolean addCar(Vehicle car, String dealershipId) {
        return false;
    }

    @Override
    public boolean removeCar(String vehicleId, String dealershipId) {
        return false;
    }

    @Override
    public boolean enableAcquisition(String dealershipId) {
        return false;
    }

    @Override
    public boolean disableAcquisition(String dealershipId) {
        return false;
    }

    @Override
    public boolean importJsonFile(String fileName) throws IOException, ParseException {
        //Suggest trying src/main/resources/inventory.json for testing
        JSONObject object = fileHandler.read(fileName);
        return false;
    }

    @Override
    public boolean exportVehiclesToJson(String dealershipId) {
        List<Vehicle> dealershipVehicles = getVehiclesByDealershipId(dealershipId);
        return false;
    }
}