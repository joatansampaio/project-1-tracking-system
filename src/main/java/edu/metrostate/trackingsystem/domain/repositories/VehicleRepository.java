package edu.metrostate.trackingsystem.domain.repositories;

import edu.metrostate.trackingsystem.domain.models.Vehicle;
import edu.metrostate.trackingsystem.infrastructure.database.IDatabaseContext;
import edu.metrostate.trackingsystem.infrastructure.database.Result;

import java.util.List;

public class VehicleRepository {

    private final IDatabaseContext databaseContext;

    public VehicleRepository(IDatabaseContext databaseContext) {
        this.databaseContext = databaseContext;
    }

    public Result<Boolean> deleteVehicle(Vehicle vehicle) {
        return databaseContext.deleteVehicle(vehicle);
    }

    public Result<Boolean> addVehicle(Vehicle vehicle) {
        return databaseContext.addVehicle(vehicle);
    }

    public List<Vehicle> getVehicles() {
        return databaseContext.getVehicles();
    }
}
