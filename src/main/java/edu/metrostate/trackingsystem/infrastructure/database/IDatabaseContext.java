package edu.metrostate.trackingsystem.infrastructure.database;

import edu.metrostate.trackingsystem.domain.models.Dealer;
import edu.metrostate.trackingsystem.domain.models.Vehicle;
import javafx.collections.ObservableList;

import java.util.List;

public interface IDatabaseContext {
    ObservableList<Dealer> getDealers();
    ObservableList<Vehicle> getVehicles();

    List<String> getDealershipIDs();
    Dealer getDealerByID(String dealershipId);
    Result<Boolean> deleteDealer(String dealershipId);
    Result<Boolean> addVehicle(Vehicle vehicle);
    Result<Boolean> deleteVehicle(String id, String dealerId);
    boolean updateDealer(String dealershipId, String name);
    void toggleAcquisition(String dealershipId);
    void toggleIsRented(Vehicle vehicle);

    void importJSON(List<Dealer> data);
    void importXML(List<Dealer> data);



}
