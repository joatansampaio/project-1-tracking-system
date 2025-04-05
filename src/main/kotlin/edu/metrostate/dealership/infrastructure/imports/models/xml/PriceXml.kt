package edu.metrostate.dealership.infrastructure.imports.models.xml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText

class PriceXml {
    @JacksonXmlText
    var value: Double = 0.0

    @JacksonXmlProperty(isAttribute = true, localName = "unit")
    var unit: String = "dollars"
}