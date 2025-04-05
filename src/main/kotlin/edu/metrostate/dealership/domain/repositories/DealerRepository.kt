//Used for transferring data between application layers from the database (domain layer) to the services layer
package edu.metrostate.dealership.domain.repositories

import edu.metrostate.dealership.domain.models.Dealer
import edu.metrostate.dealership.infrastructure.database.DatabaseContext
import edu.metrostate.dealership.infrastructure.database.Result
import javafx.collections.ObservableList

class DealerRepository(private val databaseContext: DatabaseContext) {
    val dealershipIDs: List<String?>?
        get() = databaseContext.getDealershipIDs()

    fun toggleAcquisition(dealershipId: String?) {
        if (dealershipId != null) {
            databaseContext.toggleAcquisition(dealershipId)
        }
    }

    val dealers: ObservableList<Dealer>
        get() = databaseContext.dealers

    fun deleteDealer(dealershipId: String?): Result<Boolean>? {
        return dealershipId?.let { databaseContext.deleteDealer(it) }
    }

    fun updateDealer(dealershipId: String?, name: String?): Unit? {
        return dealershipId?.let {
            if (name != null) {
                databaseContext.updateDealer(it, name)
            }
        }
    }
}