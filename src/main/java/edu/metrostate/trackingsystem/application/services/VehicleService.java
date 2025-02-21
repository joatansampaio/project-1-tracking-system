package edu.metrostate.trackingsystem.application.services;

import edu.metrostate.trackingsystem.domain.models.Vehicle;
import edu.metrostate.trackingsystem.domain.repositories.VehicleRepository;
import edu.metrostate.trackingsystem.infrastructure.database.Result;

import java.util.List;

public class VehicleService {

    private final VehicleRepository repository;

    public VehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    public Result<Boolean> deleteVehicle(Vehicle vehicle) {
        return repository.deleteVehicle(vehicle);
    }

    public Result<Boolean> addVehicle(Vehicle vehicle) {
        return repository.addVehicle(vehicle);
    }

    public List<Vehicle> getVehicles() {
        return repository.getVehicles();
    }
}
