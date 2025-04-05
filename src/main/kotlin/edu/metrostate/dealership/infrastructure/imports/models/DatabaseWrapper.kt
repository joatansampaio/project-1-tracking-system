package edu.metrostate.dealership.infrastructure.imports.models

import edu.metrostate.dealership.domain.models.Dealer
import edu.metrostate.dealership.domain.models.Vehicle
import edu.metrostate.dealership.infrastructure.imports.mappers.toJsonVehicle
import edu.metrostate.dealership.infrastructure.imports.models.json.DealerJson
import javafx.collections.FXCollections
import javafx.collections.ObservableList

data class DatabaseWrapper(
    val database: List<DealerJson>
)