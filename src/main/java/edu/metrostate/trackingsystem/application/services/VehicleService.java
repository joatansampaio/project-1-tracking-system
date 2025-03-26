//This class is used for transferring data through application layers from the database
package edu.metrostate.trackingsystem.application.services;

import edu.metrostate.trackingsystem.domain.models.Vehicle;
import edu.metrostate.trackingsystem.domain.repositories.VehicleRepository;
import edu.metrostate.trackingsystem.infrastructure.database.Result;
import javafx.collections.ObservableList;

public class VehicleService {

    private final VehicleRepository repository;

    public VehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    public Result<Boolean> deleteVehicle(String id, String dealerId) {
        return repository.deleteVehicle(id, dealerId);
    }

    public Result<Boolean> addVehicle(Vehicle vehicle) {
        return repository.addVehicle(vehicle);
    }

    public ObservableList<Vehicle> getVehicles() {
        return repository.getVehicles();
    }

    public void toggleIsRented(Vehicle vehicle) { repository.toggleIsRented(vehicle);}
}
