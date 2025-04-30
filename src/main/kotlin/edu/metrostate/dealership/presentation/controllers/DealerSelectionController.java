package edu.metrostate.dealership.presentation.controllers;

import edu.metrostate.dealership.application.services.VehicleService;
import edu.metrostate.dealership.domain.models.Dealer;
import edu.metrostate.dealership.domain.models.Vehicle;
import edu.metrostate.dealership.infrastructure.database.Database;
import edu.metrostate.dealership.infrastructure.utils.NotificationHandler;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class DealerSelectionController {

    private VehicleService vehicleService;
    private NotificationHandler notificationHandler;
    @FXML
    private ComboBox<String> dealershipIdCombo2;
    private String selectedID;
    private boolean isSingle;

    @FXML
    public void initialize() {
        dealershipIdCombo2.setItems(FXCollections.observableArrayList(
                Database.Companion.getInstance().getDealershipIDs()
        ));
    }

    private void setupFields() {
    }

    @FXML
    private void transferDealershipInventory(ActionEvent event) {
    }

    public void onConfirm(ActionEvent event) {
        boolean flag = true;
        Dealer check = null;
        for (Dealer z : Database.Companion.getInstance().getDealers()){
            if (z.getDealershipId()
                    .equals(dealershipIdCombo2.getSelectionModel().getSelectedItem())) {
                check = z;
            }
        }

        try {
            if (!check.getEnabledForAcquisition()){
                notificationHandler.notifyError("Dealer Not Enabled for Acquisition");
                flag = false;
            }
            if (!isSingle && flag) {
               System.out.print("Made it to not single");
                for (Vehicle vehicle : Database.Companion.getInstance().getVehicles()) {
                    if (vehicle.getDealershipId().equals(selectedID)) {
                        vehicle.setDealershipId(dealershipIdCombo2.getValue());
                    }
                }
            } else if (isSingle && flag) {
                System.out.print("Made it to single");
                for (Vehicle vehicle : Database.Companion.getInstance().getVehicles()) {
                    if (vehicle.getVehicleId().equals(selectedID)) {
                        vehicle.setDealershipId(dealershipIdCombo2.getValue());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            notificationHandler.notifyError("Error while transferring dealership inventory, please make sure you select a dealership \n and that the dealership is enabled for acquisition");
            flag = false;
        }
        if (flag){
            notificationHandler.notify("Success");
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

    }

    public void injects(String b, boolean single, NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
        this.selectedID = b;
        this.isSingle = single;
    }

    public void onCancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}