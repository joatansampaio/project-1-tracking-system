package edu.metrostate.dealership.infrastructure.imports.models.xml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class DealerXml(
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    var dealershipId: String = "",

    @JacksonXmlProperty(localName = "Name")
    var name: String = "Not Configured",

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Vehicle")
    var vehicles: List<VehicleXml> = listOf(),

    @JacksonXmlProperty(isAttribute = true, localName = "enabled")
    var enabledForAcquisition: Boolean? = null
)
