package edu.metrostate.trackingsystem.presentation.controllers;

import edu.metrostate.trackingsystem.application.services.DataTransferService;
import edu.metrostate.trackingsystem.application.services.DealerService;
import edu.metrostate.trackingsystem.application.services.VehicleService;
import edu.metrostate.trackingsystem.domain.models.Dealer;
import edu.metrostate.trackingsystem.infrastructure.logging.Logger;
import edu.metrostate.trackingsystem.domain.models.Vehicle;
import edu.metrostate.trackingsystem.infrastructure.utils.JsonHandler;
import edu.metrostate.trackingsystem.infrastructure.utils.NotificationHandler;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import edu.metrostate.trackingsystem.Main;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

public class MainController {

    private final Logger logger = Logger.getLogger();

    private DealerService dealerService;
    private VehicleService vehicleService;
    private DataTransferService dataTransferService;
    private JsonHandler jsonHandler;
    private NotificationHandler notificationHandler;

    @FXML private TableView<Vehicle> vehicleTableView;
    @FXML private TableColumn<Vehicle, String> vehicleIdColumn;
    @FXML private TableColumn<Vehicle, String> manufacturerColumn;
    @FXML private TableColumn<Vehicle, String> vehicleTypeColumn;
    @FXML private TableColumn<Vehicle, String> modelColumn;
    @FXML private TableColumn<Vehicle, String> dealershipIdColumn;
    @FXML private TableColumn<Vehicle, String> priceColumn;
    @FXML private TableColumn<Vehicle, String> acquisitionDate;

    @FXML private TableView<Dealer> dealerTableView;
    @FXML private TableColumn<Dealer, String> dealerIdColumn;
    @FXML private TableColumn<Dealer, String> dealerNameColumn;
    @FXML private TableColumn<Dealer, String> isEnabledForAcquisitionColumn;

    @FXML private ComboBox<String> dealershipIdMainCombo;
    @FXML private Button toggleAcquisitionBtn;
    @FXML private Button deleteVehicleBtn;
    @FXML private Button goToVehiclesViewBtn;
    @FXML private Button goToDealersViewBtn;

    @FXML
    public void initialize() {
        setupVehiclesTable();
        setupDealersTable();
        setupListeners();
        setupOtherProperties();
        InitializeDataAsync();
    }

    @FXML
    private void onAddVehicle() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/metrostate/trackingsystem/add-vehicle.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Main.setTheme(scene, getClass()); // Apply our theme

            Stage dialogStage = new Stage();
            Stage ownerStage = getStage();

            dialogStage.setScene(scene);
            dialogStage.setTitle("Add Vehicle");
            dialogStage.initModality(Modality.WINDOW_MODAL); // Make the dialog modal
            dialogStage.initOwner(ownerStage); // Set the owner of the dialog
            dialogStage.setResizable(false);
            dialogStage.show();
            // Center it based on main-view's table
            dialogStage.setX(ownerStage.getX() + (ownerStage.getWidth() - dialogStage.getWidth()) / 2);
            dialogStage.setY(ownerStage.getY() + (ownerStage.getHeight() - dialogStage.getHeight()) / 2);

