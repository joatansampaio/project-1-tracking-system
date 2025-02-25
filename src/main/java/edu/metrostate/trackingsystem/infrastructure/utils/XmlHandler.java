package edu.metrostate.trackingsystem.infrastructure.utils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import edu.metrostate.trackingsystem.infrastructure.database.DatabaseContext;
import edu.metrostate.trackingsystem.infrastructure.database.models.DealersXMLModel;
import edu.metrostate.trackingsystem.infrastructure.logging.Logger;
import java.io.File;

public class XmlHandler implements IFileHandler {

    Logger logger = Logger.getLogger();

    @Override
    public boolean importFile(File file) {

        XmlMapper mapper = new XmlMapper();

        try {
            DealersXMLModel data = mapper.readValue(file, DealersXMLModel.class);
            DatabaseContext.getInstance().importXML(data);
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
