//Handles reading of an XML formatted data file to an object,
// and exporting of database data to XML
package edu.metrostate.dealership.infrastructure.utils

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import edu.metrostate.dealership.domain.models.Dealer
import edu.metrostate.dealership.domain.models.Vehicle
import edu.metrostate.dealership.infrastructure.database.Database
import edu.metrostate.dealership.infrastructure.imports.mappers.toXmlExportVehicle
import edu.metrostate.dealership.infrastructure.imports.models.DatabaseWrapperXml
import edu.metrostate.dealership.infrastructure.imports.models.DealersXMLModel
import edu.metrostate.dealership.infrastructure.imports.models.xml.DealerExportXml
import edu.metrostate.dealership.infrastructure.logging.Logger
import java.io.File
import java.util.*


class XmlHandler private constructor() : IFileHandler {
    /**
     * Imports an XML formatted file using the jackson library
     * @param file - The file to be imported
     * @return true if operation completed otherwise log an error and return false
     */
    override fun importFile(file: File): Boolean {
        val mapper = XmlMapper()

        try {
            val data = mapper.readValue(file, DealersXMLModel::class.java)
            Database.instance.importXML(data.dealers!!)
            return true
        } catch (e: Exception) {
            logger.error(e.message!!)
        }
        return false
    }

    /**
     * Exports an XML formatted file using the jackson library
     * @param file - The file to be exported to
     * @return true if operation completed otherwise log an error and return false
     */
    override fun exportFile(file: File): Boolean {

            var exportFile = file
            val data = extractCurrentStateAsXml()
            val path = exportFile.absolutePath
            val xmlMapper = XmlMapper()
            xmlMapper.registerKotlinModule()

            //Make the XML output more readable - "pretty"
            xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true)

            //Make sure the file has the right extension
            //Necessary because the ui does allow exporting without the proper extension
            if (!path.lowercase(Locale.getDefault()).endsWith(".xml")) {
                exportFile = File("$path.xml")
            }

            //Try writing the database data to file
            try {
                xmlMapper.writeValue(exportFile, data)
                logger.info("Successfully exported to " + exportFile.absolutePath)
                return true
            } catch (e: Exception) {
                logger.error("Error exporting XML: " + e.message!!)
            }
            return false
    }

    //Retrieves state of database data
    private fun extractCurrentStateAsXml(): DatabaseWrapperXml {
        
        val dealers: List<Dealer> = databaseContext.dealers
        val vehicles: List<Vehicle> = databaseContext.vehicles

        // Group vehicles by dealershipId
        val groupedVehicles = vehicles.groupBy { it.dealershipId }

        val validDealers = dealers.filter { d -> vehicles.any { v -> v.dealershipId == d.dealershipId } }

        // Build DealerXml list
        val dealerXmlList = validDealers.map { dealer ->
            DealerExportXml(
                dealershipId = dealer.dealershipId,
                name = dealer.name,
                enabledForAcquisition = dealer.enabledForAcquisition,
                vehicles = groupedVehicles[dealer.dealershipId].orEmpty().map { it.toXmlExportVehicle() }
            )
        }

        val wrapper = DatabaseWrapperXml(dealerXmlList)
        return wrapper
    }

    companion object {
        val instance: XmlHandler by lazy {
            XmlHandler()
        }

        private val logger: Logger = Logger.logger
        private var databaseContext: Database = Database.instance
    }
}