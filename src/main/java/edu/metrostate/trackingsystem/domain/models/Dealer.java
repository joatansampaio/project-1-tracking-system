// This is the Dealer object used to store vehicles
package edu.metrostate.trackingsystem.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Dealer {

	@JacksonXmlProperty(isAttribute = true, localName = "id")
	private String dealershipId;
	private boolean enabledForAcquisition;

	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "Vehicle")
	private List<Vehicle> vehicles;

	@JacksonXmlProperty(localName = "Name")
	private String name;

	@JsonIgnore  // Ignore this field during serialization
	private final ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList();

	public Dealer() {
		this.enabledForAcquisition = true;
		this.vehicles = FXCollections.observableArrayList();
	}

	public Dealer(String dealershipId) {
		this(); // Call the default constructor.
		this.dealershipId = dealershipId;
	}

	public Dealer(String dealershipId, String name) {
		this();
		this.dealershipId = dealershipId;
		this.name = name;
	}

	public Dealer(String dealershipId, List<Vehicle> vehicles, String name) {
		this();
		setVehicles(vehicles);
		this.dealershipId = dealershipId;
		this.name = name;
	}

	/**
	 * Only dealers with acquisition enabled should be able to add a vehicle.
	 * 
	 * @param vehicle The vehicle to add.
	 */
	
	public boolean addVehicle(Vehicle vehicle) {
		var exists = vehicles.stream().anyMatch(v -> v.getVehicleId().equals(vehicle.getVehicleId()));
		vehicle.setType(vehicle.getType()); // To maintain upper-case consistency
		if (!exists) {
			vehicles.add(vehicle);
			vehicleList.add(vehicle); // Keep both lists in sync
			return true;
		}
		return false;
	}

	public boolean removeVehicle(String id) {
		vehicleList.removeIf(v -> v.getVehicleId().equals(id));
		return vehicles.removeIf(v -> v.getVehicleId().equals(id));
	}

	public boolean getEnabledForAcquisition() {
		return enabledForAcquisition;
	}

	public void setEnabledForAcquisition(boolean isEnabledForAcquisition) {
		this.enabledForAcquisition = isEnabledForAcquisition;
	}

	public String getDealershipId() {
		return dealershipId;
	}

	public void setDealershipId(String dealershipId) {
		this.dealershipId = dealershipId;
	}

	public List<Vehicle> getVehicles() {
		return vehicleList;
	}

	@JsonIgnore  // Ignore during serialization
	public ObservableList<Vehicle> getObservableVehicles() {
		return vehicleList;
	}

	public void setVehicles(List<Vehicle> vehicles) {
		this.vehicles = vehicles;
		this.vehicleList.setAll(vehicles); // Convert List to ObservableList
	}

	public String getName() {
		return name != null ? name : "Not Configured";
	}

	public void setName(String name) {
		this.name = name;
	}
}