package com.metrostate.projectone.controllers;

import com.metrostate.projectone.models.Vehicle;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

public interface IDealershipController {
    List<Vehicle> getVehicles();
    List<Vehicle> getVehiclesByDealershipId(String dealershipId);
    boolean addCar(Vehicle car, String dealershipId);
    boolean removeCar(String vehicleId, String dealershipId);
    boolean enableAcquisition(String dealershipId);
    boolean disableAcquisition(String dealershipId);
    boolean importJsonFile(String fileName) throws ParseException, IOException;
    boolean exportVehiclesToJson(String dealershipId);
}
