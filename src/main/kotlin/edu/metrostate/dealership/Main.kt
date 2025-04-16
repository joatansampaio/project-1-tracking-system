package edu.metrostate.dealership

import edu.metrostate.dealership.application.services.DataTransferService
import edu.metrostate.dealership.application.services.DealerService
import edu.metrostate.dealership.application.services.VehicleService
import edu.metrostate.dealership.domain.repositories.DealerRepository
import edu.metrostate.dealership.domain.repositories.VehicleRepository
import edu.metrostate.dealership.infrastructure.database.Database
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

        val dependencies = getDependencies()
        val notificationHandler = NotificationHandler(stage)
        val dataTransferService = DataTransferService(notificationHandler, dependencies.jsonHandler, dependencies.xmlHandler)

        val mainController = fxmlLoader.getController<MainController>()
        mainController.injectDependencies(
            dependencies.vehicleService,
            dependencies.dealerService,
            dataTransferService,
            notificationHandler
        )

        stage.title = "Dealership System v$VERSION"
        stage.scene = scene
        stage.onCloseRequest = EventHandler<WindowEvent> {
            dependencies.jsonHandler.saveSession()
        }
        stage.show()
    }

    data class DependencyPackage(
        val jsonHandler: JsonHandler,
        val xmlHandler: XmlHandler,
        val vehicleService: VehicleService,
        val dealerService: DealerService,
        val database: Database
    )

    companion object {
        const val VERSION = 3.0
        val logger: Logger = Logger.logger

        fun setTheme(scene: Scene, classRef: Class<*>) {
            scene.stylesheets.add(
                Objects.requireNonNull(classRef.getResource("/css/style.css")).toExternalForm()
            )
        }

        fun getDependencies(): DependencyPackage {
            val jsonHandler = JsonHandler.instance
            val xmlHandler = XmlHandler.instance
            val database = Database.instance

            val vehicleRepository = VehicleRepository(database)
            val dealerRepository = DealerRepository(database)

            val vehicleService = VehicleService(vehicleRepository)
            val dealerService = DealerService(dealerRepository)

            return DependencyPackage(
                jsonHandler,
                xmlHandler,
                vehicleService,
                dealerService,
                database
            )
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}
