package edu.metrostate.dealership

import edu.metrostate.dealership.application.services.DataTransferService
import edu.metrostate.dealership.application.services.DealerService
import edu.metrostate.dealership.application.services.VehicleService
import edu.metrostate.dealership.domain.repositories.DealerRepository
import edu.metrostate.dealership.domain.repositories.VehicleRepository
import edu.metrostate.dealership.infrastructure.database.DatabaseContext
import edu.metrostate.dealership.infrastructure.logging.Logger
import edu.metrostate.dealership.infrastructure.utils.JsonHandler
import edu.metrostate.dealership.infrastructure.utils.NotificationHandler
import edu.metrostate.dealership.infrastructure.utils.XmlHandler
import edu.metrostate.dealership.presentation.controllers.MainController
import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.WindowEvent
import java.util.*

class Main : Application() {

    override fun start(stage: Stage) {
        logger.info("Starting application...")

        val fxmlLoader = FXMLLoader(Main::class.java.getResource("main-view.fxml"))
        val scene = Scene(fxmlLoader.load(), 1200.0, 800.0)
        setTheme(scene, javaClass)

        val dependencies = getDependencies(stage)

        val mainController = fxmlLoader.getController<MainController>()
        mainController.injectDependencies(
            dependencies.vehicleService,
            dependencies.dealerService,
            dependencies.dataTransferService,
            dependencies.notificationHandler
        )

        stage.title = "Dealership System v$version"
        stage.scene = scene
        stage.onCloseRequest = EventHandler<WindowEvent> {
            dependencies.jsonHandler.saveSession()
        }
        stage.show()
    }

    private fun getDependencies(stage: Stage): DependencyPackage {
        val jsonHandler = JsonHandler.instance
        val xmlHandler = XmlHandler.instance
        val database = DatabaseContext.instance
        val notificationHandler = NotificationHandler(stage)

        val vehicleRepository = VehicleRepository(database)
        val dealerRepository = DealerRepository(database)

        val vehicleService = VehicleService(vehicleRepository)
        val dealerService = DealerService(dealerRepository)
        val dataTransferService = DataTransferService(notificationHandler, jsonHandler!!, xmlHandler!!)

        return DependencyPackage(
            jsonHandler,
            notificationHandler,
            vehicleService,
            dealerService,
            dataTransferService,
            database
        )
    }

    data class DependencyPackage(
        val jsonHandler: JsonHandler,
        val notificationHandler: NotificationHandler,
        val vehicleService: VehicleService,
        val dealerService: DealerService,
        val dataTransferService: DataTransferService,
        val database: DatabaseContext
    )

    companion object {
        const val version = 1.0
        val logger: Logger = Logger.logger

        fun setTheme(scene: Scene, classRef: Class<*>) {
            scene.stylesheets.add(
                Objects.requireNonNull(classRef.getResource("/css/style.css")).toExternalForm()
            )
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}
