//Main UI code
package edu.metrostate.dealership.presentation.controllers

import edu.metrostate.dealership.Main.Companion.setTheme
import edu.metrostate.dealership.application.services.DataTransferService
import edu.metrostate.dealership.application.services.DealerService
import edu.metrostate.dealership.application.services.VehicleService
import edu.metrostate.dealership.domain.models.Dealer
import edu.metrostate.dealership.domain.models.Vehicle
import edu.metrostate.dealership.domain.models.VehicleType
import edu.metrostate.dealership.infrastructure.logging.Logger
import edu.metrostate.dealership.infrastructure.utils.JsonHandler
import edu.metrostate.dealership.infrastructure.utils.JsonHandler.Companion.instance
import edu.metrostate.dealership.infrastructure.utils.NotificationHandler
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.transformation.FilteredList
import javafx.collections.transformation.SortedList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.util.Callback
import java.io.File
import kotlin.math.min
import kotlin.system.exitProcess

/**
 * Main controller for the dealership management application.
 * Manages the primary UI with both vehicle and dealer tables,
 * navigation between views, and actions such as import/export,
 * adding/deleting vehicles or dealers, and toggling properties.
 */
class MainController {
    private val logger = Logger.logger

    private lateinit var dealerService: DealerService
    private lateinit var vehicleService: VehicleService
    private lateinit var dataTransferService: DataTransferService
    private lateinit var jsonHandler: JsonHandler
    private lateinit var notificationHandler: NotificationHandler
    private var hasVisitedDealersTab = false

    // Vehicle View
    @FXML lateinit var vehicleTable: TableView<Vehicle>
    @FXML lateinit var vehicleIdColumn: TableColumn<Vehicle, String>
    @FXML lateinit var manufacturerColumn: TableColumn<Vehicle, String>
    @FXML lateinit var vehicleTypeColumn: TableColumn<Vehicle, String>
    @FXML lateinit var modelColumn: TableColumn<Vehicle, String>
    @FXML lateinit var dealershipIdColumn: TableColumn<Vehicle, String>
    @FXML lateinit var priceColumn: TableColumn<Vehicle, String>
    @FXML lateinit var acquisitionDate: TableColumn<Vehicle, String?>
    @FXML lateinit var isRentedColumn: TableColumn<Vehicle, String?>

    private lateinit var filteredVehicles: FilteredList<Vehicle>

    // Dealer View
    @FXML lateinit var dealerTable: TableView<Dealer>
    @FXML lateinit var dealerIdColumn: TableColumn<Dealer, String>
    @FXML lateinit var dealerNameColumn: TableColumn<Dealer, String>
    @FXML lateinit var isEnabledForAcquisitionColumn: TableColumn<Dealer, String?>
    @FXML lateinit var numberOfVehiclesForDealer: TableColumn<Dealer?, String?>
    @FXML lateinit var dealershipIdCombo: ComboBox<String?>
    @FXML lateinit var toggleAcquisitionBtn: Button
    @FXML lateinit var addDealerBtn: Button

    // Actions
    @FXML lateinit var addVehicleBtn: Button
    @FXML lateinit var deleteVehicleBtn: Button
    @FXML lateinit var deleteDealerBtn: Button
    @FXML lateinit var goToVehiclesViewBtn: Button
    @FXML lateinit var goToDealersViewBtn: Button
    @FXML lateinit var toggleRentedBtn: Button
    @FXML lateinit var toggleTransferBtn: Button

    @FXML lateinit var transferVehicleBtn: Button

    /**
     * Initializes the controller when the FXML is loaded.
     * Sets up tables, listeners, properties, and loads data.
     */
    @FXML
    fun initialize() {
        setupVehiclesTable()
        setupDealersTable()
        setupListeners()
        setupOtherProperties()
        initializeDataAsync()
    }

