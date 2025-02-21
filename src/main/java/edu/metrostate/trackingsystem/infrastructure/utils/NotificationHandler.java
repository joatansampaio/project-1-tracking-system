package edu.metrostate.trackingsystem.infrastructure.utils;

import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

public class NotificationHandler {
    private static NotificationHandler instance;
    private final Stage mainStage; // or MainController reference, whichever you prefer

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

}
