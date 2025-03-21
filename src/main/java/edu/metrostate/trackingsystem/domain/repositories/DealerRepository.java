package edu.metrostate.trackingsystem.domain.repositories;

import edu.metrostate.trackingsystem.domain.models.Dealer;
import edu.metrostate.trackingsystem.infrastructure.database.IDatabaseContext;
import edu.metrostate.trackingsystem.infrastructure.database.Result;
import javafx.collections.ObservableList;

import java.util.List;

public class DealerRepository {

    private final IDatabaseContext databaseContext;

    public DealerRepository(IDatabaseContext databaseContext) {
        this.databaseContext = databaseContext;
    }

    public List<String> getDealershipIDs() {
        return databaseContext.getDealershipIDs();
    }

    public void toggleAcquisition(String dealershipId) {
        databaseContext.toggleAcquisition(dealershipId);
    }

    public ObservableList<Dealer> getDealers() {
        return databaseContext.getDealers();
    }

    public Result<Boolean> deleteDealer(String dealershipId) {
        return databaseContext.deleteDealer(dealershipId);
    }

    public boolean updateDealer(String dealershipId, String name) { return databaseContext.updateDealer(dealershipId, name); }

    public boolean updateDealer(Dealer dealer, String name) { return databaseContext.updateDealer(dealer, name); }

    public boolean updateDealer(Dealer dealer) { return databaseContext.updateDealer(dealer); }

}
