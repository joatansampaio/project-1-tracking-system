package edu.metrostate.trackingsystem.infrastructure.utils;

import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class NotificationHandler {

    private final Stage mainStage;

    public NotificationHandler(Stage stage) {
        this.mainStage = stage;
    }

    public void notify(String message) {
        Notifications.create()
                .owner(mainStage)
                .title("Success")
                .text(message)
                .show();
    }

    public void notifyError(String message) {
        Notifications.create()
                .owner(mainStage)
                .title("Error")
                .text(message)
                .showError();
    }

    public void tip(String message) {
        Notifications.create()
                .owner(mainStage)
                .title("Tip")
                .text(message)
                .hideAfter(Duration.seconds(7))
                .showInformation();
    }

}
