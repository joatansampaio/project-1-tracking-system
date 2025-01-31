package com.metrostate.projectone.controllers;

import com.metrostate.projectone.models.Dealer;
import com.metrostate.projectone.models.Vehicle;
import com.metrostate.projectone.utils.FileHandler;
import com.metrostate.projectone.utils.IFileHandler;
import com.metrostate.projectone.utils.JsonHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DealershipController implements IDealershipController {
	private final List<Dealer> dealers;
	private final IFileHandler fileHandler = new FileHandler();

	public DealershipController() {
		this.dealers = new ArrayList<>();
	}

	@Override
	public List<Vehicle> getVehicles() {
		return dealers
			.stream()
			.flatMap(dealer -> dealer.getVehicles().stream())
			.collect(Collectors.toList());
	}

	@Override
	public List<Vehicle> getVehiclesByDealershipId(String dealershipId) {
		return List.of();
	}

	@Override
	public boolean addCar(Vehicle car, String dealershipId) {
		return false;
	}

	@Override
	public boolean removeCar(String vehicleId, String dealershipId) {
		return false;
	}

	@Override
	public boolean enableAcquisition(String dealershipId) {
		return false;
	}

	@Override
	public boolean disableAcquisition(String dealershipId) {
		Dealer dealer = findDealer(dealershipId);
		if (dealer == null) {
			return false;
		}
		dealer.disableAcquisition();
		return true;
	}

	@Override
	public boolean importJsonFile(String fileName) {
		try {
			JSONObject jsonObject = fileHandler.read("src/main/resources/" + fileName);
			if (jsonObject == null) {
				return false;
			}
			JSONArray inventory = (JSONArray) jsonObject.get("car_inventory");
			HandleInventoryObject(inventory);
			return true;
		} catch (IOException | ParseException e) {
			return false;
		}
	}

	@Override
	public boolean exportVehiclesToJson(String dealershipId) {
		List<Vehicle> dealershipVehicles = getVehiclesByDealershipId(dealershipId);
		return false;
	}

	private void HandleInventoryObject(JSONArray inventory) {
		for (Object item : inventory) {
			String dealershipId = JsonHelper.getString(item, "dealership_id");
			String vehicleType = JsonHelper.getString(item, "vehicle_type");
			String vehicleManufacturer = JsonHelper.getString(item, "vehicle_manufacturer");
			String vehicleModel = JsonHelper.getString(item, "vehicle_model");
			String vehicleId = JsonHelper.getString(item, "vehicle_id");
			Float vehiclePrice = JsonHelper.getFloat(item, "price");
			Long acquisitionDate = JsonHelper.getLong(item, "acquisition_date");

			if (dealershipId == null || vehicleId == null) {
				System.err.println("Vehicle ID & Dealership ID can't be null, skipping record...");
				continue;
			}

			Dealer existingDealer = findDealer(dealershipId);

			if (existingDealer != null) {
				if (!existingDealer.addVehicle(
						new Vehicle(vehicleId, vehicleManufacturer, vehicleModel, acquisitionDate, vehiclePrice, dealershipId, vehicleType))) {
					System.err.println("Error: Dealership is disabled for acquisition. Vehicle ID: " + vehicleId + " will not be added.");
				}
			} else {
				Dealer newDealer = new Dealer(dealershipId);
				newDealer.addVehicle(
						new Vehicle(vehicleId, vehicleManufacturer, vehicleModel, acquisitionDate, vehiclePrice, dealershipId, vehicleType));
				dealers.add(newDealer);
			}
		}
	}

	public Dealer findDealer(String dealershipId) {
		Dealer dealer = null;
		for (Dealer d : dealers) {
			if (d.getDealershipId().equals(dealershipId)) {
				dealer = d;
				break;
			}
		}
		return dealer;
	}
}