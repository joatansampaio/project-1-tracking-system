package edu.metrostate.trackingsystem.infrastructure.utils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import edu.metrostate.trackingsystem.infrastructure.database.DatabaseContext;
import edu.metrostate.trackingsystem.infrastructure.database.models.DealersXMLModel;
import edu.metrostate.trackingsystem.infrastructure.logging.Logger;
import java.io.File;

public class XmlHandler implements IFileHandler {
    private static final Logger logger = Logger.getLogger();
    private static XmlHandler instance;

    private XmlHandler() { }

    public static XmlHandler getInstance() {
        if (instance == null) {
            instance = new XmlHandler();
        }
        return instance;
    }

        @Override
    public boolean importFile(File file) {

        XmlMapper mapper = new XmlMapper();

        try {
            DealersXMLModel data = mapper.readValue(file, DealersXMLModel.class);
            DatabaseContext.getInstance().importXML(data.getDealers());
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    @Override
    public boolean exportFile(File file) {
        return false;
    }
}
