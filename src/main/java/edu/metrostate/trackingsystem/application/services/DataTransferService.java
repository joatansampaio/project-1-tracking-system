package edu.metrostate.trackingsystem.application.services;

import edu.metrostate.trackingsystem.infrastructure.logging.Logger;
import edu.metrostate.trackingsystem.infrastructure.utils.JsonHandler;
import edu.metrostate.trackingsystem.infrastructure.utils.XmlHandler;
import edu.metrostate.trackingsystem.infrastructure.utils.NotificationHandler;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class DataTransferService {

    private static final Logger logger = Logger.getLogger();
    private final NotificationHandler notificationHandler;
    private final JsonHandler jsonHandler;

    //Note #1 - When using this I get an uninitialized error for xmlHandler AND jsonHandler
    private final XmlHandler xmlHandler;
    //Which can be fixed in the DataTransferService constructor by adding , XmlHandler xmlHandler , but, I'm not sure if I should
    //var dataTransferService = new DataTransferService(notificationHandler, jsonHandler); In main

    public DataTransferService(NotificationHandler notificationHandler, JsonHandler jsonHandler, XmlHandler xmlHandler) {
        this.notificationHandler = notificationHandler;
        this.jsonHandler = jsonHandler;
        this.xmlHandler = xmlHandler;
    }


    public void importJson(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import JSON File");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        File initialDir = new File("src/main/resources/files");
        if (initialDir.exists() && initialDir.isDirectory()) {
            fileChooser.setInitialDirectory(initialDir);
        }

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null && jsonHandler.importFile(selectedFile)) {
            notificationHandler.notify("File imported.");
        } else if (selectedFile != null) {
            notificationHandler.notifyError("The import process failed. Ensure it is a valid JSON file.");
        } else {
            logger.info("File selection cancelled.");
        }
    }

    public void exportJson(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Database JSON");
        fileChooser.setInitialFileName("export.json");
        fileChooser.setInitialDirectory(new File("src/main/resources/files"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {
            if (JsonHandler.getInstance().exportFile(selectedFile)) {
                notificationHandler.notify("Exported Successfully!");
            } else {
                notificationHandler.notifyError("Couldn't export the file.");
            }
        }
    }


    public void importXml(Stage stage) {
        // Adapted from importJson()
        // see Note #1 in this file

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import XML File");


        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        File initialDir = new File("src/main/resources/files");
        if (initialDir.exists() && initialDir.isDirectory()) {
            fileChooser.setInitialDirectory(initialDir);
        }

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null && xmlHandler.importFile(selectedFile)) {
            notificationHandler.notify("File imported.");
        } else if (selectedFile != null) {
            notificationHandler.notifyError("The import process failed. Ensure it is a valid XML file.");
        } else {
            logger.info("File selection cancelled.");
        }
   }
}
