package edu.metrostate.trackingsystem.infrastructure.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import edu.metrostate.trackingsystem.domain.models.Dealer;
import edu.metrostate.trackingsystem.domain.models.Vehicle;
import edu.metrostate.trackingsystem.infrastructure.database.IDatabaseContext;
import edu.metrostate.trackingsystem.infrastructure.logging.Logger;
import edu.metrostate.trackingsystem.infrastructure.database.DatabaseContext;
import edu.metrostate.trackingsystem.infrastructure.database.models.DealershipDatabase;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            var json = Files.readString(file.toPath());
            var jsonObject = JsonParser
                .parseString(json)
                .getAsJsonObject();

            var listType = new TypeToken<List<Vehicle>>() {}.getType();
            List<Vehicle> data = new Gson().fromJson(jsonObject.get("car_inventory"), listType);

            // Group by dealers
            Map<String, List<Vehicle>> groupByDealershipId = data.stream()
                    .collect(Collectors.groupingBy(Vehicle::getDealershipId));

            // Build a list of dealers
            List<Dealer> dealers = groupByDealershipId.entrySet().stream()
                    .map(e -> {
                        List<Vehicle> vehicles = e.getValue();
                        vehicles.forEach(Vehicle::normalize);
                        return new Dealer(e.getKey(), vehicles, vehicles.get(0).getDealershipName());
                    })
                    .toList();

            databaseContext.importJSON(dealers);
            logger.info("Imported successfully.");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean exportFile(File file) {
        var gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
        var data = databaseContext.getVehicles();
        var wrapper = new DealershipDatabase(data);
        var json = gson.toJson(wrapper);
        var path = file.getAbsolutePath();

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
     * The following methods are internal processes to auto-save & load the database.json
     */
    public void saveSession() {
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

        var data = databaseContext.getVehicles();
        var wrapper = new DealershipDatabase(data);
        var json = gson.toJson(wrapper);

        var folder = new File("src/main/resources/database");
        var file = new File(folder, "database.json");

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json);
            logger.info("Database JSON exported successfully.");
        } catch (IOException e) {
            logger.error(e.getMessage());
            System.exit(0);
        }
    }

    public boolean loadSession(File file) {
        return true;
    }
}