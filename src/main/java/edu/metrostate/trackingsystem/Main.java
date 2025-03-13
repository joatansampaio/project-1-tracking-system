package edu.metrostate.trackingsystem;

import edu.metrostate.trackingsystem.application.services.DataTransferService;
import edu.metrostate.trackingsystem.application.services.DealerService;
import edu.metrostate.trackingsystem.application.services.VehicleService;
import edu.metrostate.trackingsystem.domain.repositories.DealerRepository;
import edu.metrostate.trackingsystem.domain.repositories.VehicleRepository;
import edu.metrostate.trackingsystem.infrastructure.database.DatabaseContext;
import edu.metrostate.trackingsystem.infrastructure.database.IDatabaseContext;
import edu.metrostate.trackingsystem.infrastructure.logging.Logger;
import edu.metrostate.trackingsystem.infrastructure.utils.JsonHandler;
import edu.metrostate.trackingsystem.infrastructure.utils.XmlHandler;
import edu.metrostate.trackingsystem.infrastructure.utils.NotificationHandler;
import edu.metrostate.trackingsystem.presentation.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    public static final double version = 1;
    public static final Logger logger = Logger.getLogger();

    @Override
    public void start(Stage stage) throws IOException {
        logger.info("Starting application...");

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        setTheme(scene, getClass());

        // Dependencies
        DependencyPackage result = getDependencies(stage);

        MainController mainController = fxmlLoader.getController();
        mainController.injectDependencies(result.vehicleService(), result.dealerService(), result.dataTransferService(), result.notificationHandler());

        stage.setTitle("Dealership System v" + version);
        stage.setScene(scene);

        // Comment here if you don't want to save when closing through the X button.
        stage.setOnCloseRequest((WindowEvent event) -> {
            // If the user closes from the native close button, we save it too.
            result.jsonHandler().saveSession();
        });

        stage.show();
    }

    public static DependencyPackage getDependencies(Stage stage) {
        var jsonHandler = JsonHandler.getInstance();
        var xmlHandler = XmlHandler.getInstance();
        var database = DatabaseContext.getInstance();
        var notificationHandler = new NotificationHandler(stage);
        var vehicleRepository = new VehicleRepository(database);
        var dealerRepository = new DealerRepository(database);
        var vehicleService = new VehicleService(vehicleRepository);
        var dealerService = new DealerService(dealerRepository);
        var dataTransferService = new DataTransferService(notificationHandler, jsonHandler, xmlHandler);
        return new DependencyPackage(jsonHandler, notificationHandler, vehicleService, dealerService, dataTransferService, database);
    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * A helper to set the css theme, because it looks terrible in the middle of a method.
     */
    public static void setTheme(Scene scene, Class<?> classRef) {
        scene.getStylesheets().add(Objects.requireNonNull(classRef.getResource("/css/style.css")).toExternalForm());
    }

    // I need that for my 'integration' unit tests.
    public record DependencyPackage(JsonHandler jsonHandler, NotificationHandler notificationHandler, VehicleService vehicleService, DealerService dealerService, DataTransferService dataTransferService, IDatabaseContext database) { }
}