package com.metrostate.projectone.models;

import java.time.Instant;
import java.util.Map;

public class Vehicle {
    private String vehicleId;
    private String manufacturer;
    private String model;
    // TODO: Check with Tim whether vehicle should be abstract
    //  or if type as String is ok.
    private String type;
    private Instant acquisitionDate;
    private float price;
    private String dealershipId;
    /**
     * TODO: Check what exactly is 'metadata' in the context of the assignment
     */
    private Map<String, Object> metadata;

    public Vehicle(String vehicleId, String manufacturer, String model, long acquisitionDate, float price, String dealershipId, String type, Map<String, Object> metadata) {
        this.vehicleId = vehicleId;
        this.manufacturer = manufacturer;
        this.model = model;
        this.acquisitionDate = Instant.ofEpochMilli(acquisitionDate);
        this.price = price;
        this.dealershipId = dealershipId;
        this.type = type;
        this.metadata = metadata;
    }

    // Getters & Setters from here.
    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Instant getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(Instant acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDealershipId() {
        return dealershipId;
    }

    public void setDealershipId(String dealershipId) {
        this.dealershipId = dealershipId;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}