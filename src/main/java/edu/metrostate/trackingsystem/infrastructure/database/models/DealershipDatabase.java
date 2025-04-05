package edu.metrostate.trackingsystem.infrastructure.database.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import edu.metrostate.trackingsystem.domain.models.Dealer;
import java.util.List;

@JacksonXmlRootElement(localName = "Dealers")
public class DealershipDatabase {
    @JacksonXmlProperty(localName = "Dealer")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Dealer> database;

    public DealershipDatabase(List<Dealer> dealers) {
        this.database = dealers;
    }

    public List<Dealer> getDatabase() {
        return database;
    }

    public void setDatabase(List<Dealer> database) {
        this.database = database;
    }
}
