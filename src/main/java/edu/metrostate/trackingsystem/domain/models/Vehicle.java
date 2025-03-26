package edu.metrostate.trackingsystem.domain.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import edu.metrostate.trackingsystem.application.exceptions.ValidationException;
import edu.metrostate.trackingsystem.infrastructure.utils.PriceDeserializer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Vehicle {

	/**
	 * Unless phase 2 requires specific actions for each vehicle in which case we would
	 * need to abstract classes, this is enough.
	 */
	private static final List<String> VALID_VEHICLE_TYPES = List.of("sedan", "sports car", "suv", "pickup");

	@JacksonXmlProperty(isAttribute = true, localName = "id")
	@SerializedName("vehicle_id")
	private String vehicleId;

	@JacksonXmlProperty(localName = "Make")
	@SerializedName("vehicle_manufacturer")
	private String manufacturer;

	@JacksonXmlProperty(localName = "Model")
	@SerializedName("vehicle_model")
	private String model;

	@JacksonXmlProperty(isAttribute = true, localName = "type")
	@SerializedName("vehicle_type")
	private String type;

	@JacksonXmlProperty(localName = "AcquisitionDate")
	@SerializedName("acquisition_date")
	private Long acquisitionDate;

	@JacksonXmlProperty(localName = "Price")
	@JsonAdapter(PriceDeserializer.class)
	private Price price;

	@SerializedName("dealership_id")
	private String dealershipId;

	@SerializedName("dealership_name")
	private String dealershipName;

	@SerializedName("is_rented")
	private boolean isRented = false;

	public Vehicle() { }

	public Vehicle(String vehicleId, String manufacturer, String model, Long acquisitionDate,
				   Price price, String dealershipId, String type) {

		this.vehicleId = vehicleId;
		this.manufacturer = manufacturer;
		this.model = model;
		this.acquisitionDate = acquisitionDate;
		this.price = price;
		this.dealershipId = dealershipId;
		this.type = type.toUpperCase();
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

		Long epochMillis = acquisitionDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return new Vehicle(vehicleId, manufacturer, model, epochMillis, new Price(price, "dollars"), dealershipId, vehicleType.toUpperCase());
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
		return acquisitionDate != null ? acquisitionDate : 0L;
	}


	public void setAcquisitionDate(Long acquisitionDate) {
		this.acquisitionDate = acquisitionDate;
	}

	public Price getPrice() {
		return price;
	}

	public String getPriceAsString() {
		return price != null ? price.toString() : "Unknown";
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	public String getDealershipId() {
		return dealershipId;
	}

	public void setDealershipId(String dealershipId) {
		this.dealershipId = dealershipId;
	}

	public String getDealershipName() {
		return dealershipName;
	}

	public void setDealershipName(String dealershipName) {
		this.dealershipName = dealershipName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type.toUpperCase();
	}

	public boolean getIsRented() { return isRented;}

	public void setIsRented(boolean rented) {
		if (type.equals("SPORTS CAR") && rented) {
			return;
		}
		this.isRented = rented;
	}

	public void toggleIsRented(){
		if(this.getType().equalsIgnoreCase("sports car")){
			isRented = false;
			System.out.println("bad");
		}
		else{
			isRented = !isRented;
		}
	}

	public String getIsRentedAsString() {
		if(isRented){
			return "Yes";
		}
		else{
			return "No";
		}
	}

	public String getFormattedAcquisitionDate() {
		if (acquisitionDate == null) {
			return "Unknown";
		}
		return DateTimeFormatter
			.ofPattern("MM/dd/yyyy HH:mm")
			.format(Instant.ofEpochMilli(acquisitionDate).atZone(ZoneId.systemDefault()));
	}

	// This is used when in the import process
	// We want to ensure consistency of UpperCase types
	// maybe an enum would be a better choice.
	// We can always probably change that later.
	public void normalize() {
		setType(type.toUpperCase());
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