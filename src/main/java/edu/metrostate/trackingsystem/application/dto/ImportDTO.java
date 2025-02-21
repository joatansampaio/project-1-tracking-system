package edu.metrostate.trackingsystem.application.dto;

import com.google.gson.annotations.SerializedName;

public class ImportDTO {

    @SerializedName("dealership_id")
    private String dealershipId;

    @SerializedName("vehicle_type")
    private String vehicleType;

    @SerializedName("vehicle_manufacturer")
    private String vehicleManufacturer;

    @SerializedName("vehicle_model")
    private String vehicleModel;

    @SerializedName("vehicle_id")
    private String vehicleId;

    @SerializedName("price")
    private float price;

    @SerializedName("acquisition_date")
    private long acquisitionDate;


    public String getDealershipId() {
        return dealershipId;
    }

    public void setDealershipId(String dealershipId) {
        this.dealershipId = dealershipId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleManufacturer() {
        return vehicleManufacturer;
    }

    public void setVehicleManufacturer(String vehicleManufacturer) {
        this.vehicleManufacturer = vehicleManufacturer;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public long getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(long acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }
}