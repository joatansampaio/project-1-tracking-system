package edu.metrostate.trackingsystem.infrastructure.database;

import edu.metrostate.trackingsystem.application.dto.ImportDTO;
import edu.metrostate.trackingsystem.domain.models.Dealer;
import edu.metrostate.trackingsystem.domain.models.Vehicle;
import edu.metrostate.trackingsystem.infrastructure.database.models.DealersXMLModel;

import java.util.List;

public interface IDatabaseContext {
    List<Dealer> getDealers();

    List<String> getDealershipIDs();

    List<Vehicle> getVehicles();

    Result<Dealer> getDealerByID(String dealershipId);

    void toggleAcquisition(String dealershipId);

    Result<Boolean> addVehicle(Vehicle vehicle);

    Result<Boolean> deleteVehicle(Vehicle vehicle);

    Vehicle getVehicle(String id, String dealerId);

    void importJson(List<Vehicle> data);
    void importXML(DealersXMLModel model);
}
