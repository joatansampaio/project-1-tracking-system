package edu.metrostate.trackingsystem.presentation.controllers;

import edu.metrostate.trackingsystem.application.exceptions.ValidationException;
import edu.metrostate.trackingsystem.application.services.VehicleService;
import edu.metrostate.trackingsystem.infrastructure.database.DatabaseContext;
import edu.metrostate.trackingsystem.domain.models.Vehicle;
import edu.metrostate.trackingsystem.infrastructure.utils.NotificationHandler;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class AddVehicleController {

    private VehicleService vehicleService;
    private NotificationHandler notificationHandler;

    @FXML private ComboBox<String> dealershipIdCombo;
    @FXML private TextField vehicleIdField;
    @FXML private TextField manufacturerField;
    @FXML private TextField modelField;
    @FXML private TextField priceField;
    @FXML private DatePicker acquisitionDatePicker;
    @FXML private ComboBox<String> vehicleTypeCombo;

    /**
     * Initializes the controller after the root element has been completely processed.
     * This method sets up the {@code dealershipIdCombo} ComboBox by populating it with a list
     * of dealership IDs retrieved from the data source.
     */
    @FXML
    public void initialize() {
        dealershipIdCombo.setItems(FXCollections.observableArrayList(
                DatabaseContext.getInstance().getDealershipIDs()
        ));
        setupFields();
    }

    @FXML
    private void onAddVehicle(ActionEvent event) {
        try {
            var vehicle = Vehicle.createVehicle(
                    vehicleIdField.getText(),
                    manufacturerField.getText(),
                    modelField.getText(),
                    priceField.getText(),
                    acquisitionDatePicker.getValue(),
                    dealershipIdCombo.getValue(),
                    vehicleTypeCombo.getValue());
            var response = vehicleService.addVehicle(vehicle);
            if (response.isSuccess()) {
                notificationHandler.notify("Vehicle added.");
                closeStage(event);
            } else {
                notificationHandler.notifyError(response.getErrorMessage());
            }
        } catch (ValidationException e) {
            for (String error : e.getValidationErrors()) {
                notificationHandler.notifyError(error);
            }
        }
    }

    /**
     * Handles the cancel action triggered by the UI.
     * Closes the window associated with the given action event.
     *
     * @param event The {@code ActionEvent} triggered when the cancel action is performed.
     */
    @FXML
    private void onCancel(ActionEvent event) {
        Stage stage = getStage(event);
        stage.close();
    }

    /**
     * Configures the input fields and combo boxes in the controller.
     * <p>
     * This method sets up a text formatter on the {@code priceField} to ensure
     * that only valid price data is entered. The input is validated using a
     * regular expression that allows numbers with up to two decimal places.
     * <p>
     * Additionally, the method enables auto-complete functionality for the
     * {@code dealershipIdCombo} and {@code vehicleTypeCombo} combo boxes
     * by utilizing the {@code AutoCompleteComboBox.enableAutoComplete} method.
     */
    private void setupFields() {
        // Let's use a simple regex to apply a new text formatter to the price TextField
        // That way we don't need to worry about type mismatch.
        Pattern validPricePattern = Pattern.compile("-?\\d*(\\.\\d{0,2})?");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (validPricePattern.matcher(newText).matches()) {
                return change; // Accept it because it passed the regex.
            }
            return null;
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        priceField.setTextFormatter(textFormatter);
    }

    /**
     * Closes the stage (window) associated with the given action event.
     *
     * @param event The {@code ActionEvent} that triggers the stage closure.
     *              The source of this event is used to identify and close the window.
     */
    private void closeStage(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Retrieves the {@code Stage} associated with the {@code Scene} of the source event.
     *
     * @param event The {@code ActionEvent} from which the source {@code Node} will be used
     *              to obtain the associated {@code Stage}.
     * @return The {@code Stage} tied to the source {@code Node} of the event.
     */
    private Stage getStage(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    /**
     * Injects the required dependencies into the AddVehicleController.
     * @param notificationHandler The notification handler responsible for managing notifications.
     */
    public void injectDependencies(VehicleService vehicleService, NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
        this.vehicleService = vehicleService;
    }
}
