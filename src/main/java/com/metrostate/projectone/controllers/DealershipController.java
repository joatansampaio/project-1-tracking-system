package com.metrostate.projectone.controllers;

import com.metrostate.projectone.infrastructure.database.DatabaseContext;
import com.metrostate.projectone.infrastructure.database.IDatabaseContext;
import com.metrostate.projectone.models.Dealer;
import com.metrostate.projectone.models.Vehicle;
import com.metrostate.projectone.utils.FileHandler;
import com.metrostate.projectone.utils.IFileHandler;
import com.metrostate.projectone.utils.JsonHelper;
import com.metrostate.projectone.utils.Printer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DealershipController implements IDealershipController {

	/*
	 * For the purpose of Phase 1, this is enough. If we ever need particular
	 * behaviors based on vehicle type, we'll probably need to abstract their
	 * classes and extend Vehicle.
	 */
	private final List<String> VALID_VEHICLE_TYPES = List.of("sports car", "pickup", "sedan", "suv");

	// TODO: [Not-required] This could also be a singleton
	private final IFileHandler fileHandler = new FileHandler();
	// This is an in-memory fake database session.
	private final IDatabaseContext db;

	public DealershipController() {
		this.db = DatabaseContext.getInstance();
	}

	/**
	 * Prints all vehicles in current JSON file.
	 */
	@Override
	public void printAllVehicles() {
		var vehicles = db.getVehicles();
		if (vehicles.isEmpty()) {
			Printer.println("No vehicles registered yet.");
			return;
		}
		for (Vehicle v : vehicles) {
			Printer.println(v.toString());
		}
	}

	/**
	 * Prints a list of dealers in the current JSON file.
	 * Returns a boolean so that {@link com.metrostate.projectone.Main#listVehiclesByDealershipId() ListVehiclesByDealershipID} doesn't get stuck.
	 * @return False if no dealers loaded to memory, True otherwise
	 */
	@Override
	public boolean printAllDealers() {
		var dealers = db.getDealers();
		if (dealers.isEmpty()){
			Printer.println("No dealers registered yet.");
			return false;
		}
		else{
			Printer.println("Current Dealers:\n");
			for(Dealer d : dealers){
				Printer.println(d.toString());
			}
			return true;
		}
	}

	/**
	 * Determines if a given string matches with a dealershipID of a dealer in memory
	 * @param dealershipID The string to be compared against the dealershipID of dealers in memory
	 * @return True if a match is found, False if not
	 */
	@Override
	public boolean isValidDealershipID(String dealershipID) {
		var dealers = db.getDealers();
		for(Dealer d : dealers){
			if(d.getDealershipId().equals(dealershipID)){
				return true;
			}
		}
		return false;
	}
	/**
	 * Prints all vehicles held by a particular dealer.
	 * @param dealershipId the dealer ID
	 */
	@Override
	public void printVehiclesForDealershipId(String dealershipId) {
		var result = db.getDealerByDealershipId(dealershipId);
		if (!result.IsSuccess()) {
			Printer.println(result.getErrorMessage(), Printer.Color.RED);
		}

		var vehicles = result.getData().getVehicles();
		if (vehicles.isEmpty()) {
			Printer.println("No vehicles registered yet.");
			return;
		}
		for (Vehicle v : vehicles) {
			Printer.println(v.toString());
		}
	}

	// TODO: [Required] Implement the incoming vehicle operation
	// use db.AddVehicleToDealer()

	/**
	 * Adds a given vehicle to a given dealer.
	 * @param vehicle The vehicle to be added
	 * @param dealershipId The ID of the dealer to which the vehicle is added
	 */
	@Override
	public void addVehicle(Vehicle vehicle, String dealershipId) {
		db.addVehicleToDealer(vehicle, dealershipId);
	}

	/**
	 * Sets whether a dealer can receive new vehicles
	 * @param isEnabled whether the dealer can receive new vehicles
	 * @param dealershipId the ID of the dealer to which isEnabled is set
	 */
	@Override
	public void setDealerAcquisition(boolean isEnabled, String dealershipId) {
		var result = db.getDealerByDealershipId(dealershipId);
		if (!result.IsSuccess()) {
			Printer.println(result.getErrorMessage(), Printer.Color.RED);
			return;
		}

		var dealer = result.getData();
		if (isEnabled) {
			dealer.enableAcquisition();
		} else {
			dealer.disableAcquisition();
		}
		Printer.println("Acquisition changed successfully");
	}

	/**
	 * Retrieves list of JSON files in src/main/resources and prompts user to choose 1 to be imported
	 * @param scan Java scanner for user input
	 */
	@Override
	public void importJsonFile(Scanner scan) {
		Printer.println("\nAvailable JSON Files: ");
		File folder = new File("src/main/resources");
		File[] jsonFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
		if (jsonFiles == null || jsonFiles.length == 0) {
			Printer.println("No JSON files were found in the resources folder.", Printer.Color.RED);
			return;
		}

		int counter = 1;
		for (File file : jsonFiles) {
			Printer.println(counter + " - " + file.getName(), Printer.Color.YELLOW);
			counter++;
		}

		Printer.print("Select a file number to import: ");
		int fileNumber;
		try {
			int option = scan.nextInt();
			if (option <= 0 || option > jsonFiles.length) {
				Printer.println("Invalid file number, try again...", Printer.Color.RED);
				scan.nextLine();
				return;
			}
			fileNumber = option;
		} catch (InputMismatchException e) {
			Printer.println("Invalid file number, try again...", Printer.Color.RED);
			scan.nextLine();
			return;
		}

		try {
			JSONObject jsonObject = fileHandler.read("src/main/resources/" + jsonFiles[fileNumber - 1].getName());
			if (jsonObject == null) {
				Printer.println("Error loading file", Printer.Color.RED);
				return;
			}
			JSONArray inventory = (JSONArray) jsonObject.get("car_inventory");
			HandleInventoryObject(inventory);
		} catch (IOException | ParseException e) {
			Printer.println("Error loading file", Printer.Color.RED);
		}
		Printer.println("File loaded", Printer.Color.YELLOW);
		scan.nextLine();
	}

	// To suppress warning about type safety because "JSONObject extends HashMap but
	// doesn’t support Generics." as per
	// reference:
	// https://www.digitalocean.com/community/tutorials/json-simple-example
	// @SuppressWarnings("unchecked")
	@Override
	public void exportVehiclesToJson(Scanner scan) {
		var result = db.getVehiclesByDealershipId(null);
		if (!result.IsSuccess()) {
			Printer.println(result.getErrorMessage(), Printer.Color.RED);
			return;
		}

		var dealershipVehicles = result.getData();
		for (Vehicle vehicle : dealershipVehicles) {
			JSONObject jsonFormatVehicle = vehicle.getJSONFormat();
			fileHandler.writeVehicleToJson(jsonFormatVehicle, vehicle.getDealershipId(), vehicle.getVehicleId());
		}
	}

	// TODO: Needs review and testing after getVehiclesByDealershipId() gets
	// implemented
	// To suppress warning about type safety because "JSONObject extends HashMap but
	// doesn’t support Generics." as per
	// reference:
	// https://www.digitalocean.com/community/tutorials/json-simple-example
	@SuppressWarnings("unchecked")
	@Override
	public void exportDealerToJson(Scanner scan) {
		Printer.println("Enter the dealership ID for the dealer that you want to export:");
		String dealershipId = scan.next();
		var result = db.getDealerByDealershipId(dealershipId);
		if (!result.IsSuccess()) {
			Printer.println(result.getErrorMessage(), Printer.Color.RED);
			return;
		}

		var dealer = result.getData();
		scan.nextLine();

		var dealershipVehicles = dealer.getVehicles();

		JSONObject outputObj = new JSONObject();
		JSONArray carInventory = new JSONArray();

		for (Vehicle vehicle : dealershipVehicles) {
			JSONObject vehicleObj = new JSONObject();
			vehicleObj = vehicle.getJSONFormat();
			carInventory.add(vehicleObj);
		}

		outputObj.put("car_inventory", carInventory);
		if (fileHandler.writeDealerToJson(outputObj, dealershipId)) {
			Printer.println("Dealer " + dealershipId + " successfully exported to JSON");
		} else {
			Printer.println("Dealer " + dealershipId + " couldn't be exported", Printer.Color.RED);
		}
	}

	/**
	 * Initializes dealership information from given JSONArray
	 * @param inventory JSON array being imported
	 */
	private void HandleInventoryObject(JSONArray inventory) {
		for (Object item : inventory) {
			String dealershipId = JsonHelper.getString(item, "dealership_id");
			String vehicleType = JsonHelper.getString(item, "vehicle_type");
			String vehicleManufacturer = JsonHelper.getString(item, "vehicle_manufacturer");
			String vehicleModel = JsonHelper.getString(item, "vehicle_model");
			String vehicleId = JsonHelper.getString(item, "vehicle_id");
			Float vehiclePrice = JsonHelper.getFloat(item, "price");
			Long acquisitionDate = JsonHelper.getLong(item, "acquisition_date");

			// IDs are required.
			if (dealershipId == null || vehicleId == null) {
				Printer.println("Vehicle ID & Dealership ID can't be null, skipping record...", Printer.Color.RED);
				continue;
			}

			// Checking for invalid types.
			if (!VALID_VEHICLE_TYPES.contains(vehicleType)) {
				Printer.println("Vehicle Type " + vehicleType + " is not valid. skipping record...", Printer.Color.RED);
				continue;
			}

			var getDealerResult = db.getDealerByDealershipId(dealershipId);
			if (getDealerResult.IsSuccess()) {
				var addVehicleResult = db.addVehicleToDealer(new Vehicle(vehicleId, vehicleManufacturer, vehicleModel,
						acquisitionDate, vehiclePrice, dealershipId, vehicleType), dealershipId);
				if (!addVehicleResult.IsSuccess()) {
					Printer.println(addVehicleResult.getErrorMessage(), Printer.Color.RED);
				}
			} else {
				Dealer newDealer = new Dealer(dealershipId);
				newDealer.addVehicle(new Vehicle(vehicleId, vehicleManufacturer, vehicleModel, acquisitionDate,
						vehiclePrice, dealershipId, vehicleType));
				var addDealerResult = db.addDealer(newDealer);
				if (!addDealerResult.IsSuccess()) {
					Printer.println(addDealerResult.getErrorMessage(), Printer.Color.RED);
				}
			}
		}
	}
}