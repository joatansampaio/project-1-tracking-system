package edu.metrostate.dealership.infrastructure.database

import edu.metrostate.dealership.domain.models.Dealer
import edu.metrostate.dealership.domain.models.Vehicle
import javafx.collections.FXCollections
import javafx.collections.ObservableList

/**
 * In-memory singleton acting as a fake database
 */
class DatabaseContext private constructor()  {

    var dealers: ObservableList<Dealer> = FXCollections.observableArrayList()
    val vehicles: ObservableList<Vehicle> = FXCollections.observableArrayList()

    fun getDealershipIDs(): List<String> {
        return dealers.map { it.dealershipId.toString() }
    }

    fun deleteDealer(dealershipId: String): Result<Boolean> {
        return if (dealers.removeIf { it.dealershipId == dealershipId }) {
            Result.success()
        } else {
            Result.failure("Can't delete... dealership ID not found!")
        }
    }

    fun getDealerByID(dealershipId: String): Dealer? {
        return dealers.find { it.dealershipId == dealershipId }
    }

    fun setDealers(dealers: List<Dealer>) {
        this.dealers.setAll(dealers)
    }

    fun toggleAcquisition(dealershipId: String) {
        getDealerByID(dealershipId)?.let {
            it.enabledForAcquisition = !it.enabledForAcquisition
        }
    }

    fun updateDealer(dealershipId: String, name: String): Boolean {
        val dealer = getDealerByID(dealershipId) ?: return false
        if (!name.isNullOrBlank()) dealer.setName(name)
        return true
    }

    fun addVehicle(vehicle: Vehicle): Result<Boolean> {
        val dealer = getDealerByID(vehicle.dealershipId!!) ?: return Result.failure("Dealer ID not found...")

        if (!dealer.enabledForAcquisition) {
            return Result.failure("Dealer is not enabled for acquisition.")
        }

        val alreadyExists = dealer.getVehicles().any { it.vehicleId == vehicle.vehicleId } ||
                vehicles.any { it.vehicleId == vehicle.vehicleId }

        if (alreadyExists) {
            return Result.failure("A Vehicle with ID ${vehicle.vehicleId} already exists.")
        }

        dealer.addVehicle(vehicle)
        return Result.success()
    }

    fun deleteVehicle(id: String, dealershipId: String): Result<Boolean> {
        val dealer = getDealerByID(dealershipId) ?: return Result.failure("Dealer ID not found...")
        return if (dealer.removeVehicle(id)) {
            Result.success()
        } else {
            Result.failure("Vehicle ID not found.")
        }
    }

    fun importJSON(incomingDealers: List<Dealer?>) = importDealers(incomingDealers)

    fun importXML(incomingDealers: List<Dealer>) = importDealers(incomingDealers)

    private fun importDealers(incomingDealers: List<Dealer?>) {
        val systemVehicleIdsMap = dealers
            .flatMap { dealer -> dealer.getVehicles().map { it.vehicleId to dealer.dealershipId } }
            .toMap()
            .toMutableMap()

        for (incomingDealer in incomingDealers) {
            val dealerId = incomingDealer?.dealershipId ?: continue

            val existingDealer = dealers.find { it.dealershipId == dealerId }

            if (existingDealer != null) {
                if (existingDealer.getName().isNullOrBlank()) {
                    if (incomingDealer != null) {
                        existingDealer.setName(incomingDealer.getName())
                    }
                }

                for (incomingVehicle in incomingDealer.getVehicles()) {
                    var id = incomingVehicle.vehicleId
                    val existingDealerId = systemVehicleIdsMap[id]
                    if (existingDealerId != null && existingDealerId != dealerId) {
                        id = "${dealerId}_$id"
                        incomingVehicle.vehicleId = id
                    }
                    if (!existingDealer.getVehicles().any { it.vehicleId == id }) {
                        existingDealer.addVehicle(incomingVehicle)
                        systemVehicleIdsMap[id] = dealerId
                    }
                }

            } else {
                val uniqueVehicles = incomingDealer.getVehicles().map { vehicle ->
                    val id = vehicle.vehicleId
                    if (systemVehicleIdsMap.containsKey(id)) {
                        val newId = "${dealerId}_$id"
                        vehicle.vehicleId = newId
                        systemVehicleIdsMap[newId] = dealerId
                    } else {
                        systemVehicleIdsMap[id] = dealerId
                    }
                    vehicle
                }
                incomingDealer.setVehicles(uniqueVehicles)
                dealers.add(incomingDealer)
            }
        }
    }

    fun toggleIsRented(vehicle: Vehicle) {
        vehicle.toggleIsRented()
    }

    companion object {
        val instance: DatabaseContext by lazy { DatabaseContext() }
    }
}
