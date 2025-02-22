package edu.metrostate.trackingsystem.infrastructure.database.models;

import edu.metrostate.trackingsystem.domain.models.Vehicle;

import java.util.List;

public class DealershipDatabase {
    private List<Vehicle> car_inventory;

    public DealershipDatabase(List<Vehicle> vehicles) {
        this.car_inventory = vehicles;
    }

    public List<Vehicle> getCar_inventory() {
        return car_inventory;
    }

    public void setCar_inventory(List<Vehicle> car_inventory) {
        this.car_inventory = car_inventory;
    }
}
