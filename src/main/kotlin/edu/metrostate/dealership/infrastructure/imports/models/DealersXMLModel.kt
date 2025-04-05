package edu.metrostate.dealership.infrastructure.imports.models

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import edu.metrostate.dealership.infrastructure.imports.models.xml.DealerXml

@JacksonXmlRootElement(localName = "Dealers")
class DealersXMLModel {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Dealer")
    var dealers: List<DealerXml>? = null
}