    /**
     * Opens a dialog to add a new vehicle.
     * Loads the add-vehicle.fxml and centers it on the main window.
     */
    @FXML
    fun onAddVehicle() {
        try {
            val loader = FXMLLoader(javaClass.getResource("/edu/metrostate/dealership/add-vehicle.fxml"))
            val root = loader.load<Parent>()

            val scene = Scene(root)
            setTheme(scene, javaClass) // Apply our theme

            val dialogStage = Stage()
            val ownerStage = stage

            dialogStage.scene = scene
            dialogStage.title = "Add Vehicle"
            dialogStage.initModality(Modality.WINDOW_MODAL) // Make the dialog modal
            dialogStage.initOwner(ownerStage) // Set the owner of the dialog
            dialogStage.isResizable = false
            dialogStage.show()
            // Center it based on main-view's table
            dialogStage.x = ownerStage.x + (ownerStage.width - dialogStage.width) / 2
            dialogStage.y = ownerStage.y + (ownerStage.height - dialogStage.height) / 2

            val addVehicleController = loader.getController<AddVehicleController>()
            addVehicleController.injectDependencies(vehicleService, notificationHandler)
        } catch (e: Exception) {
            notificationHandler.notifyError("Error loading the add vehicles screen. Check the logs for more info.")
            logger.error("An error occurred: $e")
        }
    }

    /**
     * Shows a simple text input dialogs to add a new dealer.
     */
    @FXML
    fun onAddDealer() {
        // Create a dialog for dealer ID
        val idDialog = TextInputDialog()
        idDialog.title = "Add Dealer"
        idDialog.headerText = null
        idDialog.contentText = "Dealer ID:"
        setTheme(idDialog.dialogPane.scene, javaClass)

        // Show the dialog and wait for input
        val idResult = idDialog.showAndWait()
        // Process the ID if present
        idResult.ifPresent { id ->
            val dealerId = id.trim()

            // Check if ID already exists
            if (dealerId.isEmpty()) {
                notificationHandler.notifyError("Dealer ID cannot be empty.")
                return@ifPresent
            }
            if (dealerService.dealershipIDs.contains(dealerId)) {
                notificationHandler.notifyError("Dealer ID already exists.")
                return@ifPresent
            }

            // Ask for dealer name
            val nameDialog = TextInputDialog("New Dealer")
            nameDialog.title = "Add Dealer"
            nameDialog.headerText = null
            nameDialog.contentText = "Name:"

            setTheme(nameDialog.dialogPane.scene, javaClass)

            val nameResult = nameDialog.showAndWait()

            // Process the name and create dealer
            nameResult.ifPresent { name ->
                try {
                    // Create new dealer
                    val newDealer = Dealer(
                        dealershipId = dealerId,
                        name = if (name.isNotBlank()) name.trim() else "New Dealer",
                        enabledForAcquisition = true // This is our default
                    )

                    dealerService.addDealer(newDealer)

                    // Update UI
                    updateDealershipIds()
                    dealerTable.refresh()
                    notificationHandler.notify("Dealer added successfully.")

                } catch (e: Exception) {
                    notificationHandler.notifyError("Failed to add dealer: ${e.message}")
                    logger.error("Error adding dealer: $e")
                }
            }
        }
    }

    /**
     * Deletes the currently selected vehicle.
     * Updates the selection after deletion to maintain a smooth UI experience.
     */
    @FXML
    fun onDeleteVehicle() {
        val selected = vehicleTable.selectionModel.selectedItem
        val selectedIndex = vehicleTable.selectionModel.selectedIndex

        vehicleService.deleteVehicle(selected.vehicleId)
        notificationHandler.notify("Vehicle deleted.")
        vehicleTable.items.remove(selected)

        // Select the next available item
        val itemCount = vehicleTable.items.size
        if (itemCount > 0) {
            val newIndex = min(selectedIndex.toDouble(), (itemCount - 1).toDouble()).toInt()
            vehicleTable.selectionModel.select(newIndex)
        }
    }

    @FXML
    fun onDeleteDealer() {
        val selected = dealerTable.selectionModel.selectedItem
        val response = dealerService.deleteDealer(selected.dealershipId)
        if (response!!.isSuccess) {
            notificationHandler.notify("Dealer deleted.")
            return
        }
        notificationHandler.notifyError(response.errorMessage)
    }

    /**
     * Toggles the acquisition status of the selected dealer.
     * Updates the table to reflect the change.
     */
    @FXML
    fun toggleVehicleAcquisition() {
        val dealer = dealerTable.selectionModel.selectedItem
        dealerService.toggleAcquisition(dealer.dealershipId)
        dealerTable.refresh()
        notificationHandler.notify("Success")
    }

