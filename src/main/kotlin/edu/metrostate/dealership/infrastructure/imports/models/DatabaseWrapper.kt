package edu.metrostate.dealership.infrastructure.imports.models

import edu.metrostate.dealership.infrastructure.imports.models.json.DealerJson
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import edu.metrostate.dealership.infrastructure.imports.models.xml.DealerExportXml


data class DatabaseWrapper(
    val database: List<DealerJson>
)

@JacksonXmlRootElement(localName = "Dealers")
data class DatabaseWrapperXml(

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Dealer")
    val entries: List<DealerExportXml> = listOf() // rename from "dealers" to anything else
)