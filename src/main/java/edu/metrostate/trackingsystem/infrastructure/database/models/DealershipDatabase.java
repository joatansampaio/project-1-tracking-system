package edu.metrostate.trackingsystem.infrastructure.database.models;

import edu.metrostate.trackingsystem.domain.models.Dealer;
import edu.metrostate.trackingsystem.domain.models.Vehicle;

import java.util.List;

public class DealershipDatabase {
    private List<Dealer> database;

    public DealershipDatabase(List<Dealer> dealers) {
        this.database = dealers;
    }

    public List<Dealer> getDatabase() {
        return database;
    }

    public void setDatabase(List<Dealer> database) {
        this.database = database;
    }
}
