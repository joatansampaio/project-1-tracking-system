package edu.metrostate.trackingsystem.application.services;

import edu.metrostate.trackingsystem.domain.models.Dealer;
import edu.metrostate.trackingsystem.domain.repositories.DealerRepository;
import edu.metrostate.trackingsystem.infrastructure.database.Result;
import javafx.collections.ObservableList;

import java.util.List;

public class DealerService {

    private final DealerRepository repository;

    public DealerService(DealerRepository repository) {
        this.repository = repository;
    }

    public List<String> getDealershipIDs() {
        return repository.getDealershipIDs();
    }

    public void toggleAcquisition(String dealershipId) {
        repository.toggleAcquisition(dealershipId);
    }

    public ObservableList<Dealer> getDealers() {
        return repository.getDealers();
    }

    public Result<Boolean> deleteDealer(String dealershipId) {
        return repository.deleteDealer(dealershipId);
    }

    public boolean updateDealer(String dealershipId, String name) { return repository.updateDealer(dealershipId, name); }



}
