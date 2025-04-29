//This class is used for transferring data through application layers from the database
package edu.metrostate.dealership.application.services

import edu.metrostate.dealership.domain.models.Dealer
import edu.metrostate.dealership.domain.repositories.DealerRepository
import edu.metrostate.dealership.infrastructure.database.Result
import javafx.collections.ObservableList

class DealerService(private val repository: DealerRepository) {
    val dealershipIDs: List<String>
        get() = repository.dealershipIDs

    fun toggleAcquisition(dealershipId: String?) {
        repository.toggleAcquisition(dealershipId)
    }

    fun transferInventory() {}
    val dealers: ObservableList<Dealer>
        get() = repository.dealers

    fun deleteDealer(dealershipId: String?): Result<Boolean>? {
        return repository.deleteDealer(dealershipId)
    }

    fun updateDealer(dealershipId: String?, name: String?): Unit? {
        return repository.updateDealer(dealershipId, name)
    }


    /**
     * Adds a new dealer to the system.
     * Validates that the dealer ID doesn't already exist.
     *
     * @param dealer The dealer object to add
     * @return The added dealer
     * @throws IllegalArgumentException If a dealer with the same ID already exists
     */
    fun addDealer(dealer: Dealer): Dealer {
        if (dealershipIDs.contains(dealer.dealershipId)) {
            throw IllegalArgumentException("A dealer with ID ${dealer.dealershipId} already exists.")
        }

        repository.dealers.add(dealer)
        return dealer
    }
}