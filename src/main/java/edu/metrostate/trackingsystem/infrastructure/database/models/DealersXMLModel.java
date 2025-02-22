package edu.metrostate.trackingsystem.infrastructure.database.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import edu.metrostate.trackingsystem.domain.models.Dealer;

import java.util.List;

@JacksonXmlRootElement(localName = "Dealers")
public class DealersXMLModel {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Dealer")
    public List<Dealer> dealers;

    public List<Dealer> getDealers() {
        return dealers;
    }

    public void setDealers(List<Dealer> dealers) {
        this.dealers = dealers;
    }
}
