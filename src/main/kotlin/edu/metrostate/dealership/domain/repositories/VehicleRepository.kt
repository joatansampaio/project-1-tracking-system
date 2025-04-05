//Used for transferring data between application layers from the database (domain layer) to the services layer
package edu.metrostate.dealership.domain.repositories

import edu.metrostate.dealership.domain.models.Vehicle
import edu.metrostate.dealership.infrastructure.database.DatabaseContext
import edu.metrostate.dealership.infrastructure.database.Result
import javafx.collections.ObservableList

class VehicleRepository(private val databaseContext: DatabaseContext) {
    fun deleteVehicle(id: String?, dealerId: String?): Unit? {
        return dealerId?.let {
            if (id != null) {
                databaseContext.deleteVehicle(id, it)
            }
        }
    }

    fun addVehicle(vehicle: Vehicle?): Result<Boolean>? {
        return vehicle?.let { databaseContext.addVehicle(it) }
    }

    val vehicles: ObservableList<Vehicle>
        get() = databaseContext.vehicles

    fun toggleIsRented(vehicle: Vehicle?) {
        if (vehicle != null) {
            databaseContext.toggleIsRented(vehicle)
        }
    }
}