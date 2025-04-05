//Handles reading of a JSON formatted data file
package edu.metrostate.dealership.infrastructure.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import edu.metrostate.dealership.domain.models.Dealer
import edu.metrostate.dealership.domain.models.Vehicle
import edu.metrostate.dealership.infrastructure.database.DatabaseContext
import edu.metrostate.dealership.infrastructure.database.models.DealershipDatabase
import edu.metrostate.dealership.infrastructure.logging.Logger
import edu.metrostate.dealership.infrastructure.utils.GsonConfig.gson
import javafx.collections.ObservableList
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.util.*

class JsonHandler private constructor() : IFileHandler {
    /**
     * @param file the file to be imported.
     * @return true if there was an error.
     */
    override fun importFile(file: File): Boolean {
        try {
            val json = Files.readString(file.toPath() )
            val jsonObject = JsonParser
                .parseString(json)
                .asJsonObject

            val listType = object : TypeToken<List<Vehicle?>?>() {}.type
            val data = gson.fromJson<List<Vehicle>>(jsonObject["car_inventory"], listType)

            // Group vehicles by dealershipId
            val groupByDealershipId: Map<String?, List<Vehicle>> = data.groupBy { it.dealershipId }

            // Build list of Dealer objects
            val dealers: List<Dealer> = groupByDealershipId.map { (dealershipId, vehicles) ->
                vehicles.forEach { it.normalize() }
                Dealer(
                    dealershipId,
                    vehicles,
                    vehicles.firstOrNull()?.dealershipName
                )
            }

            databaseContext!!.importJSON(dealers)
            logger.info("Imported successfully.")
            return true
        } catch (e: Exception) {
            logger.error(e.message!!)
            return false
        }
    }

    override fun exportFile(file: File): Boolean {
        var file = file
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()
        val data: ObservableList<Dealer> = databaseContext!!.dealers
        val wrapper = DealershipDatabase(data)
        val json = gson.toJson(wrapper)
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
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()

        val data: ObservableList<Dealer> = databaseContext!!.dealers
        val wrapper = DealershipDatabase(data)
        val json = gson.toJson(wrapper)

        val folder = File("src/main/resources/database")
        val file = File(folder, "database.json")

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

    fun loadSession(file: File): Boolean {
        try {
            val json = Files.readString(file.toPath())
            val jsonObject = JsonParser
                .parseString(json)
                .asJsonObject

            val listType = object : TypeToken<List<Dealer?>?>() {}.type
            val data = gson.fromJson<List<Dealer?>>(jsonObject["database"], listType)

            databaseContext.importJSON(data)
            logger.info("Imported successfully.")
            return true
        } catch (e: Exception) {
            logger.error(e.message!!)
            return false
        }
    }

    companion object {
        val instance: JsonHandler by lazy {
            JsonHandler()
        }

        val logger: Logger = Logger.logger;

        private var databaseContext: DatabaseContext = DatabaseContext.instance
    }
}