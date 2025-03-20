package edu.metrostate.trackingsystem.infrastructure.database;

import edu.metrostate.trackingsystem.domain.models.Dealer;
import edu.metrostate.trackingsystem.domain.models.Vehicle;
import edu.metrostate.trackingsystem.infrastructure.database.models.DealersXMLModel;
import javafx.collections.ObservableList;

import java.util.List;

public interface IDatabaseContext {
    ObservableList<Dealer> getDealers();
    Result<Boolean> deleteDealer(String dealershipId);
    List<String> getDealershipIDs();
    ObservableList<Vehicle> getVehicles();
    Result<Dealer> getDealerByID(String dealershipId);
    void toggleAcquisition(String dealershipId);
    Result<Boolean> addVehicle(Vehicle vehicle);
    Result<Boolean> deleteVehicle(String id, String dealerId);
    Vehicle getVehicle(String id, String dealerId);
    void importJson(List<Vehicle> data);
    void importXML(DealersXMLModel model);

    void toggleIsRented(Vehicle vehicle);
}