            AddVehicleController addVehicleController = loader.getController();
            addVehicleController.injectDependencies(this, vehicleService, notificationHandler);
        } catch (Exception e) {
            notificationHandler.notifyError("Error loading the add vehicles screen. Check the logs for more info.");
            logger.error("An error occurred: " + e);
        }
    }

    @FXML
    private void onDeleteVehicle() {
        var selected = vehicleTableView.getSelectionModel().getSelectedItem();
        var response = vehicleService.deleteVehicle(selected);
        if (response.isSuccess()) {
            notificationHandler.notify("Vehicle deleted.");
            vehicleTableView.getItems().remove(selected);
            return;
        }
        notificationHandler.notifyError(response.getErrorMessage());
    }

    @FXML
    private void toggleVehicleAcquisition() {
        var dealer = dealerTableView.getSelectionModel().getSelectedItem();
        if (dealer != null) {
            dealerService.toggleAcquisition(dealer.getDealershipId());
            notificationHandler.notify("Success");
            dealerTableView.refresh();
        }
    }

    @FXML
    private void onImportJson() {
        dataTransferService.importJson(getStage());
        refreshVehiclesTable();
    }

    @FXML
    private void onImportXml() {
        dataTransferService.importXml(getStage());
        refreshVehiclesTable();
    }

    @FXML
    private void onExportJson() {
        dataTransferService.exportJson(getStage());
    }

    @FXML
    private void onExit(ActionEvent event) {
        MenuItem item = (MenuItem) event.getSource();
        boolean shouldSave = Boolean.parseBoolean(item.getUserData().toString());
        if (shouldSave) {
            jsonHandler.saveSession();
        }
        System.exit(0);
    }

    private void InitializeDataAsync() {
        // To avoid the situation where dependency injection hasn't occurred
        Platform.runLater(() -> {
            initializeFromPreviousState();
            refreshVehiclesTable();
            refreshDealersTable();

            ObservableList<String> dealerIds = FXCollections.observableArrayList();
            dealerIds.add("All");
            dealerIds.addAll(dealerService.getDealershipIDs());

            dealershipIdMainCombo.setItems(dealerIds);
        });
    }

    private void setupOtherProperties() {
        // That will make the button not take the space when hidden
        toggleAcquisitionBtn.managedProperty().bind(toggleAcquisitionBtn.visibleProperty());
        goToDealersViewBtn.setStyle("--fx-min-width: 100px; -fx-background-color: #212121");
        goToVehiclesViewBtn.setStyle("--fx-min-width: 100px; -fx-background-color: #343434");
    }

    public void refreshVehiclesTable() {
        refreshVehiclesTable(null);
    }

    public void refreshVehiclesTable(String dealershipIdFilter) {
        if (dealershipIdFilter == null || dealershipIdFilter.isEmpty()) {
            vehicleTableView.setItems(FXCollections.observableArrayList(vehicleService.getVehicles()));
        } else {
            vehicleTableView.setItems(FXCollections.observableArrayList(
                    vehicleService.getVehicles()
                            .stream()
                            .filter(vehicle -> dealershipIdFilter.equals(vehicle.getDealershipId()))
                            .toList()
            ));
        }
    }

    public void refreshDealersTable() {
        refreshDealersTable(null);
    }

    public void refreshDealersTable(String dealershipIdFilter) {
        if (dealershipIdFilter == null || dealershipIdFilter.isEmpty()) {
            dealerTableView.setItems(FXCollections.observableArrayList(dealerService.getDealers()));
        } else {
            dealerTableView.setItems(FXCollections.observableArrayList(
                    dealerService.getDealers()
                            .stream()
                            .filter(vehicle -> dealershipIdFilter.equals(vehicle.getDealershipId()))
                            .toList()
            ));
        }
    }

    private void initializeFromPreviousState() {
        this.jsonHandler = JsonHandler.getInstance();
        File dbFile = new File("src/main/resources/database/database.json");
        if (!dbFile.exists()) {
            logger.info("No previous state found. Starting fresh.");
            return;
        }

        if (!jsonHandler.importFile(dbFile)) {
            logger.error("Failed to load previous database state.");
        } else {
            logger.info("Successfully restored previous session.");
        }
    }

    private void setupVehiclesTable() {
        logger.info("Configuring Vehicle TableView columns.");
        vehicleIdColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        manufacturerColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        vehicleTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dealershipIdColumn.setCellValueFactory(new PropertyValueFactory<>("dealershipId"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        priceColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getPriceAsString());
        });

        acquisitionDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFormattedAcquisitionDate()));
    }

    private void setupDealersTable() {
        logger.info("Configuring Dealer TableView columns.");
        dealerIdColumn.setCellValueFactory(new PropertyValueFactory<>("dealershipId"));
        isEnabledForAcquisitionColumn.setCellValueFactory(new PropertyValueFactory<>("enabledForAcquisition"));
        // Set up the checkbox column for Acquisition Status
        isEnabledForAcquisitionColumn.setCellValueFactory(cellData -> {
            Dealer dealer = cellData.getValue();
            if( dealer.getEnabledForAcquisition()) {
                return new SimpleStringProperty("Enabled");
            }
            return new SimpleStringProperty("Disabled");
        });
    }

    private void setupListeners() {
        logger.info("Configuring listeners.");
        // To enable/disable the delete button based on if a row is selected.
        vehicleTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            deleteVehicleBtn.setDisable(newSelection == null);
            deleteVehicleBtn.setStyle("-fx-background-color:" + (newSelection == null ? "#2a2a2a": "#f54444"));
        });

        // Let's clear the selection when clicking in an empty row because that seems right.
        vehicleTableView.setRowFactory(tv -> {
            TableRow<Vehicle> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (row.isEmpty()) {
                    vehicleTableView.getSelectionModel().clearSelection();
                }
            });
            return row;
        });

        dealershipIdMainCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            String id = newValue;
            if (newValue.equals("All")) {
                id = null;
            }
            refreshVehiclesTable(id);
            refreshDealersTable(id);
        });
    }

    public void goToDealersView(ActionEvent actionEvent) {
        toggleView(true);
    }

    public void goToVehiclesView(ActionEvent actionEvent) {
        toggleView(false);
    }

    private void toggleView(boolean b) {
        dealerTableView.setVisible(b);
        vehicleTableView.setVisible(!b);
        deleteVehicleBtn.setVisible(!b);
        toggleAcquisitionBtn.setVisible(b);
        goToDealersViewBtn.setStyle("--fx-min-width: 100px; -fx-background-color:" + (!b ? "#212121": "#343434"));
        goToVehiclesViewBtn.setStyle("--fx-min-width: 100px; -fx-background-color:" + (b ? "#212121": "#343434"));
    }

    public void onShowAll() {
        refreshDealersTable();
        refreshVehiclesTable();
        dealershipIdMainCombo.getSelectionModel().clearSelection();
    }

    private Stage getStage() {
        return (Stage) vehicleTableView.getScene().getWindow();
    }

    // I couldn't make it happen through the constructor with JavaFX
    // apparently there are libs that can use @autowire.
    // I don't think it is worth it right now, but feel free to change it if you want.
    public void injectDependencies(
            VehicleService vehicleService,
            DealerService dealerService,
            DataTransferService dataTransferService,
            NotificationHandler notificationHandler) {

        this.vehicleService = vehicleService;
        this.dealerService = dealerService;
        this.dataTransferService = dataTransferService;
        this.notificationHandler = notificationHandler;
    }
}
