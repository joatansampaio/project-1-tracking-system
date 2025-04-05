//Handles reading of an XML formatted data file to an object
package edu.metrostate.trackingsystem.infrastructure.utils;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import edu.metrostate.trackingsystem.infrastructure.database.DatabaseContext;
import edu.metrostate.trackingsystem.infrastructure.database.IDatabaseContext;
import edu.metrostate.trackingsystem.infrastructure.database.models.DealersXMLModel;
import edu.metrostate.trackingsystem.infrastructure.database.models.DealershipDatabase;
import edu.metrostate.trackingsystem.infrastructure.logging.Logger;

import java.io.File;
import java.io.IOException;

public class XmlHandler implements IFileHandler {
    private static final Logger logger = Logger.getLogger();
    private static XmlHandler instance;

    private static IDatabaseContext databaseContext;

    private XmlHandler() { }

    public static XmlHandler getInstance() {
        if (instance == null) {
            instance = new XmlHandler();
            databaseContext = DatabaseContext.getInstance();
        }
        return instance;
    }

    /**
     * Imports an XML formatted file using the jackson library
     * @param file - The file to be imported
     * @return true if operation completed otherwise log and error and return false
     */
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
        var path = file.getAbsolutePath();

        try {
            XmlMapper xmlMapper = new XmlMapper();
            var data = databaseContext.getDealers();
            var wrapper = new DealershipDatabase(data);
            xmlMapper.writeValue(file, wrapper);
            return true;
        } catch (StreamWriteException e) {
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
