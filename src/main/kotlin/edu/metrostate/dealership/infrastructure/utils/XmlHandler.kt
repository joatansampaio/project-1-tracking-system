//Handles reading of an XML formatted data file to an object
package edu.metrostate.dealership.infrastructure.utils

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

class XmlHandler private constructor() : IFileHandler {
    /**
     * Imports an XML formatted file using the jackson library
     * @param file - The file to be imported
     * @return true if operation completed otherwise log and error and return false
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


    override fun exportFile(file: File): Boolean {
        try {
            val xmlMapper = XmlMapper()
            xmlMapper.registerKotlinModule();
            val data = extractCurrentStateAsXml()
            xmlMapper.writeValue(file, data)
            return true
        } catch (e: Exception) {
            logger.error(e.message!!)
        }
        return false
    }

    private fun extractCurrentStateAsXml(): DatabaseWrapperXml {
        
        val dealers: List<Dealer> = databaseContext.dealers
        val vehicles: List<Vehicle> = databaseContext.vehicles

        // Group vehicles by dealershipId
        val groupedVehicles = vehicles.groupBy { it.dealershipId }

        // Build DealerXml list
        val dealerXmlList = dealers.map { dealer ->
            DealerExportXml(
                dealershipId = dealer.dealershipId,
                name = dealer.getName(),
                enabledForAcquisition = dealer.enabledForAcquisition,
                vehicles = groupedVehicles[dealer.dealershipId].orEmpty().map { it.toXmlExportVehicle() }
            )
        }

        val wrapper = DatabaseWrapperXml(dealerXmlList)
        return wrapper
    }

    companion object {
        private val logger: Logger = Logger.logger
        private var databaseContext: Database = Database.instance
        var instance: XmlHandler? = null
            get() {
                if (field == null) {
                    field = XmlHandler()
                }
                return field
            }
            private set
    }
}