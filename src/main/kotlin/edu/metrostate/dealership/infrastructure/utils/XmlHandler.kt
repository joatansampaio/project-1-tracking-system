//Handles reading of an XML formatted data file to an object
package edu.metrostate.dealership.infrastructure.utils

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import edu.metrostate.dealership.infrastructure.database.DatabaseContext
import edu.metrostate.dealership.infrastructure.database.models.DealersXMLModel
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
            data.dealers?.let { DatabaseContext.instance!!.importXML(it) }
            return true
        } catch (e: Exception) {
            logger.error(e.message!!)
        }

        return false
    }


    override fun exportFile(file: File): Boolean {
        return false
    }

    companion object {
        private val logger: Logger = Logger.logger;
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