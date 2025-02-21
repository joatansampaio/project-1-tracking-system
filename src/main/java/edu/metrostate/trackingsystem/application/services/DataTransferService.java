package edu.metrostate.trackingsystem.application.services;

import edu.metrostate.trackingsystem.infrastructure.logging.Logger;
import edu.metrostate.trackingsystem.infrastructure.utils.JsonHandler;
import edu.metrostate.trackingsystem.infrastructure.utils.NotificationHandler;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class DataTransferService {

    private final NotificationHandler notificationHandler;
    private final JsonHandler jsonHandler;
    private final Logger logger = Logger.getLogger();

    public DataTransferService(NotificationHandler notificationHandler, JsonHandler jsonHandler) {
        this.notificationHandler = notificationHandler;
        this.jsonHandler = jsonHandler;
    }

    public void importData(Stage stage) {
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

    public void exportData(Stage stage) {
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
}
