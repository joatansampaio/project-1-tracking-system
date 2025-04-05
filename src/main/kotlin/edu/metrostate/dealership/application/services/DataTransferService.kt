// This class is part of the UI code for handling import of json/XML files
package edu.metrostate.dealership.application.services

import edu.metrostate.dealership.infrastructure.logging.Logger
import edu.metrostate.dealership.infrastructure.utils.JsonHandler
import edu.metrostate.dealership.infrastructure.utils.NotificationHandler
import edu.metrostate.dealership.infrastructure.utils.XmlHandler
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File

class DataTransferService(
    private val notificationHandler: NotificationHandler,
    private val jsonHandler: JsonHandler,
    private val xmlHandler: XmlHandler
) {
    fun importJson(stage: Stage?) {
        val fileChooser = FileChooser()
        fileChooser.title = "Import JSON File"

        val extFilter = FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json")
        fileChooser.extensionFilters.add(extFilter)

        val initialDir = File("import-files")
        if (initialDir.exists() && initialDir.isDirectory) {
            fileChooser.initialDirectory = initialDir
        }

        val selectedFile = fileChooser.showOpenDialog(stage)
        if (selectedFile != null && jsonHandler.importFile(selectedFile)) {
            notificationHandler.notify("File imported.")
        } else if (selectedFile != null) {
            notificationHandler.notifyError("The import process failed. Ensure it is a valid JSON file.")
        } else {
            logger.info("File selection cancelled.")
        }
    }

    fun exportJson(stage: Stage?) {
        val fileChooser = FileChooser()
        fileChooser.title = "Export Database JSON"
        fileChooser.initialFileName = "export.json"
        fileChooser.initialDirectory = File("export-files")
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("JSON Files", "*.json"))

        val selectedFile = fileChooser.showSaveDialog(stage)

        if (selectedFile != null) {
            if (JsonHandler.instance.exportFile(selectedFile)) {
                notificationHandler.notify("Exported Successfully!")
            } else {
                notificationHandler.notifyError("Couldn't export the file.")
            }
        }
    }


    fun importXml(stage: Stage?) {
        // Adapted from importJson()

        val fileChooser = FileChooser()
        fileChooser.title = "Import XML File"


        val extFilter = FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml")
        fileChooser.extensionFilters.add(extFilter)

        val initialDir = File("import-files")
        if (initialDir.exists() && initialDir.isDirectory) {
            fileChooser.initialDirectory = initialDir
        }

        val selectedFile = fileChooser.showOpenDialog(stage)
        if (selectedFile != null && xmlHandler.importFile(selectedFile)) {
            notificationHandler.notify("File imported.")
        } else if (selectedFile != null) {
            notificationHandler.notifyError("The import process failed. Ensure it is a valid XML file.")
        } else {
            logger.info("File selection cancelled.")
        }
    }

    companion object {
        private val logger: Logger = Logger.logger
    }
}