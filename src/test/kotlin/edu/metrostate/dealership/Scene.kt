package edu.metrostate.dealership

import edu.metrostate.dealership.domain.models.Dealer
import edu.metrostate.dealership.domain.models.Price
import edu.metrostate.dealership.domain.models.Vehicle
import edu.metrostate.dealership.domain.models.VehicleType.Companion.fromString
import java.io.File
import java.time.Instant
import java.util.*

class Scene {
    private val dp = Main.Companion.getDependencies()
    fun setup(): Scene {
        dp.database.dealers.clear()
        dp.database.vehicles.clear()
        vehicleDealerMap.clear()
        lastVehicleIndex = 0
        lastDealerIndex = 0
        return this
    }

    fun dealer(dealerName: String?, enabledForAcquisition: Boolean): Scene {
        val dealer = Dealer("#" + ++lastDealerIndex, dealerName ?: "Unknown", true)
        if (!enabledForAcquisition) {
            dealer.enabledForAcquisition = false
        }
        dp.database.dealers.add(dealer)
        return this
    }

    /**
     * @param vehicle dealer#1 ford f-150 pickup 50000 2/3/2025
     */
    fun vehicle(vehicle: String): Scene {
        addVehicle(vehicle)
        return this
    }

    fun addVehicle(vehicle: String, overrideLastVehicleIndex: Int): Boolean {
        lastVehicleIndex = overrideLastVehicleIndex
        return addVehicle(vehicle)
    }

    fun addVehicle(vehicle: String): Boolean {
        val vehicleId = "#" + ++lastVehicleIndex

        val parts = vehicle.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val dealershipId = parts[0].replace("dealer", "")
        val manufacturer = parts[1]
        val model = parts[2]
        val type = parts[3]
        val price = parts[4].toDouble()
        val acquisitionDate = Instant.now().toEpochMilli()

        val newVehicle = Vehicle(
            vehicleId,
            manufacturer,
            model,
            acquisitionDate,
            Price(price, "dollars"),
            dealershipId,
            fromString(type),
            false
        )
        val isSuccess = dp.vehicleService.addVehicle(newVehicle).isSuccess
        if (isSuccess) {
            vehicleDealerMap[vehicleId] = dealershipId
        }
        return isSuccess
    }

    fun deleteVehicle(vehicleId: String): Boolean {
        val effectiveVehicleId = vehicleId.replace("vehicle", "")
        return dp.vehicleService.deleteVehicle(effectiveVehicleId)
    }

    val dealers: List<Dealer>
        get() = dp.dealerService.dealers

    val vehicles: List<Vehicle>
        get() = dp.vehicleService.vehicles

    fun getDealer(dealershipId: String): Dealer? {
        return dp.database.getDealerByID(dealershipId)
    }

    fun importJson(): Boolean {
        val file = File("src/test/kotlin/jsonTest.json")
        return dp.jsonHandler.importFile(file)
    }

    fun importXml(): Boolean {
        val file = File("src/test/kotlin/xmlTest.xml")
        return dp.xmlHandler.importFile(file)
    }

    companion object {
        private val vehicleDealerMap = HashMap<String, String>()

        private var lastDealerIndex = 0
        private var lastVehicleIndex = 0
    }
}