    /**
     * Transfers inventory between dealerships.
     * Note: This functionality is currently commented out/incomplete.
     */
    @FXML
    fun transferDealershipInventory() {
        print("herehere")
        val deal = dealerTable.selectionModel.selectedItem
        val j = deal.dealershipId
        //intelliJ is wrong this is used
        val loader = FXMLLoader(javaClass.getResource("/edu/metrostate/dealership/DealerSelect.fxml"))
        val root = loader.load<Parent>()
        val dealerSelectionController = loader.getController<DealerSelectionController>()
        dealerSelectionController.injects(j, false, notificationHandler)
        val scene = Scene(root)
        setTheme(scene, javaClass) // Apply our theme

        val dialogStage = Stage()
        val ownerStage = stage
        dialogStage.scene = scene
        dialogStage.title = "Select Dealer for transfer"
        dialogStage.initModality(Modality.WINDOW_MODAL) // Make the dialog modal
        dialogStage.initOwner(ownerStage) // Set the owner of the dialog
        dialogStage.isResizable = false
        dialogStage.showAndWait()
        vehicleTable.refresh()
        dealerTable.refresh()
    }

    fun fixID(vehicleID: String, dealershipId: String): String {
        val c = vehicleID.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (c.size == 2) {
            dealershipId + "_" + c[1]
        } else dealershipId + "_" + c[0]
    }

    /**
     * Imports data from a JSON file.
     * Delegates to the data transfer service.
     */
    @FXML fun onImportJson() {
        dataTransferService.importJson(stage)
        updateDealershipIds()
    }

    /**
     * Imports data from an XML file.
     * Delegates to the data transfer service.
     */
    @FXML fun onImportXml() {
        dataTransferService.importXml(stage)
        updateDealershipIds()
    }

    /**
     * Exports data to a JSON file.
     * Delegates to the data transfer service.
     */
    @FXML fun onExportJson() { dataTransferService.exportJson(stage) }

    /**
     * Exports data to an XML file.
     * Delegates to the data transfer service.
     */
    @FXML fun onExportXml() { dataTransferService.exportXml(stage) }

    /**
     * Toggles the rental status of the selected vehicle.
     * Updates the table to reflect the change.
     */
    @FXML
    fun toggleRented() {
        vehicleTable.selectionModel.selectedItem?.let {
            vehicleService.toggleIsRented(it.vehicleId)
            vehicleTable.refresh()
            notificationHandler.notify("Success!")
        }
    }

    /**
     * Handles the exit action from the menu.
     * Optionally saves the session before exiting.
     *
     * @param event The action event containing information about the source
     */
    @FXML
    fun onExit(event: ActionEvent) {
        val item = event.source as MenuItem
        val shouldSave = item.userData.toString().toBoolean()
        if (shouldSave) jsonHandler.saveSession()

        exitProcess(0)
    }

    /**
     * Initializes application state on the JavaFX thread after the UI is rendered.
     *
     * This method performs the following:
     * - Loads any previously saved session data from disk.
     * - Populates the dealership ComboBox with dealer IDs.
     * - Binds the dealer and vehicle data to their respective tables.
     * - Adds listeners to the tables
     */
    private fun initializeDataAsync() {
        Platform.runLater {
            initializeFromPreviousState()
            updateDealershipIds()

            dealerTable.items = dealerService.dealers
            filteredVehicles = FilteredList(vehicleService.vehicles)
            val sortedVehicles = SortedList(filteredVehicles)
            sortedVehicles.comparatorProperty().bind(vehicleTable.comparatorProperty())
            vehicleTable.items = sortedVehicles

            // When a dealer is removed, removed the vehicles too.
            dealerService.dealers.addListener(ListChangeListener { change ->
                while (change.next()) {
                    if (change.wasRemoved()) {
                        val removedDealers = change.removed
                        val removedIds = removedDealers.map { it.dealershipId }

                        vehicleService.vehicles.removeIf { it.dealershipId in removedIds }
                        vehicleTable.refresh()
                    }
                }
            })

            // To update the vehicle count correctly when adding a vehicle.
            vehicleService.vehicles.addListener(ListChangeListener {
                applyVehicleFilter(dealershipIdCombo.value)
                dealerTable.refresh()
            })
        }
    }

