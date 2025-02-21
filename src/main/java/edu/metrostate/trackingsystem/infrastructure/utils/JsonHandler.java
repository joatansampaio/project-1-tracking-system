package edu.metrostate.trackingsystem.infrastructure.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import edu.metrostate.trackingsystem.application.dto.ImportDTO;
import edu.metrostate.trackingsystem.infrastructure.database.IDatabaseContext;
import edu.metrostate.trackingsystem.infrastructure.logging.Logger;
import edu.metrostate.trackingsystem.infrastructure.database.DatabaseContext;
import edu.metrostate.trackingsystem.domain.models.CarInventory;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.List;

public class JsonHandler implements IFileHandler {

    private static Logger logger = Logger.getLogger();
    private static JsonHandler instance;
    private static IDatabaseContext databaseContext;

    private JsonHandler() { }

    public static JsonHandler getInstance() {
        if (instance == null) {
            instance = new JsonHandler();
            logger = Logger.getLogger();
            databaseContext = DatabaseContext.getInstance();
        }
        return instance;
    }

    /**
     * @param file the file to be imported.
     * @return true if there was an error.
     */
    @Override
    public boolean importFile(File file) {
        try {
            String json = Files.readString(file.toPath());
            JsonObject jsonObject = JsonParser
                .parseString(json)
                .getAsJsonObject();

            Type listType = new TypeToken<List<ImportDTO>>() {}.getType();
            List<ImportDTO> data = new Gson().fromJson(jsonObject.get("car_inventory"), listType);
            databaseContext.loadDatabase(data);
            logger.info("Imported successfully.");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean exportFile(File file) {
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
        var data = databaseContext.getVehicles();
        CarInventory wrapper = new CarInventory(data);
        String json = gson.toJson(wrapper);
        String path = file.getAbsolutePath();

        if (!path.toLowerCase().endsWith(".json")) {
            file = new File(path + ".json");
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json);
            logger.info("Successfully exported to " + file.getAbsolutePath());
            return true;
        } catch (IOException e) {
            logger.error("Error exporting JSON: " + e.getMessage());
            return false;
        }
    }

    /**
     * This is a special internal process to auto-save the session's data to database.json
     */
    public void saveSession() {
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

        var data = databaseContext.getVehicles();
        CarInventory wrapper = new CarInventory(data);
        String json = gson.toJson(wrapper);

        File folder = new File("src/main/resources/database");
        File file = new File(folder, "database.json");

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json);
            logger.info("Database JSON exported successfully.");
        } catch (IOException e) {
            logger.error(e.getMessage());
            System.exit(0);
        }
    }
}