
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
     * @return true if added, false if acquisition is disabled.
     * TODO: Check with Tim if we need to worry about duplicate ids.
     */
    public boolean addVehicle(Vehicle vehicle) {
        if (!this.acquisitionEnabled) {
            return false;
        }
        vehicles.add(vehicle);
        return true;
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

    /**
     * Serializes this class into a JSON String.
     * @return a JSON String
     */
    public String toJsonString() {
        return null; // Not implemented.
    }

    // Getters & Setters from here.
    public String getDealershipId() {
        return dealershipId;
    }

    public void setDealershipId(String dealershipId) {
        this.dealershipId = dealershipId;
    }

    public boolean getAcquisitionEnabled() {
        return acquisitionEnabled;
    }

    public void setAcquisitionEnabled(boolean acquisitionEnabled) {
        this.acquisitionEnabled = acquisitionEnabled;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }
}