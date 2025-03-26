package edu.metrostate.trackingsystem.infrastructure.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import edu.metrostate.trackingsystem.domain.models.Dealer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {
    private static final String FILE_NAME = "data-persistence.xml";

    public static List<Dealer> loadDealers() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        XmlMapper mapper = new XmlMapper();
        try {
            return mapper.readValue(file, new TypeReference<List<Dealer>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
