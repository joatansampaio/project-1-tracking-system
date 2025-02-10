package com.metrostate.projectone.infrastructure.database;

import com.metrostate.projectone.infrastructure.Result;
import com.metrostate.projectone.models.Dealer;
import com.metrostate.projectone.models.Vehicle;

import java.util.List;

public interface IDatabaseContext {
    List<Dealer> getDealers();
    List<Vehicle> getVehicles();
    Result<List<Vehicle>> getVehiclesByDealershipId(String dealershipId);
    Result<Dealer> getDealerByDealershipId(String dealershipId);
    Result<Boolean> addDealer(Dealer dealer);
    Result<Boolean> addVehicleToDealer(Vehicle vehicle, String dealershipId);
	Result<Boolean> addVehicleToDealer2(Vehicle g, String string);
}