    /**
     * Updates the dealership ID ComboBox with current dealer IDs.
     * Adds an "All" option for showing vehicles from all dealerships.
     */
    private fun updateDealershipIds() {
        val dealerIds = FXCollections.observableArrayList<String?>()
        dealerIds.add("All")
        dealerIds.addAll(dealerService.dealershipIDs)

        dealershipIdCombo.items = dealerIds
    }

    /**
     * Sets up visibility and style properties for UI elements.
     * Binds managed properties to visible properties to prevent empty space.
     */
    private fun setupOtherProperties() {
        // That will make the buttons not take the space when hidden
        toggleAcquisitionBtn.managedProperty().bind(toggleAcquisitionBtn.visibleProperty())
        deleteDealerBtn.managedProperty().bind(deleteDealerBtn.visibleProperty())
        toggleTransferBtn.managedProperty().bind(toggleTransferBtn.visibleProperty())
        deleteVehicleBtn.managedProperty().bind(deleteVehicleBtn.visibleProperty())
        addVehicleBtn.managedProperty().bind(addVehicleBtn.visibleProperty())
        dealershipIdCombo.managedProperty().bind(dealershipIdCombo.visibleProperty())
        toggleRentedBtn.managedProperty().bind(toggleRentedBtn.visibleProperty())
        addDealerBtn.managedProperty().bind(addDealerBtn.visibleProperty())
        goToDealersViewBtn.style = "--fx-min-width: 100px; -fx-background-color: #212121"
        goToVehiclesViewBtn.style = "--fx-min-width: 100px; -fx-background-color: #343434"
    }

    /**
     * Loads the previous application state from disk.
     * Attempts to load from database.json if it exists.
     */
    private fun initializeFromPreviousState() {
        this.jsonHandler = instance
        val dbFile = File("database.json")
        if (!dbFile.exists()) {
            logger.info("No previous state found. Starting fresh.")
            return
        }
        if (jsonHandler.loadSession(dbFile)) {
            logger.info("Successfully restored previous session.")
            return
        }
        logger.error("Failed to load previous database state.")
    }

