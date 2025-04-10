package edu.metrostate.dealership.infrastructure.imports.mappers


import edu.metrostate.dealership.domain.models.Dealer
import edu.metrostate.dealership.domain.models.Price
import edu.metrostate.dealership.domain.models.Vehicle
import edu.metrostate.dealership.domain.models.VehicleType
import edu.metrostate.dealership.infrastructure.imports.models.json.DealerJson
import edu.metrostate.dealership.infrastructure.imports.models.json.VehicleJson
import edu.metrostate.dealership.infrastructure.imports.models.xml.DealerXml
import edu.metrostate.dealership.infrastructure.imports.models.xml.VehicleExportXml
import edu.metrostate.dealership.infrastructure.imports.models.xml.VehicleXml

fun DealerXml.toDomainDealer(): Dealer =
    Dealer(dealershipId, name, enabledForAcquisition ?: true)

fun VehicleXml.toDomainVehicle(dealershipId: String): Vehicle {
    return Vehicle(
        dealershipId = dealershipId,
        vehicleId = this.vehicleId,
        type = VehicleType.fromString(this.vehicleType),
        manufacturer = this.make,
        model = this.model,
        price = Price(price.value, price.unit),
        acquisitionDate = acquisitionDate
    )
}
fun Vehicle.toXmlExportVehicle(): VehicleExportXml =
    VehicleExportXml(
        //dealershipId = dealershipId,
        //dealershipName = "",
        vehicleType = type.toString(),
        //vehicleManufacturer = manufacturer,
        model = model,
        vehicleId = vehicleId,
        //price = price,
        acquisitionDate = acquisitionDate,
        //isRented = isRented
    )

fun Vehicle.toJsonVehicle(): VehicleJson =
    VehicleJson(
        dealershipId = dealershipId,
        dealershipName = "",
        vehicleType = type.toString(),
        vehicleManufacturer = manufacturer,
        vehicleModel = model,
        vehicleId = vehicleId,
        price = price,
        acquisitionDate = acquisitionDate,
        isRented = isRented
    )

fun DealerJson.toDomainDealer(): Dealer =
    Dealer(dealershipId, name, enabledForAcquisition)

fun VehicleJson.toDomainVehicle(): Vehicle =
    Vehicle(
        vehicleId = vehicleId,
        manufacturer = vehicleManufacturer,
        model = vehicleModel,
        acquisitionDate = acquisitionDate,
        price = price,
        dealershipId = dealershipId,
        type = VehicleType.fromString(vehicleType),
        isRented = isRented
    )

fun Vehicle.updateFrom(other: Vehicle) {
    this.type = other.type
    this.isRented = other.isRented
    this.price = other.price
    this.acquisitionDate = other.acquisitionDate
    this.manufacturer = other.manufacturer
    this.model = other.model
}