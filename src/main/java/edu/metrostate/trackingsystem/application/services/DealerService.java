package edu.metrostate.trackingsystem.application.services;

import edu.metrostate.trackingsystem.domain.models.Dealer;
import edu.metrostate.trackingsystem.domain.repositories.DealerRepository;

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

    public List<Dealer> getDealers() {
        return repository.getDealers();
    }

    public boolean UpdateDealer(Dealer dealer) { return true; }
}