    /**
     * Sets up the vehicles table with appropriate cell factories and value factories.
     * Configures the display format for each column.
     */
    private fun setupVehiclesTable() {
        logger.info("Configuring Vehicle TableView columns.")
        vehicleIdColumn.cellValueFactory = PropertyValueFactory("vehicleId")
        manufacturerColumn.cellValueFactory = PropertyValueFactory("manufacturer")
        modelColumn.cellValueFactory = PropertyValueFactory("model")
        vehicleTypeColumn.setCellValueFactory { cellData ->
            SimpleStringProperty(
                when (cellData.value.type) {
                    VehicleType.SUV -> "SUV"
                    VehicleType.SEDAN -> "Sedan"
                    VehicleType.SPORTS_CAR -> "Sports Car"
                    VehicleType.PICKUP -> "Pickup"
                    VehicleType.UNKNOWN -> "Unknown Type"
                }
            )
        }
        dealershipIdColumn.cellValueFactory = PropertyValueFactory("dealershipId")
        priceColumn.cellValueFactory = PropertyValueFactory("price")

        priceColumn.cellValueFactory =
            Callback { cellData: TableColumn.CellDataFeatures<Vehicle, String> -> SimpleStringProperty(cellData.value.priceAsString) }

        acquisitionDate.cellValueFactory =
            Callback { cellData: TableColumn.CellDataFeatures<Vehicle, String?> -> SimpleStringProperty(cellData.value.formattedAcquisitionDate) }

        isRentedColumn.cellValueFactory =
            Callback { cellData: TableColumn.CellDataFeatures<Vehicle, String?> -> SimpleStringProperty(cellData.value.isRentedAsString) }

        isRentedColumn.setCellFactory {
            object : TableCell<Vehicle?, String?>() {
                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty) {
                        text = null
                        style = ""
                    } else {
                        text = item
                        if ("Yes" == item) {
                            style = "-fx-text-fill: #FFA07A; -fx-font-weight: bold;"
                        } else if ("No" == item) {
                            style = "-fx-text-fill: #90EE90; -fx-font-weight: bold;"
                        }
                    }
                }
            }
        }
    }

    /**
     * Sets up the dealers table with appropriate cell factories and value factories.
     * Configures the display format for each column and enables editing for the name column.
     */
    private fun setupDealersTable() {
        logger.info("Configuring Dealer TableView columns.")
        dealerIdColumn.cellValueFactory = PropertyValueFactory("dealershipId")
        dealerNameColumn.cellValueFactory = PropertyValueFactory("name")
        // Set up the checkbox column for Acquisition Status
        isEnabledForAcquisitionColumn.setCellValueFactory { cellData: TableColumn.CellDataFeatures<Dealer, String?> ->
            if (cellData.value.enabledForAcquisition)
                SimpleStringProperty("Enabled")
            else
                SimpleStringProperty("Disabled")
        }
        isEnabledForAcquisitionColumn.setCellFactory {
            object : TableCell<Dealer?, String?>() {
                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty) {
                        text = null
                        style = ""
                    } else {
                        text = item
                        if ("Disabled" == item) {
                            style = "-fx-text-fill: #FFA07A; -fx-font-weight: bold;"
                        } else if ("Enabled" == item) {
                            style = "-fx-text-fill: #90EE90; -fx-font-weight: bold;"
                        }
                    }
                }
            }
        }

        numberOfVehiclesForDealer.setCellValueFactory { cellData: TableColumn.CellDataFeatures<Dealer?, String?>? ->
            val count = vehicleService.vehicles.count { v -> v.dealershipId == cellData?.value?.dealershipId }
            SimpleStringProperty(count.toString())
        }

        dealerTable.isEditable = true
        dealerNameColumn.cellFactory = TextFieldTableCell.forTableColumn()
        dealerNameColumn.setOnEditCommit { event: TableColumn.CellEditEvent<Dealer, String> ->
            val dealer = event.rowValue
            dealer.name = event.newValue
            notificationHandler.notify("Dealer Updated")
        }
    }

    /**
     * Sets up listeners for table selections and dropdown changes.
     * Configures button states based on selections and applies filters.
     */
    private fun setupListeners() {
        logger.info("Configuring listeners.")
        // To enable/disable the delete button based on if a row is selected.
        vehicleTable.selectionModel.selectedItemProperty()
            .addListener { _: ObservableValue<out Vehicle>?, _: Vehicle?, newSelection: Vehicle? ->
                deleteVehicleBtn.isDisable =
                    newSelection == null
                deleteVehicleBtn.style = "-fx-background-color:" + (if (newSelection == null) "#2a2a2a" else "#f54444")
                transferVehicleBtn.isDisable =
                    newSelection == null
                transferVehicleBtn.style = "-fx-background-color:" + (if (newSelection == null) "#2a2a2a" else "#3399ff")
            }
        dealerTable.selectionModel.selectedItemProperty()
            .addListener { _: ObservableValue<out Dealer>?, _: Dealer?, newSelection: Dealer? ->
                toggleAcquisitionBtn.isDisable =
                    newSelection == null
                toggleAcquisitionBtn.style =
                    "-fx-background-color:" + (if (newSelection == null) "#2a2a2a" else "#3399ff")
                toggleTransferBtn.isDisable = newSelection == null
                toggleTransferBtn.style = "-fx-background-color:" + (if (newSelection == null) "#2a2a2a" else "#3399ff")
                deleteDealerBtn.isDisable = newSelection == null
                deleteDealerBtn.style = "-fx-background-color:" + (if (newSelection == null) "#2a2a2a" else "#f54444")
            }

        // Clearing selection when clicking on an empty row.
        vehicleTable.setRowFactory {
            TableRow<Vehicle>().apply {
                onMouseClicked = EventHandler {
                    if (isEmpty) vehicleTable.selectionModel.clearSelection()
                }
            }
        }
        // Clearing selection when clicking on an empty row.
        dealerTable.setRowFactory {
            TableRow<Dealer>().apply {
                onMouseClicked = EventHandler {
                    if (isEmpty) dealerTable.selectionModel.clearSelection()
                }
            }
        }

        dealershipIdCombo.valueProperty().addListener { _, _, newValue ->
            applyVehicleFilter(newValue)
        }

        // To disable the Toggle Rented button if the selected vehicle is a sports car
        vehicleTable.selectionModel.selectedItemProperty()
            .addListener((ChangeListener { _: ObservableValue<out Vehicle>?, _: Vehicle?, v2: Vehicle? ->
                toggleRentedBtn.isDisable =
                    v2 == null || v2.type == VehicleType.SPORTS_CAR
                toggleRentedBtn.style =
                    "-fx-background-color:" + (if (v2 == null || v2.type == VehicleType.SPORTS_CAR) "#2a2a2a" else "#3c3c3c")
            }))
    }

    /**
     * Toggles visibility of UI elements based on which tab/view is active.
     *
     * @param isDealerTab true if dealer tab is active, false for vehicle tab
     */
