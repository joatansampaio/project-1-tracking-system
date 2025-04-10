package edu.metrostate.dealership.infrastructure.imports.models.xml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class DealerExportXml(
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    var dealershipId: String = "",

    @JacksonXmlProperty(localName = "Name")
    var name: String = "Not Configured",

    @JacksonXmlElementWrapper(localName = "Vehicles") // wrapper tag
    @JacksonXmlProperty(localName = "Vehicle")        // each item tag
    var vehicles: List<VehicleExportXml> = listOf(),

    @JacksonXmlProperty(isAttribute = true, localName = "enabled")
    var enabledForAcquisition: Boolean? = null
)