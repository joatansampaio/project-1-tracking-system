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
import javafx.collections.ListChangeListener;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.stream.Collectors;

public class MainController {

    private final Logger logger = Logger.getLogger();

    private DealerService dealerService;
    private VehicleService vehicleService;
    private DataTransferService dataTransferService;
    private JsonHandler jsonHandler;
    private NotificationHandler notificationHandler;
    private boolean hasVisitedDealersTab = false;

    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, String> vehicleIdColumn;
    @FXML private TableColumn<Vehicle, String> manufacturerColumn;
    @FXML private TableColumn<Vehicle, String> vehicleTypeColumn;
    @FXML private TableColumn<Vehicle, String> modelColumn;
    @FXML private TableColumn<Vehicle, String> dealershipIdColumn;
    @FXML private TableColumn<Vehicle, String> priceColumn;
    @FXML private TableColumn<Vehicle, String> acquisitionDate;
    @FXML private TableColumn<Vehicle, String> isRentedColumn;

    @FXML private TableView<Dealer> dealerTable;
    @FXML private TableColumn<Dealer, String> dealerIdColumn;
    @FXML private TableColumn<Dealer, String> dealerNameColumn;
    @FXML private TableColumn<Dealer, String> isEnabledForAcquisitionColumn;
    @FXML private TableColumn<Dealer, String> numberOfVehiclesForDealer;

    @FXML private ComboBox<String> dealershipIdCombo;