// Modify the toggleTabView method to include the addDealerBtn
    private fun toggleTabView(isDealerTab: Boolean) {
        listOf(vehicleTable, deleteVehicleBtn, addVehicleBtn, dealershipIdCombo, toggleRentedBtn, transferVehicleBtn)
            .forEach { it.isVisible = !isDealerTab }

        listOf(dealerTable, deleteDealerBtn, toggleAcquisitionBtn, toggleTransferBtn, addDealerBtn)
            .forEach { it.isVisible = isDealerTab }

        goToDealersViewBtn.style = "--fx-min-width: 100px; -fx-background-color:" +
                if (!isDealerTab) "#212121" else "#343434"

        goToVehiclesViewBtn.style = "--fx-min-width: 100px; -fx-background-color:" +
                if (isDealerTab) "#212121" else "#343434"
    }

    /**
     * Switches to the dealers view.
     * Shows a helpful tip the first time the user visits this tab.
     */
    fun goToDealersView() {
        toggleTabView(true)
        if (!hasVisitedDealersTab) {
            hasVisitedDealersTab = true
            notificationHandler.tip("Double-click Name cell to edit the dealer's name.")
        }
    }

    /**
     * Applies a filter to the vehicles table based on the selected dealership.
     *
     * @param dealershipId The ID of the dealer to filter by, or "All" for no filter
     */
    fun goToVehiclesView() {
        toggleTabView(false)
    }

    private fun applyVehicleFilter(dealershipId: String?) {
        filteredVehicles.setPredicate { vehicle ->
            dealershipId == null || dealershipId == "All" || vehicle.dealershipId == dealershipId
        }
    }

    /**
     * Gets the current stage from the scene.
     *
     * @return The Stage containing this controller's view
     */
    private val stage: Stage
        get() = vehicleTable.scene.window as Stage

    /**
     * Injects required dependencies into the controller.
     *
     * @param vehicleService Service for managing vehicles
     * @param dealerService Service for managing dealers
     * @param dataTransferService Service for importing/exporting data
     * @param notificationHandler Service for displaying notifications
     */
    fun injectDependencies(
        vehicleService: VehicleService,
        dealerService: DealerService,
        dataTransferService: DataTransferService,
        notificationHandler: NotificationHandler
    ) {
        this.vehicleService = vehicleService
        this.dealerService = dealerService
        this.dataTransferService = dataTransferService
        this.notificationHandler = notificationHandler
    }

    @FXML
    fun transferVehicle() {
        print("here")
        val veh = vehicleTable.selectionModel.selectedItem
        val j = veh.vehicleId
        //intelliJ is wrong this is used
        val loader = FXMLLoader(javaClass.getResource("/edu/metrostate/dealership/DealerSelect.fxml"))
        val root = loader.load<Parent>()
        val dealerSelectionController = loader.getController<DealerSelectionController>()
        dealerSelectionController.injects(j, true, notificationHandler)
        val scene = Scene(root)
        setTheme(scene, javaClass) // Apply our theme

        val dialogStage = Stage()
        val ownerStage = stage
        dialogStage.scene = scene
        dialogStage.title = "Select Dealer for transfer"
        dialogStage.initModality(Modality.WINDOW_MODAL) // Make the dialog modal
        dialogStage.initOwner(ownerStage) // Set the owner of the dialog
        dialogStage.isResizable = false
        dialogStage.showAndWait()
        vehicleTable.refresh()
        dealerTable.refresh()

    }
}