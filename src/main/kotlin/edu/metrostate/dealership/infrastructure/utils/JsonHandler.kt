//Handles reading of a JSON formatted data file
package edu.metrostate.dealership.infrastructure.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import edu.metrostate.dealership.domain.models.Dealer
import edu.metrostate.dealership.domain.models.Vehicle
import edu.metrostate.dealership.infrastructure.database.Database
import edu.metrostate.dealership.infrastructure.imports.mappers.toDomainDealer
import edu.metrostate.dealership.infrastructure.imports.mappers.toDomainVehicle
import edu.metrostate.dealership.infrastructure.imports.mappers.toJsonVehicle
import edu.metrostate.dealership.infrastructure.imports.models.DatabaseWrapper
import edu.metrostate.dealership.infrastructure.imports.models.json.DealerJson
import edu.metrostate.dealership.infrastructure.imports.models.json.VehicleJson
import edu.metrostate.dealership.infrastructure.logging.Logger
import edu.metrostate.dealership.infrastructure.utils.GsonConfig.gson
import javafx.collections.ObservableList
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.util.*

class JsonHandler private constructor() : IFileHandler {

    override fun importFile(file: File): Boolean {
        return try {
            val json = Files.readString(file.toPath())
            val jsonObject = JsonParser.parseString(json).asJsonObject

            // Deserialize car_inventory array
            val listType = object : TypeToken<List<VehicleJson>>() {}.type
            val vehicles: List<VehicleJson> = gson.fromJson(jsonObject["car_inventory"], listType)

            // Group by dealershipId and create DealerJson
            val dealerJsonList = vehicles
                .groupBy { it.dealershipId }
                .map { (dealershipId, dealerVehicles) ->
                    DealerJson(
                        dealershipId = dealershipId,
                        name = "Unknown",
                        enabledForAcquisition = true,
                        vehicles = dealerVehicles
                    )
                }

            databaseContext.importJSON(dealerJsonList)

            val dealers = dealerJsonList.map { it.toDomainDealer() }
            val domainVehicles = dealerJsonList.flatMap { it.vehicles.map { v -> v.toDomainVehicle() } }
            logger.info("Imported ${dealers.size} dealers and ${domainVehicles.size} vehicles.")
            true
        } catch (e: Exception) {
            logger.error("Failed to import file: ${e.message}")
            false
        }
    }

    override fun exportFile(file: File): Boolean {
        var file = file
        val json = extractCurrentStateAsJson()
        val path = file.absolutePath

        if (!path.lowercase(Locale.getDefault()).endsWith(".json")) {
            file = File("$path.json")
        }

        try {
            FileWriter(file).use { writer ->
                writer.write(json)
                logger.info("Successfully exported to " + file.absolutePath)
                return true
            }
        } catch (e: IOException) {
            logger.error("Error exporting JSON: " + e.message)
            return false
        }
    }

    /**
     * The following methods are internal processes to auto-save & load the database.json
     */
    fun saveSession() {
        val json = extractCurrentStateAsJson()

        val file = File("database.json")

        try {
            FileWriter(file).use { writer ->
                writer.write(json)
                logger.info("Database JSON exported successfully.")
            }
        } catch (e: IOException) {
            logger.error(e.message!!)
            System.exit(0)
        }
    }

    private fun extractCurrentStateAsJson(): String {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()

        val dealers: List<Dealer> = databaseContext.dealers
        val vehicles: List<Vehicle> = databaseContext.vehicles

        // Group vehicles by dealershipId
        val groupedVehicles = vehicles.groupBy { it.dealershipId }

        // Build DealerJson list
        val dealerJsonList = dealers.map { dealer ->
            DealerJson(
                dealershipId = dealer.dealershipId,
                name = dealer.getName(),
                enabledForAcquisition = dealer.enabledForAcquisition,
                vehicles = groupedVehicles[dealer.dealershipId].orEmpty().map { it.toJsonVehicle() }
            )
        }

        val wrapper = DatabaseWrapper(dealerJsonList)
        return gson.toJson(wrapper)
    }
    fun loadSession(file: File): Boolean {
        return try {
            val json = Files.readString(file.toPath())
            val data = gson.fromJson(json, DatabaseWrapper::class.java)

            // Map DealerJson to Dealer
            val dealers = data.database.map { it.toDomainDealer() }

            // Flatten all VehicleJson to Vehicle (dealer ID already inside each vehicle)
            val vehicles = data.database.flatMap { it.vehicles.map { vj -> vj.toDomainVehicle() } }

            databaseContext.setDealers(dealers)
            databaseContext.setVehicles(vehicles)

            logger.info("Imported ${dealers.size} dealers and ${vehicles.size} vehicles.")
            true
        } catch (e: Exception) {
            logger.error("Failed to load DB: ${e.message}")
            false
        }
    }

    companion object {
        val instance: JsonHandler by lazy {
            JsonHandler()
        }

        val logger: Logger = Logger.logger;

        private var databaseContext: Database = Database.instance
    }
}