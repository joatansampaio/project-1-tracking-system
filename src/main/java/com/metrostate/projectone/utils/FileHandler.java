package com.metrostate.projectone.utils;

//Maven json-simple dependency imports
//https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//Java API imports
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.FileWriter;

// This class should be able to Import a file into the system
// as well as given a JsonString, export a JSON File.


//Successfully tested in main with this code
//FileHandler handler = new FileHandler();
//JSONObject jsonObject = handler.read("src/main/resources/inventory.json");
//System.out.println(jsonObject.toJSONString());

//Note: If ParseException catching is added,
// when you throw ParseException it requires an int for error type
//if (parser == null) {
//    throw new ParseException();

//}
public class FileHandler implements IFileHandler {
    //Initialized here to be able to use .close() if an exception occurs
    Reader reader = null;

    // We'll use the resources folder, and the fileName will always be a valid
    // json filename in main>resources

    /**
     * Reads a json formatted file to a JSONObject using the json-simple Maven dependency
     *
     * @param fileName The name of the json formatted file to be loaded.
     * @return JSONObject of the file if it loaded properly, null if the json file did not load properly.
     */
    @Override
    public JSONObject read(String fileName) throws IOException {
        try {
            JSONParser parser = new JSONParser();

            // Correct path for the main inventory  file should be src/main/resources/inventory.json
            reader = new FileReader(fileName);
            Object jsonObj = parser.parse(reader);
            reader.close();
            return (JSONObject) jsonObj;
        } catch (IOException | ParseException e) {
            reader.close();
            return null;
        }
    }

    /**
     * Write the inventory from a single dealership to json file in src/main/resources/
     *
     * @param jsonObj Formatted JSON information about dealer
     * @param dealershipId The dealer's ID
     * @return True if successfully exported, false if unsuccessful
     */
    @Override
    public boolean writeDealerToJson(JSONObject jsonObj, String dealershipId) {
        try {
            String outputFile = "src/main/resources/" + dealershipId + ".json";
            FileWriter file = new FileWriter(outputFile);
            file.write(JsonHelper.prettify(jsonObj.toJSONString()));
            file.flush();
            file.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Not needed, is overcoding, we could potentially flood folder with files for a large company. That is bad!
     * Write a single vehicle to json file in src/main/resources/
     * Filename output is in format of dealerShipId-vehicleId.json
     *
     * @param jsonFormatVehicle A vehicle object formatted as JSON and stored as a JSONObject
     * @param dealerShipId A dealership ID to be used for the filename
     * @param vehicleId The vehicle ID to be used for the filename
     * @return true if the operation was completed, false if some error occurred
     */

    public boolean writeVehicleToJson(JSONObject jsonFormatVehicle, String dealerShipId, String vehicleId) {
        try {
            String outputFile = "src/main/resources/" + dealerShipId + "-" + vehicleId + ".json";
            FileWriter file = new FileWriter(outputFile);
            file.write(jsonFormatVehicle.toJSONString());
            file.flush();
            file.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}