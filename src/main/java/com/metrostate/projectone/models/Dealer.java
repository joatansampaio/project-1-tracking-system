
package com.metrostate.projectone.models;

import java.util.ArrayList;
import java.util.List;

public class Dealer {

    private String dealershipId;
    private boolean acquisitionEnabled;
    private List<Vehicle> vehicles;

    public Dealer(String dealershipId) {
        this.dealershipId = dealershipId;
        this.acquisitionEnabled = true;
        this.vehicles = new ArrayList<>();
    }

    /**
     * Only dealers with acquisition enabled should be able to add a vehicle.
     * @param vehicle The vehicle to add.
     */
    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    /**
     * Enables vehicle acquisition
     */
    public void enableAcquisition() {
        this.acquisitionEnabled = true;
    }

    /**
     * Disables vehicle acquisition
     */
    public void disableAcquisition() {
        this.acquisitionEnabled = false;
    }

    public boolean isEnabledForAcquisition() {
        return acquisitionEnabled;
    }

    // Getters & Setters from here.
    public String getDealershipId() {
        return dealershipId;
    }

    public void setDealershipId(String dealershipId) {
        this.dealershipId = dealershipId;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }
}