package com.metrostate.projectone.models;

import org.json.simple.JSONObject;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Vehicle {
	private String vehicleId;
	private String manufacturer;
	private String model;
	private String type;
	private Instant acquisitionDate;
	private Float price;
	private String dealershipId;

	public Vehicle(String vehicleId, String manufacturer, String model, Long acquisitionDate, Float price, String dealershipId, String type) {
		this.vehicleId = vehicleId;
		this.manufacturer = manufacturer;
		this.model = model;
		this.acquisitionDate = acquisitionDate != null
			? Instant.ofEpochMilli(acquisitionDate)
			: null;
		this.price = price;
		this.dealershipId = dealershipId;
		this.type = type;
	}

	// Getters & Setters from here.
	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getManufacturer() {
		if (manufacturer == null) {
			return "Unknown";
		}
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModel() {
		if (model == null) {
			return "Unknown";
		}
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

	public Float getPrice() {
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

	public String getType() {
		if (type == null) {
			return "Unknown";
		}
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFormattedAcquisitionDate() {
		if (acquisitionDate == null) {
			return "Acquisition date not specified.";
		}
		return DateTimeFormatter
			.ofPattern("MM/dd/yyyy HH:mm")
			.format(acquisitionDate.atZone(ZoneId.systemDefault()));
	}

	@Override
	public String toString() {
		return "ID: " + vehicleId + "\n" +
				"Dealership ID: " + dealershipId + "\n" +
				"Manufacturer: " + getManufacturer() + " Model: " + getModel() + "\tType: " + getType() + "\n" +
				"Price: " + (price == null ? "Unknown" : price) + "\n" +
				"Acquisition Date: " + getFormattedAcquisitionDate() + "\n";
	}

	//To suppress warning about type safety because "JSONObject extends HashMap but doesn’t support Generics." as per
	//reference: https://www.digitalocean.com/community/tutorials/json-simple-example
	@SuppressWarnings("unchecked")
    public JSONObject getJSONFormat() {

			//https://stackoverflow.com/questions/30458975/content-of-collection-never-updated-warning-in-intellij-idea
            @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") JSONObject jsonVehicle = new JSONObject();
            jsonVehicle.put("dealership_id", (this.getDealershipId()));
			jsonVehicle.put("vehicle_type", (this.getType()));
			jsonVehicle.put("vehicle_manufacturer", (this.getManufacturer()));
			jsonVehicle.put("vehicle_model", (this.getModel()));
			jsonVehicle.put("vehicle_id", (this.getVehicleId()));
			jsonVehicle.put("price", (this.getPrice()));
			jsonVehicle.put("acquisition_date", (this.getAcquisitionDate()));
			return jsonVehicle;




	}
}