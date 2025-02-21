package edu.metrostate.trackingsystem.domain.repositories;

import edu.metrostate.trackingsystem.domain.models.Dealer;
import edu.metrostate.trackingsystem.infrastructure.database.IDatabaseContext;

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

    public List<Dealer> getDealers() {
        return databaseContext.getDealers();
    }
}