    @FXML private Button toggleAcquisitionBtn;
    @FXML private Button addVehicleBtn;
    @FXML private Button deleteVehicleBtn;
    @FXML private Button deleteDealerBtn;
    @FXML private Button goToVehiclesViewBtn;
    @FXML private Button goToDealersViewBtn;
    @FXML private Button toggleRentedBtn;

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
            addVehicleController.injectDependencies(vehicleService, notificationHandler);
        } catch (Exception e) {
            notificationHandler.notifyError("Error loading the add vehicles screen. Check the logs for more info.");
            logger.error("An error occurred: " + e);
        }
    }

    public void updateAllVehicles() {
        vehicleService.getVehicles().setAll(dealerService.getDealers().stream()
                   .flatMap(dealer -> dealer.getVehicles().stream())
                   .collect(Collectors.toList()));
    }

    @FXML
    private void onDeleteVehicle() {
        var selected = vehicleTable.getSelectionModel().getSelectedItem();
        int selectedIndex = vehicleTable.getSelectionModel().getSelectedIndex();
        var response = vehicleService.deleteVehicle(selected.getVehicleId(), selected.getDealershipId());
        if (response.isSuccess()) {
            notificationHandler.notify("Vehicle deleted.");
            vehicleTable.getItems().remove(selected);

            // Select the next available item
            int itemCount = vehicleTable.getItems().size();
            if (itemCount > 0) {
                int newIndex = Math.min(selectedIndex, itemCount - 1);
                vehicleTable.getSelectionModel().select(newIndex);
            }

            return;
        }
        notificationHandler.notifyError(response.getErrorMessage());
    }

    @FXML
    private void onDeleteDealer() {
        var selected = dealerTable.getSelectionModel().getSelectedItem();
        var response = dealerService.deleteDealer(selected.getDealershipId());
        if (response.isSuccess()) {
            notificationHandler.notify("Dealer deleted.");
            return;
        }
        notificationHandler.notifyError(response.getErrorMessage());
    }

    @FXML
    private void toggleVehicleAcquisition() {
        var dealer = dealerTable.getSelectionModel().getSelectedItem();
        if (dealer != null) {
            dealerService.toggleAcquisition(dealer.getDealershipId());
            notificationHandler.notify("Success");
            dealerTable.refresh();
        }
    }

    @FXML private void onImportJson() { dataTransferService.importJson(getStage()); }
    @FXML private void onImportXml() { dataTransferService.importXml(getStage()); }
    @FXML private void onExportJson() { dataTransferService.exportJson(getStage()); }

    @FXML
    private void toggleRented() {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        vehicleService.toggleIsRented(selected);
        vehicleTable.refresh();
    }

    @FXML
    private void onExit(ActionEvent event) {
        MenuItem item = (MenuItem) event.getSource();
        boolean shouldSave = Boolean.parseBoolean(item.getUserData().toString());
        if (shouldSave) jsonHandler.saveSession();

        System.exit(0);
    }

    private void InitializeDataAsync() {
        // To avoid the situation where dependency injection hasn't occurred
        Platform.runLater(() -> {
            initializeFromPreviousState();
            updateDealershipIds();

            dealerService.getDealers().addListener((ListChangeListener<Dealer>) change -> {
                updateAllVehicles();
                while (change.next()) {
                    if (change.wasAdded()) {
                        for (Dealer dealer : change.getAddedSubList()) {
                            dealer.getObservableVehicles().addListener((ListChangeListener<Vehicle>) v -> updateAllVehicles());
                        }
                    }
                }
                updateDealershipIds();
            });

            dealerService.getDealers().forEach(d -> d.getObservableVehicles().addListener((ListChangeListener<Vehicle>) v -> updateAllVehicles()));
            dealerTable.setItems(dealerService.getDealers());
            vehicleTable.setItems(vehicleService.getVehicles());
        });
    }

    private void updateDealershipIds() {
        ObservableList<String> dealerIds = FXCollections.observableArrayList();
        dealerIds.add("All");
        dealerIds.addAll(dealerService.getDealershipIDs());

        dealershipIdCombo.setItems(dealerIds);
    }

    private void setupOtherProperties() {
        // That will make the buttons not take the space when hidden
        toggleAcquisitionBtn.managedProperty().bind(toggleAcquisitionBtn.visibleProperty());
        deleteDealerBtn.managedProperty().bind(deleteDealerBtn.visibleProperty());
        deleteVehicleBtn.managedProperty().bind(deleteVehicleBtn.visibleProperty());
        addVehicleBtn.managedProperty().bind(addVehicleBtn.visibleProperty());
        dealershipIdCombo.managedProperty().bind(dealershipIdCombo.visibleProperty());
        toggleRentedBtn.managedProperty().bind(toggleRentedBtn.visibleProperty());
        goToDealersViewBtn.setStyle("--fx-min-width: 100px; -fx-background-color: #212121");
        goToVehiclesViewBtn.setStyle("--fx-min-width: 100px; -fx-background-color: #343434");
    }

    private void initializeFromPreviousState() {
        this.jsonHandler = JsonHandler.getInstance();
        File dbFile = new File("src/main/resources/database/database.json");
        if (!dbFile.exists()) {
            logger.info("No previous state found. Starting fresh.");
            return;
        }
        if (jsonHandler.importFile(dbFile)) {
            logger.info("Successfully restored previous session.");
            updateAllVehicles();
            return;
        }
        logger.error("Failed to load previous database state.");
    }

    private void setupVehiclesTable() {
        logger.info("Configuring Vehicle TableView columns.");
        vehicleIdColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        manufacturerColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        vehicleTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dealershipIdColumn.setCellValueFactory(new PropertyValueFactory<>("dealershipId"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPriceAsString()));

        acquisitionDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFormattedAcquisitionDate()));

        isRentedColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getIsRentedAsString()));

        isRentedColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Yes".equals(item)) {
                        setStyle("-fx-text-fill: #FFA07A; -fx-font-weight: bold;");
                    } else if ("No".equals(item)) {
                        setStyle("-fx-text-fill: #90EE90; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void setupDealersTable() {
        logger.info("Configuring Dealer TableView columns.");
        dealerIdColumn.setCellValueFactory(new PropertyValueFactory<>("dealershipId"));
        dealerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        // Set up the checkbox column for Acquisition Status
        isEnabledForAcquisitionColumn.setCellValueFactory(cellData -> cellData.getValue().getEnabledForAcquisition()
                ? new SimpleStringProperty("Enabled")
                : new SimpleStringProperty("Disabled"));
        isEnabledForAcquisitionColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Disabled".equals(item)) {
                        setStyle("-fx-text-fill: #FFA07A; -fx-font-weight: bold;");
                    } else if ("Enabled".equals(item)) {
                        setStyle("-fx-text-fill: #90EE90; -fx-font-weight: bold;");
                    }
                }
            }
        });
        numberOfVehiclesForDealer.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getVehicles().size() + ""));

        dealerTable.setEditable(true);
        dealerNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        dealerNameColumn.setOnEditCommit(event -> {
            Dealer dealer = event.getRowValue();
            dealer.setName(event.getNewValue());
            notificationHandler.notify("Dealer Updated");
        });
    }

    private void setupListeners() {
        logger.info("Configuring listeners.");
        // To enable/disable the delete button based on if a row is selected.
        vehicleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            deleteVehicleBtn.setDisable(newSelection == null);
            deleteVehicleBtn.setStyle("-fx-background-color:" + (newSelection == null ? "#2a2a2a": "#f54444"));
        });
        dealerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            toggleAcquisitionBtn.setDisable(newSelection == null);
            toggleAcquisitionBtn.setStyle("-fx-background-color:" + (newSelection == null ? "#2a2a2a": "#3399ff"));
            deleteDealerBtn.setDisable(newSelection == null);
            deleteDealerBtn.setStyle("-fx-background-color:" + (newSelection == null ? "#2a2a2a": "#f54444"));
        });

        // Let's clear the selection when clicking in an empty row because that seems right.
        vehicleTable.setRowFactory(tv -> {
            TableRow<Vehicle> row = new TableRow<>();
            row.setOnMouseClicked(event -> { if (row.isEmpty()) vehicleTable.getSelectionModel().clearSelection(); });
            return row;
        });
        dealerTable.setRowFactory(tv -> {
            TableRow<Dealer> row = new TableRow<>();
            row.setOnMouseClicked(event -> { if (row.isEmpty())  dealerTable.getSelectionModel().clearSelection(); });
            return row;
        });

        dealershipIdCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.equals("All")) {
                updateAllVehicles();
                return;
            }
            vehicleService.getVehicles().setAll(
                    dealerService.getDealers()
                                 .stream()
                                 .filter(d -> d.getDealershipId().equals(newValue))
                                 .flatMap(dealer -> dealer.getVehicles().stream())
                                 .collect(Collectors.toList()));
        });

        // To disable the Toggle Rented button if the selected vehicle is a sports car
        vehicleTable.getSelectionModel().selectedItemProperty().addListener(((observableValue, v1, v2) -> {
            toggleRentedBtn.setDisable(v2 == null || v2.getType().equalsIgnoreCase("sports car"));
            toggleRentedBtn.setStyle("-fx-background-color:" + ((v2 == null || v2.getType().equalsIgnoreCase("sports car")) ? "#2a2a2a" : "#3c3c3c"));
        }));
    }

    private void toggleTabView(boolean isDealerTab) {
        // Vehicle Tab
        vehicleTable.setVisible(!isDealerTab);
        deleteVehicleBtn.setVisible(!isDealerTab);
        addVehicleBtn.setVisible(!isDealerTab);
        dealershipIdCombo.setVisible(!isDealerTab);
        toggleRentedBtn.setVisible(!isDealerTab);

        // Dealer Tab
        dealerTable.setVisible(isDealerTab);
        deleteDealerBtn.setVisible(isDealerTab);
        toggleAcquisitionBtn.setVisible(isDealerTab);

        goToDealersViewBtn.setStyle("--fx-min-width: 100px; -fx-background-color:" + (!isDealerTab ? "#212121": "#343434"));
        goToVehiclesViewBtn.setStyle("--fx-min-width: 100px; -fx-background-color:" + (isDealerTab ? "#212121": "#343434"));
    }

    public void goToDealersView() {
        toggleTabView(true);
        if (!hasVisitedDealersTab) {
            hasVisitedDealersTab = true;
            notificationHandler.tip("Double-click Name cell to edit the dealer's name.");
        }
    }
    public void goToVehiclesView() { toggleTabView(false); }

    private Stage getStage() { return (Stage) vehicleTable.getScene().getWindow(); }

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
