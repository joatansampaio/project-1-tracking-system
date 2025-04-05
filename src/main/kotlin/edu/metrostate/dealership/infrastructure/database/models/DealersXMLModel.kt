package edu.metrostate.dealership.infrastructure.database.models

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import edu.metrostate.dealership.domain.models.Dealer

@JacksonXmlRootElement(localName = "Dealers")
class DealersXMLModel {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Dealer")
    var dealers: List<Dealer>? = null
}