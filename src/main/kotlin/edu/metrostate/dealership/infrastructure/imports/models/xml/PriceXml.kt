package edu.metrostate.dealership.infrastructure.imports.models.xml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText

class PriceXml(price: Double = 0.0, currency: String = "dollars") {

    @JacksonXmlText
    var value: Double = price

    @JacksonXmlProperty(isAttribute = true, localName = "unit")
    var unit: String = currency
}