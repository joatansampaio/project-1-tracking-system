//This class is used for transferring data through application layers from the database
package edu.metrostate.dealership.application.services

import edu.metrostate.dealership.domain.models.Vehicle
import edu.metrostate.dealership.domain.repositories.VehicleRepository
import edu.metrostate.dealership.infrastructure.database.Result
import javafx.collections.ObservableList

class VehicleService(private val repository: VehicleRepository) {
    fun deleteVehicle(id: String): Boolean {
       return repository.deleteVehicle(id)
    }

    fun addVehicle(vehicle: Vehicle): Result<Boolean> {
        return repository.addVehicle(vehicle)
    }

    val vehicles: ObservableList<Vehicle>
        get() = repository.vehicles

    fun toggleIsRented(vehicleId: String) {
        repository.toggleIsRented(vehicleId)
    }
}