package edu.metrostate.dealership.infrastructure.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
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
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.util.*


/**
 * Handles reading and writing of JSON formatted data files for the dealership management system.
 * Implements the IFileHandler interface to provide specific JSON processing capabilities.
 * This class uses the singleton pattern for application-wide access.
 */
class JsonHandler private constructor() : IFileHandler {

    /**
     * Imports dealership and vehicle data from a JSON file.
     *
     * The expected JSON format contains a "car_inventory" array of vehicle objects,
     * where each vehicle contains dealership information. The import process will:
     * 1. Parse the vehicle data
     * 2. Group vehicles by dealership
     * 3. Create dealership objects with their associated vehicles
     * 4. Import the data into the database context
     *
     * @param file The JSON file to import
     * @return true if the import was successful, false if an error occurred
     */
    override fun importFile(file: File): Boolean {
        return try {
            val json = Files.readString(file.toPath())
            val jsonObject = JsonParser.parseString(json).asJsonObject

            val listType = object : TypeToken<List<VehicleJson>>() {}.type
            val vehicles: List<VehicleJson> = gson.fromJson(jsonObject["car_inventory"], listType)

            val dealerJsonList = vehicles
                .groupBy { it.dealershipId }
                .map { (dealershipId, dealerVehicles) ->
                    DealerJson(
                        dealershipId = dealershipId,
                        name = dealerVehicles.first().dealershipName,
                        enabledForAcquisition = dealerVehicles.first().dealershipEnabled,
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

    /**
     * Exports the current dealership and vehicle data to a JSON file.
     *
     * The export format matches the expected import format with a "car_inventory" array
     * of vehicle objects that include dealership information. This ensures compatibility
     * with the import function.
     *
     * @param file The destination file for the export (will append .json extension if missing)
     * @return true if the export was successful, false if an error occurred
     */
    override fun exportFile(file: File): Boolean {
        var exportFile = file
        // the json structure of our database.json is not the same as the import.
        // the regular export follows the same import structure.
        val json = extractCurrentStateAsJson(isDatabase = false)
        val path = exportFile.absolutePath

        //Make sure the file has the right extension
        //Necessary because the ui does allow exporting without the proper extension
        if (!path.lowercase(Locale.getDefault()).endsWith(".json")) {
            exportFile = File("$path.json")
        }

        //Try writing the json to file
        try {
            FileWriter(exportFile).use { writer ->
                writer.write(json)
                logger.info("Successfully exported to " + exportFile.absolutePath)
                return true
            }
        } catch (e: IOException) {
            logger.error("Error exporting JSON: " + e.message)
            return false
        }
    }

    /**
     * Saves the current database state to a file named "database.json" in the application directory.
     *
     * This method is used for auto-saving functionality and persistence between application sessions.
     * The format used is the internal database format, not the import/export format.
     *
     * @throws IOException if writing to the file fails (will terminate the application)
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

    /**
     * Extracts the current state of the system as a JSON string.
     *
     * @param isDatabase Flag indicating whether to format the JSON for database storage (true)
     *                   or for export (false). Default is true.
     * @return JSON string representation of the current state
     */
    private fun extractCurrentStateAsJson(isDatabase: Boolean = true): String {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()

        val dealers: List<Dealer> = databaseContext.dealers
        val vehicles: List<Vehicle> = databaseContext.vehicles

        // Group vehicles by dealershipId
        val groupedVehicles = vehicles.groupBy { it.dealershipId }


        if (isDatabase) {
            // Build DealerJson list
            val dealerJsonList = dealers.map { dealer ->
                DealerJson(
                    dealershipId = dealer.dealershipId,
                    name = dealer.name,
                    enabledForAcquisition = dealer.enabledForAcquisition,
                    vehicles = groupedVehicles[dealer.dealershipId].orEmpty().map { it.toJsonVehicle() }
                )
            }
            return gson.toJson(DatabaseWrapper(dealerJsonList))
        }
        // Not isDatabase: Flatten to list of VehicleJson with dealer info
        val dealerMap = dealers.associateBy { it.dealershipId }

        val vehicleJsonList = vehicles.map { vehicle ->
            val dealer = dealerMap[vehicle.dealershipId]
            vehicle.toJsonVehicle(dealer?.enabledForAcquisition, dealer?.name)
        }

        val wrapper = JsonObject()
        wrapper.add("car_inventory", gson.toJsonTree(vehicleJsonList))
        return gson.toJson(wrapper)
    }

    /**
     * Loads a previously saved database state from a JSON file.
     *
     * This method expects the JSON file to be in the internal database format,
     * as created by the saveSession() method.
     *
     * @param file The database JSON file to load (typically "database.json")
     * @return true if the load was successful, false if an error occurred
     */
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

        val logger: Logger = Logger.logger

        private var databaseContext: Database = Database.instance
    }
}