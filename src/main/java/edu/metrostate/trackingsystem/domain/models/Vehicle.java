package edu.metrostate.trackingsystem.domain.models;

import com.google.gson.annotations.SerializedName;
import edu.metrostate.trackingsystem.application.exceptions.ValidationException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Vehicle {

	/**
	 * Unless phase 2 requires specific actions for each vehicle in which case we would
	 * need to abstract classes, this is enough.
	 */
	private static final List<String> VALID_VEHICLE_TYPES = List.of("sedan", "sports car", "suv", "pickup");

	@SerializedName("vehicle_id") private String vehicleId;
	@SerializedName("vehicle_manufacturer")	private String manufacturer;
	@SerializedName("vehicle_model") private String model;
	@SerializedName("vehicle_type")	private String type;
	@SerializedName("acquisition_date")	private long acquisitionDate;
	@SerializedName("price") private double price;
	@SerializedName("dealership_id") private String dealershipId;

	public Vehicle(String vehicleId, String manufacturer, String model, long acquisitionDate,
				   double price, String dealershipId, String type) {

		this.vehicleId = vehicleId;
		this.manufacturer = manufacturer;
		this.model = model;
		this.acquisitionDate = acquisitionDate;
		this.price = price;
		this.dealershipId = dealershipId;
		this.type = type;
	}

	/**
	 * Creates a new Vehicle instance after validating the provided attributes.
	 * If any of the input parameters fail validation, a {@link ValidationException} is thrown.
	 *
	 * @param vehicleId The unique identifier for the vehicle.
	 * @param manufacturer The manufacturer of the vehicle.
	 * @param model The model name of the vehicle.
	 * @param priceText The price of the vehicle as a string (to be parsed as a decimal value).
	 * @param acquisitionDate The acquisition date of the vehicle.
	 * @param dealershipId The identifier of the dealership where the vehicle is located.
	 * @param vehicleType The type of the vehicle.
	 * @return A {@link Vehicle} instance with the provided and validated attributes.
	 * @throws ValidationException If any validation errors occur with the input parameters.
	 */
	public static Vehicle createVehicle(String vehicleId, String manufacturer, String model,
										String priceText, LocalDate acquisitionDate,
										String dealershipId, String vehicleType) throws ValidationException {

		List<String> errors = new ArrayList<>();

		if (vehicleId == null || vehicleId.trim().isEmpty()) {
			errors.add("Vehicle ID is required.");
		}
		if (manufacturer == null || manufacturer.trim().isEmpty()) {
			errors.add("Manufacturer is required.");
		}
		if (model == null || model.trim().isEmpty()) {
			errors.add("Model is required.");
		}
		if (priceText == null || priceText.trim().isEmpty()) {
			errors.add("Price is required.");
		}

		double price = -1;
		if (priceText != null && !priceText.trim().isEmpty()) {
			try {
				price = Double.parseDouble(priceText);
				if (price < 0) {
					errors.add("Price must be a positive number.");
				}
			} catch (NumberFormatException e) {
				errors.add("Invalid price format.");
			}
		}

		if (acquisitionDate == null) {
			errors.add("Acquisition date is required.");
		}
		if (dealershipId == null || dealershipId.trim().isEmpty()) {
			errors.add("Dealership ID is required.");
		}
		if (vehicleType == null || vehicleType.trim().isEmpty()) {
			errors.add("Vehicle type is required.");
		} else if (!VALID_VEHICLE_TYPES.contains(vehicleType.toLowerCase())) {
			errors.add("Invalid vehicle type.");
		}

		if (!errors.isEmpty()) {
			throw new ValidationException(errors);
		}

		long epochMillis = acquisitionDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return new Vehicle(vehicleId, manufacturer, model, epochMillis, price, dealershipId, vehicleType);
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getVehicleId() {
		return vehicleId;
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

	public long getAcquisitionDate() {
		return acquisitionDate;
	}

	public void setAcquisitionDate(long acquisitionDate) {
		this.acquisitionDate = acquisitionDate;
	}

	public double getPrice() {
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
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFormattedAcquisitionDate() {
		return DateTimeFormatter
			.ofPattern("MM/dd/yyyy HH:mm")
			.format(Instant.ofEpochMilli(acquisitionDate).atZone(ZoneId.systemDefault()));
	}

	@Override
	public String toString() {
		return "ID: " + vehicleId + "\n" +
				"Dealership ID: " + dealershipId + "\n" +
				"Manufacturer: " + getManufacturer() + " Model: " + getModel() + "\tType: " + getType() + "\n" +
				"Price: " + price + "\n" +
				"Acquisition Date: " + getFormattedAcquisitionDate() + "\n";
	}
}