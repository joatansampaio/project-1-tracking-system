package com.metrostate.projectone.controllers;

import com.metrostate.projectone.models.Vehicle;

import java.util.List;

public interface IDealershipController {
    List<Vehicle> getVehicles();
    List<Vehicle> getVehiclesByDealershipId(String dealershipId);
    boolean addCar(Vehicle car, String dealershipId);
    boolean removeCar(String vehicleId, String dealershipId);
    boolean enableAcquisition(String dealershipId);
    boolean disableAcquisition(String dealershipId);
    boolean importJsonFile(String fileName);
    boolean exportVehiclesToJson(String dealershipId);
}
