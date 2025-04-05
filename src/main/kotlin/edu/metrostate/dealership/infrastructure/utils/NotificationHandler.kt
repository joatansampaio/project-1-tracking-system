package edu.metrostate.dealership.infrastructure.utils

import javafx.stage.Stage
import javafx.util.Duration
import org.controlsfx.control.Notifications

class NotificationHandler(private val mainStage: Stage) {
    fun notify(message: String?) {
        Notifications.create()
            .owner(mainStage)
            .title("Success")
            .text(message)
            .show()
    }

    fun notifyError(message: String?) {
        Notifications.create()
            .owner(mainStage)
            .title("Error")
            .text(message)
            .showError()
    }

    fun tip(message: String?) {
        Notifications.create()
            .owner(mainStage)
            .title("Tip")
            .text(message)
            .hideAfter(Duration.seconds(7.0))
            .showInformation()
    }
}