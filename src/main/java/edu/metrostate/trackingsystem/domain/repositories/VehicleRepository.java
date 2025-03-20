package edu.metrostate.trackingsystem.domain.repositories;

import edu.metrostate.trackingsystem.domain.models.Vehicle;
import edu.metrostate.trackingsystem.infrastructure.database.IDatabaseContext;
import edu.metrostate.trackingsystem.infrastructure.database.Result;
import javafx.collections.ObservableList;

import java.util.List;

public class VehicleRepository {

    private final IDatabaseContext databaseContext;

    public VehicleRepository(IDatabaseContext databaseContext) {
        this.databaseContext = databaseContext;
    }

    public Result<Boolean> deleteVehicle(String id, String dealerId) {
        return databaseContext.deleteVehicle(id, dealerId);
    }

    public Result<Boolean> addVehicle(Vehicle vehicle) {
        return databaseContext.addVehicle(vehicle);
    }

    public ObservableList<Vehicle> getVehicles() {
        return databaseContext.getVehicles();
    }

    public void toggleIsRented(Vehicle vehicle) { databaseContext.toggleIsRented(vehicle);}
}
