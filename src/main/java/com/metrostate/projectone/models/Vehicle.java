package com.metrostate.projectone.models;

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
}