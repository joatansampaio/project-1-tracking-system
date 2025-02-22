
package edu.metrostate.trackingsystem.domain.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public class Dealer {

	@JacksonXmlProperty(isAttribute = true, localName = "id")
	private String dealershipId;
	private boolean enabledForAcquisition;

	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "Vehicle")
	private List<Vehicle> vehicles;

	public Dealer() {
		this.enabledForAcquisition = true;
		this.vehicles = new ArrayList<>();
	}

	public Dealer(String dealershipId) {
		this(); // Call the default constructor.
		this.dealershipId = dealershipId;
	}

	/**
	 * Only dealers with acquisition enabled should be able to add a vehicle.
	 * 
	 * @param vehicle The vehicle to add.
	 */
	
	public void addVehicle(Vehicle vehicle) {
	vehicles.add(vehicle);
	}

	public boolean getEnabledForAcquisition() {
		return enabledForAcquisition;
	}

	public void setEnabledForAcquisition(boolean isEnabledForAcquisition) {
		this.enabledForAcquisition = isEnabledForAcquisition;
	}

	// Getters & Setters from here.
	public String getDealershipId() {
		return dealershipId;
	}

	public void setDealershipId(String dealershipId) {
		this.dealershipId = dealershipId;
	}

	public List<Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(List<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}

}