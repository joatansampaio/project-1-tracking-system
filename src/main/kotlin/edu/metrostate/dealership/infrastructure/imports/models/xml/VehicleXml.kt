package edu.metrostate.dealership.infrastructure.imports.models.xml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class VehicleXml(
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    var vehicleId: String,

    @JacksonXmlProperty(isAttribute = true, localName = "type")
    var vehicleType: String = "Unknown",

    @JacksonXmlProperty(localName = "Make")
    var make: String = "Unknown",

    @JacksonXmlProperty(localName = "Model")
    var model: String = "Unknown",

    @JacksonXmlProperty(localName = "Price")
    var price: PriceXml = PriceXml(),

    @JacksonXmlProperty(localName = "AcquisitionDate")
    var acquisitionDate: Long
)
