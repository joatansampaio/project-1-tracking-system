package edu.metrostate.trackingsystem.infrastructure.utils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import edu.metrostate.trackingsystem.domain.models.Dealer;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataSaver {
    private static final String FILE_NAME = "data-persistence.xml";


    public static void saveDealers(List<Dealer> dealers) {
        XmlMapper mapper = new XmlMapper();
        try {
            mapper.writeValue(new File(FILE_NAME), dealers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
