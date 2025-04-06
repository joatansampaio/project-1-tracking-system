//Used for transferring data between application layers from the database (domain layer) to the services layer
package edu.metrostate.dealership.domain.repositories

import edu.metrostate.dealership.domain.models.Vehicle
import edu.metrostate.dealership.infrastructure.database.Database
import edu.metrostate.dealership.infrastructure.database.Result
import javafx.collections.ObservableList

class VehicleRepository(private val databaseContext: Database) {
    fun deleteVehicle(id: String) {
        databaseContext.deleteVehicle(id)
    }

    fun addVehicle(vehicle: Vehicle?): Result<Boolean>? {
        return vehicle?.let { databaseContext.addVehicle(it) }
    }

    val vehicles: ObservableList<Vehicle>
        get() = databaseContext.vehicles

    fun toggleIsRented(vehicleId: String) {
            databaseContext.toggleIsRented(vehicleId)
    }
}